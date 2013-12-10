package com.filmreel.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.filmreel.common.MessageStore;
import com.filmreel.models.Inbox;
import com.filmreel.models.User;
import com.filmreel.persistence.HibernateInboxManager;
import com.filmreel.persistence.HibernateUserManager;
import com.opensymphony.xwork2.ActionSupport;

public class GetInboxAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_NAME = "<name>";
    private static String XML_XNAME = "</name>\n";
    private static String XML_REEL = "<reel>";
    private static String XML_XREEL = "</reel>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	private HibernateUserManager manager;
	private HibernateInboxManager inboxManager;
	private User user;
	Set<Inbox> inbox; 
	
	public String execute() throws Exception 
	{
		messageStore = new MessageStore();
		
		try
		{
			String token = getServletRequest().getParameter(PARAMETER_1);
			token = token.replace(" ", "+");
			
			//Check to make sure the token parameter is not empty
			if(token.isEmpty()) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: GetInbox - Returned fail (the token parameter was empty)");
				return "fail"; 
			}
			
			manager = HibernateUserManager.getDefault();
			inboxManager = HibernateInboxManager.getDefault();
	
			user = manager.getUserByToken(token);
			
			//Check to make sure the token is valid and is not expired
			if(!manager.isTokenValid(token)) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("InvalidToken");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: GetInbox - Token is no longer valid");
				return "userError";
			}
			//If all is good return all the users inbox's
			//in an inbox string that is separated with 
			//separators parsed out on the server
			else 
			{
				inbox = user.getInbox();
					
				if(inbox.isEmpty()) 
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("NoNewMail");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
					return "fail";		
				}
				else 
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("Success");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_REEL);
					for (Inbox i : inbox) 
					{
						messageStore.appendToMessage(manager.decrypt(i.getSenderEmail()));
						messageStore.appendToMessage("-");
						messageStore.appendToMessage(manager.decrypt(i.getImageLocation()));
						messageStore.appendToMessage("-");
						inboxManager.delete(i);
					}
					messageStore.appendToMessage(XML_XREEL);
					messageStore.appendToMessage(XML_XDATA);
					return "success";
				}	
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
			
			System.out.println("ACTION :: GetInbox - Fatal error occured.");
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
