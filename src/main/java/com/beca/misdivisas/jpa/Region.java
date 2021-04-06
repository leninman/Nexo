package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the "REGION" database table.
 * 
 */
@Entity
@Table(name="\"REGION\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Region.findAll", query="SELECT r FROM Region r")
public class Region implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_region\"")
	private Integer idRegion;

	private String region;

	//bi-directional many-to-one association to Estado
	@JsonBackReference
	@OneToMany(mappedBy="region")
	private List<Estado> estados;

	public Region() {
	}

	public Integer getIdRegion() {
		return this.idRegion;
	}

	public void setIdRegion(Integer idRegion) {
		this.idRegion = idRegion;
	}

	public String getRegion() {
		return this.region;
	}

	public void setRegion(String region) {
		this.region = region;
	}

	public List<Estado> getEstados() {
		return this.estados;
	}

	public void setEstados(List<Estado> estados) {
		this.estados = estados;
	}

	public Estado addEstado(Estado estado) {
		getEstados().add(estado);
		estado.setRegion(this);

		return estado;
	}

	public Estado removeEstado(Estado estado) {
		getEstados().remove(estado);
		estado.setRegion(null);

		return estado;
	}

}