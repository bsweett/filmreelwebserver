package com.persistence;

import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

import org.apache.commons.codec.binary.Base64;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.JDBCConnectionException;

import com.common.BookingLogger;
import com.common.Messages;
import com.models.Inbox;
import com.models.User;

public class HibernateInboxManager extends AbstractHibernateDatabaseManager {
private static final byte[] KEY = ";EZ��6WS��ԙS".getBytes();
	
	private static String INBOX_TABLE_NAME = "INBOX";
	private static String INBOX_CLASS_NAME = "Inbox";
	
	private static String SELECT_INBOX_WITH_TOKEN_AND_TYPE = "from "
			+ INBOX_CLASS_NAME + " as inbox where inbox.email = :email and inbox.type = :type";

	private static final String DROP_TABLE_SQL = "drop table " + INBOX_TABLE_NAME + ";";
	
	//NO CLUE IF THIS WORKS!!!!
	private static String SELECT_INBOX_FOR_USER = "select * from INBOX_CLASS_NAME where inbox.email = :email and inbox.type = :type";
		
	
	private static final String CREATE_TABLE_SQL = "create table " + INBOX_TABLE_NAME + "(USER_ID_PRIMARY_KEY char(36) primary key,"
			+ "TOKEN tinytext, TYPE tinytext);";

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
	 * Adds given object (user) to the database 
	 */
	public synchronized boolean add(Object object) 
	{
		
		System.out.println("====== IN ADDING NEW USER=======");
		Transaction transaction = null;
		Session session = null;
		Inbox inbox = (Inbox) object;
		
		
		//Encrpyt the users information
		inbox = encryptInbox(inbox);
		
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_INBOX_WITH_TOKEN_AND_TYPE);
		 	query.setParameter("email", inbox.getEmail());
		 	query.setParameter("type", inbox.getType());
			@SuppressWarnings("unchecked")
			List<User> users = query.list();

			if (!users.isEmpty()) 
			{
				System.out.println("Adding Failed\n");
				return false;
			}
				
			session.save(inbox);
			transaction.commit();
			System.out.println("Added User\n");
			return true;

		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this, Messages.METHOD_ADD_USER,
					"error.addUserToDatabase", exception);

			rollback(transaction);
			return false;
		} finally {
			closeSession();
		}
	}

	
	/**
	 * Updates given object (user).
	 * 
	 * @param object
	 * @return
	 */
	public synchronized boolean update(Inbox inbox) {
		boolean result = super.update(this.encryptInbox(inbox));	
		return result;
	}

	
	/**
	 * Deletes given user from the database.
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
			BookingLogger.getDefault().severe(this, Messages.METHOD_DELETE_USER, Messages.HIBERNATE_FAILED, exception);
			return errorResult;
		}	
		catch (RuntimeException exception) {
			rollback(transaction);
			BookingLogger.getDefault().severe(this, Messages.METHOD_DELETE_USER, Messages.GENERIC_FAILED, exception);
			return errorResult;
		}
		finally {
			closeSession();
		}
	}
	
	public String getTableName() { return INBOX_TABLE_NAME; }
	
	/**
	 * Returns number of users.
	 * 
	 * Upon error returns empty list.
	 * 
	 * @param a charge status
	 * @return
	 */
	
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
		inbox.setType(encrypt(inbox.getType()));
		inbox.setSender(encrypt(inbox.getSender()));
		inbox.setRequestStatus(encrypt(inbox.getRequestStatus()));

		return inbox;
	}
	
	public Inbox decryptInbox(Inbox inbox) {
		
		inbox.setType(decrypt(inbox.getType()));
		inbox.setSender(decrypt(inbox.getSender()));
		inbox.setRequestStatus(decrypt(inbox.getRequestStatus()));
		
		return inbox;
	}
}