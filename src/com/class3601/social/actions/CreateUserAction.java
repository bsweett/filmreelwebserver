package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class CreateUserAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "name";
    private static String PARAMETER_2 = "password";
    private static String PARAMETER_3 = "email";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_TOKEN = "<token>";
    private static String XML_XTOKEN = "</token>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_NAME = "<name>";
    private static String XML_XNAME = "</name>\n";
    private static String XML_EMAIL = "<email>";
    private static String XML_XEMAIL = "</email>\n";
    private static String XML_XUSER = "</user>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		String  parameter3 = getServletRequest().getParameter(PARAMETER_3);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty() || parameter2.isEmpty() || parameter3.isEmpty()) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User testuser = manager.getUserByEmailAddressAndName(parameter3, parameter1);
		if(testuser == null)
		{
			System.out.println("TestUser is Null\n");
			User newUser = new User();
			newUser.setName(parameter1);
			newUser.setPassword(parameter2);
			newUser.setEmailAddress(parameter3);
			manager.add(newUser);
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_TOKEN);
			messageStore.appendToMessage(newUser.getToken());
			messageStore.appendToMessage(XML_XTOKEN);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_NAME);
			messageStore.appendToMessage(parameter1);
			messageStore.appendToMessage(XML_XNAME);
			messageStore.appendToMessage(XML_EMAIL);
			messageStore.appendToMessage(parameter3);
			messageStore.appendToMessage(XML_XEMAIL);
			messageStore.appendToMessage(XML_XUSER);
			return "success";
		} 
		else 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserAlreadyExists");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "UserAlreadyExists";
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
