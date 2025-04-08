package node.com.usernamechange.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;

import node.com.usernamechange.dao.RawDataDAO;
import node.com.usernamechange.entity.RawData;
import node.com.usernamechange.service.MainService;
import node.com.usernamechange.service.ProducerService;

@Service
public class MainServiceImpl implements MainService {
	private final RawDataDAO rawDataDAO;
	private final ProducerService producerService;

	public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService) {
		this.rawDataDAO = rawDataDAO;
		this.producerService = producerService;
	}

	@Override
	public void processTextMessage(Update update) {
		saveRawData(update);

		var message = update.getMessage();
		var sendMessage = new SendMessage();
		sendMessage.setChatId(message.getChatId().toString());
		sendMessage.setText("hello from node");
		producerService.producerReply(sendMessage);
	}

	private void saveRawData(Update update) {
		RawData rawData = RawData.builder().event(update).build();
		rawDataDAO.save(rawData);
	}

}
