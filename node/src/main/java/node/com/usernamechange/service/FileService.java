package node.com.usernamechange.service;

import org.telegram.telegrambots.meta.api.objects.Message;

import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.service.enums.LinkType;

public interface FileService {
	AppDocument processDoc(Message telegramMessage);

	AppPhoto processPhoto(Message telegramMessage);
	
	String generationLink(Long docId, LinkType linkType);
}
