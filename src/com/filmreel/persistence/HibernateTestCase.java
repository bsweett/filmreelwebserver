package com.filmreel.persistence;

import com.filmreel.persistence.HibernateUserManager;

//Test class used to clear databases

public class HibernateTestCase {

	public static void main(String[] args) {
		HibernateUserManager manager;
		manager = HibernateUserManager.getDefault();
		manager.setupTable();
		
		HibernateInboxManager inboxManager;
		inboxManager = HibernateInboxManager.getDefault();
		inboxManager.setupTable();
	}
}
