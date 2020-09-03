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
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_rol_idRol\"", sequenceName = "\"SEGURIDAD\".\"seq_rol_idRol\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_rol_idRol\"")
	@Column(name="\"id_rol\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idRol;
	
	@Column(name="rol")
	private String rol;
	
	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;
	
	@Column(name="estado")
	private String estado;

	//bi-directional many-to-one association to UsuarioRol
	@OneToMany(mappedBy="rol")
	private List<UsuarioRol> usuarioRols;
	
	//bi-directional many-to-one association to MenuRol
	@OneToMany(mappedBy="rol")
	private List<MenuRol> menuRols;
	
	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa\"", insertable = false, updatable = false)
	})
	private Empresa empresa;

	public Rol() {
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
	
	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}
	
	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
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
	
	public List<MenuRol> getMenuRols() {
		return this.menuRols;
	}

	public void setMenuRols(List<MenuRol> menuRols) {
		this.menuRols = menuRols;
	}

	public MenuRol addMenuRol(MenuRol menuRol) {
		getMenuRols().add(menuRol);
		menuRol.setRol(this);

		return menuRol;
	}

	public MenuRol removeMenuRol(MenuRol menuRol) {
		getMenuRols().remove(menuRol);
		menuRol.setRol(null);

		return menuRol;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	@Override
	public int compareTo(Rol r) {		
		return rol.compareTo(r.rol);	
	}

}