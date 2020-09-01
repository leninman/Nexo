package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IRolRepo;
import com.beca.misdivisas.jpa.MenuRol;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.model.Rol;

@Service
public class RolService {

	@Autowired
	private IRolRepo rolRepo;
	
	public Rol getRolById(int rolId ) {
		
		Optional<com.beca.misdivisas.jpa.Rol> rol = rolRepo.findById(rolId);
		  Rol rolModel = new Rol();
		  rolModel.setNombreRol(rol.get().getRol());
		  rolModel.setIdRol(rol.get().getIdRol());
		  
		  return rolModel;
	}
	
	public List<Menu> getRolMenuByRolId(int rolId){		
		Optional<com.beca.misdivisas.jpa.Rol> rol = rolRepo.findById(rolId);
		List<Menu> menuList = new ArrayList<Menu>();
		
		List<MenuRol> menuRols = rol.get().getMenuRols();
		for (com.beca.misdivisas.jpa.MenuRol menuR : menuRols) {
			if(!menuR.getMenu().getNivel().equals(1))
			menuList.add(getMenu(menuR.getMenu()));
		}
		
		return menuList;
	}
	
	public Menu getMenu(com.beca.misdivisas.jpa.Menu menu) {
		Menu m = new Menu();
		m.setIdMenu(menu.getIdMenu());

		if (menu.getIdMenuPadre() != null)
			m.setIdMenuPadre(menu.getIdMenuPadre());

		m.setNivel(menu.getNivel());
		m.setNombreOpcion(menu.getNombreOpcion());
		if (menu.getAccion() != null)
			m.setAccion(menu.getAccion());

		m.setIcono(menu.getIcono());

		return m;
	}
}
