package node.com.usernamechange.service.impl;

import static model.RabbitQueue.*;
import org.springframework.amqp.rabbit.core.RabbitTemplate;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;

import node.com.usernamechange.service.ProducerService;

@Service
public class ProducerServiceImpl implements ProducerService{
	private final RabbitTemplate rabbitTemplate;
	
	public ProducerServiceImpl(RabbitTemplate rabbitTemplate) {
		this.rabbitTemplate = rabbitTemplate;
	}

	@Override
	public void producerReply(SendMessage sendMessage) {
		rabbitTemplate.convertAndSend(REPLY_MESSAGE_UPDATE, sendMessage);
	}

}
