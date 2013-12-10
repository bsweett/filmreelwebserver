package com.filmreel.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.filmreel.common.MessageStore;
import com.filmreel.models.Inbox;
import com.filmreel.models.User;
import com.filmreel.persistence.HibernateInboxManager;
import com.filmreel.persistence.HibernateUserManager;
import com.opensymphony.xwork2.ActionSupport;

public class SendReelAction extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "from";
    private static String PARAMETER_2 = "to";
    private static String PARAMETER_3 = "imagelocation";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";

	private MessageStore messageStore;
	private HttpServletRequest request;
	
	private HibernateUserManager manager;
	private HibernateInboxManager inboxManager;
	private User receiver;
	private User sender;
	
	public String execute() throws Exception 
	{
		messageStore = new MessageStore();
		
		try
		{
			String fromEmail = getServletRequest().getParameter(PARAMETER_1);
			String toEmail = getServletRequest().getParameter(PARAMETER_2);
			String imageLocation = getServletRequest().getParameter(PARAMETER_3);
			
			//Check to make sure that none of parameters are empty
			if(fromEmail.isEmpty() || toEmail.isEmpty() || imageLocation.isEmpty()) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: SendReel - Returned fail (fromEmail, toEmail or imageLocation parameter is empty)");
				return "fail";
			}
				
			manager = HibernateUserManager.getDefault();
			inboxManager = HibernateInboxManager.getDefault();
			
			receiver = manager.getUserByEmailAddress(toEmail);
			sender = manager.getUserByEmailAddress(fromEmail);
			
			//Check to make sure that the sender exists and the receiver exists
			if(receiver == null || sender == null)
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("UserNotFound");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
			
				System.out.println("ACTION :: SendReel - Sender or receiver could not be found.");
				return "CurrentUserError";
			} 	
			//If all is good create the reel inbox for the receiver and save it to the database
			else 
			{
				Inbox inbox = new Inbox();
				inbox.setSenderEmail(sender.getEmailAddress());
				inbox.setReceiverEmail(receiver.getEmailAddress());
				inbox.setImageLocation(imageLocation);
				inbox = inboxManager.encryptInbox(inbox);
				
				receiver.addInbox(inbox);
				manager.updateUser(receiver);
				
				sender.incrementReelCount();
				manager.updateUser(sender);
				
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Success");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
		
				System.out.println("ACTION :: SendReel - Reel was sent successfully");
				return "success";
			}
		}
		//Catches unexpected errors
		catch (Exception e)
		{
			e.printStackTrace();
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Error");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			
			System.out.println("ACTION :: SendReel - Fatal error occured.");
			return "error";
		}
	}	
		
	public MessageStore getMessageStore() 
	{
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) 
	{
		this.messageStore = messageStore;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) 
	{
		this.request = request;
	}
	
	private HttpServletRequest getServletRequest() 
	{
		return request;
	}
}
