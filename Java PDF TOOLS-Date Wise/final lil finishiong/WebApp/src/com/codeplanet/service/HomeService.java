package com.codeplanet.service;

import java.sql.SQLException;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.codeplanet.model.User;
import com.codeplanet.dao.HomeDao;



@Service
public class HomeService {
@Autowired
HomeDao homedao;
	public void insertInfo(User user) throws SQLException {
		// TODO Auto-generated method stub
		System.out.println("Service first");
		homedao.insertInfo(user);
		System.out.println("Service last");
		
	}
	public String getInfo() {
		// TODO Auto-generated method stub
		return homedao.getInfo();
	}
	public User getUser() {
		// TODO Auto-generated method stub
		return homedao.getUser();
	}
}
