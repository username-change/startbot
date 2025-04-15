package node.com.usernamechange.utils;

import org.hashids.Hashids;

public class CryptoTool {
	private final Hashids hashids;

	public CryptoTool(String salt) {
		var minHashLenght = 10;
		this.hashids = new Hashids(salt, minHashLenght);
	}

	public String hashOf(Long value) {
		return hashids.encode(value);
	}

	public Long idOf(String value) {
		long[] res = hashids.decode(value);
		if (res != null && res.length > 0) {
			return res[0];
		}
		return null;
	}

}
