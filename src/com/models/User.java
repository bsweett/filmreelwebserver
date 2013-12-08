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
	private int count;
	private char gender;
	private int popularity;
	private int reelCount;
	private String displayPicturePath;
	private Set<User> friends = new HashSet<User>(0);
	private Set<Inbox> inbox = new HashSet<Inbox>(0);

	public User() {
		Calendar calendar = Calendar.getInstance();
		setName(Messages.UNKNOWN);
		setEmailAddress(Messages.UNKNOWN);
		setPassword(Messages.UNKNOWN);
		setDisplayPicturePath("default");
		setLocation(Messages.UNKNOWN);
		setReelCount(0);
		setBio(Messages.UNKNOWN);
		setCount(0);
		setCreationTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setLastUpdateTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setLastAccessedTimestamp(new Timestamp(calendar.getTimeInMillis()));
		setToken(Messages.UNKNOWN);
		setGender('U');
		setPopularity(0);
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
		if(friends.size() == 0)
			popularity = 0;
		else if(friends.size() < 3)
			popularity = 1;
		else if(friends.size() < 6)
			popularity = 2;
		else if(friends.size() < 9)
			popularity = 3;
		else if(friends.size() < 12)
			popularity = 4;
		else
			popularity = 5;
	}
	
	public Set<Inbox> getInbox() {
		return inbox;
	}
	
	public void setInbox(Set<Inbox> inbox) {
		this.inbox = inbox;
	}
	
	public void addInbox(Inbox inbox){
		getInbox().add(inbox);
	}
	
	public void setToken(String token){
		this.token = token;
	}

	public String getToken() {
		return token;
	}

	public char getGender() {
		return gender;
	}

	public void setGender(char gender) {
		this.gender = gender;
	}

	public int getPopularity() {
		return popularity;
	}

	public void setPopularity(int popularity) {
		this.popularity = popularity;
	}
	public int getReelCount() {
		return reelCount;
	}
	public void setReelCount(int reelCount) {
		this.reelCount = reelCount;
	}
	public void incrementReelCount() {
		this.reelCount = this.reelCount + 1;
	}
	
	public String getDisplayPicturePath() {
		return displayPicturePath;
	}
	public void setDisplayPicturePath(String path) {
		this.displayPicturePath = path;
	}
}
