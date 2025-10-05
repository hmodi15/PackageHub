package onetomany.Packages;

import java.awt.image.BufferedImage;
import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.IOException;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.List;
import java.util.Date;


import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import jakarta.transaction.Transactional;


import io.swagger.v3.oas.annotations.Operation;

import jakarta.transaction.Transactional;
import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;


import onetomany.Buildings.BuildingRepository;
import onetomany.Buildings.Buildings;
import onetomany.Credentials.CredentialsRepository;
import onetomany.Credentials.Credentials;
import onetomany.Labels.LabelRepository;
import onetomany.Labels.Label_Image;
import onetomany.Rooms.RoomRepository;
import onetomany.Rooms.Room;
import onetomany.Users.UserRepository;
import onetomany.Users.User;


import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import org.apache.commons.io.FileUtils;


import com.google.zxing.client.j2se.BufferedImageLuminanceSource;
import com.google.zxing.common.HybridBinarizer;
import com.google.zxing.*;
import com.google.zxing.MultiFormatReader;


@RestController
public class PackageController {

    private static String directory = "../../../../../../../home/hmodi/img";//"src/main/resources/imgfolder";//"\\src\\main\\resources\\imgfolder";

    @Autowired
    BuildingRepository buildingRepository;

    @Autowired
    RoomRepository roomRepository;

    @Autowired
    LabelRepository labelRepository;

    @Autowired
    UserRepository userRepository;

    @Autowired
    PackageRepository packageRepository;

    @Autowired
    CredentialsRepository credentialsRepository;

    private String success = "{\"message\":\"success\"}";
    private String failure = "{\"message\":\"failure\"}";

    @Operation(summary = "Get a list of all the packages")
    @GetMapping(path = "/packages")
    List<Package> getAllPackage(){
        return packageRepository.findAll();
    }

    @Operation(summary = "Get a specific package via id")
    @GetMapping(path = "/packages/{id}")
    Package getPackageById( @PathVariable int id){
        return packageRepository.findById(id);
    }


    @Operation(summary = "Get the newest package via email")
    @GetMapping(path = "/users/package/{email}")
    Package getNewestPackageByEmail( @PathVariable String email){
        Credentials userCred = credentialsRepository.findByemailId(email);
        User user = userRepository.findBycredentials(userCred);
        Package newPkg = new Package();
        int i = 0;
        List<Package> packages = user.getPackages();
        if(packages.isEmpty()){
            new Package("NO PACKAGES");
        }
        for(Package pkg : packages){
            if(i == 0){
                newPkg = pkg;
                i++;
            }
            else if(pkg.getid() > newPkg.getid()){
                newPkg = pkg;
            }
        }
        return newPkg;
    }

    @Operation(summary = "Get all packages via user's email")
    @GetMapping(path = "/users/packages/{email}")
    List<Package> getPackageByEmail(@PathVariable String email){
        Credentials userCred = credentialsRepository.findByemailId(email);
        User user = userRepository.findBycredentials(userCred);
        return user.getPackages();
    }

    @Operation(summary = "Create an empty package object")
    @PostMapping(path = "/packages")
    String createPackage(){
        Package pack = new Package();
        pack.setScan_Date_Str(new Date());
        packageRepository.save(pack);
        return success;
    }

    @Operation(summary = "Changes the package pickup status")
    @PutMapping(path = "/packages/pickup/{id}")
    String changePackagePickUpStatus(@PathVariable int id){
        Package pack = packageRepository.findById(id);
        pack.changePickUpStatus();
        packageRepository.save(pack);
        return success + "\nPickup status is now: " + pack.getPickUpStatus();
    }

