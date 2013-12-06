package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.models.Inbox;
import com.models.User;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateInboxManager;
import com.persistence.HibernateUserManager;

public class SendSnapAction extends ActionSupport implements ServletRequestAware {
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "from";
    private static String PARAMETER_2 = "to";
    private static String PARAMETER_3 = "imagelocation";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>";
    private static String XML_XDATA = "</data>\n";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";

	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String parameter2 = getServletRequest().getParameter(PARAMETER_2);
		String parameter3 = getServletRequest().getParameter(PARAMETER_3);
		messageStore = new MessageStore();
		
		if(parameter1.isEmpty() || parameter2.isEmpty() || parameter3.isEmpty()) 
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
		
		User receiver = manager.getUserByEmailAddress(parameter2);
		User sender = manager.getUserByEmailAddress(parameter1);
		
		// Check the users token
		if(receiver == null || sender == null)
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("UserNotFound");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
		
			return "CurrentUserError";
		} 
		else 
		{
			Inbox inbox = new Inbox();
			inbox.setSenderEmail(sender.getEmailAddress());
			inbox.setReceiverEmail(receiver.getEmailAddress());
			inbox.setImageLocation(parameter3);
			
			receiver.addInbox(inbox);
			sender.incrementReelCount();
			
			manager.updateUser(sender);
			manager.updateUser(receiver);
			inboxManager.add(inbox);
			
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
