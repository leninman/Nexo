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
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.interfaces.IPerfilRepo;
import com.beca.misdivisas.interfaces.IPerfilUsuarioRepo;
import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.jpa.Perfil;
import com.beca.misdivisas.jpa.PerfilUsuario;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;
import com.beca.misdivisas.util.ValidationUtils;

@Controller
public class UserController {

	@Autowired
	private IUsuarioRepo usuarioRepository;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	private BCryptPasswordEncoder encoder2;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private IPerfilRepo perfilRepo;
	
	@Autowired
	private IPerfilUsuarioRepo perfilUsuarioRepo;
	
	@GetMapping("/usuarioHome")
	public String usuarioHome(Model modelo) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		try {			
			com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
			usuarioModel.setUsuario(usuario);
			modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
			modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
			
			int idEmpresa = usuario.getIdEmpresa();
			List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCaseOrderByNombreUsuarioAsc(idEmpresa, Constantes.ACTIVO);
			if (usuarios.isEmpty())
				usuarios = null;
			modelo.addAttribute(Constantes.USUARIOS, usuarios);
			
			if(usuario!=null && usuario.getIdUsuario()!=null) { //externo
				modelo.addAttribute(Constantes.EXISTE_AD, false);
			}else {
				//modelo.addAttribute(Constantes.EXISTE_AD, usuarioRepository.findByEmpresaAndEstadoAndRol(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.ID_ROL_ADMIN)!=null);
				List<Usuario> usAdmin = usuarioRepository.findAllByEmpresaInPerfil(usuario.getIdEmpresa(), "ADMIN");
				
				modelo.addAttribute(Constantes.EXISTE_AD, usAdmin != null && usAdmin.size() > 0);
			}			
			
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.U_SUARIOS, true, Util.getRemoteIp(request), usuario);

		}catch (Exception e) {			
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, e.getLocalizedMessage(), Constantes.U_SUARIOS, false, Util.getRemoteIp(request), usuario);
		}
		return Constantes.USUARIO_MAIN;
	}
	
	@GetMapping("usuarioListar")
	public String listadoUsuario(Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		model.addAttribute(Constantes.USUARIOS, usuarioRepository.findByIdEmpresaAndEstadoIgnoreCaseOrderByNombreUsuarioAsc(idEmpresa, Constantes.ACTIVO));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);	
		return Constantes.USUARIO_MAIN;
	}

	@GetMapping("resultadoCambio")
	public String showResultado(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);

		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		List<Usuario> usuarios = usuarioRepository.findByIdEmpresaAndEstadoIgnoreCaseOrderByNombreUsuarioAsc(usuario.getIdEmpresa(), Constantes.ACTIVO);
		if (usuarios.isEmpty())
			usuarios = null;
		model.addAttribute(Constantes.USUARIOS, usuarios);

		return Constantes.VER_RESULTADO;

	}

	@GetMapping("/usuarioMainAgregar")
	public String agregarUsuario(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		
		try {
			Usuario usuarioN = new Usuario();
			usuarioN.setPerfilUsuarios(new ArrayList<PerfilUsuario>());
			usuarioN.setHabilitado(false);
			usuarioN.setEmpresa(usuario.getEmpresa());
			
			com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
			usuarioModel.setUsuario(usuarioN);
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);			
			
			List<Perfil> perfiles = perfilRepo.findByIdEmpresaEstadoTipoEditable(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.TIPO_PERFIL_E, true, Constantes.PERFIL_ADMINISTRADOR.toUpperCase());
			Collections.sort(perfiles);
			model.addAttribute(Constantes.PERFILES, perfiles);
			
			logServ.registrarLog("Crear Usuario", Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.U_SUARIOS, true, Util.getRemoteIp(request), usuario);

		} catch (Exception e) {
			e.printStackTrace();
		}
		
		return Constantes.USUARIO_ADD;
	}
	
	@Transactional
	@PostMapping("usuarioAgregar")
	public String addUsuario(@Valid com.beca.misdivisas.model.Usuario usuarioModel, BindingResult result, Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuario= usuarioModel.getUsuario();

		try {
			if(usuario.getNombreUsuario()==null || usuario.getNombreUsuario().trim().isEmpty())
				result.rejectValue(Constantes.NOMBRE_USUARIO, "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if (usuarioRepository.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(usuario.getNombreUsuario(), Constantes.ACTIVO) != null)
				result.rejectValue(Constantes.NOMBRE_USUARIO, "", Constantes.MENSAJE_VAL_USUARIO);
			else if (!sonValidosCaracteres(usuario.getNombreUsuario()))
				result.rejectValue(Constantes.NOMBRE_USUARIO, "", Constantes.MENSAJE_VAL_USUARIO_1);
			
			if(usuario.getContrasena()==null || usuario.getContrasena().trim().isEmpty())
				result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if (usuario.getContrasena().length() < 8 || usuario.getContrasena().length() > 20)
				result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
			else if (!esValidaContrasena(usuario.getContrasena()))
				result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);
			else if (!usuario.getContrasena().equals(usuario.getRepitaContrasena()))
				result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);
			
			if(usuario.getRepitaContrasena()==null || usuario.getRepitaContrasena().trim().isEmpty())
				result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_REQUERIDO);
			
			if(usuario.getEmail() ==null || usuario.getEmail().trim().isEmpty())
				result.rejectValue("usuario.email", "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if(!ValidationUtils.isValidEmail(usuario.getEmail()))
				result.rejectValue("usuario.email", "", "inv\u00E1lido");
			
			if(usuario.getNombreCompleto() ==null || usuario.getNombreCompleto().trim().isEmpty())
				result.rejectValue("usuario.nombreCompleto", "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if(!usuario.getNombreCompleto().matches("[a-zA-Z0-9\\ \\Á\\á\\É\\é\\Í\\í\\Ó\\ó\\Ú\\ú\\Ñ\\ñ]*")) 
				result.rejectValue("usuario.nombreCompleto", "", "contiene caracteres no v\u00E1lidos");
			
			usuario.setEmpresa(us.getEmpresa());
			
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);

			if (result.hasErrors()) {
				List<Perfil> perfiles = perfilRepo.findByIdEmpresaEstadoTipoEditable(us.getIdEmpresa(), Constantes.ACTIVO, Constantes.TIPO_PERFIL_E, true, Constantes.PERFIL_ADMINISTRADOR.toUpperCase());
				perfiles.removeAll(usuarioModel.getPerfiles());
				Collections.sort(perfiles);
				model.addAttribute(Constantes.PERFILES, perfiles);
				return Constantes.USUARIO_ADD;
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
			usuario.setHabilitado(usuario.getHabilitado());
			usuarioRepository.save(usuario);
			
			
			
			//le asigno el rol por defecto y el perfil Admin
			
			/*UsuarioRol urol= new UsuarioRol();
			
			urol.setIdUsuario(usuario.getIdUsuario());
			urol.setUsuario(usuario);
			if(us!=null && us.getIdUsuario()!=null) { 
				urol.setIdRol(2);
				urol.setRol(rolRepo.findById(2).get());
			}else {
				PerfilUsuario perfilUsuario = new PerfilUsuario();
				Perfil perfil = perfilRepo.findByNombrePerfilAndIdEmpresaEstado(Constantes.PERFIL_ADMINISTRADOR, idEmpresa, Constantes.ACTIVO);
							
				perfilUsuario.setPerfil(perfil);
				perfilUsuario.setIdPerfil(perfil.getIdPerfil());
				perfilUsuario.setUsuario(usuario);
				perfilUsuario.setIdUsuario(usuario.getIdUsuario());				
				perfilUsuarioRepo.save(perfilUsuario);
				
				urol.setIdRol(1);
				urol.setRol(rolRepo.findById(1).get());
			}				
			uRolRepo.save(urol);*/
			PerfilUsuario perfilUsuario = new PerfilUsuario();
			String perfilesAsignados = "";
			if(us!=null && us.getIdUsuario()==null) {
				perfilUsuario = new PerfilUsuario();
				Perfil perfil = perfilRepo.findByNombrePerfilAndIdEmpresaEstado(Constantes.PERFIL_ADMINISTRADOR, idEmpresa, Constantes.ACTIVO);
							
				perfilUsuario.setPerfil(perfil);
				perfilUsuario.setIdPerfil(perfil.getIdPerfil());
				perfilUsuario.setUsuario(usuario);
				perfilUsuario.setIdUsuario(usuario.getIdUsuario());				
				perfilUsuarioRepo.save(perfilUsuario);
				perfilesAsignados = perfil!= null ? perfil.getIdPerfil().toString() : "";
			}else {	
				for (Perfil p : usuarioModel.getPerfiles()) {
					if(!perfilesAsignados.equals(""))
						perfilesAsignados = perfilesAsignados + " - ";
					perfilesAsignados = perfilesAsignados + p!= null ? p.getIdPerfil().toString() : "";
					perfilUsuario.setIdPerfil(p.getIdPerfil());
					perfilUsuario.setPerfil(p);
					perfilUsuario.setIdUsuario(usuario.getIdUsuario());
					perfilUsuario.setUsuario(usuario);
					perfilUsuarioRepo.save(perfilUsuario);
					perfilUsuario = new PerfilUsuario();
				}
			}
			String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_CREAR, usuario.getIdUsuario(), usuario.getNombreUsuario(),
					us.getEmpresa().getEmpresa(), perfilesAsignados);
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_CREAR, true, Util.getRemoteIp(request), us);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, e.getLocalizedMessage(), Constantes.OP_CREAR, true, Util.getRemoteIp(request), us);
			return Constantes.ERROR;
		}

		return "redirect:usuarioListar?success";
	}

	@PostMapping("usuarioEditar")
	public String usuarioMainEditar(@RequestParam("usuarioId") int id, Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		int idEmpresa = usuario.getIdEmpresa();
		Usuario usuarioRep = usuarioRepository.findById(id);
		
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		for (PerfilUsuario perfilU : usuarioRep.getPerfilUsuarios()) {
			usuarioModel.getPerfiles().add(perfilU.getPerfil());
		}
		
		List<Perfil> perfiles = perfilRepo.findByIdEmpresaEstadoTipoEditable(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.TIPO_PERFIL_E, true, Constantes.PERFIL_ADMINISTRADOR.toUpperCase());
		perfiles.removeAll(usuarioModel.getPerfiles());
		Collections.sort(perfiles);
		model.addAttribute(Constantes.PERFILES, perfiles);
		
		usuarioModel.setUsuario(usuarioRep);

		if (!usuarioRep.getIdEmpresa().equals(idEmpresa))
			return "redirect:/usuarioListar?error";

		else {			
			usuarioRep.setContrasena(null);
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);
			return "updateUsuario";
		}
	}

	@PostMapping("usuarioUpdate")
	public String updateUsuario(@RequestParam("usuarioId") int id, @Valid com.beca.misdivisas.model.Usuario usuarioRep, BindingResult result, Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		usuarioRep.getUsuario().setEmpresa(us.getEmpresa());
		
		  try {
			
			if(usuarioRep.getUsuario().getContrasena()==null || usuarioRep.getUsuario().getContrasena().trim().isEmpty())
					result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if (usuarioRep.getUsuario().getContrasena().length() < 8 || usuarioRep.getUsuario().getContrasena().length() > 20)
			  result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1); 
			else if (!esValidaContrasena(usuarioRep.getUsuario().getContrasena()))
			  result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2); 
			else if (!usuarioRep.getUsuario().getContrasena().equals(usuarioRep.getUsuario().getRepitaContrasena()))
			  result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);		 

			if(usuarioRep.getUsuario().getRepitaContrasena()==null || usuarioRep.getUsuario().getRepitaContrasena().trim().isEmpty())
				result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_REQUERIDO);
			
			if(usuarioRep.getUsuario().getEmail() ==null || usuarioRep.getUsuario().getEmail().trim().isEmpty())
				result.rejectValue("usuario.email", "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if(!ValidationUtils.isValidEmail(usuarioRep.getUsuario().getEmail()))
				result.rejectValue("usuario.email", "", "inv\u00E1lido");
			
			if(usuarioRep.getUsuario().getNombreCompleto() ==null || usuarioRep.getUsuario().getNombreCompleto().trim().isEmpty())
				result.rejectValue("usuario.nombreCompleto", "", Constantes.MENSAJE_VAL_REQUERIDO);
			else if(!usuarioRep.getUsuario().getNombreCompleto().matches("[a-zA-Z0-9\\ \\Á\\á\\É\\é\\Í\\í\\Ó\\ó\\Ú\\ú\\Ñ\\ñ]*")) 
				result.rejectValue("usuario.nombreCompleto", "", "contiene caracteres no v\u00E1lidos");			
			
			encoder2 = new BCryptPasswordEncoder();
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
				List<Perfil> perfiles = perfilRepo.findByIdEmpresaEstadoTipoEditable(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.TIPO_PERFIL_E, true, Constantes.PERFIL_ADMINISTRADOR.toUpperCase());
				perfiles.removeAll(usuarioRep.getPerfiles());
				Collections.sort(perfiles);
				model.addAttribute(Constantes.PERFILES, perfiles);
				
				return "updateUsuario";
			}
			
			actualizarUsuario(usuarioRep, usuario,us);

			String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_EDICION, usuario.getIdUsuario(), usuario.getNombreUsuario(), us.getEmpresa().getEmpresa());

			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_EDICION, true, Util.getRemoteIp(request), us);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, e.getLocalizedMessage(), Constantes.OP_EDICION, true, Util.getRemoteIp(request), us);
			return Constantes.ERROR;
		}

		return "redirect:/usuarioHome?success";
	}
	
	@Transactional
	private void actualizarUsuario(com.beca.misdivisas.model.Usuario usuarioRep, Usuario usuario, Usuario usuarioSesion) throws Exception{
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
		
		if(usuarioSesion!=null && usuarioSesion.getIdUsuario()!=null)
			usuario.setPerfilUsuarios(usuarioRep.getUsuario().getPerfilUsuarios());

		
		usuarioRepository.save(usuario);

		if(usuarioSesion!=null && usuarioSesion.getIdUsuario()!=null) {
			perfilUsuarioRepo.deleteByUserId(usuarioRep.getUsuario().getIdUsuario());
			PerfilUsuario pu =null;
			//for (PerfilUsuario p : usuario.getPerfilUsuarios()) {
			for (Perfil p : usuarioRep.getPerfiles()) {
				pu = new PerfilUsuario();
				pu.setIdPerfil(p.getIdPerfil());
				pu.setPerfil(p);
				pu.setIdUsuario(usuario.getIdUsuario());
				pu.setUsuario(usuario);
				perfilUsuarioRepo.save(pu);
			}
		}		
	}

	@GetMapping("/cambiarContrasena")
	public String cambiarContrasena(Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		return Constantes.CHANGE_PASSWORD;
	}
	
	@PostMapping("usuarioChange")
	public String cambioContrasenaUsuario(@RequestParam("usuarioId") int id, @Valid com.beca.misdivisas.model.Usuario usuarioRep,
			BindingResult result, Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Usuario usuario = usuarioRepository.findById(id);
		BCryptPasswordEncoder encoder = new BCryptPasswordEncoder();
		if (!encoder.matches(usuarioRep.getUsuario().getContrasena(), usuario.getContrasena()))
			result.rejectValue(Constantes.USUARIO_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_5);
		else if (!usuarioRep.getUsuario().getNuevaContrasena().equals(usuarioRep.getUsuario().getRepitaContrasena()))
			result.rejectValue(Constantes.USUARIO_REPITA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_3);

		if (usuarioRep.getUsuario().getNuevaContrasena().length() < 8 || usuarioRep.getUsuario().getNuevaContrasena().length() > 20)
			result.rejectValue(Constantes.USUARIO_NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_1);
		else if (!esValidaContrasena(usuarioRep.getUsuario().getNuevaContrasena()))
			result.rejectValue(Constantes.USUARIO_NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_2);

		encoder2 = new BCryptPasswordEncoder();
		
		usuario.setContrasena5(usuario.getContrasena4());
		usuario.setContrasena4(usuario.getContrasena3());
		usuario.setContrasena3(usuario.getContrasena2());
		usuario.setContrasena2(usuario.getContrasena1());
		usuario.setContrasena1(usuario.getContrasena());
		usuario.setContrasena(usuarioRep.getUsuario().getNuevaContrasena());
		usuarioRep.getUsuario().setEmpresa(usuario.getEmpresa());
		usuarioRep.getUsuario().setIdUsuario(id);
		model.addAttribute(Constantes.U_SUARIO, usuarioRep);
		if (!esValidaUltimasContrasenas(usuario))
			result.rejectValue(Constantes.USUARIO_NUEVA_CONTRASENA, "", Constantes.MENSAJE_VAL_CONTRASENA_4);

		if (result.hasErrors()) {
			return Constantes.CHANGE_PASSWORD;
		}

		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		usuarioRep.getUsuario().setFechaActualizacionContrasena(ts);

		usuarioRep.getUsuario().setContrasena(encoder2.encode(usuarioRep.getUsuario().getNuevaContrasena()));
		
		usuario.setContrasena(usuarioRep.getUsuario().getContrasena());

		usuario.setHabilitado(true);
		usuario.setFechaActualizacionContrasena(usuarioRep.getUsuario().getFechaActualizacionContrasena());
		usuario.setTipoUsuario(us.getTipoUsuario());

		usuarioRepository.save(usuario);
		factory.getObject().removeAttribute(Constantes.USUARIO);
		factory.getObject().setAttribute(Constantes.USUARIO, usuario);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		factory.getObject().removeAttribute(Constantes.CAMBIO_C);
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, Constantes.CAMBIO_CLAVE, Constantes.OP_EDICION,
				true, Util.getRemoteIp(request), us);

		return "redirect:/main?success";
	}

	@PostMapping("usuarioEliminar")
	public String deleteUsuario(@RequestParam("usuarioId") int id, Model model) {
		Usuario us = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		Usuario usuario = usuarioRepository.findById(id);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		int idEmpresa = us.getIdEmpresa();
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);

		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		if (!usuario.getIdEmpresa().equals(idEmpresa))

			return "redirect:/usuarioListar?error";

		else {

			usuario.setEstado("I");
			usuarioRepository.save(usuario);
			String detalle = MessageFormat.format(Constantes.ACCION_USUARIO, Constantes.OP_EDICION, usuario.getNombreUsuario(),
					usuario.getIdUsuario());

			HttpSession session = factory.getObject();
			logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_USUARIO, detalle, Constantes.OP_ELIMINAR,
					true, Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

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