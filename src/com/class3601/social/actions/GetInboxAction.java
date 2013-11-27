package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class GetInboxAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "type";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_XUSER = "</user>";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String parameter2 = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
				
		if(parameter1.isEmpty() && parameter2.isEmpty()) 
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
		
		User user = manager.getUserByToken(parameter1);
		
		if(user == null) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("InvalidUser");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "fail";
		}
		
		else 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Invalid");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "fail";
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
