package onetomany.Labels;

import io.swagger.v3.oas.annotations.Operation;
import onetomany.Labels.Label_Image;
import onetomany.Labels.LabelRepository;
import org.apache.commons.io.FileUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import javax.sql.rowset.serial.SerialBlob;
import javax.sql.rowset.serial.SerialException;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.sql.SQLException;
import java.util.ArrayList;

@RestController
public class LabelController {


    // replace this! careful with the operating system in use
    private static String directory = "../../../../../../../home/benjbart/imgfolder";//"src/main/resources/imgfolder";//"\\src\\main\\resources\\imgfolder";

    @Autowired
    private LabelRepository labelRepository;


    @Operation(summary = "Returns a byte array of the image")
    @GetMapping(value = "/labels/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getLabelById(@PathVariable int id) throws IOException {
        Label_Image image = labelRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }

    @Operation(summary = "Save the provided image as a Blob")
    @PostMapping("labels")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile)  {
        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk
            byte[] byteArray = FileUtils.readFileToByteArray(destinationFile);
            Label_Image image = new Label_Image();
            image.setFileName(imageFile.getOriginalFilename());
            image.setFilePath(destinationFile.getAbsolutePath());
            SerialBlob label_img = new SerialBlob(byteArray);
            image.setImageBlob(label_img);
            labelRepository.save(image);
            return "File uploaded successfully: " + image.getId();//+ destinationFile.getAbsolutePath() + "\n"
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        } catch (SQLException e) {
            throw new RuntimeException(e);
        }
    }


    @Operation(summary = "Process an image stored in the Label Repository with Tesseract and return the string from Tesseract")
    @GetMapping(value = "/labels/OCR/{id}")
    String processImageOCRById(@PathVariable int id) throws IOException {
        Label_Image image = labelRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        Tesseract tesseract = new Tesseract();
        ArrayList<String> labelByLine = new ArrayList<>();

        try {
            char[] labelCharArray;
            String labelLine = "";
            //the path of your 'tessdata' folder
            tesseract.setDatapath("../../../../../../../usr/share/tesseract/tessdata");


            //inside the extracted file
            String text = tesseract.doOCR(imageFile);
            labelCharArray = text.toCharArray();
            for(char c : labelCharArray){
                if(c != '\n'){
                    labelLine = labelLine + c;
                }
                else{
                    labelByLine.add(labelLine);
                    labelLine = "";
                }
            }

            for(String line: labelByLine){

            }

            //path of the image file
            //System.out.print(text);
            return text;
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
        return null;
    }
/*
    @PostMapping("/labels/OCR")
    public String processPackageLabelOCR(@RequestParam("labels") MultipartFile imageFile)  {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("sftp://benjbart@coms-309-019.class.las.iastate.edu/home/benjbart/tess4j-5.10.0/tessdata");

        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk

            Label_Image image = new Label_Image();
            image.setFilePath(destinationFile.getAbsolutePath());

            try {
                String ocrResult = tesseract.doOCR(destinationFile);
                //Some things to sort out the specific text from the result
                return ocrResult;
            } catch (TesseractException e) {
                e.printStackTrace();
            }

        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
        return null;
    }*/


}
