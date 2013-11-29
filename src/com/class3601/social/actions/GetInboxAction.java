package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateInboxManager;

public class GetInboxAction extends ActionSupport implements ServletRequestAware{
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "token";
    private static String PARAMETER_2 = "type";
    private static String XML = "<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n";
    private static String XML_DATA = "<data>\n";
    private static String XML_XDATA = "</data>";
    private static String XML_MESSAGE = "<message>";
    private static String XML_XMESSAGE = "</message>\n";
    private static String XML_SNAP = "<snap>";
    private static String XML_XSNAP = "</snap>\n";
    private static String XML_FNAME = "<fname>";
    private static String XML_XFNAME = "</fname>\n";

    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception 
	{
		String token = getServletRequest().getParameter(PARAMETER_1);
		token = token.replace(" ", "+");
		String type = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
				
		if(token.isEmpty() && type.isEmpty()) 
		{
			messageStore.appendToMessage(XML);
			messageStore.appendToMessage(XML_DATA);
			messageStore.appendToMessage(XML_MESSAGE);
			messageStore.appendToMessage("Fail");
			messageStore.appendToMessage(XML_XMESSAGE);
			messageStore.appendToMessage(XML_XDATA);
			return "fail"; 
		}
		
		HibernateInboxManager manager;
		manager = HibernateInboxManager.getDefault();
		
		String[] friendRequests = manager.getMessagesForToken(token);
		byte[][] snaps = manager.getSnapsForToken(token);
		
		if(friendRequests == null && snaps == null) 
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
			if(type.equals("snap")) {
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Success");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_SNAP);
				messageStore.appendToMessage("Snaps array sent to the person goes here");
				messageStore.appendToMessage(XML_XSNAP);
				messageStore.appendToMessage(XML_XDATA);
				return "success";
			}
			else {
				messageStore.appendToMessage(XML);
				messageStore.appendToMessage(XML_DATA);
				messageStore.appendToMessage(XML_MESSAGE);
				messageStore.appendToMessage("Success");
				messageStore.appendToMessage(XML_XMESSAGE);
				messageStore.appendToMessage(XML_FNAME);
				messageStore.appendToMessage("Friend Request array of who added the person goes here");
				messageStore.appendToMessage(XML_XFNAME);
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
