package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "MENU_ROL" database table.
 * 
 */
@Entity
@Table(name="\"MENU_ROL\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="MenuRol.findAll", query="SELECT m FROM MenuRol m")
public class MenuRol implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_menu_rol_idMenuRol\"", sequenceName ="\"SEGURIDAD\".\"seq_menu_rol_idMenuRol\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_menu_rol_idMenuRol\"")
	@Column(name="\"id_menu_rol\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idMenuRol;

	@Column(name="\"id_rol\"")
	private Integer idRol;

	@Column(name="\"id_menu\"")
	private Integer idMenu;
	
	//bi-directional many-to-one association to Menu
	@ManyToOne
	@JoinColumn(name="\"id_menu\"", insertable = false, updatable = false)
	private Menu menu;

	//bi-directional many-to-one association to Rol
	@ManyToOne
	@JoinColumn(name="\"id_rol\"", insertable = false, updatable = false)
	private Rol rol;

	public MenuRol() {
	}

	public Integer getIdMenuRol() {
		return this.idMenuRol;
	}

	public void setIdMenuRol(Integer idMenuRol) {
		this.idMenuRol = idMenuRol;
	}
	
	public Integer getIdRol() {
		return this.idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public Integer getIdMenu() {
		return this.idMenu;
	}

	public void setIdMenu(Integer idMenu) {
		this.idMenu = idMenu;
	}

	public Menu getMenu() {
		return this.menu;
	}

	public void setMenu(Menu menu) {
		this.menu = menu;
	}

	public Rol getRol() {
		return this.rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

}