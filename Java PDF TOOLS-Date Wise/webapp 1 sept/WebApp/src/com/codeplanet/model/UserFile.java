package com.codeplanet.model;

import org.springframework.web.multipart.MultipartFile;

public class UserFile {
	String name;
	MultipartFile userfile;
	MultipartFile[] userfiles;
	public String getName() {
		return name;
	}
	public MultipartFile[] getUserfiles() {
		return userfiles;
	}
	public void setUserfiles(MultipartFile[] userfiles) {
		this.userfiles = userfiles;
	}
	public void setName(String name) {
		this.name = name;
	}
	public MultipartFile getUserfile() {
		return userfile;
	}
	public void setUserfile(MultipartFile userfile) {
		this.userfile = userfile;
	}
	
}
