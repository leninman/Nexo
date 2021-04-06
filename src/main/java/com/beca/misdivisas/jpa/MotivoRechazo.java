package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "MOTIVO_RECHAZO" database table.
 * 
 */
@Entity
@Table(name="\"MOTIVO_RECHAZO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="MotivoRechazo.findAll", query="SELECT m FROM MotivoRechazo m")
public class MotivoRechazo implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_motivo_rechazo_idMotivoRechazo\"", sequenceName ="\"ALMACEN\".\"seq_motivo_rechazo_idMotivoRechazo\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_motivo_rechazo_idMotivoRechazo\"")
	@Column(name="\"id_motivo_rechazo\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idMotivoRechazo;

	@Column(name="motivo")
	private String motivo;

	@Column(name="\"tipo_solicitud\"")
	private String tipoSolicitud;

	//bi-directional many-to-one association to SolicitudRetiroTraza
	@OneToMany(mappedBy="motivoRechazo")
	private List<SolicitudRetiroTraza> solicitudRetiroTrazas;

	public MotivoRechazo() {
	}

	public Integer getIdMotivoRechazo() {
		return this.idMotivoRechazo;
	}

	public void setIdMotivoRechazo(Integer idMotivoRechazo) {
		this.idMotivoRechazo = idMotivoRechazo;
	}

	public String getMotivo() {
		return this.motivo;
	}

	public void setMotivo(String motivo) {
		this.motivo = motivo;
	}

	public String getTipoSolicitud() {
		return this.tipoSolicitud;
	}

	public void setTipoSolicitud(String tipoSolicitud) {
		this.tipoSolicitud = tipoSolicitud;
	}

	public List<SolicitudRetiroTraza> getSolicitudRetiroTrazas() {
		return this.solicitudRetiroTrazas;
	}

	public void setSolicitudRetiroTrazas(List<SolicitudRetiroTraza> solicitudRetiroTrazas) {
		this.solicitudRetiroTrazas = solicitudRetiroTrazas;
	}

	public SolicitudRetiroTraza addSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().add(solicitudRetiroTraza);
		solicitudRetiroTraza.setMotivoRechazo(this);

		return solicitudRetiroTraza;
	}

	public SolicitudRetiroTraza removeSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().remove(solicitudRetiroTraza);
		solicitudRetiroTraza.setMotivoRechazo(null);

		return solicitudRetiroTraza;
	}

}