package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


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
}