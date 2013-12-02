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
    private static String XML_DATA = "<data>\n";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String name = getServletRequest().getParameter(PARAMETER_1);
		String  password = getServletRequest().getParameter(PARAMETER_2);
		String  emailAddress = getServletRequest().getParameter(PARAMETER_3);
		messageStore = new MessageStore();
		
		if(name.isEmpty() || password.isEmpty() || emailAddress.isEmpty()) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();

		User user = manager.getUserByEmailAddress(emailAddress);
		User user2 = manager.getUserByName(name);
		
		if(user == null && user2 == null)
		{
			User newUser = new User();
			newUser.setName(name);
			newUser.setPassword(password);
			newUser.setEmailAddress(emailAddress);
			manager.add(newUser);
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "success";
		} 
		else 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserAlreadyExists");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			
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
	
	private String decodeURL(String url) {
		url = url.replace("&quot;", "\"");
		url = url.replace("&apos;", "'");
		url = url.replace("&amp;", "&");
		url = url.replace("&lt;", "<");
		url = url.replace("&gt;", ">");
		
		return url;
	}
}