    @Operation(summary = "Create an package object from parameters sent with the URL")
    @PostMapping(path = "/packages/create")
    String createCustomPackage(@RequestParam String name, @RequestParam String pickUpCode){
        String address = "Iowa State";
        Package pack = new Package(name, pickUpCode, address);
        packageRepository.save(pack);
        try {
            User user = userRepository.findByname(name);
            if (user.getName().equals(name)) {
                user.addPackages(pack);
                pack.setUser(user);
                pack.setAddress(user.getAddress());
                pack.setAptNum(user.getAptNum());
                pack.setScan_Date_Str(new Date());
                packageRepository.save(pack);
                userRepository.save(user);
            }
        } catch(Exception e) {return failure;}
        return success;
    }

    @Operation(summary = "Create an package object from parameters sent with the URL")
    @PostMapping(path = "/packages/create/{name}")
    String createCustomPackageWithName(@PathVariable String name){
        Package pack = new Package(name);
        packageRepository.save(pack);
        try {
            User user = userRepository.findByname(name);
            if (user.getName().equals(name)) {
                user.addPackages(pack);
                pack.setUser(user);
                pack.setAddress(user.getAddress());
                pack.setAptNum(user.getAptNum());
                pack.setScan_Date_Str(new Date());
                packageRepository.save(pack);
                userRepository.save(user);
            }
        } catch(Exception e) {return failure;}
        return success;
    }

    @Operation(summary = "Create a package by processing an image and give it a specified id number")
    @PostMapping(path = "/packages/OCR/{id}")
    String createCustomPackage(@PathVariable int id, @RequestParam("image") MultipartFile imageFile){
        Tesseract tesseract = new Tesseract();
        //tesseract.setDatapath("../../../../src/main/resources/tess4j-4.5.1/tessdata");	//for testing locally. The files required for this function are not included in this repo
        tesseract.setDatapath("../../../../../../../usr/share/tesseract/tessdata");
        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk
            byte[] byteArray = FileUtils.readFileToByteArray(destinationFile);
            Label_Image image = new Label_Image();
            image.setFilePath(destinationFile.getAbsolutePath());
            SerialBlob label_img = new SerialBlob(byteArray);
            image.setImageBlob(label_img);
            //tracking number scanning
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(label_img.getBytes(1,(int)label_img.length())));
            String trackingNum = "not found";
            boolean trackingFound = false;
            int startH = 0;
            //tracking number scanning
            try {
                String ocrResult = tesseract.doOCR(img);
                StringBuilder filtered = new StringBuilder(filterOCRResult(ocrResult));
                filtered = new StringBuilder(filtered + "|" + "|" + "|" + "|");
                if(String.valueOf(filtered).contains(failure)){
                    return failure + "failed to gather all needed information. NO Package will be made.\nInformation Gathered:\n" + filtered;
                }
                String rawFiltered = String.valueOf(filtered);
                String address = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                String aptNum = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                String name = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                if(String.valueOf(filtered).startsWith("9")){
                    trackingNum = filtered.substring(0, (filtered.indexOf("|")));
                }
                else{	// searching for the tracking number if its not a USPS package
                    while(!trackingFound){
                        try{
                            if(img.getHeight() > startH){
                                System.out.println("main:" + startH);
                                trackingNum = readAndFilterTrackingNumber(img, startH);
                                System.out.println(trackingNum);
                                trackingFound = true;
                                if((trackingFound = true) && (String.valueOf(filtered).startsWith("U") && (!trackingNum.startsWith("1Z")))){
                                    trackingFound = false;
                                    trackingNum = "";
                                    if(img.getHeight() > startH){
                                        startH += 500;
                                    }
                                    else{
                                        trackingNum = "not found";
                                        trackingFound = true;
                                    }
                                }
                            }
                            else{
                                trackingNum = "not found";
                            }
                        } catch (ChecksumException | FormatException e) {
                            throw new RuntimeException(e);
                        } catch (NotFoundException e) {
                            if(img.getHeight() > startH){
                                startH += 500;
                            }
                            else{
                                trackingNum = "not found";
                            }
                        }
                    }
                }
                //finished extracting the data from The filtered Tesseract result & ZXing

                User user = userRepository.findByname(name);
                Package pack = new Package(user, address, aptNum, trackingNum, label_img, rawFiltered);
                pack.setScan_Date_Str(new Date());
                pack.setid(id);
                packageRepository.save(pack);
                //Some things to sort out the specific text from the result
                //System.out.println(ocrResult + "\n\nPackage_id:" + pack.getid() + "\n\n filtered OCR result:" + rawFiltered);
                return ocrResult + "\n\nPackage_id:" + pack.getid() + "\n\n filtered OCR result:" + rawFiltered;
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return failure;
    }



