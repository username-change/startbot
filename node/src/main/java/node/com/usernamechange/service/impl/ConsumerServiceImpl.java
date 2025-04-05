package node.com.usernamechange.service.impl;

import org.springframework.amqp.rabbit.annotation.RabbitListener;
import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import lombok.extern.log4j.Log4j;
import node.com.usernamechange.service.ConsumerService;
import node.com.usernamechange.service.ProducerService;

import static model.RabbitQueue.*;

@Service
@Log4j
public class ConsumerServiceImpl implements ConsumerService{
	private final ProducerService producerService;
	
	public ConsumerServiceImpl(ProducerService producerService) {
		this.producerService = producerService;
	}

	@Override
	@RabbitListener(queues = TEXT_MESSAGE_UPDATE)
	public void consumeTextMessageUpdate(Update update) {
		log.debug("node: text message is received");
		
		var message = update.getMessage();
		var sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("hello from node");
		producerService.producerReply(sendMessage);
	}

	@Override
	@RabbitListener(queues = DOC_MESSAGE_UPDATE)
	public void consumeDocMessageUpdate(Update update) {
		log.debug("node: doc message is received");
	}

	@Override
	@RabbitListener(queues =  PHOTO_MESSAGE_UPDATE)
	public void consumePhotoMessageUpdate(Update update) {
		log.debug("node: photo message is received");
	}
	
}
