package com.beca.misdivisas.controller;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class FormsController {

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private MenuService menuService;

	@GetMapping(value = "/EnvioEfectivo")
	public String envioRemesa(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		if (usuario.getContrasena1() != null)
			modelo.addAttribute(Constantes.U_SUARIO, usuario);

		int id = usuario.getEmpresa().getRif();
		modelo.addAttribute(Constantes.ID_EMPRESA, id);

		HttpSession session = factory.getObject();
		logServ.registrarLog("EnvioEfectivo", "Solicitud Envio de Efectivo", "Solicitud", Util.getRemoteIp(request),
				(Usuario) session.getAttribute(Constantes.USUARIO));

		return "EnvioEfectivo";
	}

	@GetMapping(value = "/TraspasoEfectivo")
	public String traspasoEfectivo(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES,menuService.getMenu(usuario.getIdUsuario()));

		if (usuario.getContrasena1() != null)
			modelo.addAttribute(Constantes.U_SUARIO, usuario);

		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		HttpSession session = factory.getObject();
		logServ.registrarLog("TraspasoEfectivo", "Solicitud Traspaso de Efectivo", "Solicitud",
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return "TraspasoEfectivo";
	}

	@GetMapping(value = "/RetiroEfectivo")
	public String retiroEfectivo(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES,menuService.getMenu(usuario.getIdUsuario()));
		if (usuario.getContrasena1() != null)
			modelo.addAttribute(Constantes.U_SUARIO, usuario);

		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		HttpSession session = factory.getObject();
		logServ.registrarLog("RetiroEfectivo", "Solicitud Retiro de Efectivo", "Solicitud ", Util.getRemoteIp(request),
				(Usuario) session.getAttribute(Constantes.USUARIO));

		return "RetiroEfectivo";
	}
}
