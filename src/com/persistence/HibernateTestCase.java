package com.persistence;

import com.models.Inbox;
import com.models.User;
import com.persistence.HibernateUserManager;

public class HibernateTestCase {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		manager.setupTable();
		
		HibernateInboxManager inboxManager;
		inboxManager = HibernateInboxManager.getDefault();
		inboxManager.setupTable();
		
		
		User user = new User();
		user.setName("Brayden");
		user.setToken("Testoken");
		
		Inbox inbox = new Inbox();
		inbox.setReceiverEmail("braydengirard@icloud.com");
		
		user.addInbox(inbox);
		
		manager.add(user);

		/*User myuser = new User();
		myuser.setCount(0);
		myuser.setName("TestUser");
		myuser.setEmailAddress("bob@gmail.com");
		myuser.setPassword("filmreel");
		myuser.setImage("Unknown");
		myuser.setLocation("Ottawa");
		myuser.setBio("Just your everyday normal user object");
		manager.add(myuser);
		
		User userByEmailPass = manager.getUserByEmailAddressAndName("bob@gmail.com", "TestUser");
		System.out.println("The token is: " + userByEmailPass.getToken());
		
		User userByToken = manager.getUserByToken(userByEmailPass.getToken());
		
		if(manager.isTokenValid(userByToken.getToken()))
			System.out.println("Token is good");	
		
		System.out.println("The users email is: " + userByToken.getEmailAddress());
		System.out.println("The token timestamp is: " + manager.getTokenTime(userByToken.getToken()));*/
	}

}
