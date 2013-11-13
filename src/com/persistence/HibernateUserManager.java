package com.persistence;

import static org.apache.commons.io.FileUtils.readFileToByteArray;
import static org.apache.commons.io.FileUtils.writeByteArrayToFile;

import java.io.File;
import java.io.IOException;
import java.io.UnsupportedEncodingException;
import java.security.InvalidAlgorithmParameterException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.List;

import javax.crypto.BadPaddingException;
import javax.crypto.Cipher;
import javax.crypto.IllegalBlockSizeException;
import javax.crypto.KeyGenerator;
import javax.crypto.NoSuchPaddingException;
import javax.crypto.SecretKey;
import javax.crypto.spec.IvParameterSpec;
import javax.crypto.spec.SecretKeySpec;

//import org.apache.struts.action.ActionError;
//import org.apache.struts.action.ActionErrors;
//import org.apache.struts.action.ActionMessage;
//import org.apache.struts.action.ActionMessages;





import org.apache.commons.codec.binary.Base64;
import org.hibernate.HibernateException;
import org.hibernate.ObjectNotFoundException;
import org.hibernate.Query;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.exception.JDBCConnectionException;

import com.common.BookingLogger;
import com.common.Messages;
import com.models.User;

public class HibernateUserManager extends
		AbstractHibernateDatabaseManager {

	private static String USER_TABLE_NAME = "USER";
	private static String USER_JOIN_TABLE_NAME = "USER_FRIEND_USER";
	private static String USER_CLASS_NAME = "User";

	private static String SELECT_ALL_USERS = "from "
			+ USER_CLASS_NAME + " as user";
	private static String SELECT_USER_WITH_EMAIL_ADDRESS = "from "
			+ USER_CLASS_NAME + " as user where user.emailAddress = :email";
	private static String SELECT_USER_WITH_NAME = "from "
			+ USER_CLASS_NAME + " as user where user.name = :name";
	private static String SELECT_USER_WITH_EMAIL_PASSWORD = "from "
			+ USER_CLASS_NAME
			+ " as user where user.emailAddress = :email and user.password = :password";
	private static String SELECT_USER_WITH_USER_PASSWORD = "from "
			+ USER_CLASS_NAME
			+ " as user where user.name = :username and user.password = :password";
	private static String SELECT_USER_WITH_TOKEN = "from "
			+ USER_CLASS_NAME + " as user where user.token = :token";
	/* private static String DELETE_USER_WITH_PRIMARY_KEY = " delete from "
			+ USER_CLASS_NAME + " as user where user.id = ?"; */
	/* private static String SELECT_ALL_USER_EMAIL_ADDRESSES = "select user.emailAddress from "
			+ USER_CLASS_NAME + " as user"; */

	private static final String DROP_TABLE_SQL = "drop table "
			+ USER_TABLE_NAME + ";";
	
	private static final String DROP_JOIN_TABLE_SQL = "drop table "
			+ USER_JOIN_TABLE_NAME + ";";
	
	private static String SELECT_NUMBER_USERS = "select count (*) from "
		+ USER_CLASS_NAME;
	
	private static final String CREATE_TABLE_SQL = "create table " + USER_TABLE_NAME + "(USER_ID_PRIMARY_KEY char(36) primary key,"
			+ "TOKEN tinytext, NAME tinytext, EMAIL_ADDRESS tinytext, PASSWORD tinytext, LOCATION tinytext, USERBIO tinytext, IMAGEPATH tinytext, COUNT int,"
			+ "CREATION_TIME timestamp, LAST_UPDATE_TIME timestamp, LAST_ACCESSED_TIME timestamp);";

	private static final String CREATE_JOIN_TABLE_SQL = "create table " + USER_JOIN_TABLE_NAME + "(USER_ID char(36), FRIEND_USER_ID char(36));";
	
	private static final String METHOD_GET_N_USERS = "getNUsersStartingAtIndex";

	private static HibernateUserManager manager;

	HibernateUserManager() {
		super();
	}

	/**
	 * Returns default instance.
	 * 
	 * @return
	 */
	public static HibernateUserManager getDefault() {
		
		if (manager == null) {
			manager = new HibernateUserManager();
		}
		return manager;
	}

	public String getClassName() {
		return USER_CLASS_NAME;
	}

	@Override
	public boolean setupTable() {
		HibernateUtil.executeSQLQuery(DROP_TABLE_SQL);
		HibernateUtil.executeSQLQuery(DROP_JOIN_TABLE_SQL);
		HibernateUtil.executeSQLQuery(CREATE_JOIN_TABLE_SQL);
		this.generateNewKey();
		return HibernateUtil.executeSQLQuery(CREATE_TABLE_SQL);
	}

	/**
	 * Adds given object (user) to the database 
	 */
	public synchronized boolean add(Object object) {
		Transaction transaction = null;
		Session session = null;
		User user = (User) object;
		
		//Encrpyt the users information
		user = encryptUser(user);
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_EMAIL_ADDRESS);
		 	query.setParameter("email", user.getEmailAddress());
			@SuppressWarnings("unchecked")
			List<User> users = query.list();

			if (!users.isEmpty()) {
				return false;
			}
				
			session.save(user);
			transaction.commit();
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
	public synchronized boolean update(User user) {
		user.updateToken();
		boolean result = super.update(user);	
		return result;
	}

	
	/**
	 * Deletes given user from the database.
	 * Returns true if successful, otherwise returns false.
	 * 
	 * @param object
	 * @return
	 */
	public synchronized boolean delete(User user){
		
		Session session = null;
		Transaction transaction = null;
		boolean errorResult = false;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			session.delete(user);
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
	
	/**
	 * Returns user without decrypted credit card information, 
	 * from database based on given Name.
	 * If not found returns null.
	 * Upon error returns null.
	 * 
	 * @param phonenumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized User getUserByNameAndPassword(String name, String password) {
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_USER_PASSWORD);
			query.setParameter("username", name);
			query.setParameter("password", password);
			List<User> users = query.list();
			transaction.commit();

			if (users.isEmpty()) {
				return null;
			} else {
				User user = users.get(0);
				return decryptUser(user);
			}
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_USER_BY_NAME,
					"error.getUserByName", exception);
			return null;
		} finally {
			closeSession();
		} 
	}
	
	@SuppressWarnings("unchecked")
	public synchronized User getUserByEmailAddress(String emailAddress) {
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_EMAIL_ADDRESS);
			query.setParameter("email", emailAddress);
			List<User> users = query.list();
			transaction.commit();

			if (users.isEmpty()) {
				return null;
			} else {
				User user = users.get(0);
				return decryptUser(user);
			}
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_USER_BY_EMAIL_ADDRESS,
					"error.getUserByEmailAddressInDatabase", exception);
			return null;
		} finally {
			closeSession();
		}
	}

	/**
	 * Returns user from database based on given email and password.
	 * If not found returns null.
	 * Upon error returns null.
	 * 
	 * @param emailAddress
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized User getUserByEmailAddressPasssword(
			String emailAddress, String password) {
		
		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session
					.createQuery(SELECT_USER_WITH_EMAIL_PASSWORD);
			query.setParameter("email", emailAddress);
			query.setParameter("password", password);
			List<User> users = query.list();
			transaction.commit();

			if (users.isEmpty()) {
				return null;
			} else {
				User user = users.get(0);
				return decryptUser(user);
			}
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_USER_BY_EMAIL_ADDRESS_PASSWORD,
					"error.getUserByEmailAddressInDatabase", exception);
			return null;
		} finally {
			closeSession();
		}
	}
	
	/**
	 * Returns users, 
	 * from database.
	 * If not found returns null.
	 * Upon error returns null.
	 * 
	 * @param phonenumber
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized  List<User> getNUsersStartingAtIndex(int index, int n) {
		List<User> errorResult = null;
		Session session = null;
		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session.createQuery(SELECT_ALL_USERS);
			query.setFirstResult(index);
			query.setMaxResults(n);
			List<User> users = query.list();

			return users;
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_N_USERS,
					Messages.OBJECT_NOT_FOUND_FAILED, exception);
			return errorResult;
		}  catch (JDBCConnectionException exception) {
			HibernateUtil.clearSessionFactory();
			BookingLogger.getDefault().severe(this, METHOD_GET_N_USERS,
					Messages.HIBERNATE_CONNECTION_FAILED, exception);
			return errorResult;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_N_USERS,
					Messages.HIBERNATE_FAILED, exception);
			return errorResult;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this, METHOD_GET_N_USERS,
					Messages.GENERIC_FAILED, exception);
			return errorResult;
		} finally {
			closeSession();
		}
	}
	
	public String getTableName() {
		return USER_TABLE_NAME;
	}
	
	
	/**
	 * Returns number of users.
	 * 
	 * Upon error returns empty list.
	 * 
	 * @param a charge status
	 * @return
	 */
	public synchronized int getNumberOfUsers() {
		
		Session session = null;
		Long aLong;

		try {
			session = HibernateUtil.getCurrentSession();
			Query query = session
					.createQuery(SELECT_NUMBER_USERS);
			aLong = (Long) query.uniqueResult();
			return aLong.intValue();
		} catch (ObjectNotFoundException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_NUMBER_OF_USERS,
					Messages.OBJECT_NOT_FOUND_FAILED, exception);
			return 0;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_NUMBER_OF_USERS,
					Messages.HIBERNATE_FAILED, exception);
			return 0;
		} catch (RuntimeException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_NUMBER_OF_USERS,
					Messages.GENERIC_FAILED, exception);
			return 0;
		} finally {
			closeSession();
		}
	}
	
	public String encrypt(String textToEncrypt) {
	 	
		try {
			
			byte[] plainText = textToEncrypt.getBytes("UTF-8");
			
			SecretKeySpec skeySpec = new SecretKeySpec(loadKey(), "AES");
			
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
			
			SecretKeySpec skeySpec = new SecretKeySpec(loadKey(), "AES");

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
		  //  return decrypted;
		    
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
	
	
	
	public User encryptUser(User user) {
		user.setToken(encrypt(user.getToken()));
		user.setName(encrypt(user.getName()));
		user.setEmailAddress(encrypt(user.getEmailAddress()));
		user.setPassword(encrypt(user.getName()));
		user.setLocation(encrypt(user.getName()));
		user.setUserBio(encrypt(user.getName()));
		user.setImagePath(encrypt(user.getName()));
		
		return user;
	}
	
	public User decryptUser(User user) {
		user.setToken(decrypt(user.getToken()));
		user.setName(decrypt(user.getName()));
		user.setEmailAddress(decrypt(user.getEmailAddress()));
		user.setPassword(decrypt(user.getPassword()));
		user.setLocation(decrypt(user.getLocation()));
		user.setUserBio(decrypt(user.getUserBio()));
		user.setImagePath(decrypt(user.getImagePath()));
		
		return user;
	}

	private void generateNewKey() {
		File file = new File("key.txt");
        KeyGenerator keygen;
        
		try {
			keygen = KeyGenerator.getInstance("AES");
			keygen.init(128);  
	        byte[] key = keygen.generateKey().getEncoded(); 
			writeByteArrayToFile(file, key);
		} catch (NoSuchAlgorithmException e1) {
			e1.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
	}  
	
	private void saveKey(SecretKey key, File file) {
	    byte[] encoded = key.getEncoded();
	    String data = new String(encoded);
	    
	}
	private byte[] loadKey() {
		File file = new File("key.txt");
		try {
			byte[] key = readFileToByteArray(file);
			return key;
		} catch (IOException e) {
			e.printStackTrace();
		}
	   return null;
	}
}
