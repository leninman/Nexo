package com.beca.misdivisas.jpa;


import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the "DENOMINACION" database table.
 * 
 */
@Entity
@Table(name="\"DENOMINACION\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Denominacion.findAll", query="SELECT d FROM Denominacion d")
public class Denominacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_denominacion\"")
	private Integer idDenominacion;

	private Integer denominacion;

	//bi-directional many-to-one association to Pieza
	@JsonBackReference
	@OneToMany(mappedBy="denominacion")
	private List<Pieza> piezas;

	public Denominacion() {
	}

	public Integer getIdDenominacion() {
		return this.idDenominacion;
	}

	public void setIdDenominacion(Integer idDenominacion) {
		this.idDenominacion = idDenominacion;
	}

	public Integer getDenominacion() {
		return this.denominacion;
	}

	public void setDenominacion(Integer denominacion) {
		this.denominacion = denominacion;
	}

	public List<Pieza> getPiezas() {
		return this.piezas;
	}

	public void setPiezas(List<Pieza> piezas) {
		this.piezas = piezas;
	}

	public Pieza addPieza(Pieza pieza) {
		getPiezas().add(pieza);
		pieza.setDenominacion(this);

		return pieza;
	}

	public Pieza removePieza(Pieza pieza) {
		getPiezas().remove(pieza);
		pieza.setDenominacion(null);

		return pieza;
	}

}