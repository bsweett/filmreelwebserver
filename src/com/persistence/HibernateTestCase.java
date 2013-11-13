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
		System.out.println("The token is: " + myuser.getToken());
		System.out.println("The timestamp is: " + myuser.getTokenTime(myuser.getToken()));
		if(myuser.isTokenValid(myuser.getToken()))
			System.out.println("Token is good");	
		manager.add(myuser);
		
		User newUser = manager.getUserByEmailAddress(myuser.getEmailAddress());
		System.out.println("The users email is: " + newUser.getEmailAddress());
		
		String encryptedText = manager.encrypt("My name is brayden");
		String decryptedText = manager.decrypt(encryptedText);
		System.out.println(encryptedText);
		System.out.println(decryptedText);
		
	}

}
