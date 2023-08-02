package com.PDFExtraction.demo.controller;

import com.PDFExtraction.demo.DemoApplication;
import com.PDFExtraction.demo.aspect.TrackExecutionTime;
import com.PDFExtraction.demo.service.Service;
import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.ui.ModelMap;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.thymeleaf.TemplateEngine;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.*;

import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

import java.util.Map;
//import java.util.logging.Logger;

@org.springframework.stereotype.Controller
@RequestMapping("/api")
public class Controller {
    @Autowired
    private Service service;
    @Autowired
    private TemplateEngine templateEngine;
    Logger logger = (Logger) LoggerFactory.getLogger(Controller.class);
    @PostMapping("/extract-info")
    @TrackExecutionTime
    public  String extractInfo(@RequestParam("file") MultipartFile[] file, ModelMap model) throws Exception {

        InputStream[] inputStream = new InputStream[file.length];
        String[] fileName = new String[file.length];
        String uploadDir = System.getProperty("user.dir") + "/src/main/resources/templates";
        String[] filePath = new String[file.length];
        for(int i=0;i<file.length;i++) {
            String filename = file[i].getOriginalFilename();
            Path filepath = Paths.get(uploadDir, filename);
            Files.copy(file[i].getInputStream(), filepath, StandardCopyOption.REPLACE_EXISTING);
            filePath[i] = filepath.toString();
            inputStream[i] = file[i].getInputStream();
            fileName[i] = filename;
        }

        Map<Integer,Map<String, String>> infoMap = service.extractInfoFromPdf(inputStream,fileName,filePath);
        System.out.println(infoMap);
        File folder = new File(System.getProperty("java.io.tmpdir"));
        File[] listOfFiles = folder.listFiles();
        model.addAttribute("infoMap", infoMap);
        return  "result";
    }


    @RequestMapping(value="/{filename}", method=RequestMethod.GET)
    public ResponseEntity<byte[]> getPDF1(@PathVariable String filename) {
        File file = new File("C:/Users/Hardik/Downloads/PDFExtraction/demo/src/main/resources/templates/" + filename);
        // Read the content of the file into a byte array
        byte[] pdf1Bytes;
        try (FileInputStream fis = new FileInputStream(file);
             ByteArrayOutputStream baos = new ByteArrayOutputStream()) {
            byte[] buffer = new byte[1024];
            int bytesRead;
            while ((bytesRead = fis.read(buffer)) != -1) {
                baos.write(buffer, 0, bytesRead);
            }
            pdf1Bytes = baos.toByteArray();
        } catch (FileNotFoundException e) {
            throw new RuntimeException(e);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.parseMediaType("application/vnd.openxmlformats-officedocument.wordprocessingml.document"));
        headers.add("content-disposition", "inline;filename=" + filename);

//        headers.setContentType(MediaType.parseMediaType("application/pdf"));
//        headers.setContentType(new MediaType("application", "ms`1 aZ2word"));
        headers.add("content-disposition", "inline;filename=" + filename);

//        headers.setContentDispositionFormData(filename, filename);
        headers.setCacheControl("must-revalidate, post-check=0, pre-check=0");
        ResponseEntity<byte[]> response = new ResponseEntity<byte[]>(pdf1Bytes, headers, HttpStatus.OK);
        return response;
    }
    @RequestMapping("/")
    @TrackExecutionTime
    public String index(){
        return "index";
    }
}
