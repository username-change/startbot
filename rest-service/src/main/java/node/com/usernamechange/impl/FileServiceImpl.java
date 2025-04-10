package node.com.usernamechange.impl;

import java.io.File;
import java.io.IOException;

import org.apache.commons.io.FileUtils;
import org.springframework.core.io.FileSystemResource;
import org.springframework.stereotype.Service;

import lombok.extern.log4j.Log4j;
import node.com.usernamechange.dao.AppDocumentDAO;
import node.com.usernamechange.dao.AppPhotoDAO;
import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.entity.BinaryContent;
import node.com.usernamechange.service.FileService;

@Log4j
@Service
public class FileServiceImpl implements FileService {
	private final AppDocumentDAO appDocumentDAO;
	private final AppPhotoDAO appPhotoDAO;

	public FileServiceImpl(AppDocumentDAO appDocumentDAO, AppPhotoDAO appPhotoDAO) {
		this.appDocumentDAO = appDocumentDAO;
		this.appPhotoDAO = appPhotoDAO;
	}

	@Override
	public AppDocument getDocument(String docId) {
		var id = Long.parseLong(docId);
		return appDocumentDAO.findById(id).orElse(null);
	}

	@Override
	public AppPhoto getPhoto(String photoId) {
		var id = Long.parseLong(photoId);
		return appPhotoDAO.findById(id).orElse(null);
	}

	@Override
	public FileSystemResource getFileSystemResource(BinaryContent binaryContent) {
		try {
			File temp = File.createTempFile("tempFile", ".bin");
			temp.deleteOnExit();
			FileUtils.writeByteArrayToFile(temp, binaryContent.getFileAsArrayOfBytes());
			return new FileSystemResource(temp);
		} catch (IOException e) {
			log.error(e);
			return null;
		}
	}

}
