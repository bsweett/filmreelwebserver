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
    private static String XML_1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_2 = "<user>\n";
    private static String XML_3 = "<token>";
    
    private static String XML_5 = "</token>\n";
    private static String XML_6 = "<femail>";
    
    private static String XML_8 = "</femail>\n";
    private static String XML_9 = "</user>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String parameter2 = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty() || parameter2.isEmpty()) 
		{
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_8);
			messageStore.appendToMessage(XML_9);
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User currentUser = manager.getUserByToken(parameter1);
		
		// Check the users token
		if(!manager.isTokenValid(parameter1))
		{
			System.out.println("TestUser is Null\n");
			messageStore.appendToMessage(XML_1);
			messageStore.appendToMessage(XML_2);
			messageStore.appendToMessage(XML_3);
			messageStore.appendToMessage("InvalidToken");
			messageStore.appendToMessage(XML_5);
			messageStore.appendToMessage(XML_6);
			messageStore.appendToMessage("InvalidToken");
			messageStore.appendToMessage(XML_8);
			messageStore.appendToMessage(XML_9);
			return "CurrentUserError";
		} 
		else 
		{
			User searchForFriend = manager.getUserByEmailAddress(parameter2);
			
			// Check if friend to add exists
			if(searchForFriend == null)
			{
				messageStore.appendToMessage(XML_1);
				messageStore.appendToMessage(XML_2);
				messageStore.appendToMessage(XML_3);
				messageStore.appendToMessage("NoUserFound");
				messageStore.appendToMessage(XML_5);
				messageStore.appendToMessage(XML_6);
				messageStore.appendToMessage("NoUserFound");
				messageStore.appendToMessage(XML_8);
				messageStore.appendToMessage(XML_9);
				return "NoUserFound";
			}
			
			// Check if you are already friends
			else 
			{
				Set<User> allFriends = currentUser.getFriends();
				if(allFriends.contains(searchForFriend))
				{
					messageStore.appendToMessage(XML_1);
					messageStore.appendToMessage(XML_2);
					messageStore.appendToMessage(XML_3);
					messageStore.appendToMessage("AlreadyFriends");
					messageStore.appendToMessage(XML_5);
					messageStore.appendToMessage(XML_6);
					messageStore.appendToMessage("AlreadyFriends");
					messageStore.appendToMessage(XML_8);
					messageStore.appendToMessage(XML_9);
					return "AlreadyFriends";
				}
				
				// If not friends add them
				// NOTE:: Should we implement it to be bidirectional friendships?
				// Send request - when they fetch their inbox grab their requests from database
						// probably need to create an inbox table to store their requests
				else 
				{
					currentUser.addFriend(searchForFriend);
					manager.update(currentUser);
					messageStore.appendToMessage(XML_1);
					messageStore.appendToMessage(XML_2);
					messageStore.appendToMessage(XML_3);
					messageStore.appendToMessage(currentUser.getName());
					messageStore.appendToMessage(XML_5);
					messageStore.appendToMessage(XML_6);
					messageStore.appendToMessage(searchForFriend.getName());
					messageStore.appendToMessage(XML_8);
					messageStore.appendToMessage(XML_9);
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
