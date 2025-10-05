package onetomany.Images;

import io.swagger.v3.oas.annotations.Operation;
import onetomany.Labels.LabelRepository;
import onetomany.Labels.Label_Image;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import net.sourceforge.tess4j.Tesseract;
import net.sourceforge.tess4j.TesseractException;

import javax.imageio.ImageIO;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;

@RestController
public class ImageController {

    // replace this! careful with the operating system in use
    private static String directory = "../../../../../../../home/benjbart/imgfolder";//"src/main/resources/imgfolder";//"\\src\\main\\resources\\imgfolder";

    @Autowired
    private ImageRepository imageRepository;

    @Autowired
    private LabelRepository labelRepository;

    @Operation(summary = "Get the byte array for the image from the provided Id")
    @GetMapping(value = "/images/{id}", produces = MediaType.IMAGE_JPEG_VALUE)
    byte[] getImageById(@PathVariable int id) throws IOException {
        Image image = imageRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        return Files.readAllBytes(imageFile.toPath());
    }
/*
    @PostMapping("images")
    public String handleFileUpload(@RequestParam("image") MultipartFile imageFile)  {

        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk

            Image image = new Image();
            image.setFilePath(destinationFile.getAbsolutePath());
            imageRepository.save(image);

            return "File uploaded successfully: " + destinationFile.getAbsolutePath();
        } catch (IOException e) {
            return "Failed to upload file: " + e.getMessage();
        }
    }*/


    @Operation(summary = "Process an image from the provided Id with Tesseract and return the string from Tesseract")
    @GetMapping(value = "/images/OCR/{id}")
    String processImageOCRById(@PathVariable int id) throws IOException {
        Label_Image image = labelRepository.findById(id);
        File imageFile = new File(image.getFilePath());
        Tesseract tesseract = new Tesseract();
        try {
            //the path of your 'tessdata' folder
            tesseract.setDatapath("../../../../../../../usr/share/tesseract/tessdata");
            //inside the extracted file
            String text = tesseract.doOCR(imageFile);
            //path of the image file
            //System.out.print(text);
            return text;
        }
        catch (TesseractException e) {
            e.printStackTrace();
        }
        return null;
    }

    @Operation(summary = "Process the provided image with Tesseract and return the string from Tesseract")
    @PostMapping("/images")
    public String processPackageLabelOCR(@RequestParam("image") MultipartFile imageFile)  {
        Tesseract tesseract = new Tesseract();
        tesseract.setDatapath("../../../../../../../usr/share/tesseract/tessdata");

        try {
            File destinationFile = new File(directory + File.separator + imageFile.getOriginalFilename());
            imageFile.transferTo(destinationFile);  // save file to disk

            Image image = new Image();
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
    }



}
