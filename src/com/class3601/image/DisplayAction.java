package com.class3601.image;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;

public class DisplayAction extends ActionSupport implements ServletRequestAware
{
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private static String PARAMETER_1 = "filename";
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		messageStore = new MessageStore();
		messageStore.appendToMessage(parameter1);
		return SUCCESS;
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