    @Operation(summary = "Process the provided image with Tesseract and return the string from Tesseract")
    @PostMapping("/packages/OCR")
    public String processPackageLabelOCR(@RequestParam("image") MultipartFile imageFile)  {
        Tesseract tesseract = new Tesseract();
        //tesseract.setDatapath("../../../../src/main/resources/tess4j-4.5.1/tessdata");	//for testing locally. The files required for this function are not included in this repo
        tesseract.setDatapath("../../../../../../../usr/share/tesseract/tessdata");
        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk
            byte[] byteArray = FileUtils.readFileToByteArray(destinationFile);
            Label_Image image = new Label_Image();
            image.setFilePath(destinationFile.getAbsolutePath());
            SerialBlob label_img = new SerialBlob(byteArray);
            image.setImageBlob(label_img);
            BufferedImage img = ImageIO.read(new ByteArrayInputStream(label_img.getBytes(1,(int)label_img.length())));
            String trackingNum = "not found";
            boolean trackingFound = false;
            int startH = 0;
            //tracking number scanning
            try {
                String ocrResult = tesseract.doOCR(img);
                StringBuilder filtered = new StringBuilder(filterOCRResult(ocrResult));
                filtered = new StringBuilder(filtered + "|" + "|" + "|" + "|");
                if(String.valueOf(filtered).contains(failure)){
                    return failure + "failed to gather all needed information. NO Package will be made.\nInformation Gathered:\n" + filtered;
                }
                String rawFiltered = String.valueOf(filtered);
                String address = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                String aptNum = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                String name = filtered.substring(0, (filtered.indexOf("|")));
                filtered = filtered.delete(0, filtered.indexOf("|"));
                filtered.deleteCharAt(0);
                if(String.valueOf(filtered).startsWith("9")){
                    trackingNum = filtered.substring(0, (filtered.indexOf("|")));
                }
                else{
                    while(!trackingFound){
                        try{
                            if(img.getHeight() > startH){
                                System.out.println("main:" + startH);
                                trackingNum = readAndFilterTrackingNumber(img, startH);
                                System.out.println(trackingNum);
                                trackingFound = true;
                                if((trackingFound) && (String.valueOf(filtered).startsWith("U") && (!trackingNum.startsWith("1Z")))){
                                    trackingFound = false;
                                    trackingNum = "";
                                    if(img.getHeight() > startH){
                                        startH += 500;
                                    }
                                    else{
                                        trackingNum = "not found";
                                        trackingFound = true;
                                    }
                                }
                            }
                            else{
                                trackingNum = "not found";
                            }
                        } catch (ChecksumException | FormatException e) {
                            throw new RuntimeException(e);
                        } catch (NotFoundException e) {
                            if(img.getHeight() > startH){
                                startH += 500;
                            }
                            else{
                                trackingNum = "not found";
                            }
                        }
                    }
                }

                if((trackingFound) && (!String.valueOf(filtered).startsWith("U"))){ // separates the Fedex tracking number
                    StringBuilder trackSB = new StringBuilder(trackingNum.substring(trackingNum.length()-16, trackingNum.length()-1));
                    while(trackingNum.startsWith("0")){
                        trackSB.deleteCharAt(0);
                    }
                    trackingNum = trackSB.toString();
                }
                //finished extracting the data from The filtered Tesseract result & ZXing

                User user = userRepository.findByname(name);
                Package pack = new Package(user, address, aptNum, trackingNum, label_img, rawFiltered);
                pack.setScan_Date_Str(new Date());
                packageRepository.save(pack);
                //Some things to sort out the specific text from the result
                System.out.println(ocrResult + "\n\nPackage_id:" + pack.getid() + "\n\n filtered OCR result:" + rawFiltered);
                return ocrResult + "\n\nPackage_id:" + pack.getid() + "\n\n filtered OCR result:" + rawFiltered;
            } catch (TesseractException e) {
                e.printStackTrace();
            }
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
        return failure;
    }

