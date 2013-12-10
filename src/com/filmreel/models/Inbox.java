package com.filmreel.models;

import com.filmreel.common.Messages;

/*
====================================================================================
|| This is our inbox model class. Currently an inbox holds information about a    ||
|| reel that has been sent to that user. The user can have one to many inbox's    ||
|| and each inbox contains a sender email from the person who sent the reel, a    ||
|| recipient email address and an image location where the recipient can download ||
|| the reel from. 																  ||	
==================================================================================== 
 */

public class Inbox 
{
	private String inboxIdPrimarKey;
	private String receiverEmail;
	private String senderEmail;
	private String imageLocation;
	private User user;

	public Inbox() 
	{
		setReceiverEmail(Messages.UNKNOWN);
		setSenderEmail(Messages.UNKNOWN);
		setImageLocation(Messages.UNKNOWN);
	}
	
	public String getInboxIdPrimarKey() 
	{
		return inboxIdPrimarKey;
	}

	public void setInboxIdPrimarKey(String inboxIdPrimarKey) 
	{
		this.inboxIdPrimarKey = inboxIdPrimarKey;
	}
	
	public String getReceiverEmail()
	{
		return receiverEmail;
	}
	
	public void setReceiverEmail(String email)
	{
		this.receiverEmail = email;
	}
	
	public String getSenderEmail() 
	{
		return senderEmail;
	}
	
	public void setSenderEmail(String email) 
	{
		this.senderEmail = email;
	}
	
	public String getImageLocation()
	{
		return imageLocation;
	}
	
	public void setImageLocation(String location) 
	{
		this.imageLocation = location;
	}
	
	public User getUser()
	{
		return user;
	}
	
	public void setUser(User user)
	{
		this.user = user;
	}
}
