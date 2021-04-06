package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ROL" database table.
 * 
 */
@Entity
@Table(name="\"ROL\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="Rol.findAll", query="SELECT r FROM Rol r")
public class Rol implements Serializable, Comparable<Rol> {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_rol_idRol\"", sequenceName ="\"SEGURIDAD\".\"seq_rol_idRol\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_rol_idRol\"")
	@Column(name="\"id_rol\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idRol;
	
	@Column(name="rol")
	private String rol;
	
	@Column(name="\"tipo_usuario\"")
	private String tipoUsuario;
	
	//bi-directional many-to-one association to Menu
	@OneToMany(mappedBy="rol")
	private List<Menu> menus;

	//bi-directional many-to-one association to UsuarioRol
	@OneToMany(mappedBy="rol")
	private List<UsuarioRol> usuarioRols;

	public Rol() {
	}

	public Rol(Integer idRol) {
		this.idRol = idRol;
	}
	
	public Integer getIdRol() {
		return this.idRol;
	}

	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}

	public String getRol() {
		return this.rol;
	}

	public void setRol(String rol) {
		this.rol = rol;
	}
	
	public String getTipoUsuario() {
		return this.tipoUsuario;
	}

	public void setTipoUsuario(String tipoUsuario) {
		this.tipoUsuario = tipoUsuario;
	}

	public List<Menu> getMenus() {
		return this.menus;
	}

	public void setMenus(List<Menu> menus) {
		this.menus = menus;
	}

	public Menu addMenus(Menu menus) {
		getMenus().add(menus);
		menus.setRol(this);

		return menus;
	}

	public Menu removeMenus(Menu menus) {
		getMenus().remove(menus);
		menus.setRol(null);

		return menus;
	}
	
	public List<UsuarioRol> getUsuarioRols() {
		return this.usuarioRols;
	}

	public void setUsuarioRols(List<UsuarioRol> usuarioRols) {
		this.usuarioRols = usuarioRols;
	}

	public UsuarioRol addUsuarioRol(UsuarioRol usuarioRol) {
		getUsuarioRols().add(usuarioRol);
		usuarioRol.setRol(this);

		return usuarioRol;
	}

	public UsuarioRol removeUsuarioRol(UsuarioRol usuarioRol) {
		getUsuarioRols().remove(usuarioRol);
		usuarioRol.setRol(null);

		return usuarioRol;
	}

	@Override
	public int compareTo(Rol r) {		
		return rol.compareTo(r.rol);	
	}

}