    @Operation(summary = "Update a specific package's details")
    @PutMapping("/packages/{id}")
    Package updatePackage(@PathVariable int id, @RequestBody Package request){
        Package pack = packageRepository.findById(id);
        if(pack == null){ return null; }
        packageRepository.save(request);
        return packageRepository.findById(id);
    }


    @Operation(summary = "Delete a specific package")
    @DeleteMapping(path = "/packages/{id}")
    @Transactional
    public String deletePackage(@PathVariable int id){
        Package pkg = packageRepository.findById(id);
        packageRepository.deleteById(pkg.getid());
        return success;
    }

    @Operation(summary = "This function is to read the barcode to extract the tracking number of the package")
    String readAndFilterTrackingNumber(BufferedImage buffimg, int startHeight) throws ChecksumException, NotFoundException, FormatException {
        String tracking_num;
        int h;
        BufferedImage bufferedimg;
        if ((startHeight + 500) < buffimg.getHeight()) {
            h = 500;
        } else {
            h = buffimg.getHeight() % 500;
        }
        System.out.println(h);
        bufferedimg = buffimg.getSubimage(0, startHeight, buffimg.getWidth(), h);
        BufferedImageLuminanceSource bils = new BufferedImageLuminanceSource(bufferedimg);
        Binarizer binarizer = new HybridBinarizer(bils);
        BinaryBitmap bbm = new BinaryBitmap(binarizer);
        Result result = new MultiFormatReader().decode(bbm);
        tracking_num = result.getText() + " | " + result.getBarcodeFormat();
        if(tracking_num.contains("1Z")){
            return tracking_num;
        }
        else if(tracking_num.length() == 34){
            tracking_num = tracking_num.substring(21, 33);
        }
        return tracking_num;
    }


