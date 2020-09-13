package com.codeplanet.model;

public class User {
	private String userName;
	private String mobile;
	private String email;
	private String Password;
	public String userFileString;
	public int pages;
	public String getUserFileString() {
		return userFileString;
	}
	public void setUserFileString(String userFileString) {
		this.userFileString = userFileString;
	}
	public String getUserName() {
		return userName;
	}
	public void setUserName(String userName) {
		this.userName = userName;
	}
	public String getMobile() {
		return mobile;
	}
	public void setMobile(String mobile) {
		this.mobile = mobile;
	}
	public int getPages() {
		return pages;
	}

	public void setPages(int pages) {
		this.pages = pages;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getPassword() {
		return Password;
	}
	public void setPassword(String password) {
		Password = password;
	}
}
