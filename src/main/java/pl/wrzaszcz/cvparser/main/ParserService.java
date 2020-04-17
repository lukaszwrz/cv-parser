package pl.wrzaszcz.cvparser.main;

import org.apache.commons.io.FilenameUtils;
import org.apache.tika.exception.TikaException;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.sax.ToXMLContentHandler;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.ContentHandler;
import org.xml.sax.SAXException;
import pl.wrzaszcz.cvparser.parser.impl.ResumeParser;

import java.io.*;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@Service
public class ParserService {

    public JSONObject parseResume(MultipartFile file) {
//        String uploadedFolder = System.getProperty("user.dir");
//        if (uploadedFolder != null && !uploadedFolder.isEmpty()) {
//            uploadedFolder += "/Resumes/";
//        } else
//            throw new RuntimeException("User Directory not found");
//        byte[] bytes = null;
//        try {
//            bytes = file.getBytes();
//        } catch (IOException exception) {
//            throw new RuntimeException(exception.getMessage());
//        }
//        Path path = null;
//        try {
//            path = Paths.get(uploadedFolder + file.getOriginalFilename());
//            if (!Files.exists(path.getParent()))
//                Files.createDirectories(path.getParent());
//            path = Files.write(path, bytes);
//        } catch (IOException exception) {
//            throw new RuntimeException(exception.getMessage());
//
//        }

        try {
            File tempFile = File.createTempFile("prefix", "postfix");

// ask JVM to delete it upon JVM exit if you forgot / can't delete due exception
            tempFile.deleteOnExit();

// transfer MultipartFile to File
            file.transferTo(tempFile);

// do business logic here
            ResumeParser resumeParser = new ResumeParser();
            String resume = resumeParser.parse(tempFile);

// tidy up
            tempFile.delete();

//            File tikkaConvertedFile = parseToHTMLUsingApacheTikka(path.toAbsolutePath().toString());
//            ResumeParser resumeParser = new ResumeParser();
//            File uploadedFile = path.toFile();
//            String resume = resumeParser.parse(uploadedFile);
            JSONParser parser = new JSONParser();
            return (JSONObject) parser.parse(resume);
        } catch (IOException | ParseException e) {
            e.printStackTrace();
        }
        return null;
    }

    public File parseToHTMLUsingApacheTikka(String file) throws IOException, SAXException, TikaException {
        String ext = FilenameUtils.getExtension(file);
        String outputFileFormat = "";
        if (ext.equalsIgnoreCase("html") | ext.equalsIgnoreCase("pdf") | ext.equalsIgnoreCase("doc")
                | ext.equalsIgnoreCase("docx")) {
            outputFileFormat = ".html";
        } else if (ext.equalsIgnoreCase("txt") | ext.equalsIgnoreCase("rtf")) {
            outputFileFormat = ".txt";
        } else {
            System.out.println("Input format of the file " + file + " is not supported.");
            return null;
        }
        String OUTPUT_FILE_NAME = FilenameUtils.removeExtension(file) + outputFileFormat;
        ContentHandler handler = new ToXMLContentHandler();
        InputStream stream = new FileInputStream(file);
        AutoDetectParser parser = new AutoDetectParser();
        Metadata metadata = new Metadata();
        try {
            parser.parse(stream, handler, metadata);
            FileWriter htmlFileWriter = new FileWriter(OUTPUT_FILE_NAME);
            htmlFileWriter.write(handler.toString());
            htmlFileWriter.flush();
            htmlFileWriter.close();
            return new File(OUTPUT_FILE_NAME);
        } finally {
            stream.close();
        }
    }
}