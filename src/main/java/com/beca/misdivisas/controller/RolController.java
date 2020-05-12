package com.beca.misdivisas.controller;

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
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.jpa.Usuario;
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
	
	
	@GetMapping("/rolHome")
	public String rolHome(Model model) {
		model.addAttribute("menus", getMenu());

		Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		model.addAttribute("usuario", usuario);

		model.addAttribute("opciones", menuService.loadMenuByUserIdAndLevel(usuario.getIdUsuario(),2));
		
		Rol rol = new Rol();
		model.addAttribute("rol", rol);
		
		return "rolHome";
	}
	
	@PostMapping("/createRol")
	public String createRol( Model model, @ModelAttribute("rol") Rol rol) {
	
		System.out.println(rol.getOpciones());
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
