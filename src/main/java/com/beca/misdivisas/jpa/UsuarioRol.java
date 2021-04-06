package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "USUARIO_ROL" database table.
 * 
 */
@Entity
@Table(name="\"USUARIO_ROL\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="UsuarioRol.findAll", query="SELECT u FROM UsuarioRol u")
public class UsuarioRol implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Column(name="\"id_rol\"")
	private Integer idRol;

	@Column(name="\"id_usuario\"")
	private Integer idUsuario;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_usuario_rol_idUsuarioRol\"", sequenceName ="\"SEGURIDAD\".\"seq_usuario_rol_idUsuarioRol\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_usuario_rol_idUsuarioRol\"")
	@Column(name="\"id_usuario_rol\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idUsuarioRol;

	//bi-directional many-to-one association to Rol
	@ManyToOne
	@JoinColumn(name="\"id_rol\"", insertable = false, updatable = false)
	private Rol rol;

	//bi-directional many-to-one association to Usuario
	@ManyToOne
	@JoinColumn(name="\"id_usuario\"", insertable = false, updatable = false)
	private Usuario usuario;

	public UsuarioRol() {
		this.rol = new Rol();
		this.usuario = new Usuario();
	}

	
	public UsuarioRol(Rol rol, Usuario usuario) {
		super();
		this.rol = rol;
		this.usuario = usuario;
	}


	public Integer getIdUsuarioRol() {
		return this.idUsuarioRol;
	}

	public void setIdUsuarioRol(Integer idUsuarioRol) {
		this.idUsuarioRol = idUsuarioRol;
	}

	public Rol getRol() {
		return this.rol;
	}

	public void setRol(Rol rol) {
		this.rol = rol;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}


	public Integer getIdRol() {
		return idRol;
	}


	public void setIdRol(Integer idRol) {
		this.idRol = idRol;
	}


	public Integer getIdUsuario() {
		return idUsuario;
	}


	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

}