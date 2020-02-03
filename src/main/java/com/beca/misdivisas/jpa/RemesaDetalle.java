package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.math.BigDecimal;
import java.sql.Timestamp;


/**
 * The persistent class for the "REMESA_DETALLE" database table.
 * 
 */
@Entity
@Table(name="\"REMESA_DETALLE\"", schema ="\"ALMACEN\"")
@NamedQuery(name="RemesaDetalle.findAll", query="SELECT r FROM RemesaDetalle r")
public class RemesaDetalle implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_remesa_detalle\"")
	private Integer idRemesaDetalle;

	private Timestamp fecha;

	@Column(name="\"id_estatus_remesa\"")
	private Integer idEstatusRemesa;

	@Column(name="\"id_moneda\"")
	private Integer idMoneda;

	@Column(name="\"id_remesa\"")
	private Integer idRemesa;

	private BigDecimal monto;

	//bi-directional many-to-one association to Remesa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_remesa\"", insertable = false, updatable = false)
		})
	private Remesa remesa;

	public RemesaDetalle() {
	}

	public Integer getIdRemesaDetalle() {
		return this.idRemesaDetalle;
	}

	public void setIdRemesaDetalle(Integer idRemesaDetalle) {
		this.idRemesaDetalle = idRemesaDetalle;
	}

	public Timestamp getFecha() {
		return this.fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Integer getIdEstatusRemesa() {
		return this.idEstatusRemesa;
	}

	public void setIdEstatusRemesa(Integer idEstatusRemesa) {
		this.idEstatusRemesa = idEstatusRemesa;
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

	public BigDecimal getMonto() {
		return this.monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public Remesa getRemesa() {
		return this.remesa;
	}

	public void setRemesa(Remesa remesa) {
		this.remesa = remesa;
	}

}