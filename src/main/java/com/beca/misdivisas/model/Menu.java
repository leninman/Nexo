package com.beca.misdivisas.model;

import java.util.List;

public class Menu {
	
	private int idMenu;
	private int idMenuPadre;	
	private int nivel;
	private String nombreOpcion;
	private String accion;
	private String icono;
	public List<Menu> subMenu;
	
	public Menu() {
		
	}

	public Menu(int idMenu, int idMenuPadre, int nivel, String nombreOpcion, String accion, String icono) {
		super();
		this.idMenu = idMenu;
		this.idMenuPadre = idMenuPadre;
		this.nivel = nivel;
		this.nombreOpcion = nombreOpcion;
		this.accion = accion;
		this.icono = icono;
	}

	public String getNombreOpcion() {
		return nombreOpcion;
	}

	public void setNombreOpcion(String nombreOpcion) {
		this.nombreOpcion = nombreOpcion;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getIcono() {
		return icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}

	public List<Menu> getSubMenu() {
		return subMenu;
	}

	public void setSubMenu(List<Menu> subMenu) {
		this.subMenu = subMenu;
	}

	public int getIdMenu() {
		return idMenu;
	}

	public void setIdMenu(int idMenu) {
		this.idMenu = idMenu;
	}

	public int getIdMenuPadre() {
		return idMenuPadre;
	}

	public void setIdMenuPadre(int idMenuPadre) {
		this.idMenuPadre = idMenuPadre;
	}

	public int getNivel() {
		return nivel;
	}

	public void setNivel(int nivel) {
		this.nivel = nivel;
	}
}
