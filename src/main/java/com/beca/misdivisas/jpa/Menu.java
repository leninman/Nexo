package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "MENU" database table.
 * 
 */
@Entity
@Table(name="\"MENU\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="Menu.findAll", query="SELECT m FROM Menu m")
public class Menu implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_menu_idMenu\"" )
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_menu_idMenu\"")
	@Column(name="\"id_menu\"")
	private Integer idMenu;

	private String accion;

	private String icono;

	@Column(name="\"id_menu_padre\"")
	private Integer idMenuPadre;

	private Integer nivel;

	@Column(name="\"nombre_opcion\"")
	private String nombreOpcion;

	//bi-directional many-to-one association to MenuRol
	@OneToMany(mappedBy="menu")
	private List<MenuRol> menuRols;

	public Menu() {
	}

	public Integer getIdMenu() {
		return this.idMenu;
	}

	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}

	public String getAccion() {
		return this.accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public String getIcono() {
		return this.icono;
	}

	public void setIcono(String icono) {
		this.icono = icono;
	}

	public Integer getIdMenuPadre() {
		return this.idMenuPadre;
	}

	public void setIdMenuPadre(Integer idMenuPadre) {
		this.idMenuPadre = idMenuPadre;
	}

	public Integer getNivel() {
		return this.nivel;
	}

	public void setNivel(Integer nivel) {
		this.nivel = nivel;
	}

	public String getNombreOpcion() {
		return this.nombreOpcion;
	}

	public void setNombreOpcion(String nombreOpcion) {
		this.nombreOpcion = nombreOpcion;
	}

	public List<MenuRol> getMenuRols() {
		return this.menuRols;
	}

	public void setMenuRols(List<MenuRol> menuRols) {
		this.menuRols = menuRols;
	}

	public MenuRol addMenuRol(MenuRol menuRol) {
		getMenuRols().add(menuRol);
		menuRol.setMenu(this);

		return menuRol;
	}

	public MenuRol removeMenuRol(MenuRol menuRol) {
		getMenuRols().remove(menuRol);
		menuRol.setMenu(null);

		return menuRol;
	}
}