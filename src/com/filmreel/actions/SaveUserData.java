package com.filmreel.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.filmreel.common.MessageStore;
import com.filmreel.models.User;
import com.filmreel.persistence.HibernateUserManager;
import com.opensymphony.xwork2.ActionSupport;

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
	
	private HibernateUserManager manager;
	private User user;
	
	public String execute() throws Exception 
	{
		messageStore = new MessageStore();
		
		try {
			String token = getServletRequest().getParameter(PARAMETER_1);
			token = token.replace(" ", "+");
			String location = getServletRequest().getParameter(PARAMETER_2);
			String bio = getServletRequest().getParameter(PARAMETER_3);
			String gender = getServletRequest().getParameter(PARAMETER_4);
			String path = getServletRequest().getParameter(PARAMETER_5);
				
			//Check to make sure the token parameter is not empty
			if(token.isEmpty()) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: SaveUserData - Returned fail (token parameter is empty)");
				return "fail"; 
			}
			
			manager = HibernateUserManager.getDefault();
	
			user = manager.getUserByToken(token);
			
			//Check to make sure the token is valid and is not expired
			if(!manager.isTokenValid(token)) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("UserNotFound");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: SaveUserData - User could not be found");
				return "NoUserFound";
			}
			
			//If all is good save the users data to the database
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
				
				System.out.println("ACTION :: SaveUserData - User data was saved successfully");
				return "success";
			}
		}
		//Catches unexpected errors
		catch (Exception e)
		{
			e.printStackTrace();
			
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Error");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			
			System.out.println("ACTION :: SaveUserData - Fatal error occured.");
			return "error";
		}
	}	
		
	public MessageStore getMessageStore() 
	{
		return messageStore;
	}

	public void setMessageStore(MessageStore messageStore) 
	{
		this.messageStore = messageStore;
	}

	@Override
	public void setServletRequest(HttpServletRequest request) 
	{
		this.request = request;
	}
	
	private HttpServletRequest getServletRequest() 
	{
		return request;
	}

}