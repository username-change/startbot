package node.com.usernamechange.services;

import node.com.usernamechange.dto.MailParams;

public interface MailSenderService {
	void send(MailParams mailParams);
}
