package com.beca.misdivisas.controller;

import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.jpa.Rol;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.jpa.UsuarioRol;
import com.beca.misdivisas.model.Login;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class HomeController {

	@Autowired
	private IEmpresaRepo empresaRepository;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private MenuService menuService;

	@GetMapping(value = "/")
	public String home() {
		return Constantes.LOGIN;
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		return Constantes.LOGIN;
	}

	@PostMapping(value = "/login")
	public String main() {
		return Constantes.MAIN;
	}

	@PostMapping(value = "/mainBECA")
	public String mainBECA(Login login, Model model) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();

		Usuario usuario = new Usuario();

		usuario.setNombreUsuario(auth.getName());
		usuario.setEmpresa(empresaRepository.findById(login.getEmpresa().getIdEmpresa().intValue()));
		usuario.setIdEmpresa(login.getEmpresa().getIdEmpresa());
		usuario.setTipoUsuario(Constantes.USUARIO_INTERNO);
		usuario.setContrasena1("local");
		Rol rol = new Rol();
		rol.setRol(Constantes.ROL_ADMIN_BECA);
		UsuarioRol urol = new UsuarioRol();
		urol.setRol(rol);
		List<UsuarioRol> usuarioRols = new ArrayList<>();
		usuarioRols.add(urol);
		usuario.setUsuarioRols(usuarioRols);
		factory.getObject().removeAttribute(Constantes.USUARIO);
		factory.getObject().setAttribute(Constantes.USUARIO, usuario);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		HttpSession session = factory.getObject();
		logServ.registrarLog("Acceso al sistema", "Ingreso", Constantes.LOGIN, Util.getRemoteIp(request),
				(Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.MAIN;
	}

	@GetMapping(value = "/main")
	public String main_href(Login login, Model model) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		
		if (usuario != null) {
			return Constantes.MAIN;
		} else {
			login.setEmpresas(empresaRepository.findAllOrderByName());
			model.addAttribute(Constantes.LOGIN, login);
			return "loginBECA";
		}
	}

	@GetMapping(value = "/changeCompany")
	public String cambioDeEmpresa(Login login, Model model) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		login.setEmpresas(empresaRepository.findAllOrderByName());
		model.addAttribute(Constantes.LOGIN, login);
		String detalle = MessageFormat.format(Constantes.TEXTO_CAMBIO_EMPRESA, usuario.getEmpresa().getEmpresa());
		logServ.registrarLog(Constantes.CAMBIO_EMPRESA, detalle, Constantes.CAMBIO_EMPRESA, Util.getRemoteIp(request), usuario);

		return "loginBECA";
	}

	@GetMapping(value = "/grafico")
	public String grafico(Model model) {

		return "grafico";
	}

	@GetMapping("/index")
	public String index() {
		return "index";
	}
	
}