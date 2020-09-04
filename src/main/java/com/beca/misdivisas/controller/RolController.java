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
public class RolController {
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

		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));

		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		return "rol/mainRole";
	}
	
	@GetMapping("/createRoleHome")
	public String createRoleHome(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute(Constantes.U_SUARIO, usuario);

		model.addAttribute("opciones", menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2));

		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.ROL_ADMIN);
		model.addAttribute(Constantes.USUARIOS, usuarios);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute("edit",false);

		Rol rol = new Rol();
		model.addAttribute("rol", rol);

		return "rol/roleHome";
	}

	@PostMapping("/createRole")
	public String createRol(@Valid Rol rol, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.ROL_ADMIN);
		
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);
		
		
		if(rol.getNombreRol().isEmpty())
			result.rejectValue("nombreRol", "", "Debes especificar un nombre para el nuevo Rol");
		
		
		if(rol.getOpciones() == null || rol.getOpciones().length <=  1) {
			result.rejectValue("opciones", "", "Debes seleccionar al menos una opción del Menú");
			
		}
		
		if (result.hasErrors()) {
			List<Menu> menuList = getMenuList(rol.getOpciones());
			menues.removeAll(menuList);
			
			model.addAttribute("opciones", menues);
			model.addAttribute("opcionesSelect", menuList);
			
			usuarios.removeAll(rol.getUsuarios());
			model.addAttribute(Constantes.USUARIOS, usuarios);
			model.addAttribute("usuariosSelect", rol.getUsuarios());
			return "rol/roleHome";
		}
		
		List<Menu> menuList = new ArrayList<Menu>();
		
		com.beca.misdivisas.jpa.Rol newRol = new com.beca.misdivisas.jpa.Rol();
		newRol.setIdEmpresa(usuario.getIdEmpresa());
		newRol.setRol(rol.getNombreRol());
		newRol.setEstado(Constantes.ACTIVO);
		rolRepo.save(newRol);
		
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
		

		menues.removeAll(menuList);
		model.addAttribute("opciones", menues);
		model.addAttribute("opcionesSelect", menuList);
		
		
		usuarios.removeAll(rol.getUsuarios());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("usuariosSelect", rol.getUsuarios());
		
		model.addAttribute("result", "success");
		
		return "rol/roleHome";
	}
	
	@PostMapping("/editRoleHome")
	public String editRol(@RequestParam("rolId") int rolId, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);

		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, Constantes.ROL_ADMIN);
		
		
		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		model.addAttribute(Constantes.EDIT,true);
		
		Rol rol = rolService.getRolById(rolId);
		model.addAttribute("rol", rol);
		
		
		List<Menu> menuList = menuService.loadMenuByroleIdAndLevel(rolId, 2);
		List<Usuario> usuariosSelect = userRepo.findAllByEmpresaAndEstadoInRol(usuario.getIdEmpresa(), Constantes.ACTIVO, rolId);
		
		if(menuList != null)
		menues.removeAll(menuList);
		
		model.addAttribute("opciones", menues);
		model.addAttribute("opcionesSelect", menuList);
		
		if(usuariosSelect !=null)
		usuarios.removeAll(usuariosSelect);
		
		model.addAttribute(Constantes.USUARIOS, usuarios);
		model.addAttribute("usuariosSelect", usuariosSelect);

		return "rol/roleHome";
	}
	
	
	@PostMapping("/editRole")
	public String editRol(@RequestParam("rolId") int rolId, @Valid Rol rol, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute("usuario", usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.ROL_ADMIN);
		
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);
		
		if(rol.getOpciones() == null || rol.getOpciones().length <=  1) {
			result.rejectValue("opciones", "", "Debes seleccionar al menos una opción del Menú");
			
		}
		
		if (result.hasErrors()) {
			List<Menu> menuList = getMenuList(rol.getOpciones());
			menues.removeAll(menuList);
			
			model.addAttribute("opciones", menues);
			model.addAttribute("opcionesSelect", menuList);
			
			usuarios.removeAll(rol.getUsuarios());
			model.addAttribute("usuarios", usuarios);
			model.addAttribute("usuariosSelect", rol.getUsuarios());
			return "rol/roleHome";
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

		
		model.addAttribute("result", "success");
		
		return "rol/roleHome";
	}

	@PostMapping("/deleteRole")
	public String deleteRole(@RequestParam("rolId") int rolId, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		rolRepo.deleteByRolId(rolId, Constantes.INACTIVO);

		model.addAttribute(Constantes.ROLES, rolRepo.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), Constantes.ACTIVO));
		
		model.addAttribute("result", "success");
		
		return "rol/mainRole";

	}

	
	private List<Menu> getMenuList(String[] mr){
		List<Menu> menuList = new ArrayList<Menu>();
		
		for (int i = 0; i < mr.length; i++) {
			if(!mr[i].equalsIgnoreCase("1")) {
				com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
				menuList.add(menuService.getMenu(menu));
			}
			
		}
		return menuList;
	}

}
