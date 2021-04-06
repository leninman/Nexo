package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IPerfilRepo;
import com.beca.misdivisas.jpa.Perfil;
import com.beca.misdivisas.jpa.PerfilMenu;
import com.beca.misdivisas.model.Menu;

@Service
public class PerfilServices {
	@Autowired
	private IPerfilRepo perfilRepo;
	
	
	public com.beca.misdivisas.model.Perfil getPerfilById(int idPerfil ) {
		
		Optional<Perfil> perfil = perfilRepo.findById(idPerfil);
		com.beca.misdivisas.model.Perfil perfilModelo = new com.beca.misdivisas.model.Perfil();
		  perfilModelo.setNombrePerfil(perfil.get().getPerfil());
		  perfilModelo.setIdPerfil(perfil.get().getIdPerfil());
		  perfilModelo.setTipoPerfil(perfil.get().getTipoPerfil());
		  perfilModelo.setTipoVista(perfil.get().getTipoVista());
		  perfilModelo.setEditable(perfil.get().getEditable());
	  
		  return perfilModelo;
	}
	
	public List<Menu> getPerfilMenuByIdPerfil(int idPerfil){		
		Optional<Perfil> perfil = perfilRepo.findById(idPerfil);
		List<Menu> menuList = new ArrayList<Menu>();
		
		List<PerfilMenu> menuPerfiles = perfil.get().getPerfilMenus();
		for (com.beca.misdivisas.jpa.PerfilMenu perfilM : menuPerfiles) {
			if(!perfilM.getMenu().getNivel().equals(1))
			menuList.add(getMenu(perfilM.getMenu()));
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
