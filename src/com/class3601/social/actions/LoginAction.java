package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;
import com.models.User;

public class LoginAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "id";
    private static String PARAMETER_2 = "password";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_TOKEN = "<token>";
    private static String XML_XTOKEN = "</token>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_XUSER = "</user>\n";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		parameter2 = parameter2.replace(" ", "+");
		messageStore = new MessageStore();
				
		if(parameter1.isEmpty() || parameter2.isEmpty()) 
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

		User testuser = manager.getUserByNameAndPassword(parameter1, parameter2);
		if(testuser == null) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("NoUserFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "NoUserFound";
		}
		
		else 
		{
			System.out.println("Hello world");
			testuser.incramentCount();
			testuser.setToken(manager.generateToken(testuser));
			manager.update(testuser);
			
			System.out.println("The token is:" + testuser.getToken());
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_TOKEN);
			messageStore.appendToMessage(testuser.getToken());
			messageStore.appendToMessage(XML_XTOKEN);
			messageStore.appendToMessage(XML_XUSER);
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
