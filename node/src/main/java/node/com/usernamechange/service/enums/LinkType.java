package node.com.usernamechange.service.enums;

public enum LinkType {
	GET_DOC("file/get-doc"),
	GET_PHOTO("file/get-photo");
	private final String link;

	private LinkType(String link) {
		this.link = link;
	}
	
	@Override
	public String toString() {
		return link;
	}
}
