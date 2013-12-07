package com.class3601.social.actions;

import java.util.Set;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.User;
import com.models.Inbox;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateInboxManager;
import com.persistence.HibernateUserManager;

public class GetInboxAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_NAME = "<name>";
    private static String XML_XNAME = "</name>\n";
    private static String XML_REEL = "<reel>";
    private static String XML_XREEL = "</reel>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String token = getServletRequest().getParameter(PARAMETER_1);
		token = token.replace(" ", "+");
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
		
		HibernateInboxManager inboxManager;
		inboxManager = HibernateInboxManager.getDefault();
		
		User user = manager.getUserByToken(token);
		
		if(!manager.isTokenValid(token)) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("NoNewMail");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "fail";
		}
		
		else 
		{
				Set<Inbox> inbox = user.getInbox();
				
				if(inbox.isEmpty()) {
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_MESSAGE);
					messageStore.appendToMessage("NoNewMail");
					messageStore.appendToMessage(XML_XMESSAGE);
					messageStore.appendToMessage(XML_XDATA);
					return "fail";
					
				}
				else {
					messageStore.appendToMessage(XML);
					messageStore.appendToMessage(XML_DATA);
					messageStore.appendToMessage(XML_REEL);
					for (Inbox i : inbox) {
						messageStore.appendToMessage(i.getSenderEmail());
						messageStore.appendToMessage("-");
						messageStore.appendToMessage(i.getImageLocation());
						messageStore.appendToMessage("-");
						inboxManager.delete(i);
					}
					messageStore.appendToMessage(XML_XREEL);
					messageStore.appendToMessage(XML_XDATA);
					
					
					return "success";
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
