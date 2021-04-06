package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.sql.Timestamp;


/**
 * The persistent class for the "MUNICIPIO" database table.
 * 
 */
@Entity
@Table(name="\"MUNICIPIO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Municipio.findAll", query="SELECT m FROM Municipio m")
public class Municipio implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_municipio\"")
	private Integer idMunicipio;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"id_estado\"")
	private Integer idEstado;

	private String municipio;

	//bi-directional many-to-one association to Estado
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estado\"", insertable = false, updatable = false)
		})
	private Estado estado;


	public Municipio() {
	}

	public Integer getIdMunicipio() {
		return this.idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public Timestamp getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Integer getIdEstado() {
		return this.idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public String getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public Estado getEstado() {
		return this.estado;
	}

	public void setEstado(Estado estado) {
		this.estado = estado;
	}

}