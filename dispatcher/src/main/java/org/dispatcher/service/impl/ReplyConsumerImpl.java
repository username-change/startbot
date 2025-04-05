package org.dispatcher.service.impl;

import static model.RabbitQueue.*;

import org.dispatcher.controller.UpdateController;
import org.dispatcher.service.ReplyConsumer;
import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

@Service
public class ReplyConsumerImpl implements ReplyConsumer {
	private final UpdateController updateController;
	
	public ReplyConsumerImpl(UpdateController updateController) {
		this.updateController = updateController;
	}

	@Override
	@RabbitListener(queues = REPLY_MESSAGE_UPDATE)
	public void consume(SendMessage sendMessage) {
		updateController.setView(sendMessage);
	}
	
}
 