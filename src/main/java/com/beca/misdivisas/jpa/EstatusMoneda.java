package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ESTATUS_MONEDA" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_MONEDA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusMoneda.findAll", query="SELECT e FROM EstatusMoneda e")
public class EstatusMoneda implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_estatus_moneda\"")
	private Integer idEstatusMoneda;

	@Column(name="\"estatus_moneda\"")
	private String estatusMoneda;

	//bi-directional many-to-one association to Moneda
	@OneToMany(mappedBy="estatusMoneda")
	private List<Moneda> monedas;

	public EstatusMoneda() {
	}

	public Integer getIdEstatusMoneda() {
		return this.idEstatusMoneda;
	}

	public void setIdEstatusMoneda(Integer idEstatusMoneda) {
		this.idEstatusMoneda = idEstatusMoneda;
	}

	public String getEstatusMoneda() {
		return this.estatusMoneda;
	}

	public void setEstatusMoneda(String estatusMoneda) {
		this.estatusMoneda = estatusMoneda;
	}

	public List<Moneda> getMonedas() {
		return this.monedas;
	}

	public void setMonedas(List<Moneda> monedas) {
		this.monedas = monedas;
	}

	public Moneda addMoneda(Moneda moneda) {
		getMonedas().add(moneda);
		moneda.setEstatusMoneda(this);

		return moneda;
	}

	public Moneda removeMoneda(Moneda moneda) {
		getMonedas().remove(moneda);
		moneda.setEstatusMoneda(null);

		return moneda;
	}

}