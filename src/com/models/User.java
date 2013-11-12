package com.models;


import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.HashSet;
import java.util.Set;

import com.common.Messages;

public class User {
	private String userIdPrimarKey;
	private byte[] token;
	private byte[] name;
	private byte[] emailAddress;
	private byte[] password;
	private Timestamp creationTimestamp;
	private Timestamp lastUpdateTimestamp;
	private Timestamp lastAccessedTimestamp;
	private byte[] location;
	private byte[] userBio;
	private byte[] imagePath;
	private int count;
	private Set<User> friends = new HashSet<User>(0);

	public User() {
		Calendar calendar = Calendar.getInstance();
		setName(Messages.UNKNOWN);
		setEmailAddress(Messages.UNKNOWN);
		setPassword(Messages.UNKNOWN);
		setLocation(Messages.UNKNOWN);
		setUserBio(Messages.UNKNOWN);
		setImagePath(Messages.UNKNOWN);
		setCount(0);
		setCreationTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setLastUpdateTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setLastAccessedTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setToken(Messages.UNKNOWN);
	}

	public Timestamp getCreationTimestamp() {
		return creationTimestamp;
	}

	public void setCreationTimestamp(Timestamp creationTimestamp) {
		this.creationTimestamp = creationTimestamp;
	}

	public Timestamp getLastUpdateTimestamp() {
		return lastUpdateTimestamp;
	}

	public void setLastUpdateTimestamp(Timestamp lastUpdateTimestamp) {
		this.lastUpdateTimestamp = lastUpdateTimestamp;
	}

	public Timestamp getLastAccessedTimestamp() {
		return lastAccessedTimestamp;
	}

	public void setLastAccessedTimestamp(Timestamp lastAccessedTimestamp) {
		this.lastAccessedTimestamp = lastAccessedTimestamp;
	}

	public boolean equals(User user) {
		return getUserIdPrimarKey().equals(user.getUserIdPrimarKey());
	}

	public String getUserIdPrimarKey() {
		return userIdPrimarKey;
	}


	public void setUserIdPrimarKey(String userIdPrimarKey) {
		this.userIdPrimarKey = userIdPrimarKey;
	}

	public void setName(String name) {
		this.name = name.getBytes();
	}
	
	public void setName(byte[] name) {
		this.name = name;
	}

	public byte[] getName() {
		return name;
	}
	
	public String getNameToString() {
		return new String(name);
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress.getBytes();
	}
	
	public void setEmailAddress(byte[] emailAddress) {
		this.emailAddress = emailAddress;
	}

	public byte[] getEmailAddress() {
		return emailAddress;
	}
	
	public String getEmailAddressToString() {
		return new String(emailAddress);
	}

	public void setPassword(String password) {
		this.password = password.getBytes();
	}
	
	public void setPassword(byte[] password) {
		this.password = password;
	}
	
	public byte[] getPassword() {
		return password;
	}
	
	public String getPasswordToString() {
		return new String(password);
	}
	
	public void setLocation(String location) {
		this.location = location.getBytes();
	}

	public void setLocation(byte[] location) {
		this.location = location;
	}
	
	public byte[] getLocation() {
		return location;
	}
	
	public String getLocationToString() {
		return new String(location);
	}
	
	public void setUserBio(String userBio) {
		this.userBio = userBio.getBytes();
	}
	
	public void setUserBio(byte[] userBio) {
		this.userBio = userBio;
	}
	
	public byte[] getUserBio() {
		return userBio;
	}
	
	public String getUserBioToString() {
		return new String(userBio);
	}
	
	public void setImagePath(String imagePath) {
		this.imagePath = imagePath.getBytes();
	}
	
	public void setImagePath(byte[] imagePath) {
		this.imagePath = imagePath;
	}

	public byte[] getImagePath() {
		return imagePath;
	}
	
	public String getImagePathToString() {
		return new String(imagePath);
	}

	public void updateFromUser(User user) {
		setUserIdPrimarKey(user.getUserIdPrimarKey());
		user.updateToken(); 
		setName(user.getName());
		setEmailAddress(user.getEmailAddress());
		setPassword(user.getPassword());
		setLocation(user.getLocation());
		setUserBio(user.getUserBio());
		setImagePath(user.getImagePath());
		setCreationTimestamp(user.getCreationTimestamp());
		setLastUpdateTimestamp(user.getLastUpdateTimestamp());
		setLastAccessedTimestamp(user.getLastAccessedTimestamp());
	}
	
	public User copy() {
		User user = new User();
	    user.updateFromUser(this);
		return user;
	}

	public int getCount() {
		return count;
	}

	public void setCount(int count) {
		this.count = count;
	}
	
	public void incramentCount () {
		this.count = getCount() + 1;
	}
	
	public Set<User> getFriends() {
		return friends;
	}
	
	public void setFriends(Set<User> friends) {
		this.friends = friends;
	}
	
	public void addFriend(User user){
		getFriends().add(user);
	}
	
	public void setToken(String token){
		this.token = token.getBytes();
	}
	
	public void setToken(byte[] token){
		this.token = token;
	}

	public byte[] getToken() {
		return token;
	}
	
	public String getTokenToString() {
		return new String(token);
	}
	
	public void generateToken() {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar calendar = Calendar.getInstance();
		
		this.setToken(this.getNameToString() + this.getPasswordToString() + "$" + dateFormat.format(calendar.getTime()));
	}
	
	public void updateToken() {
		this.setToken(this.getNameToString() + this.getPasswordToString() + "$" + this.timestampFromToken(this.getTokenToString()));
	}
	
	public String timestampFromToken(String token) {
		String[] str_array = token.split("\\$");
		return str_array[1];
	}
	
	//Have not tested this fully
	public boolean isTokenValid(String token) {
		DateFormat dateFormat = new SimpleDateFormat("yyyy/MM/dd HH:mm:ss");
		Calendar currentCalendar = Calendar.getInstance();
		Calendar tokenCalendar = Calendar.getInstance();
		try {
			Date tokenDate = dateFormat.parse(this.timestampFromToken(token));
			tokenCalendar.setTime(tokenDate);
		} catch (ParseException e) {
			e.printStackTrace();
		}
		currentCalendar.add(Calendar.HOUR_OF_DAY, -3);
		
		int result = tokenCalendar.getTime().compareTo(currentCalendar.getTime());
		
		if(result < 0) {
			return false;
		}
		
		else {
			return true;
		}
	}
	
}
