package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "ESTATUS_REMESA_OLD" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_REMESA_OLD\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusRemesaOld.findAll", query="SELECT e FROM EstatusRemesaOld e")
public class EstatusRemesaOld implements Serializable {
	private static final long serialVersionUID = 1L;

	@Column(name="\"estatusRemesa\"")
	private String estatusRemesa;

	@Id
	@Column(name="\"idEstatusRemesa\"")
	private Integer idEstatusRemesa;

	public EstatusRemesaOld() {
	}

	public String getEstatusRemesa() {
		return this.estatusRemesa;
	}

	public void setEstatusRemesa(String estatusRemesa) {
		this.estatusRemesa = estatusRemesa;
	}

	public Integer getIdEstatusRemesa() {
		return this.idEstatusRemesa;
	}

	public void setIdEstatusRemesa(Integer idEstatusRemesa) {
		this.idEstatusRemesa = idEstatusRemesa;
	}

}