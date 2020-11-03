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
	
	@Autowired
	private IUsuarioRolRepo uRolRepo;

	@GetMapping("/usuarioHome")
	public String userIndex(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		int idEmpresa = usuario.getIdEmpresa();
		List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(idEmpresa, Constantes.ACTIVO);
		if (usuarios.isEmpty())
			usuarios = null;
		model.addAttribute(Constantes.USUARIOS, usuarios);
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.TEXTO_ADMINISTRAR_USUARIO,
				Constantes.TEXTO_ADMINISTRAR_USUARIO, Util.getRemoteIp(request), usuario);

		return Constantes.USUARIO_MAIN;
	}


	@GetMapping("usuarioListar")
	public String showUpdateForm(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		model.addAttribute(Constantes.USUARIOS, usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(idEmpresa, Constantes.ACTIVO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		return Constantes.USUARIO_MAIN;
	}

	@GetMapping("resultadoCambio")
	public String showResultado(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCase(usuario.getIdEmpresa(), Constantes.ACTIVO);
		if (usuarios.isEmpty())
			usuarios = null;
		model.addAttribute(Constantes.USUARIOS, usuarios);

		return Constantes.VER_RESULTADO;

	}
	
	@GetMapping("/usuarioMainAgregar")
	public String showAddUser(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuarioN = new Usuario();
		usuarioN.setHabilitado(false);
		usuarioN.setEmpresa(usuario.getEmpresa());
		
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuarioN);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));

		List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO);
		Collections.sort(roles);
		model.addAttribute(Constantes.ROLES, roles);
		
		List<Rol> rolesSelect = null;
		model.addAttribute(Constantes.ROLES_SELECT, rolesSelect);
		
		usuarioN.setUsuarioRols(new ArrayList<UsuarioRol>());
		
		return Constantes.USUARIO_ADD;
	}
	
	@PostMapping("usuarioAgregar")
	public String addUsuario(@Valid com.beca.misdivisas.model.Usuario usuario, BindingResult result, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		if (usuarioRepository.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(usuario.getUsuario().getNombreUsuario(), Constantes.ACTIVO) != null)
			result.rejectValue(Constantes.NOMBRE_USUARIO, "", Constantes.MENSAJE_VAL_USUARIO);
		else if (!sonValidosCaracteres(usuario.getUsuario().getNombreUsuario()))
			result.rejectValue(Constantes.NOMBRE_USUARIO, "", Constantes.MENSAJE_VAL_USUARIO_1);

		if (usuario.getUsuario().getContrasena().length() < 8 || usuario.getUsuario().getContrasena().length() > 20)
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuario.getUsuario().getContrasena()))
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);

		else if (!usuario.getUsuario().getContrasena().equals(usuario.getUsuario().getRepitaContrasena()))
			result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);
		usuario.getUsuario().setEmpresa(us.getEmpresa());
		
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));

		if (result.hasErrors()) {
			List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(us.getIdEmpresa(), Constantes.ACTIVO);			
			List<Rol> rolesSelect = usuario.getPerfiles();
			
			if(roles != null && rolesSelect != null)
				roles.removeAll(rolesSelect);

			model.addAttribute(Constantes.ROLES, roles);
			model.addAttribute(Constantes.ROLES_SELECT, rolesSelect);
			return Constantes.USUARIO_ADD;
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuario.getUsuario().setFechaActualizacionContrasena(ts);
		int idEmpresa = us.getIdEmpresa();
		usuario.getUsuario().setIdEmpresa(idEmpresa);
		encoder2 = new BCryptPasswordEncoder();
		usuario.getUsuario().setContrasena(encoder2.encode(usuario.getUsuario().getContrasena()));
		usuario.getUsuario().setEstado(Constantes.ACTIVO);

		usuarioRepository.save(usuario.getUsuario());

		UsuarioRol usuarioRol = new UsuarioRol();

		if (us.getTipoUsuario().equalsIgnoreCase("Interno"))
			usuarioRol.setIdRol(rolRepository.findByRol(Constantes.ROL_ADMIN).getIdRol());
		else
			usuarioRol.setIdRol(rolRepository.findByRol(Constantes.ROL_CONSULTOR).getIdRol());

		usuarioRol.setIdUsuario(usuarioRepository
				.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(usuario.getUsuario().getNombreUsuario(), Constantes.ACTIVO).getIdUsuario());
		usuarioRolRepository.save(usuarioRol);

		String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_CREAR, usuario.getUsuario().getNombreUsuario(),
				usuario.getUsuario().getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_CREAR,
				Util.getRemoteIp(request), us);

		return "redirect:usuarioListar?success";

	}

	@PostMapping("usuarioEditar")
	public String usuarioEditarForm(@RequestParam("usuarioId") int id, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		Usuario usuarioRep = usuarioRepository.findById(id);
		
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuarioRep);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		if (!usuarioRep.getIdEmpresa().equals(idEmpresa))
			return "redirect:/usuarioListar?error";

		else {
			usuarioRep.setContrasena(null);			
			List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(((Usuario) factory.getObject().getAttribute(Constantes.USUARIO)).getIdEmpresa(), Constantes.ACTIVO);			
			
			List<Rol> rolesSelect = rolRepo.findByIdUsuarioAndEstado(id, Constantes.ACTIVO);
			
			if(roles != null && rolesSelect != null)
				roles.removeAll(rolesSelect);

			model.addAttribute(Constantes.ROLES, roles);
			model.addAttribute(Constantes.ROLES_SELECT, rolesSelect);
			return "updateUsuario";
		}
	}

	@PostMapping("usuarioUpdate")
	public String updateUsuario(@RequestParam("usuarioId") int id, @Valid com.beca.misdivisas.model.Usuario usuarioRep, BindingResult result, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		usuarioRep.getUsuario().setEmpresa(us.getEmpresa());
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));
		if (usuarioRep.getUsuario().getContrasena().length() < 8 || usuarioRep.getUsuario().getContrasena().length() > 20)
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuarioRep.getUsuario().getContrasena()))
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);
		else if (!usuarioRep.getUsuario().getContrasena().equals(usuarioRep.getUsuario().getRepitaContrasena()))
			result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);

		Usuario usuario = usuarioRepository.findById(id);

		usuario.setContrasena5(usuario.getContrasena4());
		usuario.setContrasena4(usuario.getContrasena3());
		usuario.setContrasena3(usuario.getContrasena2());
		usuario.setContrasena2(usuario.getContrasena1());
		usuario.setContrasena1(usuario.getContrasena());
		usuario.setContrasena(usuarioRep.getUsuario().getContrasena());

		usuarioRep.getUsuario().setNombreUsuario(usuario.getNombreUsuario());

		usuarioRep.getUsuario().setIdUsuario(id);
		model.addAttribute(Constantes.U_SUARIO, usuarioRep);

		if (!esValidaUltimasContrasenas(usuario))
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_4);

		if (result.hasErrors()) {
			List<Rol> roles = rolRepo.findByIdEmpresaAndEstado(((Usuario) factory.getObject().getAttribute(Constantes.USUARIO)).getIdEmpresa(), Constantes.ACTIVO);			
			List<Rol> rolesSelect = usuarioRep.getPerfiles();
			
			if(roles != null && rolesSelect != null)
				roles.removeAll(rolesSelect);

			model.addAttribute(Constantes.ROLES, roles);
			model.addAttribute(Constantes.ROLES_SELECT, rolesSelect);
			
			return "updateUsuario";
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuarioRep.getUsuario().setFechaActualizacionContrasena(ts);

		encoder2 = new BCryptPasswordEncoder();
		usuarioRep.getUsuario().setContrasena(encoder2.encode(usuarioRep.getUsuario().getContrasena()));

		usuario.setContrasena(usuarioRep.getUsuario().getContrasena());
		usuario.setNombreCompleto(usuarioRep.getUsuario().getNombreCompleto());
		usuario.setHabilitado(usuarioRep.getUsuario().getHabilitado() == null ? false : usuarioRep.getUsuario().getHabilitado());
		usuario.setEmail(usuarioRep.getUsuario().getEmail());
		usuario.setFechaActualizacionContrasena(usuarioRep.getUsuario().getFechaActualizacionContrasena());

		usuarioRepository.save(usuario);
		
		//eliminamos los perfiles asociados previos
		uRolRepo.deleteByUserId(usuario.getIdUsuario());
		//agregamos los perfiles seleccionados
		  UsuarioRol usuarioRol = new UsuarioRol();
		  
		  for (Rol rol : usuarioRep.getPerfiles()) {
			  usuarioRol.setIdRol(rol.getIdRol());
			  usuarioRol.setIdUsuario(usuario.getIdUsuario());
			  
			  uRolRepo.save(usuarioRol);
			  usuarioRol = new UsuarioRol();			
		}

		String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_EDICION, usuario.getNombreUsuario(),
				usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_EDICION,
				Util.getRemoteIp(request), us);

		return "redirect:/usuarioHome?success";
	}

	
	@GetMapping("/changePassword")
	public String changePassword(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		return Constantes.CHANGE_PASSWORD;
	}
	
	@PostMapping("usuarioChange")
	public String cambioContrasenaUsuario(@RequestParam("usuarioId") int id, @Valid Usuario usuarioRep,
			BindingResult result, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuario = usuarioRepository.findById(id);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(us.getIdUsuario()));
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(usuarioRep.getContrasena(), usuario.getContrasena()))
			result.rejectValue(Constantes.CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_5);
		else if (!usuarioRep.getNuevaContrasena().equals(usuarioRep.getRepitaContrasena()))
			result.rejectValue(Constantes.REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);

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
			result.rejectValue(Constantes.NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_4);

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
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.CAMBIO_CLAVE, Constantes.OP_EDICION,
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
			String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_EDICION, usuario.getNombreUsuario(),
					usuario.getIdUsuario());

			HttpSession session = factory.getObject();
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_BORRAR,
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