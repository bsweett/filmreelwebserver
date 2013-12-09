package com.class3601.social.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class AddFriendAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "femail";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_USER = "<user>\n";
    private static String XML_EMAIL = "<email>";
    private static String XML_DATA = "<data>\n";
    private static String XML_XDATA = "</data>\n";
    private static String XML_XEMAIL = "</email>\n";
    private static String XML_NAME = "<name>";
    
    private static String XML_XNAME = "</name>\n";
    private static String XML_XUSER = "</user>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE= "</message>\n";
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		parameter1 = parameter1.replace(" ", "+");
		String parameter2 = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty() || parameter2.isEmpty()) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			System.out.println("Fail Returned");
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User currentUser = manager.getUserByToken(parameter1);
		
		// Check the users token
		if(!manager.isTokenValid(parameter1))
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("InvalidToken");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
		
			return "CurrentUserError";
		} 
		else 
		{
			User searchForFriend = manager.getUserByEmailAddress(parameter2);
			System.out.println("The current user email is: " + currentUser.getEmailAddress());
			System.out.println("The friend emal address is: " + parameter2);
			// Check if friend to add exists
			if(searchForFriend == null)
			{
				System.out.println("User does not exist");
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("UserNotFound");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
			
				return "fail";
			}
			else if(currentUser.getEmailAddress().equals(parameter2))
			{
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("AlreadyFriends");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_XDATA);
			
				return "AlreadyFriends";
			}
			// Check if you are already friends
			else 
			{
				Set<User> allFriends = currentUser.getFriends();
				
				if(allFriends.contains(searchForFriend))
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("AlreadyFriends");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
				
					return "AlreadyFriends";
				}
				
				// If not friends add them
				// NOTE:: Should we implement it to be bidirectional friendships?
				// Send request - when they fetch their inbox grab their requests from database
						// probably need to create an inbox table to store their requests
				else 
				{
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_USER);
					messageStore.appendToMessage(XML_EMAIL);
					messageStore.appendToMessage(searchForFriend.getEmailAddress());
					messageStore.appendToMessage(XML_XEMAIL);
					messageStore.appendToMessage(XML_NAME);
					messageStore.appendToMessage(searchForFriend.getName());
					messageStore.appendToMessage(XML_XNAME);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("Success");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XUSER);
					
					currentUser.addFriend(searchForFriend);
					searchForFriend.addFriend(currentUser);
					manager.updateUser(currentUser);
					manager.updateUser(searchForFriend);
	
					return "success";
				}
			}
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
