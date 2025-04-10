package node.com.usernamechange.service.impl;

import org.springframework.http.*;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;

import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;
import org.telegram.telegrambots.meta.api.objects.Document;
import org.telegram.telegrambots.meta.api.objects.Message;

import lombok.extern.log4j.Log4j;
import node.com.usernamechange.dao.AppDocumentDAO;
import node.com.usernamechange.dao.BinaryContentDAO;
import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.service.FileService;
import node.com.usernamechange.entity.BinaryContent;
import node.com.usernamechange.exceptions.UploadFileException;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    private final AppDocumentDAO appDocumentDAO;
    private final BinaryContentDAO binaryContentDAO;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
        String fileId = telegramMessage.getDocument().getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            JSONObject jsonObject = new JSONObject(response.getBody());
            String filePath = String.valueOf(jsonObject
            		.getJSONObject("result")
            		.getString("file_path"));
            byte[] fileInByte = downloadFile(filePath);
            BinaryContent transientBinaryContent = BinaryContent.builder()
            		.fileAsArrayOfBytes(fileInByte)
            		.build();
            BinaryContent persistentBinaryContent = binaryContentDAO.save(transientBinaryContent);
            Document telegramDoc = telegramMessage.getDocument();
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("bad response from telegram service: " + response);
        }
    }

    private AppDocument buildTransientAppDoc(Document telegramDoc, BinaryContent persistenBinaryContent) {
        return AppDocument.builder()
        		.telegramField(telegramDoc.getFileId())
        		.docName(telegramDoc.getFileName())
        		.binaryContent(persistenBinaryContent)
        		.mimeType(telegramDoc.getMimeType())
        		.fileSize(telegramDoc.getFileSize())
        		.build();
    }

    private ResponseEntity<String> getFilePath(String fileId) {
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders headers = new HttpHeaders();
        HttpEntity<String> request = new HttpEntity<>(headers);

        return restTemplate.exchange(
        		fileInfoUri,
        		HttpMethod.GET,
        		request,
        		String.class,
        		token, fileId
        );
    }

    private byte[] downloadFile(String filePath) {
    	String fullUri = fileStorageUri.replace("{token}", token)
    			.replace("{filePath}", filePath);
    	URL urlObj = null;
    	try {
    		urlObj = new URL(fullUri);
    	} catch (MalformedURLException e) {
    		throw new UploadFileException(e);
    	}
    	try (InputStream is = urlObj.openStream()) {
    	    ByteArrayOutputStream buffer = new ByteArrayOutputStream();
    	    byte[] chunk = new byte[8192];
    	    int bytesRead;
    	    
    	    while ((bytesRead = is.read(chunk)) != -1) {
    	        buffer.write(chunk, 0, bytesRead);
    	    }
    	    
    	    return buffer.toByteArray();
    	} catch (IOException e) {
    	    throw new UploadFileException(urlObj.toExternalForm(), e);
    	}
    }

}
