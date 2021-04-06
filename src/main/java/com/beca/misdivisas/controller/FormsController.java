package com.beca.misdivisas.controller;

import java.text.MessageFormat;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.services.LogService;
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
	
	@Value("${ldap.domain}")
	private String dominio;

	@GetMapping(value = "/envioEfectivo")
	public String envioRemesa(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();

		if (!dominio.equalsIgnoreCase(Constantes.DOMINIO_PROD))
			request.setAttribute(Constantes.DOMINIO,0);
		else
			request.setAttribute(Constantes.DOMINIO,1);
		
		usuarioModel.setUsuario(usuario);
		if (usuario.getContrasena1() != null)
			modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);

		int id = usuario.getEmpresa().getRif();
		modelo.addAttribute(Constantes.ID_EMPRESA, id);

		logServ.registrarLog(Constantes.OP_ENV_EFECTIVO, MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Envio"), Constantes.OP_SOLICITUD, true,
				Util.getRemoteIp(request), usuario);

		return Constantes.OP_ENV_EFECTIVO;
	}

	@GetMapping(value = "/traspasoEfectivo")
	public String traspasoEfectivo(Model model) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		if (!dominio.equalsIgnoreCase(Constantes.DOMINIO_PROD))
			request.setAttribute(Constantes.DOMINIO,0);
		else
			request.setAttribute(Constantes.DOMINIO,1);
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);	

		model.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		logServ.registrarLog(Constantes.OP_TRAS_EFECTIVO, MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Traspaso"), Constantes.OP_SOLICITUD,
				true, Util.getRemoteIp(request), usuario);

		return Constantes.OP_TRAS_EFECTIVO;
	}

	@GetMapping(value = "/retiroEfectivo")
	public String retiroEfectivo(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		if (!dominio.equalsIgnoreCase(Constantes.DOMINIO_PROD))
			request.setAttribute(Constantes.DOMINIO,0);
		else
			request.setAttribute(Constantes.DOMINIO,1);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		
		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());

		logServ.registrarLog(Constantes.OP_RET_EFECTIVO, MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_SOLICITUD , true,
				Util.getRemoteIp(request), usuario);

		return Constantes.OP_RET_EFECTIVO;
	}
}
