package com.beca.misdivisas.controller;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.interfaces.IRolRepo;
import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.interfaces.IUsuarioRolRepo;
import com.beca.misdivisas.jpa.Rol;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.jpa.UsuarioRol;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class UserController {

	@Autowired
	private IUsuarioRepo usuarioRepository;

	@Autowired
	private IRolRepo rolRepository;

	@Autowired
	private IUsuarioRolRepo usuarioRolRepository;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	private BCryptPasswordEncoder encoder2;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private IRolRepo rolRepo;

	@GetMapping("/usuarioHome")
	public String userIndex(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		int idEmpresa = usuario.getIdEmpresa();
		List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(idEmpresa, "A");
		if (usuarios.isEmpty())
			usuarios = null;
		model.addAttribute(Constantes.USUARIOS, usuarios);
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.TEXTO_ADMINISTRAR_USUARIO,
				Constantes.TEXTO_ADMINISTRAR_USUARIO, Util.getRemoteIp(request), usuario);

		return "mainUsuarios";
	}

	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		return Constantes.CHANGE_PASSWORD;
	}

	@GetMapping("/usuarioMainAgregar")
	public String showSignUpForm(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuarioN = new Usuario();
		usuarioN.setHabilitado(false);
		usuarioN.setEmpresa(usuario.getEmpresa());
		model.addAttribute(Constantes.U_SUARIO, usuarioN);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));

		List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO);
		Collections.sort(roles);
		model.addAttribute(Constantes.ROLES, roles);
		
		usuario.setUsuarioRols(new ArrayList<UsuarioRol>());
		
		return "addUsuario";
	}

	@GetMapping("usuarioListar")
	public String showUpdateForm(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		model.addAttribute(Constantes.USUARIOS, usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(idEmpresa, "A"));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		return "mainUsuarios";
	}

	@GetMapping("resultadoCambio")
	public String showResultado(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(usuario.getIdEmpresa(), "A");
		if (usuarios.isEmpty())
			usuarios = null;
		model.addAttribute(Constantes.USUARIOS, usuarios);

		return "verResultado";

	}

	@PostMapping("usuarioAgregar")
	public String addUsuario(@Valid Usuario usuario, BindingResult result, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		if (usuarioRepository.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(usuario.getNombreUsuario(), "A") != null)
			result.rejectValue(Constantes.NOMBRE_USUARIO, "", "ya esta siendo utilizado");
		else if (!sonValidosCaracteres(usuario.getNombreUsuario()))
			result.rejectValue(Constantes.NOMBRE_USUARIO, "", "caracteres especiales validos @ . _ -");

		if (usuario.getContrasena().length() < 8 || usuario.getContrasena().length() > 20)
			result.rejectValue(Constantes.CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuario.getContrasena()))
			result.rejectValue(Constantes.CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);// % &

		else if (!usuario.getContrasena().equals(usuario.getRepitaContrasena()))
			result.rejectValue(Constantes.REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);
		usuario.setEmpresa(us.getEmpresa());
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));

		if (result.hasErrors()) {
			return "addUsuario";
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuario.setFechaActualizacionContrasena(ts);
		int idEmpresa = us.getIdEmpresa();
		usuario.setIdEmpresa(idEmpresa);
		encoder2 = new BCryptPasswordEncoder();
		usuario.setContrasena(encoder2.encode(usuario.getContrasena()));
		usuario.setEstado(Constantes.ACTIVO);

		usuarioRepository.save(usuario);

		UsuarioRol usuarioRol = new UsuarioRol();

		if (us.getTipoUsuario().equalsIgnoreCase("Interno"))
			usuarioRol.setIdRol(rolRepository.findByRol(Constantes.ROL_ADMIN).getIdRol());
		else
			usuarioRol.setIdRol(rolRepository.findByRol(Constantes.ROL_CONSULTOR).getIdRol());

		usuarioRol.setIdUsuario(usuarioRepository
				.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(usuario.getNombreUsuario(), "A").getIdUsuario());
		usuarioRolRepository.save(usuarioRol);

		String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OPERACION_CREAR, usuario.getNombreUsuario(),
				usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OPERACION_CREAR,
				Util.getRemoteIp(request), us);

		return "redirect:usuarioListar?success";

	}

	@PostMapping("usuarioEditar")
	public String showUpdateForm(@RequestParam("usuarioId") int id, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		Usuario usuarioRep = usuarioRepository.findById(id);
		model.addAttribute(Constantes.U_SUARIO, usuarioRep);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		if (!usuarioRep.getIdEmpresa().equals(idEmpresa))
			return "redirect:/usuarioListar?error";

		else {
			usuarioRep.setContrasena(null);			
			List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa(), Constantes.ACTIVO);			
			
			List<Rol> rolesSelect = rolRepo.findByIdUsuarioAndEstado(id, Constantes.ACTIVO);
			
			if(roles != null && rolesSelect != null)
				roles.removeAll(rolesSelect);
			model.addAttribute("usuario", usuarioRep);
			model.addAttribute(Constantes.ROLES, roles);
			model.addAttribute(Constantes.ROLES_SELECT, rolesSelect);
			return "updateUsuario";
		}
	}

	@PostMapping("usuarioUpdate")
	public String updateUsuario(@RequestParam("usuarioId") int id, @Valid Usuario usuarioRep, BindingResult result,
			Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		usuarioRep.setEmpresa(us.getEmpresa());
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));
		if (usuarioRep.getContrasena().length() < 8 || usuarioRep.getContrasena().length() > 20)
			result.rejectValue(Constantes.CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuarioRep.getContrasena()))
			result.rejectValue(Constantes.CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);
		else if (!usuarioRep.getContrasena().equals(usuarioRep.getRepitaContrasena()))
			result.rejectValue(Constantes.REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);

		Usuario usuario = usuarioRepository.findById(id);

		usuario.setContrasena5(usuario.getContrasena4());
		usuario.setContrasena4(usuario.getContrasena3());
		usuario.setContrasena3(usuario.getContrasena2());
		usuario.setContrasena2(usuario.getContrasena1());
		usuario.setContrasena1(usuario.getContrasena());
		usuario.setContrasena(usuarioRep.getContrasena());

		usuarioRep.setNombreUsuario(usuario.getNombreUsuario());

		usuarioRep.setIdUsuario(id);
		model.addAttribute(Constantes.U_SUARIO, usuarioRep);

		if (!esValidaUltimasContrasenas(usuario))
			result.rejectValue(Constantes.CONTRASENA, "", "no puede ser igual a las últimas 5 utilizadas");

		if (result.hasErrors()) {
			return "updateUsuario";
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuarioRep.setFechaActualizacionContrasena(ts);

		encoder2 = new BCryptPasswordEncoder();
		usuarioRep.setContrasena(encoder2.encode(usuarioRep.getContrasena()));

		usuario.setContrasena(usuarioRep.getContrasena());
		usuario.setNombreCompleto(usuarioRep.getNombreCompleto());
		usuario.setHabilitado(usuarioRep.getHabilitado() == null ? false : usuarioRep.getHabilitado());
		usuario.setEmail(usuarioRep.getEmail());
		usuario.setFechaActualizacionContrasena(usuarioRep.getFechaActualizacionContrasena());

		usuarioRepository.save(usuario);

		String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, "Editar", usuario.getNombreUsuario(),
				usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OPERACION_EDICION,
				Util.getRemoteIp(request), us);

		return "redirect:/main?success";
	}

	@PostMapping("usuarioChange")
	public String cambioContrasenaUsuario(@RequestParam("usuarioId") int id, @Valid Usuario usuarioRep,
			BindingResult result, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuario = usuarioRepository.findById(id);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(usuarioRep.getContrasena(), usuario.getContrasena()))
			result.rejectValue(Constantes.CONTRASENA, "", "no coincide con la actual");
		else if (!usuarioRep.getNuevaContrasena().equals(usuarioRep.getRepitaContrasena()))
			result.rejectValue(Constantes.REPITA_CONTRASENA, "", "debe coincidir con la nueva");

		if (usuarioRep.getNuevaContrasena().length() < 8 || usuarioRep.getNuevaContrasena().length() > 20)
			result.rejectValue(Constantes.NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuarioRep.getNuevaContrasena()))
			result.rejectValue(Constantes.NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);

		usuario.setContrasena5(usuario.getContrasena4());
		usuario.setContrasena4(usuario.getContrasena3());
		usuario.setContrasena3(usuario.getContrasena2());
		usuario.setContrasena2(usuario.getContrasena1());
		usuario.setContrasena1(usuario.getContrasena());
		usuario.setContrasena(usuarioRep.getNuevaContrasena());
		usuarioRep.setEmpresa(usuario.getEmpresa());
		usuarioRep.setIdUsuario(id);
		model.addAttribute(Constantes.U_SUARIO, usuarioRep);
		if (!esValidaUltimasContrasenas(usuario))
			result.rejectValue(Constantes.NUEVA_CONTRASENA, "", "no puede ser igual a las últimas 5 utilizadas");

		if (result.hasErrors()) {
			return Constantes.CHANGE_PASSWORD;
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuarioRep.setFechaActualizacionContrasena(ts);

		encoder2 = new BCryptPasswordEncoder();
		usuarioRep.setContrasena(encoder2.encode(usuarioRep.getNuevaContrasena()));

		usuario.setContrasena(usuarioRep.getContrasena());

		usuario.setHabilitado(true);
		usuario.setFechaActualizacionContrasena(usuarioRep.getFechaActualizacionContrasena());
		usuario.setTipoUsuario(us.getTipoUsuario());

		usuarioRepository.save(usuario);
		factory.getObject().removeAttribute(Constantes.USUARIO);
		factory.getObject().setAttribute(Constantes.USUARIO, usuario);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		factory.getObject().removeAttribute(Constantes.CAMBIO_C);
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.CAMBIO_CLAVE, Constantes.OPERACION_EDICION,
				Util.getRemoteIp(request), us);

		return "redirect:/main?success";
	}

	@PostMapping("usuarioEliminar")
	public String deleteUsuario(@RequestParam("usuarioId") int id, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		Usuario usuario = usuarioRepository.findById(id);

		int idEmpresa = us.getIdEmpresa();
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));

		if (!usuario.getIdEmpresa().equals(idEmpresa))

			return "redirect:/usuarioListar?error";

		else {

			usuario.setEstado("I");
			usuarioRepository.save(usuario);
			String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, "Eliminar", usuario.getNombreUsuario(),
					usuario.getIdUsuario());

			HttpSession session = factory.getObject();
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OPERACION_BORRAR,
					Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

			return "redirect:/usuarioListar?success";
		}
	}

	private static boolean esValidaUltimasContrasenas(Usuario usuario) {
		boolean result = true;

		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (encoder.matches(usuario.getContrasena(), usuario.getContrasena1()))
			result = false;
		if (encoder.matches(usuario.getContrasena(), usuario.getContrasena2()))
			result = false;
		if (encoder.matches(usuario.getContrasena(), usuario.getContrasena3()))
			result = false;
		if (encoder.matches(usuario.getContrasena(), usuario.getContrasena4()))
			result = false;
		if (encoder.matches(usuario.getContrasena(), usuario.getContrasena5()))
			result = false;
		return result;
	}

	private static boolean esValidaContrasena(String contrasena) {
		boolean result = false;
		char clave;
		byte contNumero = 0, contLetraMay = 0, contLetraMin = 0, contEspecial = 0;
		for (byte i = 0; i < contrasena.length(); i++) {
			clave = contrasena.charAt(i);
			String passValue = String.valueOf(clave);
			if (passValue.matches("[A-Z]"))
				contLetraMay++;
			else if (passValue.matches("[a-z]"))
				contLetraMin++;
			else if (passValue.matches("[0-9]"))
				contNumero++;
			else if (passValue.matches("[!@#$*._]"))
				contEspecial++;

		}

		if (contLetraMay > 0 && contLetraMin > 0 && contNumero > 0 && contEspecial > 0
				&& contrasena.length() == (contLetraMay + contLetraMin + contNumero + contEspecial))
			result = true;
		return result;

	}

	private static boolean sonValidosCaracteres(String texto) {
		char clave;
		byte contNumero = 0, contLetraMay = 0, contLetraMin = 0, contEspecial = 0;
		for (byte i = 0; i < texto.length(); i++) {
			clave = texto.charAt(i);
			String passValue = String.valueOf(clave);
			if (passValue.matches("[A-Z]"))
				contLetraMay++;
			else if (passValue.matches("[a-z]"))
				contLetraMin++;
			else if (passValue.matches("[0-9]"))
				contNumero++;
			else if (passValue.matches("[@._-]"))
				contEspecial++;

		}

		if (texto.length() == (contLetraMay + contLetraMin + contNumero + contEspecial))
			return true;
		else
			return false;
	}
}