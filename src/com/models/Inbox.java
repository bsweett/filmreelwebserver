package com.models;



import com.common.Messages;

public class Inbox {
	private String inboxIdPrimarKey;
	private String email;
	private String type;
	private byte[] snap;
	private String sender;
	private String requestStatus;

	public Inbox() {
		setEmail(Messages.UNKNOWN);
		setType(Messages.UNKNOWN);
		setSnap(new byte[0]);
		setSender(Messages.UNKNOWN);
		setRequestStatus(Messages.UNKNOWN);
	}
	
	public String getInboxIdPrimarKey() {
		return inboxIdPrimarKey;
	}

	public void setInboxIdPrimarKey(String inboxIdPrimarKey) {
		this.inboxIdPrimarKey = inboxIdPrimarKey;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getType() {
		return type;
	}
	
	public void setType(String type) {
		this.type = type;
	}
	
	public byte[] getSnap() {
		return snap;
	}
	
	public void setSnap(byte[] snap) {
		this.snap = snap;
	}
	
	public String getSender() {
		return sender;
	}
	
	public void setSender(String sender) {
		this.sender = sender;
	}
	
	public String getRequestStatus() {
		return requestStatus;
	}
	
	public void setRequestStatus(String requestStatus) {
		this.requestStatus = requestStatus;
	}
	
	
}
