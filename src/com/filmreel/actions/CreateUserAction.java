package com.filmreel.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.filmreel.common.MessageStore;
import com.filmreel.models.User;
import com.filmreel.persistence.HibernateUserManager;
import com.opensymphony.xwork2.ActionSupport;

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
	private HibernateUserManager manager;
	
	private User user1;
	private User user2;
	
	public String execute() throws Exception 
	{
		messageStore = new MessageStore();
		
		try {
			String name = getServletRequest().getParameter(PARAMETER_1);
			String  password = getServletRequest().getParameter(PARAMETER_2);
			String  emailAddress = getServletRequest().getParameter(PARAMETER_3);
			
			manager = HibernateUserManager.getDefault();
			
			//Check to make sure none of the parameters are empty
			if(name.isEmpty() || password.isEmpty() || emailAddress.isEmpty()) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: CreateUser - Returns fail (name, password or email parameter is empty)");
				return "fail";
			}
		
			user1 = manager.getUserByEmailAddress(emailAddress);
			user2 = manager.getUserByName(name);
			
			//Check to make sure that the user name or email is not already taken
			if(user1 != null || user2 != null)
			{		
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("UserAlreadyExists");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: CreateUser - The email and/or username is already taken.");
				return "UserAlreadyExists";
			} 
			//If all is good create the users account
			else 
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
				
				System.out.println("ACTION :: CreateUser - User was created successfully.");
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
			
			System.out.println("ACTION :: CreateUser - Fatal error occured.");
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
