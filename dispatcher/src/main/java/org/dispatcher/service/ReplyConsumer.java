package org.dispatcher.service;

import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

public interface ReplyConsumer {
	void consume(SendMessage sendMessage);
}
