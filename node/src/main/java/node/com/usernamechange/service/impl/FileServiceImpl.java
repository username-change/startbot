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
import org.telegram.telegrambots.meta.api.objects.PhotoSize;

import lombok.extern.log4j.Log4j;
import node.com.usernamechange.dao.AppDocumentDAO;
import node.com.usernamechange.dao.AppPhotoDAO;
import node.com.usernamechange.dao.BinaryContentDAO;
import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.service.FileService;
import node.com.usernamechange.service.enums.LinkType;
import node.com.usernamechange.entity.BinaryContent;
import node.com.usernamechange.exceptions.UploadFileException;
import node.com.usernamechange.utils.CryptoTool;

@Log4j
@Service
public class FileServiceImpl implements FileService {
    @Value("${token}")
    private String token;
    @Value("${service.file_info.uri}")
    private String fileInfoUri;
    @Value("${service.file_storage.uri}")
    private String fileStorageUri;
    @Value("${link.address}")
    private String linkAdress;
    private final AppDocumentDAO appDocumentDAO;
    private final AppPhotoDAO appPhotoDAO;
    private final BinaryContentDAO binaryContentDAO;
    private final CryptoTool cryptoTool;

    public FileServiceImpl(AppDocumentDAO appDocumentDAO, BinaryContentDAO binaryContentDAO, AppPhotoDAO appPhotoDAO, CryptoTool cryptoTool) {
        this.appDocumentDAO = appDocumentDAO;
        this.binaryContentDAO = binaryContentDAO;
        this.appPhotoDAO = appPhotoDAO;
        this.cryptoTool = cryptoTool; 
    }

    @Override
    public AppDocument processDoc(Message telegramMessage) {
    	Document telegramDoc = telegramMessage.getDocument();
        String fileId = telegramDoc.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppDocument transientAppDoc = buildTransientAppDoc(telegramDoc, persistentBinaryContent);
            return appDocumentDAO.save(transientAppDoc);
        } else {
            throw new UploadFileException("bad response from telegram service: " + response);
        }
    }
    
    @Override
	public AppPhoto processPhoto(Message telegramMessage) {
    	var photoSizeCount = telegramMessage.getPhoto().size();
    	var photoIndex = photoSizeCount > 1 ? telegramMessage.getPhoto().size() -1 : 0;
        PhotoSize telegramPhoto = telegramMessage.getPhoto().get(photoIndex);
        String fileId = telegramPhoto.getFileId();
        ResponseEntity<String> response = getFilePath(fileId);
        if (response.getStatusCode() == HttpStatus.OK) {
            BinaryContent persistentBinaryContent = getPersistentBinaryContent(response);
            AppPhoto transientAppPhoto = buildTransientAppPhoto(telegramPhoto, persistentBinaryContent);
            return appPhotoDAO.save(transientAppPhoto);
        } else {
            throw new UploadFileException("Bad response from telegram service: " + response);
        }
	}

	private BinaryContent getPersistentBinaryContent(ResponseEntity<String> response) {
		String filePath = getFilePath(response);
		byte[] fileInByte = downloadFile(filePath);
		BinaryContent transientBinaryContent = BinaryContent.builder()
				.fileAsArrayOfBytes(fileInByte)
				.build();
		return binaryContentDAO.save(transientBinaryContent);
	}

	private String getFilePath(ResponseEntity<String> response) {
		JSONObject jsonObject = new JSONObject(response.getBody());
		return String.valueOf(jsonObject
				.getJSONObject("result")
				.getString("file_path"));
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
    
    private AppPhoto buildTransientAppPhoto(PhotoSize telegramPhoto, BinaryContent persistentBinaryContent) {
        return AppPhoto.builder()
                        .telegramFileld(telegramPhoto.getFileId())
                        .binaryContent(persistentBinaryContent)
                        .fileSize(telegramPhoto.getFileSize())
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

	@Override
	public String generationLink(Long docId, LinkType linkType) {
		var hash = cryptoTool.hashOf(docId);
		return "http://" + linkAdress + "/" + linkType + "?id=" + hash;
	}
    
    

}
