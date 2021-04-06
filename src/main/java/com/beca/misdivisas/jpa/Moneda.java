package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the "MONEDA" database table.
 * 
 */
@Entity
@Table(name="\"MONEDA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Moneda.findAll", query="SELECT m FROM Moneda m")
public class Moneda implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_moneda\"")
	private Integer idMoneda;

	private String codigo;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"fecha_estatus\"")
	private Timestamp fechaEstatus;

	@Column(name="\"id_estatus_moneda\"")
	private Integer idEstatusMoneda;

	private String moneda;

	private String simbolo;

	//bi-directional many-to-one association to EstatusMoneda
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estatus_moneda\"", insertable = false, updatable = false)
		})
	private EstatusMoneda estatusMoneda;


	public Moneda() {
	}

	public Integer getIdMoneda() {
		return this.idMoneda;
	}

	public void setIdMoneda(Integer idMoneda) {
		this.idMoneda = idMoneda;
	}

	public String getCodigo() {
		return this.codigo;
	}

	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}

	public Timestamp getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Timestamp getFechaEstatus() {
		return this.fechaEstatus;
	}

	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public Integer getIdEstatusMoneda() {
		return this.idEstatusMoneda;
	}

	public void setIdEstatusMoneda(Integer idEstatusMoneda) {
		this.idEstatusMoneda = idEstatusMoneda;
	}

	public String getMoneda() {
		return this.moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getSimbolo() {
		return this.simbolo;
	}

	public void setSimbolo(String simbolo) {
		this.simbolo = simbolo;
	}

	public EstatusMoneda getEstatusMoneda() {
		return this.estatusMoneda;
	}

	public void setEstatusMoneda(EstatusMoneda estatusMoneda) {
		this.estatusMoneda = estatusMoneda;
	}


}