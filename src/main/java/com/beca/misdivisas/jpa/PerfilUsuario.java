package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "PERFIL_USUARIO" database table.
 * 
 */
@Entity
@Table(name="\"PERFIL_USUARIO\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="PerfilUsuario.findAll", query="SELECT p FROM PerfilUsuario p")
public class PerfilUsuario implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_perfil_usuario_idPerfilUsuario\"", sequenceName ="\"SEGURIDAD\".\"seq_perfil_usuario_idPerfilUsuario\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_perfil_usuario_idPerfilUsuario\"")
	@Column(name="\"id_perfil_usuario\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idPerfilUsuario;

	@Column(name="\"id_perfil\"")
	private Integer idPerfil;

	@Column(name="\"id_usuario\"")
	private Integer idUsuario;
	
	//bi-directional many-to-one association to Perfil
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_perfil\"", insertable = false, updatable = false)
	})
	private Perfil perfil;

	//bi-directional many-to-one association to Usuario
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_usuario\"", insertable = false, updatable = false)
	})
	private Usuario usuario;

	public PerfilUsuario() {
		this.perfil = new Perfil();
		this.usuario = new Usuario();
	}

	public Integer getIdPerfilUsuario() {
		return this.idPerfilUsuario;
	}

	public void setIdPerfilUsuario(Integer idPerfilUsuario) {
		this.idPerfilUsuario = idPerfilUsuario;
	}
	
	public Integer getIdPerfil() {
		return this.idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}


	public Perfil getPerfil() {
		return this.perfil;
	}

	public void setPerfil(Perfil perfil) {
		this.perfil = perfil;
	}

	public Usuario getUsuario() {
		return this.usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}

}