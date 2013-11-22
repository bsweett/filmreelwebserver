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
    private static String XML_1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_2 = "<user>\n";
    private static String XML_3 = "<token>";
    private static String XML_5 = "</token>\n";
    private static String XML_6 = "</user>\n";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
				
		if(parameter1.isEmpty() || parameter2.isEmpty()) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			return "fail";
		}
		
		User getUser = new User();
		getUser.setName(parameter1);
		getUser.setPassword(parameter2);
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		manager.encryptUser(getUser);
		
		
		User testuser = manager.getUserByNameAndPassword(getUser.getName(), getUser.getPassword());
		if(testuser == null) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("NoUserFound");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			return "NoUserFound";
		}
		
		else 
		{
			testuser.incramentCount();
			testuser.generateToken();
			testuser.setToken(manager.encrypt(testuser.getToken()));
			testuser.setPassword(manager.encrypt(testuser.getPassword()));
			
			manager.update(testuser);
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage(testuser.getToken());
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
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
