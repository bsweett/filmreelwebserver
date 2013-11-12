package com.persistence;

import com.persistence.HibernateUserManager;
import com.models.User;

public class HibernateTestCase {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		manager.setupTable();
		User myuser = new User();
		myuser.setCount(0);
		myuser.setName("TestUser");
		myuser.setEmailAddress("bob@gmail.com");
		myuser.setPassword("filmreel");
		myuser.setImagePath("/null");
		myuser.setLocation("Ottawa");
		myuser.setUserBio("Just your everyday normal user object");
		myuser.generateToken();
		System.out.println("The token is: " + myuser.getTokenToString());
		System.out.println("The timestamp is: " + myuser.timestampFromToken(myuser.getTokenToString()));
		if(myuser.isTokenValid(myuser.getTokenToString()))
			System.out.println("Token is good");	
		manager.add(myuser);
		
		User newUser = manager.getUserByEmailAddress(myuser.getEmailAddress());
		System.out.println("The users email is: " + newUser.getEmailAddressToString());
		
	}

}
