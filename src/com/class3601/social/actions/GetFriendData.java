package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class GetFriendData extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "email";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_XUSER = "</user>\n";
    private static String XML_DATA = "<data>\n";
    private static String XML_XDATA = "</data>\n";
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
    private static String XML_GENDER = "<gender>";
    private static String XML_XGENDER = "</gender>\n";
    private static String XML_POP = "<pop>";
    private static String XML_XPOP = "</pop>\n";
    private static String XML_REELCOUNT = "<reelcount>";
    private static String XML_XREELCOUNT = "</reelcount>\n";
    
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty()) 
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
		System.out.println(parameter1);
		User user = manager.getUserByEmailAddress(parameter1);
		
		if(user == null)
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserNotFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			
			return "fail";
		} 
		else 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_USER);
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
			messageStore.appendToMessage(user.getDisplayPicturePath());
			messageStore.appendToMessage(XML_XIMAGE);
			messageStore.appendToMessage(XML_GENDER);
			messageStore.appendToMessage(Character.toString(user.getGender()));
			messageStore.appendToMessage(XML_XGENDER);
			messageStore.appendToMessage(XML_POP);
			messageStore.appendToMessage(Integer.toString(user.getPopularity()));
			messageStore.appendToMessage(XML_XPOP);
			messageStore.appendToMessage(XML_REELCOUNT);
			messageStore.appendToMessage(Integer.toString(user.getReelCount()));
			messageStore.appendToMessage(XML_XREELCOUNT);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Success");
			messageStore.appendToMessage(XML_XMESSAGE);
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
