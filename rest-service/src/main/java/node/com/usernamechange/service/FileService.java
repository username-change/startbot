package node.com.usernamechange.service;

import org.springframework.core.io.FileSystemResource;

import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.entity.BinaryContent;

public interface FileService {
	AppDocument getDocument(String id);
	AppPhoto getPhoto(String id);
	FileSystemResource getFileSystemResource(BinaryContent binaryContent);
}
