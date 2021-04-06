package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "TIPO_AUTORIZADO" database table.
 * 
 */
@Entity
@Table(name="\"TIPO_AUTORIZADO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="TipoAutorizado.findAll", query="SELECT t FROM TipoAutorizado t")
public class TipoAutorizado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_tipo_autorizado_idTipoAutorizado\"", sequenceName ="\"ALMACEN\".\"seq_tipo_autorizado_idTipoAutorizado\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_tipo_autorizado_idTipoAutorizado\"")
	@Column(name="\"id_tipo_autorizado\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idTipoAutorizado;

	@Column(name="\"tipo_autorizado\"")
	private String tipoAutorizado;

	@Column(name="\"tipo_solicitud\"")
	private String tipoSolicitud;


	public TipoAutorizado() {
	}

	public Integer getIdTipoAutorizado() {
		return this.idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}

	public String getTipoAutorizado() {
		return this.tipoAutorizado;
	}

	public void setTipoAutorizado(String tipoAutorizado) {
		this.tipoAutorizado = tipoAutorizado;
	}

	public String getTipoSolicitud() {
		return this.tipoSolicitud;
	}

	public void setTipoSolicitud(String tipoSolicitud) {
		this.tipoSolicitud = tipoSolicitud;
	}
}
