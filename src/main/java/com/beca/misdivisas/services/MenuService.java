package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IMenuRepo;
import com.beca.misdivisas.model.Menu;

@Service
public class MenuService {

	@Autowired
	private IMenuRepo menuRepo;

	public List<Menu> loadMenuByUserId(int idUsuario) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByIdUsuario(idUsuario, 1);
		List<com.beca.misdivisas.jpa.Menu> subMenuItem = menuRepo.findByIdUsuario(idUsuario, 2);
		List<Menu> menuList = new ArrayList<Menu>();
		List<Menu> subMenuList = new ArrayList<Menu>();

		Menu m = null;
		Menu sm = null;

		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			m = getMenu(menu);
			subMenuList = new ArrayList<Menu>();

			for (com.beca.misdivisas.jpa.Menu subMenu : subMenuItem) {
				if (menu.getIdMenu() == subMenu.getIdMenuPadre()) {
					sm = getMenu(subMenu);
					subMenuList.add(sm);
				}
			}

			m.setSubMenu(subMenuList);
			menuList.add(m);

		}

		return menuList;

	}

	private Menu getMenu(com.beca.misdivisas.jpa.Menu menu) {
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
