package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.codec.binary.Base64;
import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class SaveUserData extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "location";
    private static String PARAMETER_3 = "bio";
    private static String PARAMETER_4 = "gender";
    private static String PARAMETER_5 = "path";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<user>\n";
    private static String XML_XDATA = "</user>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		Base64 decoder = new Base64();
		String token = getServletRequest().getParameter(PARAMETER_1);
		token = token.replace(" ", "+");
		String location = getServletRequest().getParameter(PARAMETER_2);
		String bio = getServletRequest().getParameter(PARAMETER_3);
		String gender = getServletRequest().getParameter(PARAMETER_4);
		String path = getServletRequest().getParameter(PARAMETER_5);

		messageStore = new MessageStore();
				
		if(token.isEmpty()) 
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

		User user = manager.getUserByToken(token);
		if(!manager.isTokenValid(token)) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserNotFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "NoUserFound";
		}
		
		else 
		{
			user.setLocation(location);
			user.setBio(bio);
			user.setGender(gender.charAt(0));
			user.setDisplayPicturePath(path);
			manager.updateUser(user);
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
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
