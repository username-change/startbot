package node.com.usernamechange.service;


import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;

public interface FileService {
	AppDocument getDocument(String id);
	AppPhoto getPhoto(String id);
}
