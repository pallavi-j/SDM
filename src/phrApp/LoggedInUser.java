package phrApp;

public class LoggedInUser {
	
	private Integer userID;
	private byte[] privateKeyContent;
	
	public LoggedInUser(Integer userID, byte[] privateKeyContent) {
		this.setUserID(userID);
		this.setPrivateKeyContent(privateKeyContent);
	}

	public Integer getUserID() {
		return userID;
	}

	public void setUserID(Integer userID) {
		this.userID = userID;
	}

	public byte[] getPrivateKeyContent() {
		return privateKeyContent;
	}

	public void setPrivateKeyContent(byte[] privateKeyContent) {
		this.privateKeyContent = privateKeyContent;
	}

}
