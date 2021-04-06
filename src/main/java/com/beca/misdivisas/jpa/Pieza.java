package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;


/**
 * The persistent class for the "PIEZA" database table.
 * 
 */
@Entity
@Table(name="\"PIEZA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Pieza.findAll", query="SELECT p FROM Pieza p")
public class Pieza implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_pieza\"")
	private Integer idPieza;

	@Column(name="\"cantidad_apta\"")
	private Integer cantidadApta;

	@Column(name="\"cantidad_no_apta\"")
	private Integer cantidadNoApta;

	@Column(name="\"id_denominacion\"")
	private Integer idDenominacion;

	@Column(name="\"id_moneda\"")
	private Integer idMoneda;

	@Column(name="\"id_remesa\"")
	private Integer idRemesa;

	//bi-directional many-to-one association to Denominacion
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_denominacion\"", insertable = false, updatable = false)
		})
	private Denominacion denominacion;

	//bi-directional many-to-one association to Moneda
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_moneda\"", insertable = false, updatable = false)
		})
	private Moneda moneda;

	//bi-directional many-to-one association to Remesa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_remesa\"", insertable = false, updatable = false)
		})
	private Remesa remesa;

	public Pieza() {
	}

	public Integer getIdPieza() {
		return this.idPieza;
	}

	public void setIdPieza(Integer idPieza) {
		this.idPieza = idPieza;
	}

	public Integer getCantidadApta() {
		return this.cantidadApta;
	}

	public void setCantidadApta(Integer cantidadApta) {
		this.cantidadApta = cantidadApta;
	}

	public Integer getCantidadNoApta() {
		return this.cantidadNoApta;
	}

	public void setCantidadNoApta(Integer cantidadNoApta) {
		this.cantidadNoApta = cantidadNoApta;
	}

	public Integer getIdDenominacion() {
		return this.idDenominacion;
	}

	public void setIdDenominacion(Integer idDenominacion) {
		this.idDenominacion = idDenominacion;
	}

	public Integer getIdMoneda() {
		return this.idMoneda;
	}

	public void setIdMoneda(Integer idMoneda) {
		this.idMoneda = idMoneda;
	}

	public Integer getIdRemesa() {
		return this.idRemesa;
	}

	public void setIdRemesa(Integer idRemesa) {
		this.idRemesa = idRemesa;
	}

	public Denominacion getDenominacion() {
		return this.denominacion;
	}

	public void setDenominacion(Denominacion denominacion) {
		this.denominacion = denominacion;
	}

	public Moneda getMoneda() {
		return this.moneda;
	}

	public void setMoneda(Moneda moneda) {
		this.moneda = moneda;
	}

	public Remesa getRemesa() {
		return this.remesa;
	}

	public void setRemesa(Remesa remesa) {
		this.remesa = remesa;
	}

}