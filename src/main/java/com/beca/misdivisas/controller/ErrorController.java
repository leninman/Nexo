package com.beca.misdivisas.controller;

import javax.servlet.RequestDispatcher;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class ErrorController  {
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Autowired
	private LogService logServ;
	
	/*
	 * @Override public String getErrorPath() { return "error"; }
	 * 
	 * @GetMapping("/error") public String manejoDeError(HttpServletRequest request)
	 * { Usuario usuario = (Usuario)
	 * factory.getObject().getAttribute(Constantes.USUARIO); Object status =
	 * request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
	 * 
	 * String detalle = "Código" +status.toString();
	 * logServ.registrarLog("Error de sistema", detalle, "ERROR",
	 * Util.getRemoteIp(request),usuario); return "redirect:errorPage"; }
	 * 
	 * @GetMapping("/errorPage") public String showErrorPage(HttpServletRequest
	 * request, Model model) { Usuario usuario = (Usuario)
	 * factory.getObject().getAttribute(Constantes.USUARIO);
	 * model.addAttribute(Constantes.U_SUARIO, usuario); return "error"; }
	 */
}
