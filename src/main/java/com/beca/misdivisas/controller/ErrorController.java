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
public class ErrorController implements org.springframework.boot.web.servlet.error.ErrorController {
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Autowired
	private LogService logServ;
	
	public String getErrorPath() {
		return Constantes.ERROR;
	}
	
    @GetMapping("/error")
    public String manejoDeError(HttpServletRequest request, Model modelo) {
    	Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		Object status = request.getAttribute(RequestDispatcher.ERROR_STATUS_CODE);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		
		String detalle = "Código" +status.toString();
		logServ.registrarLog("Error de sistema", detalle, "ERROR", true,Util.getRemoteIp(request), usuario);
		return "redirect:errorPage";
    }
    
    @GetMapping("/errorPage")
    public String showErrorPage(HttpServletRequest request, Model modelo) {
    	modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
    	Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);		
		return Constantes.ERROR;
    }
}