    @Operation(summary = "Filter the result from Tesseract to get the Address, Apartment Number and Name of the recipient")
    String filterOCRResult(String ocrRAWResult){
        String filtered;
        String address = "NOT FOUND";
        String aptNum = "NOT FOUND";
        String name = "NOT FOUND";
        String trackingNum = "----";
        String lineRAW;
        String builtAddress;
        String attemptAptNum;
        String attemptUserName;
        char[] charArrayRaw = ocrRAWResult.toCharArray();
        char[] charArrayAttempt;
        ArrayList<String> strArrLRAW = new ArrayList<>();
        int[] eIndexes = new int[4];
        boolean foundE = false;
        int foundInLine = 0;
        int lastFound;
        int i = 0;
        int attemptNum = 0;
        int searchMax = 3;
        int searchMin = 3;
        double percentFound = 0;
        StringBuilder temp = new StringBuilder();
        Buildings rBuilding = new Buildings();
        Room rRoom = new Room();
        User rUser = new User();
        for(i = 0; i < charArrayRaw.length; i++){
            if(charArrayRaw[i] == '\n'){
                strArrLRAW.add(temp.toString());
                temp = new StringBuilder();
            }
            else if((charArrayRaw[i] != ' ') && (charArrayRaw[i] != '\t')){
                temp.append(charArrayRaw[i]);
            }
        }

        List<Buildings> buildingsList = buildingRepository.findAll();
        //search for address numbers, then check for first instance of the first character in the street name, if it
        // can't be found check the second and so on, then check how well the street names match up.
        // then check for apt numbers. in the vincinity of the address,

        for(Buildings built : buildingsList) {
            builtAddress = built.getName().replaceAll("\\s", "").toUpperCase();
            for (i = 0; i < strArrLRAW.size(); i++) {
                lineRAW = strArrLRAW.get(i).toUpperCase();
                if(lineRAW.contains(builtAddress)){
                    foundE = true;
                    eIndexes[0] = i;
                    address = built.getName();
                    rBuilding = built;
                    break;
                }
            }
            if(!foundE) {
                for (i = 0; i < strArrLRAW.size(); i++) {
                    StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
                    charArrayAttempt = builtAddress.toCharArray();
                    foundInLine = 0;
                    for(char b : charArrayAttempt){
                        if(stringBuilderRAW.indexOf(String.valueOf(b)) > -1){
                            lastFound = stringBuilderRAW.indexOf(String.valueOf(b));
                            foundInLine++;
                            stringBuilderRAW.delete(0,lastFound);
                        }
                    }
                    percentFound = ((double)foundInLine /(charArrayAttempt.length)) * 100;
                    if(percentFound > 90){
                        foundE = true;
                        eIndexes[0] = i;
                        address = built.getName();
                        rBuilding = built;
                        break;
                    }
                }
            }
            if(foundE){
                break;
            }
        }
        //finished searching for address. will fail if no address found.
        if(rBuilding.equals(new Buildings())){
            return failure;
        }

        for(i = 0; i <= 3; i++){
            if(eIndexes[0] - i == 0){
                searchMin = i;
            } else if ((eIndexes[0] - i >= 0) && (i == 3)) {
                searchMin = 3;
            }
            if(eIndexes[0] + i == strArrLRAW.size()-1){
                searchMax = i;
            } else if ((eIndexes[0] + i <= strArrLRAW.size()-1) && (i == 3)) {
                searchMax = 3;
            }
        }


        //if it succeeds then it will go onto make search for a room number.
        List<Room> roomsList = roomRepository.findAllByBuildingId(rBuilding.getId());
        foundE = false;

        for(Room room : roomsList) {
            attemptAptNum = room.getAptNum().replaceAll("\\s", "").toUpperCase();
            for (i = eIndexes[0]-searchMin; i <= eIndexes[0]+searchMax; i++) {
                lineRAW = strArrLRAW.get(i).toUpperCase();
                if(lineRAW.contains(attemptAptNum)){
                    foundE = true;
                    eIndexes[1] = i;
                    aptNum = room.getAptNum();
                    rRoom = room;
                    break;
                }
            }
            if(!foundE) {
                for (i = eIndexes[0]-searchMin; i <= eIndexes[0]+searchMax; i++) {
                    StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
                    charArrayAttempt = attemptAptNum.toCharArray();
                    foundInLine = 0;
                    for(char b : charArrayAttempt){
                        if(stringBuilderRAW.indexOf(String.valueOf(b)) > -1){
                            lastFound = stringBuilderRAW.indexOf(String.valueOf(b));
                            foundInLine++;
                            if(lastFound == 0){
                                stringBuilderRAW.deleteCharAt(0);
                            }else{
                                stringBuilderRAW.delete(0,lastFound);
                            }
                        }
                    }
                    percentFound = ((double)foundInLine/(charArrayAttempt.length)) * 100;
                    if(percentFound > 90){
                        foundE = true;
                        eIndexes[1] = i;
                        aptNum = room.getAptNum();
                        rRoom = room;
                        break;
                    }
                }
            }
            if(foundE){
                break;
            }
        }
        //finished searching for apartment number. will fail if no matching apartment number found.
        if(rRoom.equals(new Room())){
            return address + "|" + failure;
        }

        List<User> userList = userRepository.findAllByRoomId(rRoom.getId());
        foundE = false;

        for(User user : userList) {
            attemptUserName = user.getName().replaceAll("\\s", "").toUpperCase();
            for (i = eIndexes[0]-searchMin; i <= eIndexes[0]+searchMax; i++) {
                lineRAW = strArrLRAW.get(i).toUpperCase();
                if(lineRAW.contains(attemptUserName)){
                    foundE = true;
                    eIndexes[2] = i;
                    name = user.getName();
                    rUser = user;
                    break;
                }
            }
            if(!foundE) {
                for (i = eIndexes[0]-searchMin; i <= eIndexes[0]+searchMax; i++) {
                    StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
                    charArrayAttempt = attemptUserName.toCharArray();
                    foundInLine = 0;
                    for(char b : charArrayAttempt){
                        if(stringBuilderRAW.indexOf(String.valueOf(b)) > -1){
                            lastFound = stringBuilderRAW.indexOf(String.valueOf(b));
                            foundInLine++;
                            if(lastFound == 0){
                                stringBuilderRAW.deleteCharAt(0);
                            }else{
                                stringBuilderRAW.delete(0,lastFound);
                            }
                        }
                    }
                    percentFound = ((double)foundInLine/(charArrayAttempt.length)) * 100;
                    if(percentFound > 90){
                        foundE = true;
                        eIndexes[2] = i;
                        name = user.getName();
                        rUser = user;
                        break;
                    }
                }
            }
            if(foundE){
                break;
            }
        }

        if(rUser.equals(new User())){
            return address + "|" + aptNum + "|" + failure;
        }


        foundE = false;

        //search for a string to see if its a USPS package or not
        for (i = 0; i < strArrLRAW.size(); i++) {
            StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
            charArrayAttempt = "USPSTRACKING#".toCharArray();
            foundInLine = 0;
            for(char b : charArrayAttempt){
                if(stringBuilderRAW.indexOf(String.valueOf(b)) > -1){
                    lastFound = stringBuilderRAW.indexOf(String.valueOf(b));
                    foundInLine++;
                    stringBuilderRAW.delete(0,lastFound);
                }
            }
            percentFound = ((double)foundInLine /(charArrayAttempt.length)) * 100;
            if(percentFound > 90){
                foundE = true;
                eIndexes[3] = i;
                break;
            }
        }

        if(foundE){
            foundE = false;
            for (i = eIndexes[3]-1; i < strArrLRAW.size(); i++) {
                StringBuilder trackSB = new StringBuilder();
                StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
                charArrayAttempt = stringBuilderRAW.toString().toCharArray();
                foundInLine = 0;
                if(stringBuilderRAW.indexOf("9") > -1){
                    for(char c : charArrayAttempt){
                        if((Character.isDigit(c)) && (foundInLine == 0)) {
                            if(c == '9'){
                                break;
                            }
                            lastFound = stringBuilderRAW.indexOf(String.valueOf(c));
                            foundInLine++;
                            trackSB.append(c);
                            stringBuilderRAW.delete(0,lastFound);
                        }
                        else if(Character.isDigit(c)){
                            lastFound = stringBuilderRAW.indexOf(String.valueOf(c));
                            foundInLine++;
                            trackSB.append(c);
                            stringBuilderRAW.delete(0,lastFound);
                        }
                    }
                }
                if((foundInLine == 22) && (trackSB.indexOf("9") < 1)){
                    trackingNum = trackSB.toString();
                    foundE = true;
                    eIndexes[3] = i;
                    break;
                }
            }
        }
        else{
            for (i = 0; i < strArrLRAW.size(); i++) {
                StringBuilder stringBuilderRAW = new StringBuilder(strArrLRAW.get(i).toUpperCase());
                charArrayAttempt = "UPS".toCharArray();
                foundInLine = 0;
                for(char b : charArrayAttempt){
                    if(stringBuilderRAW.indexOf(String.valueOf(b)) > -1){
                        lastFound = stringBuilderRAW.indexOf(String.valueOf(b));
                        foundInLine++;
                        stringBuilderRAW.delete(0,lastFound);
                    }
                }
                percentFound = ((double)foundInLine /(charArrayAttempt.length)) * 100;
                if(percentFound > 90){
                    trackingNum = "UPS";
                    foundE = true;
                    eIndexes[3] = i;
                    break;
                }
            }
        }

        filtered = address + "|" + aptNum + "|" + name + "|" + trackingNum + "|";
        return filtered;
    }
}
