package node.com.usernamechange.services.impl;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Service;

import node.com.usernamechange.dto.MailParams;
import node.com.usernamechange.services.MailSenderService;

@Service
public class MailSenderServiceImpl implements MailSenderService {
	private final JavaMailSender javaMailSender;
	@Value("${spring.mail.username}")
	private String emailFrom;
	@Value("${service.activation.uri")
	private String activationServiceUri;

	public MailSenderServiceImpl(JavaMailSender javaMailSender) {
		this.javaMailSender = javaMailSender;
	}

	@Override
	public void send(MailParams mailParams) {
		var subject = "активация учетной записи";
		var messageBody = getActivationMailBody(mailParams.getId());
		var emailTo = mailParams.getEmailTo();

		SimpleMailMessage mailMessage = new SimpleMailMessage();
		mailMessage.setFrom(emailFrom);
		mailMessage.setTo(emailTo);
		mailMessage.setSubject(subject);
		mailMessage.setText(messageBody);

		javaMailSender.send(mailMessage);
	}

	private String getActivationMailBody(String id) {
		var msg = String.format("для завершения активации перейти по ссылке:\n%s",
				activationServiceUri);
		return msg.replace("{id}", id);
	}

}
