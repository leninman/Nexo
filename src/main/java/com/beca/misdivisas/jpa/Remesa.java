package com.beca.misdivisas.jpa;

import java.io.Serializable;

import javax.persistence.*;

import java.util.Date;
import java.util.List;

/**
 * The persistent class for the "REMESA" database table.
 * 
 */
@Entity
@Table(name = "\"REMESA\"", schema = "\"ALMACEN\"")
@NamedQuery(name = "Remesa.findAll", query = "SELECT r FROM Remesa r")
public class Remesa implements Serializable, Comparable<Remesa> {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name = "\"id_remesa\"")
	private Integer idRemesa;

	@Column(name = "\"carta_porte\"")
	private String cartaPorte;

	private String descripcion;

	@Column(name = "\"id_agencia\"")
	private Integer idAgencia;

	@Column(name = "\"id_operacion\"")
	private Integer idOperacion;

	@Column(name = "\"id_remesa_coe\"")
	private Integer idRemesaCoe;

	@Column(name = "\"id_sucursal\"")
	private Integer idSucursal;

	@Column(name = "\"id_transportista\"")
	private Integer idTransportista;

	// bi-directional many-to-one association to Pieza
	@OneToMany(mappedBy = "remesa")
	private List<Pieza> piezas;

	// bi-directional many-to-one association to Agencia
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "\"id_agencia\"", insertable = false, updatable = false) })
	private Agencia agencia;

	// bi-directional many-to-one association to Operacion
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "\"id_operacion\"", insertable = false, updatable = false) })
	private Operacion operacion;

	// bi-directional many-to-one association to Sucursal
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "\"id_sucursal\"", insertable = false, updatable = false) })
	private Sucursal sucursal;

	// bi-directional many-to-one association to Transportista
	@ManyToOne
	@JoinColumns({ @JoinColumn(name = "\"id_transportista\"", insertable = false, updatable = false) })
	private Transportista transportista;

	// bi-directional many-to-one association to RemesaMonto
	@OneToMany(mappedBy = "remesa")
	private List<RemesaDetalle> remesaDetalles;

	public Remesa() {
	}

	public Integer getIdRemesa() {
		return this.idRemesa;
	}

	public void setIdRemesa(Integer idRemesa) {
		this.idRemesa = idRemesa;
	}

	public String getCartaPorte() {
		return this.cartaPorte;
	}

	public void setCartaPorte(String cartaPorte) {
		this.cartaPorte = cartaPorte;
	}

	public String getDescripcion() {
		return this.descripcion;
	}

	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}

	public Integer getIdAgencia() {
		return this.idAgencia;
	}

	public void setIdAgencia(Integer idAgencia) {
		this.idAgencia = idAgencia;
	}


	public Integer getIdOperacion() {
		return this.idOperacion;
	}

	public void setIdOperacion(Integer idOperacion) {
		this.idOperacion = idOperacion;
	}

	public Integer getIdRemesaCoe() {
		return this.idRemesaCoe;
	}

	public void setIdRemesaCoe(Integer idRemesaCoe) {
		this.idRemesaCoe = idRemesaCoe;
	}

	public Integer getIdSucursal() {
		return this.idSucursal;
	}

	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}

	public Integer getIdTransportista() {
		return this.idTransportista;
	}

	public void setIdTransportista(Integer idTransportista) {
		this.idTransportista = idTransportista;
	}

	public List<Pieza> getPiezas() {
		return this.piezas;
	}

	public void setPiezas(List<Pieza> piezas) {
		this.piezas = piezas;
	}

	public Pieza addPieza(Pieza pieza) {
		getPiezas().add(pieza);
		pieza.setRemesa(this);

		return pieza;
	}

	public Pieza removePieza(Pieza pieza) {
		getPiezas().remove(pieza);
		pieza.setRemesa(null);

		return pieza;
	}

	public Agencia getAgencia() {
		return this.agencia;
	}

	public void setAgencia(Agencia agencia) {
		this.agencia = agencia;
	}

	public Operacion getOperacion() {
		return this.operacion;
	}

	public void setOperacion(Operacion operacion) {
		this.operacion = operacion;
	}

	public Sucursal getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(Sucursal sucursal) {
		this.sucursal = sucursal;
	}

	public Transportista getTransportista() {
		return this.transportista;
	}

	public void setTransportista(Transportista transportista) {
		this.transportista = transportista;
	}

	public List<RemesaDetalle> getRemesaDetalles() {
		return this.remesaDetalles;
	}

	public void setRemesaDetalles(List<RemesaDetalle> remesaDetalles) {
		this.remesaDetalles = remesaDetalles;
	}

	public RemesaDetalle addRemesaDetalle(RemesaDetalle remesaDetalle) {
		getRemesaDetalles().add(remesaDetalle);
		remesaDetalle.setRemesa(this);

		return remesaDetalle;
	}

	public RemesaDetalle removeRemesaDetalle(RemesaDetalle remesaDetalle) {
		getRemesaDetalles().remove(remesaDetalle);
		remesaDetalle.setRemesa(null);

		return remesaDetalle;
	}

	@Override
	public int compareTo(Remesa o) {
		Date date1, date2;

		if (getRemesaDetalles().get(0).getFecha() != null && o.getRemesaDetalles().get(0).getFecha() != null) {
			date1 = getRemesaDetalles().get(0).getFecha();
			date2 = o.getRemesaDetalles().get(0).getFecha();
			return date1.compareTo(date2);
		}

		return 0;
	}

}