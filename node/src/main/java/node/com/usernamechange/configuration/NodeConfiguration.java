package node.com.usernamechange.configuration;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import node.com.usernamechange.utils.CryptoTool;

@Configuration
public class NodeConfiguration {
	@Value("${salt}")
	private String salt;
	
	@Bean
	public CryptoTool getCryptoTool() {
		return new CryptoTool(salt);
	}
}
