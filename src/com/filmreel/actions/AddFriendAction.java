package com.filmreel.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.filmreel.common.MessageStore;
import com.filmreel.models.User;
import com.filmreel.persistence.HibernateUserManager;
import com.opensymphony.xwork2.ActionSupport;

public class AddFriendAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "femail";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_XUSER = "</user>\n";
    private static String XML_DATA = "<data>\n";
    private static String XML_XDATA = "</data>\n";
    private static String XML_EMAIL = "<email>";
    private static String XML_XEMAIL = "</email>\n";
    private static String XML_NAME = "<name>";
    private static String XML_XNAME = "</name>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE= "</message>\n";

	private MessageStore messageStore;
	private HttpServletRequest request;
	private HibernateUserManager manager;
	
	private User user;
	private User friend;
	private Set<User> allFriends;
	
	public String execute() throws Exception 
	{
		messageStore = new MessageStore();
		
		try 
		{
			String token = getServletRequest().getParameter(PARAMETER_1);
			token = token.replace(" ", "+");
		
			String friendEmail = getServletRequest().getParameter(PARAMETER_2);
		
			manager = HibernateUserManager.getDefault();
			
			//Check to make sure that the parameters came through
			if(token.isEmpty() || friendEmail.isEmpty()) 
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: AddFriend - Fail Returned (token or friendEmail parameter is empty)");
				return "fail";
			}
			
			user = manager.getUserByToken(token);
			
			//Check if the token has not expired and is valid
			if(!manager.isTokenValid(token))
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("InvalidToken");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
				
				System.out.println("ACTION :: AddFriend - Token is no longer valid.");
				return "userError";
			} 
			else 
			{
				friend = manager.getUserByEmailAddress(friendEmail);
				
				//Check to make sure that the friend exists
				if(friend == null)
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("UserNotFound");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
					
					System.out.println("ACTION :: AddFriend - User trying to be added was not found.");
					return "fail";
				}
				//Check to make sure that the user is not adding them self
				if(user.getEmailAddress().equals(friendEmail))
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("AlreadyFriends");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
					
					System.out.println("ACTION :: AddFriend - User trying to add themself.");
					return "AlreadyFriends";
				}
				
				allFriends = user.getFriends();
				
				//Check to make sure that the user is not already friends with the person
				if(allFriends.contains(friend))
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("AlreadyFriends");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
						
					System.out.println("ACTION :: AddFriend - Users are already friends.");
					return "AlreadyFriends";
				}
				
				//If all is good add the user to the friend list
				else 
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_USER);
					messageStore.appendToMessage(XML_EMAIL);
					messageStore.appendToMessage(friend.getEmailAddress());
					messageStore.appendToMessage(XML_XEMAIL);
					messageStore.appendToMessage(XML_NAME);
					messageStore.appendToMessage(friend.getName());
					messageStore.appendToMessage(XML_XNAME);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("Success");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XUSER);
						
					user.addFriend(friend);
					friend.addFriend(user);
					
					manager.updateUser(user);
					manager.updateUser(friend);
						
					System.out.println("ACTION :: AddFriend - Friend was added succesfully.");
					return "success";
				}
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
			
			System.out.println("ACTION :: AddFriend - Fatal error occured.");
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
