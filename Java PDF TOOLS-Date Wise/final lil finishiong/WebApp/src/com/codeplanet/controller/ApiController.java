package com.codeplanet.controller;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import com.codeplanet.model.User;
import com.codeplanet.service.HomeService;

@RestController
public class ApiController {
@Autowired
HomeService hs;
	@GetMapping(value="/apiget")
	public 	User api1(User u1) {
		System.out.println(u1.getEmail());
		User u=hs.getUser();
		return u;
	}
	
	@PostMapping(value="/apipost")
	public 	User api2(@RequestBody User u1) {
		System.out.println(u1.getEmail());
		User u=hs.getUser();
		return u;
	}
}
