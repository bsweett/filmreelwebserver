package com.class3601.social.actions;

import java.util.Set;

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
    private static String XML_NAME = "<name>";
    private static String XML_XNAME = "</name>\n";
    private static String XML_EMAIL = "<email>";
    private static String XML_XEMAIL = "</email>\n";
    private static String XML_LOCATION = "<location>";
    private static String XML_XLOCATION = "</location>\n";
    private static String XML_BIO = "<bio>";
    private static String XML_XBIO = "</bio>\n";
    private static String XML_IMAGE = "<image>";
    private static String XML_XIMAGE = "</image>\n";
    private static String XML_XUSER = "</user>\n";   
    private static String XML_FRIENDS = "<friends>";  
    private static String XML_XFRIENDS = "</friends>\n";  
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String username = getServletRequest().getParameter(PARAMETER_1);
		String  password = getServletRequest().getParameter(PARAMETER_2);
		password = password.replace(" ", "+");
		messageStore = new MessageStore();
				
		if(username.isEmpty() || password.isEmpty()) 
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

		User user = manager.getUserByNameAndPassword(username, password);
		
		if(user == null) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserNotFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XUSER);
			return "NoUserFound";
		}
		
		else 
		{
			user.incramentCount();
			user.setToken(manager.generateToken(user));
			
			Set<User> allFriends = user.getFriends();
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
			messageStore.appendToMessage(XML_TOKEN);
			messageStore.appendToMessage(user.getToken());
			messageStore.appendToMessage(XML_XTOKEN);
			messageStore.appendToMessage(XML_NAME);
			messageStore.appendToMessage(user.getName());
			messageStore.appendToMessage(XML_XNAME);
			messageStore.appendToMessage(XML_EMAIL);
			messageStore.appendToMessage(user.getEmailAddress());
			messageStore.appendToMessage(XML_XEMAIL);
			messageStore.appendToMessage(XML_LOCATION);
			messageStore.appendToMessage(user.getLocation());
			messageStore.appendToMessage(XML_XLOCATION);
			messageStore.appendToMessage(XML_BIO);
			messageStore.appendToMessage(user.getBio());
			messageStore.appendToMessage(XML_XBIO);
			messageStore.appendToMessage(XML_IMAGE);
			messageStore.appendToMessage(user.getImage());
			messageStore.appendToMessage(XML_XIMAGE);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_FRIENDS);
			for (User u : allFriends) {
				messageStore.appendToMessage(u.getEmailAddress());
				messageStore.appendToMessage("-");
				messageStore.appendToMessage(u.getName());
				messageStore.appendToMessage("-");
			}
			messageStore.appendToMessage(XML_XFRIENDS);
			messageStore.appendToMessage(XML_XUSER);
			
			manager.update(user);
			
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
