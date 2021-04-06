package com.beca.misdivisas.security.config;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.web.authentication.logout.SecurityContextLogoutHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.beca.misdivisas.interfaces.ILogRepo;

import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;

@Component
@Controller
public class CustomLogoutHandler extends SecurityContextLogoutHandler {

private ILogRepo repo;
	public CustomLogoutHandler(ILogRepo repo) {
		super();
		this.repo=repo;
	}

	@Override
	public void logout(HttpServletRequest request, HttpServletResponse response, Authentication auth) {
		HttpSession sesion = request.getSession();
		LogService log = new LogService();
		log.registrar(sesion, request, repo, Constantes.LOGOUT, Constantes.LOGOUT);

	}
}
