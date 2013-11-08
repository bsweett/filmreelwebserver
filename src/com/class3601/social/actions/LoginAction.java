package com.class3601.social.actions;

import javax.servlet.http.HttpServletRequest;

import org.apache.struts2.interceptor.ServletRequestAware;

import com.class3601.social.common.MessageStore;
import com.opensymphony.xwork2.ActionSupport;
import com.persistence.HibernateUserManager;
import com.models.User;

public class LoginAction extends ActionSupport implements ServletRequestAware {
	
	private static final long serialVersionUID = 1L;
    private static String PARAMETER_1 = "id";
    private static String PARAMETER_2 = "password";
    private static String XML_1 = 
    		"<?xml version=\"1.0\" encoding=\"ISO-8859-1\"?>\n\n" +
    				"<stuff>\n" +
    					"   <parameter>";
    private static String XML_2 = 
    					"</parameter>\n" +
    					"   <parameter>";
    private static String XML_3 = 
						"</parameter>\n";
    
    private static String XML_4 = "</stuff>\n";
    
	private MessageStore messageStore;
	private HttpServletRequest request;
	
	public String execute() throws Exception {
		// HttpServletRequest request = ServletActionContext.getRequest();
		// preferred method is to implement ServletRequestAware interface
		// http://struts.apache.org/2.0.14/docs/how-can-we-access-the-httpservletrequest.html
	
		//http://localhost:8080/social/initial?parameter1=dog&parameter2=cat
		//http://localhost:8080/social/initial?parameter1=dog&parameter2=error
		String parameter1 = getServletRequest().getParameter(PARAMETER_1);
		String  parameter2 = getServletRequest().getParameter(PARAMETER_2);
		messageStore = new MessageStore();
		
		messageStore.appendToMessage(XML_1);
		messageStore.appendToMessage(parameter1);
		messageStore.appendToMessage(XML_2);
		messageStore.appendToMessage(parameter2);
		messageStore.appendToMessage(XML_3);
		messageStore.appendToMessage(XML_4);
		
		if(parameter1.isEmpty() || parameter2.isEmpty()) {
			return "fail";
		}
		
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		User testuser = manager.getUserByNameAndPassword(parameter1, parameter2);
		if(testuser == null) {
			User myuser = new User();
			myuser.setName(parameter1);
			myuser.setEmailAddress(parameter1);
			myuser.setPassword(parameter2);
			manager.add(myuser);
			return "newuser";
		}
		
		else {
			User gotUser = manager.getUserByNameAndPassword(parameter1, parameter2);
			gotUser.incramentCount();
			String countAsString = Integer.toString(gotUser.getCount());
			manager.update(gotUser);
			messageStore.appendToMessage(countAsString);
			return "success";
		}
		
//		if(!parameter1.isEmpty() && !parameter2.isEmpty()) {
//			User testuser = manager.getUserByNameAndPassword(parameter1, parameter2);
//			if(testuser == null) {
//				System.out.println("Creating new user\n");
//				User user = new User();
//				user.setName(parameter1);
//				user.setPassword(parameter2);
//				manager.add(user);
//				String countAsString = Integer.toString(user.getCount());
//				messageStore.appendToMessage(countAsString);
//				return "newuser";
//			} else if(testuser != null) {
//				User user = manager.getUserByNameAndPassword(parameter1, parameter2);
//				user.incramentCount();
//				String countAsString = Integer.toString(user.getCount());
//				manager.update(user);
//				messageStore.appendToMessage(countAsString);
//				return "succuss";
//			}
//		} 
//			addActionError("Wrong user name or Password");
//			return "fail";
				
//		if(parameter1.equals("aaa") && parameter2.equals("bbb")){
//			User user = manager.getUserByNameAndPassword("aaa", "bbb");
//			System.out.println("" + user.getName() + user.getCount());
//			user.incramentCount();
//			System.out.println("New count:" + user.getCount());
//			String countAsString = Integer.toString(user.getCount());
//			manager.update(user);
//			messageStore.appendToMessage(countAsString);
//			return "success";
//		} else {
//			addActionError("Wrong user name or Password");
//			return "fail";
//		}
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
