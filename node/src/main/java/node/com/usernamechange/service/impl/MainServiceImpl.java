package node.com.usernamechange.service.impl;

import org.springframework.stereotype.Service;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.api.objects.User;

import lombok.experimental.var;
import lombok.extern.log4j.Log4j;
import node.com.usernamechange.dao.AppUserDAO;
import node.com.usernamechange.dao.RawDataDAO;
import node.com.usernamechange.entity.AppDocument;
import node.com.usernamechange.entity.AppPhoto;
import node.com.usernamechange.entity.AppUser;
import node.com.usernamechange.entity.RawData;
import node.com.usernamechange.exceptions.UploadFileException;
import node.com.usernamechange.service.FileService;
import node.com.usernamechange.service.MainService;
import node.com.usernamechange.service.ProducerService;
import node.com.usernamechange.service.enums.ServiceCommands;

import static node.com.usernamechange.service.enums.ServiceCommands.*;
import static node.com.usernamechange.entity.enums.UserState.*;

@Service
@Log4j
public class MainServiceImpl implements MainService {
	private final RawDataDAO rawDataDAO;
	private final ProducerService producerService;
	private final AppUserDAO appUserDAO;
	private final FileService fileService;

	public MainServiceImpl(RawDataDAO rawDataDAO, ProducerService producerService, AppUserDAO appUserDAO,
			final FileService fileService) {
		this.rawDataDAO = rawDataDAO;
		this.producerService = producerService;
		this.appUserDAO = appUserDAO;
		this.fileService = fileService;
	}

	@Override
	public void processTextMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var userState = appUser.getState();
		var text = update.getMessage().getText();
		var output = "";

		var serviceCommand = ServiceCommands.fromValue(text);
		if (CANCEL.equals(text)) {
			output = cancelProcess(appUser);
		} else if (BASIC_STATE.equals(userState)) {
			output = processServiceCommand(appUser, text);
		} else if (WAIT_FOR_EMAIL_STATE.equals(userState)) {
			// TODO добавить обработку email
		} else {
			log.error("unknown user state: " + userState);
			output = "неизвестная ошибка. введите /cancel и попробуй снова!";
		}

		var chatId = update.getMessage().getChatId();
		sendAnswer(output, chatId);

	}

	@Override
	public void processDocMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var chatId = update.getMessage().getChatId();
		if (isNotAllowToSendContent(chatId, appUser)) {
			return;
		}

		try {
			AppDocument doc = fileService.processDoc(update.getMessage());
			// TODO добавить сохранение документа
			var answer = "документ успешно загружен! ссылка для скачивания: http://test.ru/get-doc/777\"";
			sendAnswer(answer, chatId);
		} catch (UploadFileException ex) {
			log.error(ex);
			String error = "загрузка файла не удалась. повторите попытку позже";
			sendAnswer(error, chatId);
		}
	}

	@Override
	public void processPhotoMessage(Update update) {
		saveRawData(update);
		var appUser = findOrSaveAppUser(update);
		var chatId = update.getMessage().getChatId();
		if (isNotAllowToSendContent(chatId, appUser)) {
			return;
		}

		try {
			AppPhoto photo = fileService.processPhoto(update.getMessage());
			// TODO добавить генерацию ссылки для скачивания фото
			var answer = "фото успешно загружено! ссылка для скачивания: http://test.ru/get-doc/777";
			sendAnswer(answer, chatId);
		} catch (UploadFileException ex) {
			log.error(ex);
			String error = "загрузка фото не удалась. повторите попытку позже";
			sendAnswer(error, chatId);
		}
	}

	private boolean isNotAllowToSendContent(Long chatId, AppUser appUser) {
		var userState = appUser.getState();
		if (!appUser.getIsActive()) {
			var error = "зарегестрируйтесь или активируйте свою учетную запись для загрузки контента";
			sendAnswer(error, chatId);
			return true;
		} else if (!BASIC_STATE.equals(userState)) {
			var error = "отмените текущую команду с помощью /cancel для отправки файлов";
			return true;
		}
		return false;
	}

	private void sendAnswer(String output, Long chatId) {
		SendMessage sendMessage = new SendMessage();
		sendMessage.setChatId(chatId);
		sendMessage.setText(output);
		producerService.producerReply(sendMessage);
	}

	private String processServiceCommand(AppUser appUser, String cmd) {
		if (REGISTRATION.equals(cmd)) {
			// TODO добавить регистрацию
			return "временно недоступно";
		} else if (HELP.equals(cmd)) {
			return help();
		} else if (START.equals(cmd)) {
			return "приветствую! чтобы посмтреть список доступных команд введите /help";
		} else {
			return "неизвестная команда! чтобы посмтреть список доступных команд введите /help";

		}

	}

	private String help() {
		return "список доступных команд:\n" + "/cancel - отмена выполнения текущей команды:\n"
				+ "/registration - регистрация пользователя.";
	}

	private String cancelProcess(AppUser appUser) {
		appUser.setState(BASIC_STATE);
		appUserDAO.save(appUser);
		return "команда отменена";
	}

	private AppUser findOrSaveAppUser(Update update) {
		User telegramUser = update.getMessage().getFrom();
		AppUser persistentAppUser = appUserDAO.findAppUserByTelegramUserId(telegramUser.getId());
		if (persistentAppUser == null) {
			AppUser transientAppUser = AppUser.builder().telegramUserId(telegramUser.getId())
					.username(telegramUser.getUserName()).firstName(telegramUser.getFirstName())
					.lastName(telegramUser.getLastName())
					// TODO изменить значения по умолчанию после добавления регистрации
					.isActive(true).state(BASIC_STATE).build();
			return appUserDAO.save(transientAppUser);
		}

		return persistentAppUser;
	}

	private void saveRawData(Update update) {
		RawData rawData = RawData.builder().event(update).build();
		rawDataDAO.save(rawData);
	}

}