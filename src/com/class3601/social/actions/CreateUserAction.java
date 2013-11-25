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
    private static String XML_1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_2 = "<user>\n";
    private static String XML_3 = "<token>";
    private static String XML_5 = "</token>\n";
    private static String XML_6 = "</user>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		String  parameter3 = getServletRequest().getParameter(PARAMETER_3);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty() || parameter2.isEmpty() || parameter3.isEmpty()) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User testuser = manager.getUserByEmailAddressAndPassword(parameter3, parameter2);
		if(testuser == null)
		{
			System.out.println("TestUser is Null\n");
			User newUser = new User();
			newUser.setName(parameter1);
			newUser.setPassword(parameter2);
			newUser.setEmailAddress(parameter3);
			manager.add(newUser);
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage(newUser.getToken());
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			return "success";
		} 
		else 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("UserAlreadyExists");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
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
