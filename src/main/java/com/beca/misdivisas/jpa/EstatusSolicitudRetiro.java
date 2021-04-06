package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ESTATUS_SOLICITUD_RETIRO" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_SOLICITUD_RETIRO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusSolicitudRetiro.findAll", query="SELECT e FROM EstatusSolicitudRetiro e")
public class EstatusSolicitudRetiro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_estatus_solicitud_retiro_idEstatusSolicitud\"", sequenceName ="\"ALMACEN\".\"seq_estatus_solicitud_retiro_idEstatusSolicitud\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_estatus_solicitud_retiro_idEstatusSolicitud\"")
	@Column(name="\"id_estatus_solicitud\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idEstatusSolicitud;

	@Column(name="\"estatus_solicitud\"")
	private String estatusSolicitud;

	//bi-directional many-to-one association to SolicitudRetiroTraza
	@OneToMany(mappedBy="estatusSolicitudRetiro")
	private List<SolicitudRetiroTraza> solicitudRetiroTrazas;

	public EstatusSolicitudRetiro() {
	}

	public Integer getIdEstatusSolicitud() {
		return this.idEstatusSolicitud;
	}

	public void setIdEstatusSolicitud(Integer idEstatusSolicitud) {
		this.idEstatusSolicitud = idEstatusSolicitud;
	}

	public String getEstatusSolicitud() {
		return this.estatusSolicitud;
	}

	public void setEstatusSolicitud(String estatusSolicitud) {
		this.estatusSolicitud = estatusSolicitud;
	}

	public List<SolicitudRetiroTraza> getSolicitudRetiroTrazas() {
		return this.solicitudRetiroTrazas;
	}

	public void setSolicitudRetiroTrazas(List<SolicitudRetiroTraza> solicitudRetiroTrazas) {
		this.solicitudRetiroTrazas = solicitudRetiroTrazas;
	}

	public SolicitudRetiroTraza addSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().add(solicitudRetiroTraza);
		solicitudRetiroTraza.setEstatusSolicitudRetiro(this);

		return solicitudRetiroTraza;
	}

	public SolicitudRetiroTraza removeSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().remove(solicitudRetiroTraza);
		solicitudRetiroTraza.setEstatusSolicitudRetiro(null);

		return solicitudRetiroTraza;
	}

}