package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;


/**
 * The persistent class for the "SOLICITUD_RETIRO_TRAZA" database table.
 * 
 */
@Entity
@Table(name="\"SOLICITUD_RETIRO_TRAZA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="SolicitudRetiroTraza.findAll", query="SELECT s FROM SolicitudRetiroTraza s")
public class SolicitudRetiroTraza implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_solicitud_retiro_traza_idSolicitudRetiroTraza\"", sequenceName ="\"ALMACEN\".\"seq_solicitud_retiro_traza_idSolicitudRetiroTraza\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_solicitud_retiro_traza_idSolicitudRetiroTraza\"")
	@Column(name="\"id_solicitud_retiro_traza\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idSolicitudRetiroTraza;

	@Column(name="\"codigo_usuario\"")
	private String codigoUsuario;
	
	@Column(name="fecha")
	private Timestamp fecha;

	@Column(name="\"id_usuario\"")
	private Integer idUsuario;
	
	@Column(name="\"id_estatus_solicitud\"")
	private Integer idEstatusSolicitud;

	//bi-directional many-to-one association to EstatusSolicitudRetiro
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estatus_solicitud\"", insertable = false, updatable = false)
	})
	private EstatusSolicitudRetiro estatusSolicitudRetiro;

	@Column(name="\"id_motivo_rechazo\"")
	private Integer idMotivoRechazo;
	
	//bi-directional many-to-one association to MotivoRechazo
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_motivo_rechazo\"", insertable = false, updatable = false)
	})
	private MotivoRechazo motivoRechazo;

	public Integer getIdEstatusSolicitud() {
		return idEstatusSolicitud;
	}

	public void setIdEstatusSolicitud(Integer idEstatusSolicitud) {
		this.idEstatusSolicitud = idEstatusSolicitud;
	}

	public Integer getIdMotivoRechazo() {
		return idMotivoRechazo;
	}

	public void setIdMotivoRechazo(Integer idMotivoRechazo) {
		this.idMotivoRechazo = idMotivoRechazo;
	}

	public Integer getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	@Column(name="\"id_solicitud\"")
	private Integer idSolicitud;
	
	//bi-directional many-to-one association to SolicitudRetiro
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_solicitud\"", insertable = false, updatable = false)
	})
	private SolicitudRetiro solicitudRetiro;
	
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_usuario\"", insertable = false, updatable = false)
	})
	private Usuario usuario;

	public SolicitudRetiroTraza() {
	}
	
	public SolicitudRetiroTraza(Integer idSolicitud, Integer idEstatusSolicitud, Integer idUsuario, Timestamp fecha, String codigoUsuario) {
		this.idSolicitud = idSolicitud;
		this.idEstatusSolicitud = idEstatusSolicitud;
		this.idUsuario = idUsuario;
		this.fecha = fecha;
		this.codigoUsuario = codigoUsuario;
	}

	public Integer getIdSolicitudRetiroTraza() {
		return this.idSolicitudRetiroTraza;
	}

	public void setIdSolicitudRetiroTraza(Integer idSolicitudRetiroTraza) {
		this.idSolicitudRetiroTraza = idSolicitudRetiroTraza;
	}

	public String getCodigoUsuario() {
		return this.codigoUsuario;
	}

	public void setCodigoUsuario(String codigoUsuario) {
		this.codigoUsuario = codigoUsuario;
	}

	public Timestamp getFecha() {
		return this.fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Integer getIdUsuario() {
		return this.idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public EstatusSolicitudRetiro getEstatusSolicitudRetiro() {
		return this.estatusSolicitudRetiro;
	}

	public void setEstatusSolicitudRetiro(EstatusSolicitudRetiro estatusSolicitudRetiro) {
		this.estatusSolicitudRetiro = estatusSolicitudRetiro;
	}

	public MotivoRechazo getMotivoRechazo() {
		return this.motivoRechazo;
	}

	public void setMotivoRechazo(MotivoRechazo motivoRechazo) {
		this.motivoRechazo = motivoRechazo;
	}

	public SolicitudRetiro getSolicitudRetiro() {
		return this.solicitudRetiro;
	}

	public void setSolicitudRetiro(SolicitudRetiro solicitudRetiro) {
		this.solicitudRetiro = solicitudRetiro;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public void setUsuario(Usuario usuario) {
		this.usuario = usuario;
	}
	

}