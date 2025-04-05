package node.com.usernamechange.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ProducerService {
	void producerReply(SendMessage sendMessage);
}
