package com.persistence;

import com.persistence.HibernateUserManager;

public class HibernateTestCase {


	/**
	 * @param args
	 */
	public static void main(String[] args) {
		HibernateUserManager manager;
		//manager = HibernateUserManager.getDefault();
		//manager.setupTable();
		
		HibernateInboxManager inboxManager;
		inboxManager = HibernateInboxManager.getDefault();
		inboxManager.setupTable();
	}

}
