package com.beca.misdivisas.jpa;
import java.io.Serializable;
import java.sql.Timestamp;

import javax.persistence.*;

/**
 * The persistent class for the "USUARIO_EQUIPO_FRECUENTE" database table.
 * 
 */

@Entity
@Table(name = "\"USUARIO_EQUIPO_FRECUENTE\"", schema = "\"SEGURIDAD\"")
@NamedQuery(name = "UsuarioEquipoFrecuente.findAll", query = "SELECT u FROM UsuarioEquipoFrecuente u")
public class UsuarioEquipoFrecuente implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "\"SEGURIDAD\".\"seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente\"", sequenceName = "\"SEGURIDAD\".\"seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "\"SEGURIDAD\".\"seq_usuario_equipo_frecuente_idUsuarioEquipoFrecuente\"")
	@Column(name = "\"id_usuario_equipo_frecuente\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idUsuarioEquipoFrecuente;

	@Column(name = "\"id_usuario\"")
	private Integer idUsuario;

	@Column(name = "\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name = "\"direccion_ip\"")
	private String direccionIp;

	// bi-directional many-to-one association to Usuario
	@ManyToOne
	// @JoinColumns({ })
	@JoinColumn(name = "\"id_usuario\"", insertable = false, updatable = false)
	private Usuario usuario;

	public UsuarioEquipoFrecuente() {
	}

	public Integer getIdUsuarioEquipoFrecuente() {
		return idUsuarioEquipoFrecuente;
	}

	public void setIdUsuarioEquipoFrecuente(Integer idUsuarioEquipoFrecuente) {
		this.idUsuarioEquipoFrecuente = idUsuarioEquipoFrecuente;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public Timestamp getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getDireccionIp() {
		return direccionIp;
	}

	public void setDireccionIp(String direccionIp) {
		this.direccionIp = direccionIp;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
}
