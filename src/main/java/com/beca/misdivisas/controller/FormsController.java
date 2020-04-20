package com.beca.misdivisas.controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Usuario;

@Controller
public class FormsController {
	
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Autowired
	private ILogRepo logRepo;
	
	@GetMapping(value = "/EnvioEfectivo")
	public String envioRemesa(Model modelo) {
		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getEmpresa().getRif();
			modelo.addAttribute("idEmpresa", id);
			registrarLog("EnvioEfectivo", "Solicitud Envio de Efectivo", "Solicitud", true);
			return "EnvioEfectivo";
		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}

	}

	@GetMapping(value = "/TraspasoEfectivo")
	public String traspasoEfectivo(Model modelo) {
		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getEmpresa().getRif();
			modelo.addAttribute("idEmpresa", id);
			registrarLog("TraspasoEfectivo", "Solicitud Traspaso de Efectivo", "Solicitud", true);
			return "TraspasoEfectivo";

		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}
	}

	@GetMapping(value = "/RetiroEfectivo")
	public String retiroEfectivo(Model modelo) {
		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getEmpresa().getRif();
			modelo.addAttribute("idEmpresa", id);
			registrarLog("RetiroEfectivo", "Solicitud Retiro de Efectivo", "Solicitud ", true);
			return "RetiroEfectivo";

		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}
	}
	
	public void registrarLog(String accion, String detalle, String opcion, boolean resultado) {
		Date date = new Date();
		Log audit = new Log();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		String ip = request.getRemoteAddr();
		HttpSession session = factory.getObject();
		Usuario us = (Usuario) session.getAttribute("Usuario");
		if (us != null) {
			audit.setIdEmpresa(us.getIdEmpresa());
			audit.setIdUsuario(us.getIdUsuario());
			audit.setNombreUsuario(us.getNombreUsuario());
		} else {
			audit.setNombreUsuario(auth.getName());
		}
		audit.setFecha(new Timestamp(date.getTime()));
		audit.setIpOrigen(ip);
		audit.setAccion(accion);
		audit.setDetalle(detalle);
		audit.setOpcionMenu(opcion);
		audit.setResultado(true);
		logRepo.save(audit);
	}
}
