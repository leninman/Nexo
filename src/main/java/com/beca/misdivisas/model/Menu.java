package com.beca.misdivisas.model;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

public class Menu implements Serializable, Comparable<Menu> {

	private static final long serialVersionUID = 255126259694057923L;
	private int idMenu;
	private int idMenuPadre;	
	private int nivel;
	private String nombreOpcion;
	private String accion;
	private String icono;
	private String estado;
	private Integer orden;
	private String tipoMenu;
	private String tipoVista;
	private String visibleExterno;
	private String visibleInterno;
	private String visibleSoloAdmin;
	
	public List<Menu> subMenu;
	
	public Menu() {
		this.subMenu = new ArrayList<Menu>();
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

	@Override
	public boolean equals(Object obj) {		
		return this.idMenu == ((Menu)obj).getIdMenu();
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getOrden() {
		return orden;
	}

	public void setOrden(Integer orden) {
		this.orden = orden;
	}

	public String getTipoMenu() {
		return tipoMenu;
	}

	public void setTipoMenu(String tipoMenu) {
		this.tipoMenu = tipoMenu;
	}

	public String getTipoVista() {
		return tipoVista;
	}

	public void setTipoVista(String tipoVista) {
		this.tipoVista = tipoVista;
	}

	public String getVisibleExterno() {
		return visibleExterno;
	}

	public void setVisibleExterno(String visibleExterno) {
		this.visibleExterno = visibleExterno;
	}

	public String getVisibleInterno() {
		return visibleInterno;
	}

	public void setVisibleInterno(String visibleInterno) {
		this.visibleInterno = visibleInterno;
	}

	public String getVisibleSoloAdmin() {
		return visibleSoloAdmin;
	}

	public void setVisibleSoloAdmin(String visibleSoloAdmin) {
		this.visibleSoloAdmin = visibleSoloAdmin;
	}

	@Override
	public int compareTo(Menu m) {		
		return orden.compareTo(m.orden);
	}
}
