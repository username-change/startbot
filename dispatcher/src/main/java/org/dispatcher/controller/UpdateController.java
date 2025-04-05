package org.dispatcher.controller;

import static model.RabbitQueue.*;
import org.dispatcher.service.UpdateProducer;
import org.dispatcher.utils.MessageUtils;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.extern.log4j.Log4j;

@Component
@Log4j
public class UpdateController {
	private TelegramBot telegramBot;
	private final MessageUtils messageUtils;
	private final UpdateProducer updateProducer;

	public UpdateController(MessageUtils messageUtils, UpdateProducer updateProducer) {
		this.messageUtils = messageUtils;
		this.updateProducer = updateProducer;
	}

	public void registerBot(TelegramBot telegramBot) {
		this.telegramBot = telegramBot;
	}

	public void processUpdate(Update update) {
		if (update == null) {
			log.error("received update is null");
			return;
		}

		if (update.getMessage() != null) {
			distributeMessageByType(update);
		} else {
			log.error("received unsupported message type" + update);
		}
	}

	private void distributeMessageByType(Update update) {
		var message = update.getMessage();
		if (message.getText() != null) {
			processTextMessage(update);
		} else if (message.getDocument() != null) {
			processDocMessage(update);
		} else if (message.getPhoto() != null) {
			processPhotoMessage(update);
		} else {
			setUnsupportedMessageTypeView(update);
		}
	}

	private void setUnsupportedMessageTypeView(Update update) {
		var sendMessage = messageUtils.generateSendMessageWithText(update,
				"неподдерживаемый тип сообщений");
		setView(sendMessage);

	}
	
	private void setFileIsReceiveVeiew(Update update) {
		var sendMessage = messageUtils.generateSendMessageWithText(update,
				"файл получен! обрабатывается...");
		setView(sendMessage);
	}

	public void setView(SendMessage sendMessage) {
		telegramBot.sendAnswerMessage(sendMessage);
	}

	private void processPhotoMessage(Update update) {
		updateProducer.produce(PHOTO_MESSAGE_UPDATE, update);
		setFileIsReceiveVeiew(update);
	}

	private void processDocMessage(Update update) {
		updateProducer.produce(DOC_MESSAGE_UPDATE, update);
		setFileIsReceiveVeiew(update);

	}

	private void processTextMessage(Update update) {
		updateProducer.produce(TEXT_MESSAGE_UPDATE, update);
		setFileIsReceiveVeiew(update);

	}
}
