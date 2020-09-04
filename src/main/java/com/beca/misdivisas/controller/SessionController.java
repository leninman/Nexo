package com.beca.misdivisas.controller;

import javax.servlet.http.HttpServletRequest;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class SessionController {
	
	@PostMapping(value = "/activateSession")
	@ResponseBody
	public void activateSession(HttpServletRequest request) {
		request.getSession();
	}

}
