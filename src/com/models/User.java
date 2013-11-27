package com.models;


import java.sql.Timestamp;

import java.util.Calendar;
import java.util.HashSet;
import java.util.Set;

import com.common.Messages;

public class User {
	private String userIdPrimarKey;
	private String token;
	private String name;
	private String emailAddress;
	private String password;
	private Timestamp creationTimestamp;
	private Timestamp lastUpdateTimestamp;
	private Timestamp lastAccessedTimestamp;
	private String location;
	private String bio;
	private byte[] image;
	private int count;
	private Set<User> friends = new HashSet<User>(0);

	public User() {
		Calendar calendar = Calendar.getInstance();
		setName(Messages.UNKNOWN);
		setEmailAddress(Messages.UNKNOWN);
		setPassword(Messages.UNKNOWN);
		setLocation(Messages.UNKNOWN);
		setBio(Messages.UNKNOWN);
		setImage(new byte[0]);
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
		this.name = name;
	}

	public String getName() {
		return name;
	}

	public void setEmailAddress(String emailAddress) {
		this.emailAddress = emailAddress;
	}

	public String getEmailAddress() {
		return emailAddress;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public String getPassword() {
		return password;
	}

	public void setLocation(String location) {
		this.location = location;
	}
	
	public String getLocation() {
		return location;
	}
	
	public void setBio(String userBio) {
		this.bio = userBio;
	}
	
	public String getBio() {
		return bio;
	}
	
	public void setImage(byte[] image) {
		this.image = image;
	}

	public byte[] getImage() {
		return image;
	}

	public void updateFromUser(User user) {
		setUserIdPrimarKey(user.getUserIdPrimarKey());
		setToken(user.getToken());
		setName(user.getName());
		setEmailAddress(user.getEmailAddress());
		setPassword(user.getPassword());
		setLocation(user.getLocation());
		setBio(user.getBio());
		setImage(user.getImage());
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
		this.token = token;
	}

	public String getToken() {
		return token;
	}
}
