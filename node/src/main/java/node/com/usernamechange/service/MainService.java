package node.com.usernamechange.service;

import org.telegram.telegrambots.meta.api.objects.Update;

public interface MainService {
	void processTextMessage(Update update);
}
