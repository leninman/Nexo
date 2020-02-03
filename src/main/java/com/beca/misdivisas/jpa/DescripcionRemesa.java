package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "DESCRIPCION_REMESA" database table.
 * 
 */
@Entity
@Table(name="\"DESCRIPCION_REMESA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="DescripcionRemesa.findAll", query="SELECT d FROM DescripcionRemesa d")
public class DescripcionRemesa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_descripcion_remesa\"")
	private Integer idDescripcionRemesa;

	@Column(name="\"estatus_remesa\"")
	private String estatusRemesa;

	public DescripcionRemesa() {
	}

	public Integer getIdDescripcionRemesa() {
		return this.idDescripcionRemesa;
	}

	public void setIdDescripcionRemesa(Integer idDescripcionRemesa) {
		this.idDescripcionRemesa = idDescripcionRemesa;
	}

	public String getEstatusRemesa() {
		return this.estatusRemesa;
	}

	public void setEstatusRemesa(String estatusRemesa) {
		this.estatusRemesa = estatusRemesa;
	}

}