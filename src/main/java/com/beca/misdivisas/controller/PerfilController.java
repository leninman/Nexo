package com.beca.misdivisas.controller;

import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.beca.misdivisas.interfaces.IMenuRepo;
import com.beca.misdivisas.interfaces.IPerfilMenuRepo;
import com.beca.misdivisas.interfaces.IPerfilRepo;
import com.beca.misdivisas.interfaces.IPerfilUsuarioRepo;
import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.jpa.Perfil;
import com.beca.misdivisas.jpa.PerfilMenu;
import com.beca.misdivisas.jpa.PerfilUsuario;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.services.PerfilServices;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class PerfilController {
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private MenuService menuService;

	@Autowired
	private IUsuarioRepo userRepo;

	@Autowired
	private IPerfilUsuarioRepo perfilUsuarioRepo;

	@Autowired
	private IPerfilRepo perfilRepo;

	@Autowired
	private IMenuRepo menuRepo;
	
	@Autowired
	private IPerfilMenuRepo perfilMenuRepo;
	
	@Autowired
	private PerfilServices perfilService;
	
	@Autowired
	private LogService logServ;
	
	@Autowired
	private HttpServletRequest request;

	@GetMapping("/perfilHome")
	public String perfilHome(Model model) {	
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		try {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			
			if(usuario.getIdEmpresa()!=null)
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
			else
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO));
			
			model.addAttribute(Constantes.GESTIONAR_PERFILES, false);
			
			String detalle = MessageFormat.format(Constantes.ACCION_LISTAR_PERFILES, usuario.getIdEmpresa()!=null ? usuario.getEmpresa().getEmpresa() : "Interno");
			logServ.registrarLog(Constantes.PERFIL_MAIN, detalle, Constantes.PERFILES, false, Util.getRemoteIp(request), usuario);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.PERFIL_MAIN, e.getLocalizedMessage(), Constantes.PERFILES, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}
		return Constantes.PERFIL_MAIN;
	}
	
	@GetMapping("/gestionarPerfiles")
	public String gestionarPerfiles(Model model) {	
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		try {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			List<Perfil> perfiles = perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc( Constantes.ACTIVO);
			perfiles.remove(0);
			perfiles.sort(Comparator.comparing(Perfil::getTipoVista).reversed());
			model.addAttribute(Constantes.PERFILES, perfiles);
			model.addAttribute(Constantes.GESTIONAR_PERFILES, true);
			
			logServ.registrarLog(Constantes.PERFIL_MAIN, "Listado de perfiles internos", Constantes.GESTIONAR_PERFILES, true, Util.getRemoteIp(request), usuario);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.PERFIL_MAIN, e.getLocalizedMessage(), Constantes.GESTIONAR_PERFILES, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}
		return Constantes.PERFIL_MAIN;
	}
	
	@PostMapping("/crearPerfilHome")
	public String crearPerfilHome(Model model,  @RequestParam("gestionarPerfiles") Boolean gestionarPerfiles) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		com.beca.misdivisas.model.Perfil perfil = new com.beca.misdivisas.model.Perfil();
		
		try {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			List<Menu> lm = null;
			
			if (gestionarPerfiles!=null && gestionarPerfiles) {
				model.addAttribute(Constantes.GESTIONAR_PERFILES, gestionarPerfiles);
				perfil.setTipoVista(Constantes.TIPO_VISTA_GENERAL);
				lm=menuService.loadMenuByTipoInterno(Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_GENERAL, false);
			} else {
				
				lm=menuService.loadMenuByTipoExterno(Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_EMPRESA, false);

				if(usuario.getIdEmpresa()!=null) {
					List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoNotInPerfil(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.PERFIL_ADMINISTRADOR);
					model.addAttribute(Constantes.USUARIOS, usuarios);
					model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
				}
				else
					model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO));
				
				model.addAttribute(Constantes.EDITAR,false);
				perfil.setTipoVista(Constantes.TIPO_VISTA_EMPRESA);
			}
			
			model.addAttribute(Constantes.OPCIONES,lm);		
			model.addAttribute(Constantes.PERFIL, perfil);
			
			logServ.registrarLog(Constantes.PERFIL_HOME, Constantes.GESTIONAR_PERFILES, Constantes.CREAR_PERFIL, true, Util.getRemoteIp(request), usuario);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.PERFIL_HOME, e.getLocalizedMessage(), Constantes.CREAR_PERFIL, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}

		return Constantes.PERFIL_HOME;
	}

	@Transactional
	@PostMapping("/crearPerfil")
	public String crearPerfil(@Valid com.beca.misdivisas.model.Perfil perfil, BindingResult result, Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		
		Boolean esUsuarioInterno = usuario.getTipoUsuario().equalsIgnoreCase(Constantes.USUARIO_INTERNO);
		try {
			List<Menu> menues = null;
			
			validarPerfil(perfil,result, usuario.getIdEmpresa());
			
			if(perfil.getTipoVista() == null)
				perfil.setTipoVista(Constantes.TIPO_VISTA_EMPRESA);
			
			if(esUsuarioInterno) {
				
				if(perfil.getNombrePerfil().indexOf(" ") != -1)
					result.rejectValue(Constantes.NOMBRE_PERFIL, "", "no debe contener espacios");			
				
				menues  =  menuService.loadMenuByTipoInterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
				perfil.setTipoPerfil(Constantes.TIPO_PERFIL_I);
				model.addAttribute(Constantes.GESTIONAR_PERFILES, true);
			}
			else {
				menues  =  menuService.loadMenuByTipoExterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
				perfil.setTipoPerfil(Constantes.TIPO_PERFIL_E);
			}
			
			if (result.hasErrors()) {
				//List<Menu> menuList = getMenuList(perfil.getOpciones());
				//menues.removeAll(menuList);
				
				model.addAttribute(Constantes.OPCIONES, menues);
				//model.addAttribute(Constantes.OPCION_SELECT, menuList);
				
				if(usuario.getIdEmpresa()!=null) {
					List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoNotInPerfil(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.PERFIL_ADMINISTRADOR);
					if(perfil.getUsuarios() != null)
						usuarios.removeAll(perfil.getUsuarios());
					model.addAttribute(Constantes.USUARIOS, usuarios);
					model.addAttribute(Constantes.USUARIO_SELECT, perfil.getUsuarios());
				}	
				return Constantes.PERFIL_HOME;
			}		

			List<Menu> menuList = new ArrayList<Menu>();
			
			Perfil nuevoPerfil = new Perfil();
				
			nuevoPerfil.setPerfil(perfil.getNombrePerfil());
			nuevoPerfil.setEstado(Constantes.ACTIVO);
			nuevoPerfil.setTipoPerfil(perfil.getTipoPerfil());
			nuevoPerfil.setTipoVista(perfil.getTipoVista());
			nuevoPerfil.setEditable(true);
			Timestamp ts = Util.getTimestamp();
			nuevoPerfil.setFechaCreacion(ts);
			nuevoPerfil.setFechaActualizacion(ts);
			
			if (esUsuarioInterno)
				nuevoPerfil.setIdEmpresa(null);
			else
				nuevoPerfil.setIdEmpresa(usuario.getIdEmpresa());
		
			perfilRepo.save(nuevoPerfil);
			
			if(!esUsuarioInterno) {
				
				List<Usuario> us = perfil.getUsuarios();
				PerfilUsuario perfilUsuario = new PerfilUsuario();
				  
				  for (Usuario usuario2 : us) {
					  perfilUsuario.setIdPerfil(nuevoPerfil.getIdPerfil());
					  perfilUsuario.getPerfil().setIdPerfil(nuevoPerfil.getIdPerfil());
					  perfilUsuario.setIdUsuario(usuario2.getIdUsuario());
					  perfilUsuario.getUsuario().setIdUsuario(usuario2.getIdUsuario());
					  perfilUsuarioRepo.save(perfilUsuario);
					  perfilUsuario = new PerfilUsuario();			
				}
			}

			String[] mr = perfil.getOpciones();
			PerfilMenu perfilMenu = new PerfilMenu();

			for (int i = 0; i < mr.length; i++) {
				perfilMenu.setIdPerfil(nuevoPerfil.getIdPerfil());
				perfilMenu.getPerfil().setIdPerfil(nuevoPerfil.getIdPerfil());
				
				com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));

				perfilMenu.setIdMenu(Integer.parseInt(mr[i]));
				perfilMenu.getMenu().setIdMenu(Integer.parseInt(mr[i]));
				perfilMenuRepo.save(perfilMenu);
				menuList.add(menuService.getMenu(menu));
				
				perfilMenu = null;
				perfilMenu = new PerfilMenu();			
			}
			
			if(usuario.getIdEmpresa()==null) {			
				List<Perfil> perfiles = perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO);
				perfiles.remove(0);
				perfiles.sort(Comparator.comparing(Perfil::getTipoVista).reversed());
				model.addAttribute(Constantes.PERFILES, perfiles);				
			}else {
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
			}
			
			String detalle = MessageFormat.format(Constantes.ACCION_PERFIL, Constantes.OP_CREAR,	nuevoPerfil.getIdPerfil(), nuevoPerfil.getPerfil(), nuevoPerfil.getTipoPerfil(), nuevoPerfil.getTipoVista(), usuario.getIdEmpresa()!=null ? usuario.getEmpresa().getEmpresa() : "Interno");
			logServ.registrarLog(Constantes.OP_CREAR_PERFIL, detalle, Constantes.CREAR_PERFIL, true, Util.getRemoteIp(request), usuario);

		} catch (Exception e) {
			logServ.registrarLog(Constantes.OP_CREAR_PERFIL, e.getLocalizedMessage(), Constantes.CREAR_PERFIL, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}

		model.addAttribute(Constantes.SUCCESS, true);		
		return Constantes.PERFIL_MAIN;
	}
	
	@PostMapping("/editarPerfilHome")
	public String editarPerfilHome(@RequestParam("idPerfil") int idPerfil, @RequestParam("gestionarPerfiles") Boolean gestionarPerfiles,
			@RequestParam("revisarPerfiles") Boolean revisarPerfiles, Model model) {
		
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		com.beca.misdivisas.jpa.Perfil perfilEditar = perfilRepo.findById(idPerfil).get();
		try {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			model.addAttribute(Constantes.REVISAR_PERFILES, revisarPerfiles);

			model.addAttribute(Constantes.EDITAR,true);
			
			com.beca.misdivisas.model.Perfil perfil = perfilService.getPerfilById(idPerfil);
			model.addAttribute(Constantes.PERFIL, perfil);
			
			if(usuario.getIdEmpresa() == null)
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO));
			else
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
			
			
			List<Menu> menues = null;
			
			if(perfil.getTipoPerfil().equals("I"))			
				menues  =  menuService.loadMenuByTipoInterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
			else
				menues  =  menuService.loadMenuByTipoExterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
				
			List<Menu> menuList = menuService.loadMenuByIdPerfil(idPerfil);
			menues.removeAll(menuList);
			
			model.addAttribute(Constantes.OPCIONES, menues);
			model.addAttribute(Constantes.OPCION_SELECT, menuList);
			
			if(usuario.getIdEmpresa()!=null) {
				List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoNotInPerfil(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.PERFIL_ADMINISTRADOR);
				List<Usuario> usuariosSelect = userRepo.findAllByEmpresaInPerfil(usuario.getIdEmpresa(), idPerfil);
				if(usuariosSelect !=null)
					usuarios.removeAll(usuariosSelect);			
				model.addAttribute(Constantes.USUARIOS, usuarios);
				model.addAttribute(Constantes.USUARIO_SELECT, usuariosSelect);
				
			}
			model.addAttribute(Constantes.GESTIONAR_PERFILES, gestionarPerfiles);
			String detalle = MessageFormat.format(Constantes.ACCION_PERFIL, Constantes.OP_EDICION,	idPerfil, perfilEditar.getPerfil(), perfilEditar.getTipoPerfil(), perfilEditar.getTipoVista(), usuario.getIdEmpresa()!= null ? usuario.getEmpresa().getEmpresa() : "Interno");
			logServ.registrarLog(Constantes.OP_EDITAR_PERFIL, detalle, Constantes.EDITAR_PERFIL, true, Util.getRemoteIp(request), usuario);
			
		} catch (Exception e) {
			return Constantes.ERROR;
		}
		
		return Constantes.PERFIL_HOME;
	}
	
	@Transactional
	@PostMapping("/editarPerfil")
	public String editarPerfil(@RequestParam("idPerfil") int idPerfil, @RequestParam("gestionarPerfiles") Boolean gestionarPerfiles, 
			@Valid com.beca.misdivisas.model.Perfil perfil, BindingResult resultado, Model modelo) {
		
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		
		modelo.addAttribute("usuario", usuario);		
		com.beca.misdivisas.jpa.Perfil perfilEditar = perfilRepo.findById(idPerfil).get();
		
		try {
			List<Menu> menues = null;
			
			if(perfil.getTipoVista() == null)
				perfil.setTipoVista(Constantes.TIPO_VISTA_EMPRESA);
			
			validarPerfil(perfil, resultado, perfilEditar.getIdEmpresa());
			
			if(perfilEditar.getTipoPerfil().equals("I") && perfil.getNombrePerfil().indexOf(" ") != -1)
				resultado.rejectValue(Constantes.NOMBRE_PERFIL, "", "no debe contener espacios");			
			
			if(perfilEditar.getTipoPerfil().equals("I"))			
				menues  =  menuService.loadMenuByTipoInterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
			else
				menues  =  menuService.loadMenuByTipoExterno(Constantes.TIPO_MENU_S, perfil.getTipoVista(), false);
			
			if (resultado.hasErrors()) {
				
				List<Menu> menuList = menuService.loadMenuByIdPerfil(idPerfil);
				//List<Menu> menuList = getMenuList(perfil.getOpciones());
				menues.removeAll(menuList);
				modelo.addAttribute(Constantes.PERFIL, perfil);
				modelo.addAttribute(Constantes.EDITAR,true);
				modelo.addAttribute(Constantes.OPCIONES, menues);
				modelo.addAttribute(Constantes.OPCION_SELECT, menuList);
				
				if (usuario.getIdEmpresa()!=null) {
					List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoNotInPerfil(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.PERFIL_ADMINISTRADOR);
					if(perfil.getUsuarios() != null)
						usuarios.removeAll(perfil.getUsuarios());
					modelo.addAttribute(Constantes.USUARIOS, usuarios);				
					modelo.addAttribute(Constantes.USUARIO_SELECT, perfil.getUsuarios());
				}
				if(gestionarPerfiles!=null && gestionarPerfiles)
					modelo.addAttribute(Constantes.GESTIONAR_PERFILES, gestionarPerfiles);
				return Constantes.PERFIL_HOME;
			}
			
			if(perfilEditar != null) {
				perfilEditar.setPerfil(perfil.getNombrePerfil());
				perfilEditar.setTipoVista(perfil.getTipoVista());
				perfilEditar.setFechaActualizacion(Util.getTimestamp());
				perfilRepo.save(perfilEditar);
			}
			
			List<Menu> menuList = new ArrayList<Menu>();
			List<Usuario> us = perfil.getUsuarios();
			//eliminamos los usuarios previos
			perfilUsuarioRepo.deleteByIdPerfil(idPerfil);
			if(us!=null && us.size()>0) {			
			PerfilUsuario perfilUsuario = new PerfilUsuario();		
			  for (Usuario usuario2 : us) {
				  perfilUsuario.getPerfil().setIdPerfil(perfilEditar.getIdPerfil());
				  perfilUsuario.setIdPerfil(perfilEditar.getIdPerfil());
				  perfilUsuario.getUsuario().setIdUsuario(usuario2.getIdUsuario());
				  perfilUsuario.setIdUsuario(usuario2.getIdUsuario());
				  
				  perfilUsuarioRepo.save(perfilUsuario);
				  perfilUsuario = new PerfilUsuario();			
			  }
			}

			String[] mr = perfil.getOpciones();
			PerfilMenu perfilMenu = new PerfilMenu();
			if(mr != null && mr.length > 0) {
				//eliminamos los elementos del menu previo
				perfilMenuRepo.deleteByIdPerfil(idPerfil);
				for (int i = 0; i < mr.length; i++) {
					perfilMenu.getPerfil().setIdPerfil(perfilEditar.getIdPerfil());
					perfilMenu.setIdPerfil(perfilEditar.getIdPerfil());
					
					com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
					
					perfilMenu.getMenu().setIdMenu(Integer.parseInt(mr[i]));
					perfilMenu.setIdMenu(Integer.parseInt(mr[i]));
					perfilMenuRepo.save(perfilMenu);
					
					menuList.add(menuService.getMenu(menu));
					perfilMenu = new PerfilMenu();			
				}
			}
			
			if(usuario.getIdEmpresa()!=null)
				modelo.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
			else
				modelo.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO));
			
			modelo.addAttribute(Constantes.SUCCESS, true);
			
			String detalle = MessageFormat.format(Constantes.ACCION_PERFIL, Constantes.OP_EDICION,	idPerfil, perfilEditar.getPerfil(), perfilEditar.getTipoPerfil(), perfilEditar.getTipoVista(), usuario.getIdEmpresa()!= null ? usuario.getEmpresa().getEmpresa() : "Interno");
			logServ.registrarLog(Constantes.OP_EDITAR_PERFIL, detalle, Constantes.EDITAR_PERFIL, true, Util.getRemoteIp(request), usuario);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.OP_EDITAR_PERFIL, e.getLocalizedMessage(), Constantes.EDITAR_PERFIL, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}
		
		if(gestionarPerfiles!=null && gestionarPerfiles)
			return Constantes.PERFILES_GESTIONAR;
		return Constantes.PERFIL_MAIN;
	}

	@PostMapping("/eliminarPerfil")
	public String eliminarPerfil(@RequestParam("idPerfil") int idPerfil, Model model, RedirectAttributes redirectAttributes) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Boolean esUsuarioInterno = usuario.getTipoUsuario().equalsIgnoreCase(Constantes.USUARIO_INTERNO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		try {
			Perfil p = perfilRepo.findById(idPerfil).get();
			
			if(usuario.getIdEmpresa()!=null) {
				List<Usuario> usuariosEnPerfil = userRepo.listarUsuarioByIdEmpresaAndIdPerfil(usuario.getIdEmpresa().intValue(),idPerfil);
				boolean tieneUsuarios = false;
				if(usuariosEnPerfil!= null && !usuariosEnPerfil.isEmpty())
					tieneUsuarios = true;
				if (tieneUsuarios) {				
					redirectAttributes.addFlashAttribute("UsuarioAsignado", true);
					return "redirect:perfilHome";
				}
			}
			perfilRepo.updateByIdPerfil(idPerfil, Constantes.INACTIVO, Util.getTimestamp());
			if(usuario.getIdEmpresa()!=null)
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaAndEstadoOrderByPerfilAsc(usuario.getIdEmpresa(), Constantes.ACTIVO));
			else
				model.addAttribute(Constantes.PERFILES, perfilRepo.findByIdEmpresaNullAndEstadoOrderByPerfilAsc(Constantes.ACTIVO));
			
			String detalle = MessageFormat.format(Constantes.ACCION_PERFIL, Constantes.OP_ELIMINAR,	idPerfil, p.getPerfil(), p.getTipoPerfil(), p.getTipoVista(), usuario.getIdEmpresa() != null ? usuario.getEmpresa().getEmpresa() : "Interno");
			logServ.registrarLog(Constantes.OP_ELIMINAR_PERFIL, detalle, Constantes.ELIMINAR_PERFIL, true, Util.getRemoteIp(request), usuario);
		} catch (Exception e) {
			logServ.registrarLog(Constantes.OP_ELIMINAR_PERFIL, e.getLocalizedMessage(), Constantes.ELIMINAR_PERFIL, false, Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}
		model.addAttribute(Constantes.SUCCESS, true);
		if(esUsuarioInterno)
			return Constantes.PERFILES_GESTIONAR;
		
		return Constantes.PERFIL_MAIN;
	}
	
	
	/*
	 * private List<Menu> getMenuList(String[] mr){ List<Menu> menuList = new
	 * ArrayList<Menu>();
	 * 
	 * for (int i = 0; i < mr.length; i++) {
	 * if(!mr[i].equalsIgnoreCase(Constantes.MENU_INICIO)) {
	 * com.beca.misdivisas.jpa.Menu menu =
	 * menuRepo.findById(Integer.parseInt(mr[i]));
	 * menuList.add(menuService.getMenu(menu)); } } return menuList; }
	 */
	
	@GetMapping(path = "/perfiles/{tipoVista}", produces = "application/json")
	@ResponseBody
	public List<Menu> getPerfilesByTipoVista(@PathVariable String tipoVista) {
		List<Menu> lm = null;
		
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Boolean esUsuarioInterno = usuario.getTipoUsuario().equalsIgnoreCase(Constantes.USUARIO_INTERNO);
		
		if(esUsuarioInterno)
			lm = menuService.loadMenuByTipoInterno(Constantes.TIPO_MENU_S, tipoVista, false);
		else
			lm = menuService.loadMenuByTipoExterno(Constantes.TIPO_MENU_S, tipoVista, false);
		return lm;
	}
	
	private void validarPerfil(com.beca.misdivisas.model.Perfil perfil, BindingResult result, Integer idEmpresa) {
		
		
		if(perfil.getNombrePerfil() == null || perfil.getNombrePerfil().isEmpty())
			result.rejectValue(Constantes.NOMBRE_PERFIL, "", Constantes.MENSAJE_VAL_PERFIL);
		else {
			perfil.setNombrePerfil(perfil.getNombrePerfil().trim());
			
			if (perfil.getNombrePerfil().length() > 50) {
				result.rejectValue(Constantes.NOMBRE_PERFIL, "", "debe tener una longitud m\u00E1xima de 50 caracteres");
			}
			else if (!perfil.getNombrePerfil().matches(Constantes.CARACTERES_PERMITIDOS_PERFIL_PATTERN.pattern())) {
				result.rejectValue(Constantes.NOMBRE_PERFIL, "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else {
				Perfil perfilR = null;
				if(idEmpresa == null)
					perfilR = perfilRepo.findByNombrePerfilInternoAndEstado(perfil.getNombrePerfil().toUpperCase(), Constantes.ACTIVO);
				else	
					perfilR = perfilRepo.findByNombrePerfilAndIdEmpresaEstado(perfil.getNombrePerfil().toUpperCase(),idEmpresa, Constantes.ACTIVO);
				if(perfilR!=null) {
					if(perfil.getIdPerfil() == 0)
						result.rejectValue(Constantes.NOMBRE_PERFIL, "", Constantes.MENSAJE_VAL_PERFIL_2);
					else if (perfil.getIdPerfil() != perfilR.getIdPerfil().intValue())
						result.rejectValue(Constantes.NOMBRE_PERFIL, "", Constantes.MENSAJE_VAL_PERFIL_2);
				}	
			}
		}
		
		if(perfil.getOpciones() == null || perfil.getOpciones().length <=  1) {
			result.rejectValue(Constantes.OPCIONES, "", Constantes.MENSAJE_VAL_PERFIL_1);				
		}
	}

	
}
