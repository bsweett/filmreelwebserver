package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;

public class SendSnapAction extends ActionSupport implements ServletRequestAware {
		
	private static final long serialVersionUID = 1L;
	private static String PARAMETER_1 = "email";
	private static String PARAMETER_2 = "femail";
	private static String XML_1 = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
	private static String XML_2 = "<user>\n";
	
	private static String XML_3 = "<token>";
    
	private static String XML_4 = "</token>\n";
	
	private static String XML_5 = "<message>";
	    
	private static String XML_6 = "</message>\n";
	
	private static String XML_7 = "</user>\n";
	    
		private MessageStore messageStore;
		private HttpServletRequest request;
		
		public String execute() throws Exception {
			String parameter1 = getServletRequest().getParameter(PARAMETER_1);
			parameter1 = parameter1.replace(" ", "+");
			String parameter2 = getServletRequest().getParameter(PARAMETER_2);
		
			
			messageStore = new MessageStore();
			
			System.out.println("Server says hello!");
			
			if(parameter1.isEmpty() || parameter2.isEmpty()) 
			{
				messageStore.appendToMessage(XML_1);
				messageStore.appendToMessage(XML_2);
				messageStore.appendToMessage(XML_3);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_4);
				messageStore.appendToMessage(XML_5);
				messageStore.appendToMessage("Fail");
				messageStore.appendToMessage(XML_6);
				messageStore.appendToMessage(XML_7);
			
				return "fail";
			}
			
			HibernateUserManager manager;
			manager = HibernateUserManager.getDefault();
			
			// Check if the token is valid
			if(!manager.isTokenValid(parameter1))
			{
				System.out.println("TestUser is Null\n");
				messageStore.appendToMessage(XML_1);
				messageStore.appendToMessage(XML_2);
				messageStore.appendToMessage(XML_3);
				messageStore.appendToMessage("InvalidToken");
				messageStore.appendToMessage(XML_4);
				messageStore.appendToMessage(XML_5);
				messageStore.appendToMessage("InvalidToken");
				messageStore.appendToMessage(XML_6);
				messageStore.appendToMessage(XML_7);
				
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
					messageStore.appendToMessage("UserNotFound");
					messageStore.appendToMessage(XML_4);
					messageStore.appendToMessage(XML_5);
					messageStore.appendToMessage("UserNotFound");
					messageStore.appendToMessage(XML_6);
					messageStore.appendToMessage(XML_7);
					
					return "UserNotFound";
				}
				
				//Need to send the snap to the other person here
				
				messageStore.appendToMessage(XML_1);
				messageStore.appendToMessage(XML_2);
				messageStore.appendToMessage(XML_3);
				messageStore.appendToMessage(parameter1);
				messageStore.appendToMessage(XML_4);
				messageStore.appendToMessage(XML_5);
				messageStore.appendToMessage("Success");
				messageStore.appendToMessage(XML_6);
				messageStore.appendToMessage(XML_7);
						
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
