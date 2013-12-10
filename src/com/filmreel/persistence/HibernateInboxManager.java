package com.filmreel.persistence;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.HibernateException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;

import com.filmreel.common.BookingLogger;
import com.filmreel.common.Messages;
import com.filmreel.models.Inbox;

/*
======================================================================
|| This is the hibernate manager that deals with inboxs. Hibernate  ||
|| methods suchs as add, update and remove are implemented. At the  ||
|| bottom of this class there are 4 methods for encryption to  	    ||
|| encrypt and decrypt inboxs.     								    ||
====================================================================== 
 */

public class HibernateInboxManager extends AbstractHibernateDatabaseManager {
	
	private static final byte[] KEY = ";EZ¼å6WSÝÝÔ™S".getBytes();
	
	private static String INBOX_TABLE_NAME = "INBOX";
	private static String INBOX_CLASS_NAME = "Inbox";
	
	private static String SELECT_INBOX_WITH_RECEIVER_EMAIL = "from "
			+ INBOX_CLASS_NAME + " as inbox where inbox.receiverEmail = :receiverEmail";

	private static final String DROP_TABLE_SQL = "drop table " + INBOX_TABLE_NAME + ";";
	
	private static final String CREATE_TABLE_SQL = "create table " + INBOX_TABLE_NAME + "(INBOX_ID_PRIMARY_KEY char(36) primary key,"
			+ "SENDER_EMAIL tinytext, RECEIVER_EMAIL tinytext, IMAGE_LOCATION tinytext, USER_ID_FK char(36));";
	
	private static HibernateInboxManager manager;

	HibernateInboxManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * 
	 * @return
	 */
	public static HibernateInboxManager getDefault() {
		
		if (manager == null) {
			manager = new HibernateInboxManager();
		}
		return manager;
	}

	public String getClassName() {
		return INBOX_CLASS_NAME;
	}

	@Override
	public boolean setupTable() {
		HibernateUtil.executeSQLQuery(DROP_TABLE_SQL);
		return HibernateUtil.executeSQLQuery(CREATE_TABLE_SQL);
	}

	/**
	 * Adds given object (inbox) to the database 
	 */
	public synchronized boolean add(Object object) 
	{
		
		System.out.println("====== IN ADDING INBOX=======");
		Transaction transaction = null;
		Session session = null;
		Inbox inbox = (Inbox) object;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_INBOX_WITH_RECEIVER_EMAIL);
		 	query.setParameter("receiverEmail", inbox.getReceiverEmail());
			@SuppressWarnings("unchecked")
			List<Inbox> inboxList = query.list();
			System.out.println("Testing");

			if (!inboxList.isEmpty()) 
			{
				System.out.println("Adding Reel to DB Failed\n");
				return false;
			}
			
			session.save(inbox);
			System.out.println("session save");
			
			transaction.commit();
			System.out.println("Added Reel to Inbox\n");
			return true;

		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this, Messages.METHOD_ADD_INBOX,
					"error.addUserToDatabase", exception);

			rollback(transaction);
			System.out.println("ROLLBACK\n");
			return false;
		} finally {
			System.out.println("FINALLY\n");
			closeSession();
		}
	}

	/**
	 * Updates given object (inbox).
	 * 
	 * @param object
	 * @return
	 */
	public synchronized boolean update(Inbox inbox) {
		boolean result = super.update(this.encryptInbox(inbox));	
		return result;
	}
	
	/**
	 * Deletes given inbox from the database.
	 * Returns true if successful, otherwise returns false.
	 * 
	 * @param object
	 * @return
	 */
	public synchronized boolean delete(Inbox inbox){
		
		Session session = null;
		Transaction transaction = null;
		boolean errorResult = false;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			session.delete(inbox);
			transaction.commit();
			return true;
		}
		catch (HibernateException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, Messages.METHOD_DELETE_INBOX, Messages.HIBERNATE_FAILED, exception);
			return errorResult;
		}	
		catch (RuntimeException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, Messages.METHOD_DELETE_INBOX, Messages.GENERIC_FAILED, exception);
			return errorResult;
		}
		finally {
			closeSession();
		}
	}
	
	public String getTableName() { return INBOX_TABLE_NAME; }
	
	public String encrypt(String textToEncrypt) {
	 	
		try {
			
			byte[] plainText = textToEncrypt.getBytes("UTF-8");
			
			SecretKeySpec skeySpec = new SecretKeySpec(KEY, "AES");
			
	        // build the initialization vector.  This example is all zeros, but it 
	        // could be any value or generated using a random number generator.
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);

	        // initialize the cipher for encrypt mode
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.ENCRYPT_MODE, skeySpec, ivspec);

	        // encrypt the message
	        byte[] encrypted = cipher.doFinal(plainText);
		    
	        Base64 encoder = new Base64();
	      
		    return new String (encoder.encode(encrypted));
		    
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(NoSuchPaddingException e){
			e.printStackTrace();
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
	
		return null;
	}
	
	public String decrypt(String textToDecrypt) {
		
		try {
			Base64 encoder = new Base64();
			
			byte[] encryptedText = textToDecrypt.getBytes("UTF-8");
			
			SecretKeySpec skeySpec = new SecretKeySpec(KEY, "AES");

	        // build the initialization vector.  This example is all zeros, but it 
	        // could be any value or generated using a random number generator.
	        byte[] iv = { 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0, 0 };
	        IvParameterSpec ivspec = new IvParameterSpec(iv);

	        // initialize the cipher for encrypt mode
	        Cipher cipher = Cipher.getInstance("AES/CBC/PKCS5Padding");
	        cipher.init(Cipher.DECRYPT_MODE, skeySpec, ivspec);
	        
	        // decrypt the message
	        byte[] decrypted = cipher.doFinal(encoder.decode(encryptedText));
	        
	        
		    
	        return new String(decrypted);
		    
		}catch(NoSuchAlgorithmException e){
			e.printStackTrace();
		}catch(NoSuchPaddingException e){
			e.printStackTrace();
		}catch(InvalidKeyException e){
			e.printStackTrace();
		}catch(IllegalBlockSizeException e){
			e.printStackTrace();
		}catch(BadPaddingException e){
			e.printStackTrace();
		} catch (InvalidAlgorithmParameterException e) {
			e.printStackTrace();
		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		}
		
	   return null;
	}
	
	public Inbox encryptInbox(Inbox inbox) {
		inbox.setSenderEmail(encrypt(inbox.getSenderEmail()));
		inbox.setReceiverEmail(encrypt(inbox.getReceiverEmail()));
		inbox.setImageLocation(encrypt(inbox.getImageLocation()));
		
		return inbox;
	}
	
	public Inbox decryptInbox(Inbox inbox) {
		
		inbox.setSenderEmail(decrypt(inbox.getSenderEmail()));
		inbox.setReceiverEmail(decrypt(inbox.getReceiverEmail()));
		inbox.setImageLocation(decrypt(inbox.getImageLocation()));
		
		return inbox;
	}
}
