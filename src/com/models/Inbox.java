package com.models;



import com.common.Messages;

public class Inbox {
	private String inboxIdPrimarKey;
	private String receiverEmail;
	private String senderEmail;
	private String imageLocation;

	public Inbox() {
		setReceiverEmail(Messages.UNKNOWN);
		setSenderEmail(Messages.UNKNOWN);
		setImageLocation(Messages.UNKNOWN);
	}
	
	public String getInboxIdPrimarKey() {
		return inboxIdPrimarKey;
	}

	public void setInboxIdPrimarKey(String inboxIdPrimarKey) {
		this.inboxIdPrimarKey = inboxIdPrimarKey;
	}
	
	public String getReceiverEmail() {
		return receiverEmail;
	}
	
	public void setReceiverEmail(String email) {
		this.receiverEmail = email;
	}
	
	public String getSenderEmail() {
		return senderEmail;
	}
	
	public void setSenderEmail(String email) {
		this.senderEmail = email;
	}
	
	public String getImageLocation() {
		return imageLocation;
	}
	
	public void setImageLocation(String location) {
		this.imageLocation = location;
	}
	
	
}
