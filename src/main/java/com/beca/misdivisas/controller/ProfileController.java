package com.beca.misdivisas.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.interfaces.IMenuRepo;
import com.beca.misdivisas.interfaces.IMenuRolRepo;
import com.beca.misdivisas.interfaces.IRolRepo;
import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.interfaces.IUsuarioRolRepo;
import com.beca.misdivisas.jpa.MenuRol;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.jpa.UsuarioRol;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.model.Rol;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.services.RolService;
import com.beca.misdivisas.util.Constantes;

@Controller
public class ProfileController {
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private MenuService menuService;

	@Autowired
	private IUsuarioRepo userRepo;

	@Autowired
	private IUsuarioRolRepo uRolRepo;

	@Autowired
	private IRolRepo rolRepo;

	@Autowired
	private IMenuRepo menuRepo;
	
	@Autowired
	private IMenuRolRepo mrRepo;
	
	@Autowired
	private RolService rolService;

	@GetMapping("/roleHome")
	public String roleHome(Model model) {	

		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute(Constantes.GESTIONAR_ROLES, false);
		return Constantes.ROL_MAIN;
	}
	
	@GetMapping("/gestionarRoles")
	public String gestionarRoles(Model model) {	

		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario())); 
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaNullAndEstado(Constantes.ACTIVO));
		model.addAttribute(Constantes.GESTIONAR_ROLES, true);
		
		return Constantes.ROL_MAIN;
	}
	
	@PostMapping("/createRoleHome")
	public String createRoleHome(Model model,  @RequestParam("gestionarRoles") Boolean gestionarRoles) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);

		model.addAttribute(Constantes.OPCIONES, menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2));

		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.ROL_ADMIN);
		model.addAttribute(Constantes.USUARIOS, usuarios);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute(Constantes.EDIT,false);
		model.addAttribute(Constantes.GESTIONAR_ROLES, gestionarRoles);
		
		Rol rol = new Rol();
		model.addAttribute(Constantes.ROL, rol);

		return Constantes.ROL_HOME;
	}

	@PostMapping("/createRole")
	public String createRol(@Valid Rol rol, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Boolean esUsuarioInterno = usuario.getTipoUsuario().equalsIgnoreCase(Constantes.USUARIO_INTERNO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		
		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.ROL_ADMIN);
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);
		
		
		if(rol.getNombreRol().isEmpty())
			result.rejectValue(Constantes.NOMBRE_ROL, "", Constantes.MENSAJE_VAL_PERFIL);
		
		
		if(rol.getOpciones() == null || rol.getOpciones().length <=  1) {
			result.rejectValue(Constantes.OPCIONES, "", Constantes.MENSAJE_VAL_PERFIL_1);
			
		}
		
		if (result.hasErrors()) {
			List<Menu> menuList = getMenuList(rol.getOpciones());
			menues.removeAll(menuList);
			
			model.addAttribute(Constantes.OPCIONES, menues);
			model.addAttribute(Constantes.OPCION_SELECT, menuList);
			
			if(!esUsuarioInterno) {				
				usuarios.removeAll(rol.getUsuarios());
				model.addAttribute(Constantes.USUARIOS, usuarios);
				model.addAttribute(Constantes.USUARIO_SELECT, rol.getUsuarios());
			}

			return Constantes.ROL_HOME;
		}
		
		List<Menu> menuList = new ArrayList<Menu>();
		
		com.beca.misdivisas.jpa.Rol newRol = new com.beca.misdivisas.jpa.Rol();		
		newRol.setRol(rol.getNombreRol());
		newRol.setEstado(Constantes.ACTIVO);
		if (esUsuarioInterno)
		newRol.setIdEmpresa(null);
		else
		newRol.setIdEmpresa(usuario.getIdEmpresa());
		rolRepo.save(newRol);
		
		if(!esUsuarioInterno) {
			
			List<Usuario> us = rol.getUsuarios();
			  UsuarioRol usuarioRol = new UsuarioRol();
			  
			  for (Usuario usuario2 : us) {
				  usuarioRol.setIdRol(newRol.getIdRol());
				  usuarioRol.setIdUsuario(usuario2.getIdUsuario());
				  
				  uRolRepo.save(usuarioRol);
				  usuarioRol = new UsuarioRol();			
			}
		}

		String[] mr = rol.getOpciones();
		MenuRol menuRol = new MenuRol();

		for (int i = 0; i < mr.length; i++) {
			menuRol.setIdRol(newRol.getIdRol());
			
			com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
			if(menu.getIdMenuPadre() != null) {
				if(mrRepo.findByIdmenuAndIdRol(menu.getIdMenuPadre(), newRol.getIdRol()) == null) {
					MenuRol menuRolP = new MenuRol();
					menuRolP.setIdMenu(menu.getIdMenuPadre());
					menuRolP.setIdRol(newRol.getIdRol());
					mrRepo.save(menuRolP);
				}
			}
			menuRol.setIdMenu(Integer.parseInt(mr[i]));
			mrRepo.save(menuRol);
			menuList.add(menuService.getMenu(menu));
			menuRol = new MenuRol();			
		}
		if(esUsuarioInterno) {			
			model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaNullAndEstado(Constantes.ACTIVO));
			model.addAttribute(Constantes.GESTIONAR_ROLES, true);
		}else {
			model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		}
		
		model.addAttribute(Constantes.SUCCESS, true);
		
		return Constantes.ROL_MAIN;
	}
	
	@PostMapping("/editRoleHome")
	public String editRol(@RequestParam("rolId") int rolId, @RequestParam("gestionarRoles") Boolean gestionarRoles, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.U_SUARIO, usuario);		
		
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);

		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.ROL_ADMIN);		
		
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute(Constantes.EDIT,true);
		
		Rol rol = rolService.getRolById(rolId);
		model.addAttribute(Constantes.ROL, rol);		
		
		List<Menu> menuList = menuService.loadMenuByroleIdAndLevel(rolId, 2);
		List<Usuario> usuariosSelect = userRepo.findAllByEmpresaAndEstadoInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, rolId);
		
		if(menuList != null)
		menues.removeAll(menuList);
		
		model.addAttribute(Constantes.OPCIONES, menues);
		model.addAttribute(Constantes.OPCION_SELECT, menuList);
		
		if(usuariosSelect !=null)
		usuarios.removeAll(usuariosSelect);
		
		model.addAttribute(Constantes.USUARIOS, usuarios);
		model.addAttribute(Constantes.USUARIO_SELECT, usuariosSelect);
		
		model.addAttribute(Constantes.GESTIONAR_ROLES, gestionarRoles);
		
		return Constantes.ROL_HOME;
	}
	
	
	@PostMapping("/editRole")
	public String editRol(@RequestParam("rolId") int rolId, @RequestParam("gestionarRoles") Boolean gestionarRoles, @Valid Rol rol, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute("usuario", usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.ROL_ADMIN);
		
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);
		
		if(rol.getOpciones() == null || rol.getOpciones().length <=  1) {
			result.rejectValue(Constantes.OPCIONES, "", Constantes.MENSAJE_VAL_PERFIL_1);
			
		}
		
		if (result.hasErrors()) {
			List<Menu> menuList = getMenuList(rol.getOpciones());
			menues.removeAll(menuList);
			
			model.addAttribute(Constantes.OPCIONES, menues);
			model.addAttribute(Constantes.OPCION_SELECT, menuList);
			
			usuarios.removeAll(rol.getUsuarios());
			model.addAttribute(Constantes.U_SUARIOS, usuarios);
			model.addAttribute(Constantes.USUARIO_SELECT, rol.getUsuarios());
			return Constantes.ROL_HOME;
		}
		
		List<Menu> menuList = new ArrayList<Menu>();
		
		com.beca.misdivisas.jpa.Rol newRol = rolRepo.findById(rolId).get();
		//eliminamos los elementos del menu previo
		mrRepo.deleteByRolId(rolId);
		//eliminamos los usuarios previos 
		uRolRepo.deleteByRolId(rolId);

		List<Usuario> us = rol.getUsuarios();
		  UsuarioRol usuarioRol = new UsuarioRol();
		  
		  for (Usuario usuario2 : us) {
			  usuarioRol.setIdRol(newRol.getIdRol());
			  usuarioRol.setIdUsuario(usuario2.getIdUsuario());
			  
			  uRolRepo.save(usuarioRol);
			  usuarioRol = new UsuarioRol();			
		}

		String[] mr = rol.getOpciones();
		MenuRol menuRol = new MenuRol();

		for (int i = 0; i < mr.length; i++) {
			menuRol.setIdRol(newRol.getIdRol());
			
			com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
			if(menu.getIdMenuPadre() != null) {
				if(mrRepo.findByIdmenuAndIdRol(menu.getIdMenuPadre(), newRol.getIdRol()) == null) {
					MenuRol menuRolP = new MenuRol();
					menuRolP.setIdMenu(menu.getIdMenuPadre());
					menuRolP.setIdRol(newRol.getIdRol());
					mrRepo.save(menuRolP);
				}
			}
			menuRol.setIdMenu(Integer.parseInt(mr[i]));
			mrRepo.save(menuRol);
			menuList.add(menuService.getMenu(menu));
			menuRol = new MenuRol();			
		}

		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute(Constantes.SUCCESS, true);
		
		if(gestionarRoles!=null && gestionarRoles)
			return Constantes.PERFILES_GESTIONAR;
		return Constantes.ROL_MAIN;
	}

	@PostMapping("/deleteRole")
	public String deleteRole(@RequestParam("rolId") int rolId, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Boolean esUsuarioInterno = usuario.getTipoUsuario().equalsIgnoreCase(Constantes.USUARIO_INTERNO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		rolRepo.deleteByRolId(rolId, Constantes.INACTIVO);

		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		
		if(esUsuarioInterno)
			return Constantes.PERFILES_GESTIONAR;
		return Constantes.ROL_MAIN;

	}

	
	private List<Menu> getMenuList(String[] mr){
		List<Menu> menuList = new ArrayList<Menu>();
		
		for (int i = 0; i < mr.length; i++) {
			if(!mr[i].equalsIgnoreCase(Constantes.MENU_INICIO)) {
				com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
				menuList.add(menuService.getMenu(menu));
			}
			
		}
		return menuList;
	}

}
