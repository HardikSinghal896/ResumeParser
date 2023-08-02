package com.PDFExtraction.demo.service;

import org.apache.pdfbox.pdmodel.PDDocument;
import org.apache.pdfbox.text.PDFTextStripper;
import org.apache.poi.xwpf.extractor.XWPFWordExtractor;
import org.apache.poi.xwpf.usermodel.XWPFDocument;
import java.io.*;
import java.net.URL;
import java.util.*;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

@org.springframework.stereotype.Service
public class Service {
    public Map<Integer,Map<String, String>> extractInfoFromPdf(InputStream[] pdfInputStream, String[] fileName, String[] filePath) throws IOException {

        Map<Integer,Map<String, String>> ans = new HashMap<>();
        Map<String, Integer> skillMap = new HashMap<>();

        Map<String,String> Corruptedmap = new HashMap<>();
        skillMap.put("c++",1);          skillMap.put("dbms",1);                 skillMap.put("oops",1);
        skillMap.put("html",1);         skillMap.put("operating system",1);     skillMap.put("springboot",1);
        skillMap.put("javascript",1);   skillMap.put("reactjs",1);              skillMap.put("c#",1);
        skillMap.put("java",1);         skillMap.put("Nodejs",1);               skillMap.put("css",1);
        skillMap.put("ruby",1);         skillMap.put("expressjs",1);            skillMap.put("c",1);
        skillMap.put("sql",1);          skillMap.put("python",1);               skillMap.put("data structures and algorithms",1);
        skillMap.put("oracle",1);       skillMap.put("jdbc",1);                 skillMap.put("nosql",1);
        skillMap.put("mysql",1);        skillMap.put("j2ee",1);                 skillMap.put("mvc",1);
        skillMap.put("dsa",1);          skillMap.put("spring",1);               skillMap.put("windows",1);
        skillMap.put("linux",1);        skillMap.put("unix",1);

        for(int i=0;i<pdfInputStream.length;i++)
        {
            File file1 = new File(filePath[i]);
            URL url = file1.toURI().toURL();

            Map<String,String> infoMap = new HashMap<>();
            String[] lines=null;
            String extension = "";
            InputStream pdfInputStream1=pdfInputStream[i];
            if (fileName[i].endsWith(".pdf")) {
                // Parse PDF file using Apache PDFBox
                extension="pdf";
            } else if (fileName[i].endsWith(".doc") || fileName[i].endsWith(".docx")) {
                // Parse DOC/DOCX file using Apache POI
                extension="docx";
            } else if(fileName[i].endsWith(".txt")){
                // Handle unsupported file type
                extension="txt";
            }
            else{
                infoMap.put("error","Please provide the file in PDF,DOCS,TXT format");
                ans.put(i,infoMap);
                continue;
            }
            if ("pdf".equalsIgnoreCase(extension)) {
                // Parse PDF file using Apache PDFBox
                boolean isValidPdf = true;
                String error = "";
                try{
                    PDDocument document = PDDocument.load(pdfInputStream1);
                    PDFTextStripper pdfStripper = new PDFTextStripper();
                    pdfStripper.setStartPage(1);
                    pdfStripper.setEndPage(1);
                    String pdfText = pdfStripper.getText(document);
                    lines = pdfText.split("[\\r?\\n]+");
                }catch (Exception e){
                    error = "file is courrepted";
                    isValidPdf = false;
                }
                if(isValidPdf == false)
                {
                    System.out.println(error);
                    continue;
                }

            } else if ("docx".equalsIgnoreCase(extension)) {
                // Parse DOCX file using Apache POI
                boolean isValidDoc = true;
                String error = "";
                try{
                    XWPFDocument document = new XWPFDocument(pdfInputStream[i]);
                    XWPFWordExtractor extractor = new XWPFWordExtractor(document);
                    lines = extractor.getText().split("[\\r?\\n]+");
                }catch (Exception e){
                    error = "File is corrupted";
                    isValidDoc = false;
                }
                if(isValidDoc == false){

                    System.out.println(error);
                    continue;
                }
            }else if ("txt".equalsIgnoreCase(extension)) {
                boolean isValidTxt = true;
               try{
                   List<String> linesList = new ArrayList<>();
                   BufferedReader reader = new BufferedReader(new InputStreamReader(pdfInputStream[i]));

                   String line;
                   while ((line = reader.readLine()) != null) {
                       linesList.add(line);
                   }
                   lines = linesList.toArray(new String[0]);
               }catch (Exception e){
                   isValidTxt = false;
               }
               if(isValidTxt == false)
                   continue;
            }
            else{

            }
            String firstName = null;
            String lastName = "";
            String email=null;
            Pattern firstNamePattern1 = Pattern.compile("^([A-Za-z]+) ([A-Za-z]+)");
            Pattern phoneNumberPattern1 = Pattern.compile("\\d");
            Pattern phoneNumberPattern2 = Pattern.compile("[0-9()]{3,5}[- ][0-9]{3}[- ][0-9]{4}");
            Pattern phoneNumberPattern3 = Pattern.compile("[+][0-9 ]{2,4}[0-9]{10}");
            Pattern phoneNumberPattern4 = Pattern.compile("[0-9]{10}");
            Pattern emailPattern=Pattern.compile("[a-zA-Z0-9.#$]+[@][a-zA-Z]+[.][a-zA-Z]{2,3}");
            String FinalNumber=null;
            String Skills = "";
            boolean flag1=true;
            boolean flag2=false;
            Map<String,Integer> AvailSkill = new HashMap<>();
            for (String line : lines) {
                line = line.trim();
                String[] words = line.split("[ ,;/]");
                for(String word : words){
                    String temp = word.toLowerCase();
                    if(skillMap.containsKey(temp) && !AvailSkill.containsKey(temp)){
                        Skills +=word;
                        AvailSkill.put(temp,1);
                        Skills+=",";
                    }
                }
                if(flag2)
                {
                    flag1=false;
                }

                int count=0;
                Matcher NameMatcher = firstNamePattern1.matcher(line);
                if(NameMatcher.find()){
                    if(firstName==null)
                    firstName = NameMatcher.group(1);
                    if(lastName=="")
                    lastName = NameMatcher.group(2);
                }
                Matcher emailMatcher = emailPattern.matcher(line);
                if(emailMatcher.find()){
                    email=emailMatcher.group(0);
                }
                Matcher phoneNumberMatcher=phoneNumberPattern1.matcher(line);
                Matcher phoneNumberMatcher1=phoneNumberPattern2.matcher(line);
                Matcher phoneNumberMatcher2=phoneNumberPattern3.matcher(line);
                Matcher phoneNumberMatcher3=phoneNumberPattern4.matcher(line);
                if(phoneNumberMatcher2.find() && FinalNumber==null)
                {
                    FinalNumber=phoneNumberMatcher2.group(0);
                }
                if(phoneNumberMatcher1.find() && FinalNumber==null)
                {
                    FinalNumber=phoneNumberMatcher1.group(0);
                }
                if(phoneNumberMatcher3.find() && FinalNumber==null){
                    FinalNumber=phoneNumberMatcher3.group(0);
                }
                String TempNumber = "";
                while(phoneNumberMatcher.find() && FinalNumber==null){
                    TempNumber=TempNumber + phoneNumberMatcher.group(0);
                    count++;
                    if(count>=10)
                    {
                        FinalNumber=TempNumber;
                        break;
                    }
                }
            }
            infoMap.put("firstName", firstName);
            infoMap.put("lastName", lastName);
            infoMap.put("phoneNumber", FinalNumber);
            infoMap.put("Email", email);
            infoMap.put("url", fileName[i]);
            infoMap.put("skills",Skills);
            ans.put(i,infoMap);
        }
        return ans;
    }

}
