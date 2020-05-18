package com.beca.misdivisas.controller;

import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

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
import com.beca.misdivisas.util.Constantes;

@Controller
public class RolController {
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private HttpServletRequest request;

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

	@GetMapping("/rolHome")
	public String rolHome(Model model) {
		model.addAttribute("menus", getMenu());

		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute("usuario", usuario);

		model.addAttribute("opciones", menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2));

		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),
				Constantes.ACTIVO, Constantes.ADMIN);
		model.addAttribute("usuarios", usuarios);

		Rol rol = new Rol();
		model.addAttribute("rol", rol);

		return "rolHome";
	}

	@PostMapping("/createRol")
	public String createRol(@Valid Rol rol, BindingResult result, Model model) {
		model.addAttribute("menus", getMenu());

		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute("usuario", usuario);
		if(rol.getNombreRol().isEmpty())
			result.rejectValue("nombreRol", "", "Debes especificar un nombre para el nuevo Rol");
		
		if(rol.getUsuarios() == null || rol.getUsuarios().isEmpty())
			result.rejectValue("usuarios", "", "Debes seleccionar al menos un Usuario");
		
		if(rol.getOpciones() == null || rol.getOpciones().length ==  0)
			result.rejectValue("opciones", "", "Debes seleccionar al menos una opción del Menú");
		
		if (result.hasErrors()) {
			return "rolHome";
		}
		
		List<Menu> menuList = new ArrayList<Menu>();
		
		com.beca.misdivisas.jpa.Rol newRol = new com.beca.misdivisas.jpa.Rol();
		newRol.setIdEmpresa(usuario.getIdEmpresa());
		newRol.setRol(rol.getNombreRol());
		rolRepo.save(newRol);
		
		//String[] us = rol.getUsuarios();
		List<Usuario> us = rol.getUsuarios();
		  UsuarioRol usuarioRol = new UsuarioRol();
		  
		  for (Usuario usuario2 : us) {
			  usuarioRol.setIdRol(newRol.getIdRol());
			  usuarioRol.setIdUsuario(usuario2.getIdUsuario());
			  
			  uRolRepo.save(usuarioRol);
			  usuarioRol = new UsuarioRol();			
		}
		/*
		 * for (int i = 0; i<us.length; i++) { usuarioRol.setIdRol(newRol.getIdRol());
		 * usuarioRol.setIdUsuario(Integer.parseInt(us[i]));
		 * 
		 * uRolRepo.save(usuarioRol);
		 * 
		 * usuarioRol = new UsuarioRol(); }
		 */

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
		
		List<Menu> menues =  menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(), 2);
		menues.removeAll(menuList);
		model.addAttribute("opciones", menues);
		model.addAttribute("opcionesSelect", menuList);
		
		List<Usuario> usuarios = userRepo.findAllByEmpresaAndEstadoAndNotInRol(usuario.getIdEmpresa(),Constantes.ACTIVO, Constantes.ADMIN);
		usuarios.removeAll(rol.getUsuarios());
		model.addAttribute("usuarios", usuarios);
		model.addAttribute("usuariosSelect", rol.getUsuarios());
		
		model.addAttribute("result", "success");
		
		return "rolHome";
	}

	public List<Menu> getMenu() {
		List<Menu> menu = null;

		if (request.isUserInRole(Constantes.ADMIN_BECA)) {
			menu = menuService.loadMenuByRolName(Constantes.ADMIN_BECA);

		} else {
			menu = menuService.loadMenuByUserId(((Usuario) factory.getObject().getAttribute("Usuario")).getIdUsuario());
		}

		return menu;
	}
}
