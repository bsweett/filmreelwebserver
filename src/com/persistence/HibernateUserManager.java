package com.persistence;

import java.util.List;

//import org.apache.struts.action.ActionError;
//import org.apache.struts.action.ActionErrors;
//import org.apache.struts.action.ActionMessage;
//import org.apache.struts.action.ActionMessages;





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
			+ "NAME tinytext, EMAIL_ADDRESS tinytext, PASSWORD tinytext, LOCATION tinytext, USERBIO text, IMAGEPATH tinytext, COUNT int,"
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
		return HibernateUtil.executeSQLQuery(CREATE_TABLE_SQL);
	}

	/**
	 * Adds given user to the database.
	 * First checks if user exists based on the mobile phone number.
	 * If it does then adds error to given actionErrors and skips adding the vendor.
	 * Returns true if user is added successfully, otherwise returns false.
	 * 
	 * @param object
	 * @param actionErrors
	 * @return
	
	
	public synchronized boolean add(Object object, ActionErrors actionErrors, ActionMessages actionMessages) {
		int i;
		String creditCardNumber, creditCardExpirationMonth, creditCardExpirationYear, creditCardType;
		Transaction transaction = null;
		Session session = null;
		boolean encryptionDone = false;
		User user = (User) object;
		User userCopy = user.copy();
		
		HibernateDatabaseGatewayTagManagerResult tagResult; 

		user.encryptPassword();
		creditCardNumber = user.getCreditCardNumber();
		creditCardExpirationMonth = user.getCreditCardExpirationMonth();
		creditCardExpirationYear = user.getCreditCardExpirationYear();
		creditCardType = user.getCreditCardType();
		
		user.setCreditCardNumber(Messages.EMPTY_STRING);
		user.setCreditCardExpirationMonth(Messages.EMPTY_STRING);
		user.setCreditCardExpirationYear(Messages.EMPTY_STRING);
		user.setCreditCardCvv(Messages.EMPTY_STRING);
		user.setCreditCardType(Messages.EMPTY_STRING);
		encryptionDone = true;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_PHONENUMBER);
			query.setParameter(0, user.getPhoneNumber());
			List<User> users = query.list();

			if (!users.isEmpty()) {
				actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError( "error.userExists"));
				return false;
			}
				
			session.save(user);
			transaction.commit();
			return true;

		} catch (HibernateException exception) {
			actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.addUserToDatabase"));
			BookingLogger.getDefault().severe(this, Messages.METHOD_ADD_USER,
					"error.addUserToDatabase", exception);

			rollback(transaction);
			return false;
		} finally {
			closeSession();
		}
	}
	 */

	/**
	 * Adds given object (user) to the database 
	 */
	public synchronized boolean add(Object object) {
		Transaction transaction = null;
		Session session = null;
		User user = (User) object;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(SELECT_USER_WITH_EMAIL_ADDRESS);
			System.out.println("not NULL:" +user.getEmailAddress());
			query.setParameter("email", user.getEmailAddress());
			System.out.println("BULL");
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
	 * Deletes user with given id from the database.
	 * Returns true if user deleted, otherwise return false.
	 * Upon error adds specific error to given actionErrors.
	 * 
	 * @param id
	 * @param actionErrors
	 * @return
	 
	public synchronized boolean delete(String id, ActionErrors actionErrors) {
		
		Transaction transaction = null;
		Session session = null;
		
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();
			Query query = session.createQuery(DELETE_USER_WITH_PRIMARY_KEY);
			query.setParameter(0, id);
			int rowCount = query.executeUpdate();
			transaction.commit();
			if (rowCount > 0) {
				return true;
			} else {
				return false;
			}
		} catch (HibernateException exception) {
			actionErrors.add(ActionErrors.GLOBAL_ERROR, new ActionError(
					"error.addDeletingUserInDatabase"));
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_DELETE_USER,
					"error.addDeletingUserInDatabase", exception);

			rollback(transaction);
			return false;
		} finally {
			closeSession();
		}
	}
	 */
	
	/**
	 * Returns user from database based on given email address.
	 * If not found returns null.
	 * Upon error returns null.
	 * 
	 * @param emailAddress
	 * @return
	 */
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
				return user;
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
				return user;
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
				return user;
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
	 * Returns user from the database with given id.
	 * Upon exception returns null.
	 * 
	 * @param id
	 * @return
	 */
	public synchronized User getUserById(String id) {
		
		Session session = null;
		try {
			session = HibernateUtil.getCurrentSession();
			User user = (User) session.load(User.class, id);
			return user;
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_GET_USER_BY_ID, "error.getUserById",
					exception);
			return null;
		} finally {
			closeSession();
		}
	}

	/**
	 * Sets user in given form to the user found for form's email address and password.
	 * Returns true if successful, otherwise return false.
	 * 
	 * @param userForm
	 * @return
	 
	public synchronized boolean fillInFromEmailAddressAndPassword(
			UserForm userForm) {
		
		User user = getUserByEmailAddress(userForm.getEmailAddress());
		if ((user != null)
				&& (user.getPassword().equals(userForm.getPassword()))) {
			userForm.fillInFormWithUser(user);
			return true;
		} else {
			return false;
		}
	}
	 */
	
	/**
	 * Sets user in given form to the user found for form's phone number and password.
	 * Returns true if successful, otherwise return false.
	 * 
	 * @param userForm
	 * @return
	 
	public synchronized boolean fillInFromPhoneNumberAndPassword(
			UserForm userForm) {
		
		User user = getUserByPhonenumber(userForm.getPhoneNumber());
		if ((user != null)
				&& (user.getPassword().equals(userForm.getPassword()))) {
			userForm.fillInFormWithUser(user);
			return true;
		} else {
			return false;
		}
	}
	 */
	
	/**
	 * Sets user in given form to the user found for form's selected phone number.
	 * Returns true if successful, otherwise return false.
	 * 
	 * @param userForm
	 * @return
	 
	public synchronized boolean fillInFromSelectedPhoneNumber(
			UserForm userForm) {
		
		User user = getUserByPhonenumber(userForm
				.getSelectedPhoneNumber());
		if (user != null) {
			userForm.fillInFormWithUser(user);
			return true;
		} else {
			return false;
		}
	}
	 */
	
	/**
	 * Sets user in given form to the user found for form's selected email address.
	 * Returns true if successful, otherwise return false.
	 * 
	 * @param userForm
	 * @return
	 
	public synchronized boolean fillInFromEmailAddress(UserForm userForm) {
		
		User user = getUserByEmailAddress(userForm
				.getSelectedEmailAddress());
		if (user != null) {
			userForm.fillInFormWithUser(user);
			return true;
		} else {
			return false;
		}

	}
	 */
	
	/**
	 * Sets email addresses in the given user form to email addresses found
	 * in the user table.
	 * 
	 * @param userForm
	 
	@SuppressWarnings("unchecked")
	public synchronized void fillInWithEmailAddresses(UserForm userForm) {
		
		Transaction transaction = null;
		Session session = null;
		try {
			session = HibernateUtil.getCurrentSession();
			transaction = session.beginTransaction();

			Query query = session
					.createQuery(SELECT_ALL_USER_EMAIL_ADDRESSES);
			List<String> addresses = query.list();
			transaction.commit();

			Iterator<String> iterator = addresses.iterator();
			Collection<String> collection = new ArrayList<String>(addresses
					.size());
			while (iterator.hasNext()) {
				collection.add(iterator.next());
			}
			userForm.setEmailAddresses(collection);
		} catch (HibernateException exception) {
			BookingLogger.getDefault().severe(this,
					Messages.METHOD_FILL_IN_WITH_EMAIL_ADDRESS,
					"error.fillInWithEmailAddresses", exception);
			rollback(transaction);
		} finally {
			closeSession();
		}
	}
	 */
	
	/**
	 * Returns user with given emailAddress and password from the database. 
	 * If not found returns null.
	 * 
	 * @param emailAddress
	 * @param password
	 * @return
	 */
	public User getUser(String emailAddress, String password) {
		
		User user = getUserByEmailAddress(emailAddress);
		if ((user != null) && (user.getPassword().equals(password))) {
			return user;
		} else {
			return null;
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
}
