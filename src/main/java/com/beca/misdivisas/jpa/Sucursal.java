package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the "SUCURSAL" database table.
 * 
 */
@Entity
@Table(name="\"SUCURSAL\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Sucursal.findAll", query="SELECT s FROM Sucursal s")
public class Sucursal implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_sucursal\"")
	private Integer idSucursal;

	private Boolean acopio;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"fecha_estatus\"")
	private Timestamp fechaEstatus;

	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;

	@Column(name="\"id_estatus_sucursal\"")
	private Integer idEstatusSucursal;

	@Column(name="\"id_municipio\"")
	private Integer idMunicipio;

	private String latitud;

	private String longitud;

	private String sucursal;

	//bi-directional many-to-one association to Remesa
	@OneToMany(mappedBy="sucursal")
	private List<Remesa> remesas;

	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa\"", insertable = false, updatable = false)})
	private Empresa empresa;

	//bi-directional many-to-one association to EstatusSucursal
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estatus_sucursal\"", insertable = false, updatable = false)
		})
	private EstatusSucursal estatusSucursal;

	//bi-directional many-to-one association to Municipio
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_municipio\"", insertable = false, updatable = false)
		})
	private Municipio municipio;

	public Sucursal() {
	}

	public Integer getIdSucursal() {
		return this.idSucursal;
	}

	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}

	public Boolean getAcopio() {
		return this.acopio;
	}

	public void setAcopio(Boolean acopio) {
		this.acopio = acopio;
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

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Integer getIdEstatusSucursal() {
		return this.idEstatusSucursal;
	}

	public void setIdEstatusSucursal(Integer idEstatusSucursal) {
		this.idEstatusSucursal = idEstatusSucursal;
	}

	public Integer getIdMunicipio() {
		return this.idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getLatitud() {
		return this.latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return this.longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getSucursal() {
		return this.sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public List<Remesa> getRemesas() {
		return this.remesas;
	}

	public void setRemesas(List<Remesa> remesas) {
		this.remesas = remesas;
	}

	public Remesa addRemesa(Remesa remesa) {
		getRemesas().add(remesa);
		remesa.setSucursal(this);

		return remesa;
	}

	public Remesa removeRemesa(Remesa remesa) {
		getRemesas().remove(remesa);
		remesa.setSucursal(null);

		return remesa;
	}

	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public EstatusSucursal getEstatusSucursal() {
		return this.estatusSucursal;
	}

	public void setEstatusSucursal(EstatusSucursal estatusSucursal) {
		this.estatusSucursal = estatusSucursal;
	}

	public Municipio getMunicipio() {
		return this.municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

}