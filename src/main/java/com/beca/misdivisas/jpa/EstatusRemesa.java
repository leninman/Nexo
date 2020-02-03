package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ESTATUS_REMESA" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_REMESA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusRemesa.findAll", query="SELECT e FROM EstatusRemesa e")
public class EstatusRemesa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_estatus_remesa\"")
	private Integer idEstatusRemesa;

	@Column(name="\"estatus_remesa\"")
	private String estatusRemesa;

	/*
	//bi-directional many-to-one association to Remesa
	@OneToMany(mappedBy="estatusRemesa")
	private List<Remesa> remesas;
	*/
	
	public EstatusRemesa() {
	}

	public Integer getIdEstatusRemesa() {
		return this.idEstatusRemesa;
	}

	public void setIdEstatusRemesa(Integer idEstatusRemesa) {
		this.idEstatusRemesa = idEstatusRemesa;
	}

	public String getEstatusRemesa() {
		return this.estatusRemesa;
	}

	public void setEstatusRemesa(String estatusRemesa) {
		this.estatusRemesa = estatusRemesa;
	}

	/*
	public List<Remesa> getRemesas() {
		return this.remesas;
	}

	public void setRemesas(List<Remesa> remesas) {
		this.remesas = remesas;
	}

	public Remesa addRemesa(Remesa remesa) {
		getRemesas().add(remesa);
		remesa.setEstatusRemesa(this);

		return remesa;
	}

	public Remesa removeRemesa(Remesa remesa) {
		getRemesas().remove(remesa);
		remesa.setEstatusRemesa(null);

		return remesa;
	}
	*/

}