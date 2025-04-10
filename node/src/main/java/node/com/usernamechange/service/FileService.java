package node.com.usernamechange.service;

import org.telegram.telegrambots.meta.api.objects.Message;

import node.com.usernamechange.entity.AppDocument;

public interface FileService {
	AppDocument processDoc(Message externalMessage);
}
