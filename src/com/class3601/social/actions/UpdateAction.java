package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;
import com.models.User;

public class UpdateAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "name";
    private static String PARAMETER_3 = "location";
    private static String PARAMETER_4 = "bio";
    private static String XML_1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_2 = "<user>\n";
    private static String XML_3 = "<token>";
    
    private static String XML_5 = "</token>\n";
    private static String XML_6 = "<name>";
    
    private static String XML_8 = "</name>\n";
    private static String XML_9 = "<location>";
    
    private static String XML_11 = "</location>\n";
    private static String XML_12 = "<bio>";
    
    private static String XML_14 = "<bio>\n";
    private static String XML_15 = "</user>\n";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		String  parameter3 = getServletRequest().getParameter(PARAMETER_3);
		String  parameter4 = getServletRequest().getParameter(PARAMETER_4);
		messageStore = new MessageStore();
				
		// The request should be made with at least one of these having been changed
		// Only the name and token shouldn't be empty
		if(parameter1.isEmpty() || parameter2.isEmpty()) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_8);
			messageStore.appendToMessage(XML_9);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_11);
			messageStore.appendToMessage(XML_12);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_14);
			messageStore.appendToMessage(XML_15);
			return "fail";
		}
		
		// Get user by token because its the only attribute that is unchangeable 
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User testuser = manager.getUserByToken(parameter1);
		
		// If token not found user must not be logged in or exist (This should never happen)
		if(testuser == null) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("TokenFailed");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			messageStore.appendToMessage("TokenFailed");
			messageStore.appendToMessage(XML_8);
			messageStore.appendToMessage(XML_9);
			messageStore.appendToMessage("TokenFailed");
			messageStore.appendToMessage(XML_11);
			messageStore.appendToMessage(XML_12);
			messageStore.appendToMessage("TokenFailed");
			messageStore.appendToMessage(XML_14);
			messageStore.appendToMessage(XML_15);
			return "TokenFailed";
		}
		
		// Otherwise update the values 
		else 
		{	
			testuser.setName(manager.encrypt(parameter2));
			testuser.setLocation(manager.encrypt(parameter3));
			testuser.setBio(manager.encrypt(parameter4));
			manager.update(testuser);
			
			// For now send all the stuff back to the client
			// We could just send a valid token or message
			// The fetch action should be implemented to run on every profile access or login
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage(testuser.getToken());
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			messageStore.appendToMessage(testuser.getName());
			messageStore.appendToMessage(XML_8);
			messageStore.appendToMessage(XML_9);
			messageStore.appendToMessage(testuser.getLocation());
			messageStore.appendToMessage(XML_11);
			messageStore.appendToMessage(XML_12);
			messageStore.appendToMessage(testuser.getBio());
			messageStore.appendToMessage(XML_14);
			messageStore.appendToMessage(XML_15);
			return "success";
		}
	}	
		
	public MessageStore getMessageStore() {
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) {
		this.messageStore = messageStore;
	}


	@Override
	public void setServletRequest(HttpServletRequest request) {
		this.request = request;
	}
	
	private HttpServletRequest getServletRequest() {
		return request;
	}

}
