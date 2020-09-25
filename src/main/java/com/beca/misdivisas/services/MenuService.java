package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IMenuRepo;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.util.Constantes;

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
	
	public List<Menu> loadMenuByUserIdAndLevel(Integer idUsuario, int nivel) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = null;
		
		if(idUsuario==null)
			menuItem = menuRepo.findByRolName(Constantes.ROL_ADMIN_BECA, nivel);
		else
			menuItem = menuRepo.findByIdUsuario(idUsuario, nivel);
		
		List<Menu> menuList = new ArrayList<Menu>();
		Menu m = null;

		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {			
			m = getMenu(menu);
			m.setNombreOpcion(menuRepo.findById(menu.getIdMenuPadre()).get().getNombreOpcion()+" - "+m.getNombreOpcion());
			menuList.add(m);
		}
		return menuList;

	}
	
	public List<Menu> loadMenuByroleIdAndLevel(int rolId, int level) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByRolId(rolId, 2);
		
		List<Menu> menuList = new ArrayList<Menu>();
		Menu m = null;

		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {			
			m = getMenu(menu);
			m.setNombreOpcion(menuRepo.findById(menu.getIdMenuPadre()).get().getNombreOpcion()+" - "+m.getNombreOpcion());
			menuList.add(m);
		}
		return menuList;

	}
	public List<Menu> loadMenuByRolId(int rolId) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByRolId(rolId, 1);
		List<com.beca.misdivisas.jpa.Menu> subMenuItem = menuRepo.findByRolId(rolId, 2);
		List<Menu> menuList = new ArrayList<Menu>();
		List<Menu> subMenuList = new ArrayList<Menu>();

		Menu m = null;
		Menu sm = null;

		for (com.beca.misdivisas.jpa.Menu menu : menuItem) {
			m = getMenu(menu);
			m.setNombreOpcion(menuRepo.findById(menu.getIdMenuPadre()).get().getNombreOpcion()+" - "+m.getNombreOpcion());			
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
	
	public List<Menu> loadMenuByRolName(String rolName) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByRolName(rolName, 1);
		List<com.beca.misdivisas.jpa.Menu> subMenuItem = menuRepo.findByRolName(rolName, 2);
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
	
	public List<Menu> loadMenuByUserIdAndIdEmpresa(int userId, int idEmpresa) {
		List<com.beca.misdivisas.jpa.Menu> menuItem = menuRepo.findByIdUsuarioAndIdEmpresa(userId, idEmpresa, 1);
		List<com.beca.misdivisas.jpa.Menu> subMenuItem = menuRepo.findByIdUsuarioAndIdEmpresa(userId, idEmpresa, 2);
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
	
	public List<Menu> getMenu(Integer idUsuario) {
		List<Menu> menu = null;

		/*
		 * if (request.isUserInRole(Constantes.ROL_ADMIN_BECA)) { menu =
		 * loadMenuByRolName(Constantes.ROL_ADMIN_BECA);
		 * 
		 * } else {
		 */
		if(idUsuario==null)
			menu = loadMenuByRolName(Constantes.ROL_ADMIN_BECA);
		else
			menu = loadMenuByUserId(idUsuario);
		//}

		return menu;
	}
	
	public List<Menu> getMenuList(String[] mr){
		List<Menu> menuList = new ArrayList<Menu>();
		
		for (int i = 0; i < mr.length; i++) {
			if(!mr[i].equalsIgnoreCase("1")) {
				com.beca.misdivisas.jpa.Menu menu = menuRepo.findById(Integer.parseInt(mr[i]));
				menuList.add(getMenu(menu));
			}
			
		}
		return menuList;
	}
}
