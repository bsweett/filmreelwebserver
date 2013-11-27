package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class SaveUserData extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "name";
    private static String PARAMETER_3 = "image";
    private static String PARAMETER_4 = "location";
    private static String PARAMETER_5 = "bio";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<user>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_XDATA = "</user>\n";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String token = getServletRequest().getParameter(PARAMETER_1);
		String name = getServletRequest().getParameter(PARAMETER_2);
		String image = getServletRequest().getParameter(PARAMETER_3);
		String location = getServletRequest().getParameter(PARAMETER_4);
		String bio = getServletRequest().getParameter(PARAMETER_5);
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
		if(user == null) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("NoUserFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "NoUserFound";
		}
		
		else 
		{
			user.setName(name);
			user.setLocation(location);
			user.setBio(bio);
			user.setImage(image.getBytes());
			manager.update(user);
			
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
