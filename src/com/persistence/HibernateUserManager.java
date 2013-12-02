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

import com.common.BookingLogger;
import com.common.Messages;
import com.models.User;

public class HibernateUserManager extends
		AbstractHibernateDatabaseManager {

	private static final byte[] KEY = ";EZ¼å6WSÝÝÔ™S".getBytes();
	
	private static String USER_TABLE_NAME = "USER";
	private static String USER_JOIN_TABLE_NAME = "USER_FRIEND_USER";
	private static String USER_CLASS_NAME = "User";
	
	private static String SELECT_USER_WITH_NAME = "from "
			+ USER_CLASS_NAME + " as user where user.name = :name";
	private static String SELECT_USER_WITH_TOKEN = "from "
			+ USER_CLASS_NAME + " as user where user.token = :token";
	private static String SELECT_USER_WITH_USER_PASSWORD = "from "
			+ USER_CLASS_NAME
			+ " as user where user.name = :name and user.password = :password";

	private static String SELECT_USER_WITH_EMAIL_ADDRESS = "from "
			+ USER_CLASS_NAME + " as user where user.emailAddress = :emailAddress";
	private static String SELECT_USER_WITH_EMAIL_AND_NAME = "from "
			+ USER_CLASS_NAME
			+ " as user where user.emailAddress = :emailAddress and user.name = :name";

	private static final String DROP_TABLE_SQL = "drop table " + USER_TABLE_NAME + ";";

	private static final String DROP_JOIN_TABLE_SQL = "drop table " + USER_JOIN_TABLE_NAME + ";";
	
	private static String SELECT_NUMBER_USERS = "select count (*) from "
		+ USER_CLASS_NAME;
	
	private static final String CREATE_TABLE_SQL = "create table " + USER_TABLE_NAME + "(USER_ID_PRIMARY_KEY char(36) primary key,"
			+ "TOKEN tinytext, NAME tinytext, EMAIL_ADDRESS tinytext, PASSWORD tinytext, LOCATION tinytext, BIO tinytext, GENDER varchar(1), IMAGE tinytext, POPULARITY int, COUNT int,"
			+ "CREATION_TIME timestamp, LAST_UPDATE_TIME timestamp, LAST_ACCESSED_TIME timestamp);";

	private static final String CREATE_JOIN_TABLE_SQL = "create table " + USER_JOIN_TABLE_NAME + "(USER_ID char(36), FRIEND_USER_ID char(36));";

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
		User user = (User) object;
		
		user.setToken(generateToken(user));
		
		//Encrpyt the users information
		user = encryptUser(user);
		
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_TOKEN);
		 	query.setParameter("token", user.getToken());
			@SuppressWarnings("unchecked")
			List<User> users = query.list();

			if (!users.isEmpty()) 
			{
				System.out.println("Adding Failed\n");
				return false;
			}
				
			session.save(user);
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
	public synchronized boolean update(User user) {
		user.setToken(updateToken(user));
		boolean result = super.update(this.encryptUser(user));	
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
	
	@SuppressWarnings("unchecked")
	public synchronized User getUserByName(String name) {
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_NAME);
			query.setParameter("name", this.encrypt(name));
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
		
		System.out.println("======IN GET USER BY NAME AND PASSWORD =======");
		System.out.println("Name:: " + name);
		System.out.println("Password:: " + password);
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_USER_PASSWORD);
			query.setParameter("name", this.encrypt(name));
			query.setParameter("password", this.encrypt(password));
			List<User> users = query.list();
			transaction.commit();

			if (users.isEmpty()) {
				System.out.println("Failed to find user by name and passowrd");
				return null;
			} else {
				User user = users.get(0);
				System.out.println("Found user with name and password");
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
			query.setParameter("emailAddress", this.encrypt(emailAddress));
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
	 * Returns user from database based on given email and name.
	 * If not found returns null.
	 * Upon error returns null.
	 * 
	 * @param emailAddress
	 * @param password
	 * @return
	 */
	@SuppressWarnings("unchecked")
	public synchronized User getUserByEmailAddressAndName(String emailAddress, String name) 
	{
		System.out.println("====== IN GET USER BY EMAIL AND NAME =======");
		Session session = null;
		Transaction transaction = null;

		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_EMAIL_AND_NAME);
			query.setParameter("emailAddress", this.encrypt(emailAddress));
			query.setParameter("name", this.encrypt(name));
			List<User> users = query.list();
			transaction.commit();

			if (users.isEmpty()) {
				System.out.println("Failed to find user by name and email");
				return null;
			} else {
				User user = users.get(0);
				System.out.println("Got user by name and email");
				return decryptUser(user);
			}
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this, Messages.METHOD_GET_USER_BY_EMAIL_ADDRESS_PASSWORD,"error.getUserByEmailAddressInDatabase", exception);
			return null;
		} finally {
			closeSession();
		}
	}
	
	//Gets a user by their token from the database
    @SuppressWarnings("unchecked")
	public synchronized User getUserByToken(String token) {
		
		Session session = null;
		Transaction transaction = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_TOKEN);
			query.setParameter("token", token);
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
	
	public String getTableName() { return USER_TABLE_NAME; }
	
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
	
	
	
	public User encryptUser(User user) {
		user.setName(encrypt(user.getName()));
		user.setEmailAddress(encrypt(user.getEmailAddress()));
		user.setPassword(encrypt(user.getPassword()));
		user.setLocation(encrypt(user.getLocation()));
		user.setBio(encrypt(user.getBio()));
		
		return user;
	}
	
	public User decryptUser(User user) {
		user.setName(decrypt(user.getName()));
		user.setEmailAddress(decrypt(user.getEmailAddress()));
		user.setPassword(decrypt(user.getPassword()));
		user.setLocation(decrypt(user.getLocation()));
		user.setBio(decrypt(user.getBio()));
		
		return user;
	}
	
	//==========================================//
	//	Not sure if this should go here but		//
	//	it allows for getting parts of token	//
	//	from encrypted token without decrypting //
	//	the token itself						//
	//==========================================//
	
	//Gets the name from a token
	 public String getTokenName(String token) {
		String plainToken = decrypt(token);
		String[] str_array = plainToken.split("\\$");
		
		if(str_array == null)
 			return "unknown";
		
		return str_array[0];
	}
    
	 //Gets the password from a token
	 public String getTokenPassword(String token) {
		String plainToken = decrypt(token);
 		String[] str_array = plainToken.split("\\$");
 		
 		if(str_array == null)
 			return "unknown";
 		
 		return str_array[1];
 	}
    
    //Gets the time from a token
	 public String getTokenTime(String token) {
		String plainToken = decrypt(token);
 		String[] str_array = plainToken.split("\\$");
 		
 		if(str_array == null)
 			return "unknown";
 		
 		return str_array[2];
 	}
	 
	//Generates a new user token
	public String generateToken(User user) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		return encrypt(user.getName() + "$" + user.getPassword() + "$" + dateFormat.format(calendar.getTime()));
	}
		
	//Updates an existing token
	public String updateToken(User user) {
		return encrypt(this.getTokenName(user.getToken()) + "$" + this.getTokenPassword(user.getToken()) + "$" + this.getTokenTime(user.getToken()));
	}
	
	//Have not tested this fully
	public boolean isTokenValid(String token) {
		
		User user = this.getUserByToken(token);
		
		if(user == null) {
			System.out.println("User is null");
			return false;
		}
		
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar currentCalendar = Calendar.getInstance();
		Calendar tokenCalendar = Calendar.getInstance();
		try {
			Date tokenDate = dateFormat.parse(this.getTokenTime(token));
			tokenCalendar.setTime(tokenDate);
		} catch (ParseException e) {
				e.printStackTrace();
		}
		currentCalendar.add(Calendar.HOUR_OF_DAY, -24);
	
		int result = tokenCalendar.getTime().compareTo(currentCalendar.getTime());
			
		if(result < 0) {
			return false;
		}
		
		else {
			return true;
		}
	}
		
}
