package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ESTATUS_SUCURSAL" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_SUCURSAL\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusSucursal.findAll", query="SELECT e FROM EstatusSucursal e")
public class EstatusSucursal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_estatus_sucursal\"")
	private Integer idEstatusSucursal;

	@Column(name="\"estatus_sucursal\"")
	private String estatusSucursal;

	//bi-directional many-to-one association to Sucursal
	@OneToMany(mappedBy="estatusSucursal")
	private List<Sucursal> sucursals;

	public EstatusSucursal() {
	}

	public Integer getIdEstatusSucursal() {
		return this.idEstatusSucursal;
	}

	public void setIdEstatusSucursal(Integer idEstatusSucursal) {
		this.idEstatusSucursal = idEstatusSucursal;
	}

	public String getEstatusSucursal() {
		return this.estatusSucursal;
	}

	public void setEstatusSucursal(String estatusSucursal) {
		this.estatusSucursal = estatusSucursal;
	}

	public List<Sucursal> getSucursals() {
		return this.sucursals;
	}

	public void setSucursals(List<Sucursal> sucursals) {
		this.sucursals = sucursals;
	}

	public Sucursal addSucursal(Sucursal sucursal) {
		getSucursals().add(sucursal);
		sucursal.setEstatusSucursal(this);

		return sucursal;
	}

	public Sucursal removeSucursal(Sucursal sucursal) {
		getSucursals().remove(sucursal);
		sucursal.setEstatusSucursal(null);

		return sucursal;
	}

}