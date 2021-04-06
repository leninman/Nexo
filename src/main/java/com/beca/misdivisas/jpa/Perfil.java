package com.beca.misdivisas.jpa;

import java.io.Serializable;
import java.sql.Timestamp;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "PERFIL" database table.
 * 
 */
@Entity
@Table(name="\"PERFIL\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="Perfil.findAll", query="SELECT p FROM Perfil p")
public class Perfil implements Serializable, Comparable<Perfil> {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"SEGURIDAD\".\"seq_perfil_idPerfil\"", sequenceName ="\"SEGURIDAD\".\"seq_perfil_idPerfil\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"SEGURIDAD\".\"seq_perfil_idPerfil\"")
	@Column(name="id_perfil", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idPerfil;

	@Column(name="editable")
	private Boolean editable;

	@Column(name="estado")
	private String estado;

	@Column(name="id_empresa")
	private Integer idEmpresa;

	@Column(name="perfil")
	private String perfil;

	@Column(name="tipo_perfil")
	private String tipoPerfil;

	@Column(name="tipo_vista")
	private String tipoVista;
	
	@Column(name="\"fecha_actualizacion\"")
	private Timestamp fechaActualizacion;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	//bi-directional many-to-one association to PerfilMenu
	@OneToMany(mappedBy="perfil")
	private List<PerfilMenu> perfilMenus;

	//bi-directional many-to-one association to PerfilUsuario
	@OneToMany(mappedBy="perfil")
	private List<PerfilUsuario> perfilUsuarios;

	public Perfil(Integer idPerfil) {
		super();
		this.idPerfil = idPerfil;
	}

	public Perfil() {
	}

	public Integer getIdPerfil() {
		return this.idPerfil;
	}

	public void setIdPerfil(Integer idPerfil) {
		this.idPerfil = idPerfil;
	}

	public Boolean getEditable() {
		return this.editable;
	}

	public void setEditable(Boolean editable) {
		this.editable = editable;
	}

	public String getEstado() {
		return this.estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getPerfil() {
		return this.perfil;
	}

	public void setPerfil(String perfil) {
		this.perfil = perfil;
	}

	public String getTipoPerfil() {
		return this.tipoPerfil;
	}

	public void setTipoPerfil(String tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}

	public String getTipoVista() {
		return this.tipoVista;
	}

	public void setTipoVista(String tipoVista) {
		this.tipoVista = tipoVista;
	}

	public Timestamp getFechaActualizacion() {
		return this.fechaActualizacion;
	}

	public void setFechaActualizacion(Timestamp fechaActualizacion) {
		this.fechaActualizacion = fechaActualizacion;
	}

	public Timestamp getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}
	
	public List<PerfilMenu> getPerfilMenus() {
		return this.perfilMenus;
	}

	public void setPerfilMenus(List<PerfilMenu> perfilMenus) {
		this.perfilMenus = perfilMenus;
	}

	public PerfilMenu addPerfilMenus(PerfilMenu perfilMenus) {
		getPerfilMenus().add(perfilMenus);
		perfilMenus.setPerfil(this);

		return perfilMenus;
	}

	public PerfilMenu removePerfilMenus(PerfilMenu perfilMenus) {
		getPerfilMenus().remove(perfilMenus);
		perfilMenus.setPerfil(null);

		return perfilMenus;
	}

	public List<PerfilUsuario> getPerfilUsuarios() {
		return this.perfilUsuarios;
	}

	public void setPerfilUsuarios(List<PerfilUsuario> perfilUsuarios) {
		this.perfilUsuarios = perfilUsuarios;
	}

	public PerfilUsuario addPerfilUsuario(PerfilUsuario perfilUsuario) {
		getPerfilUsuarios().add(perfilUsuario);
		perfilUsuario.setPerfil(this);

		return perfilUsuario;
	}

	public PerfilUsuario removePerfilUsuario(PerfilUsuario perfilUsuario) {
		getPerfilUsuarios().remove(perfilUsuario);
		perfilUsuario.setPerfil(null);

		return perfilUsuario;
	}

	@Override
	public int compareTo(Perfil p) {
		return perfil.compareTo(p.perfil);
	}

}