package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;
import java.util.List;


/**
 * The persistent class for the "EMPRESA" database table.
 * 
 */
@Entity
@Table(name="\"EMPRESA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Empresa.findAll", query="SELECT e FROM Empresa e")
public class Empresa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;

	@Column(name="\"caracter_rif\"")
	private String caracterRif;

	private String direccion;

	private String empresa;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"fecha_estatus\"")
	private Timestamp fechaEstatus;

	@Column(name="\"id_estatus_empresa\"")
	private Integer idEstatusEmpresa;

	@Column(name="\"id_municipio\"")
	private Integer idMunicipio;

	private Integer rif;
	
	private String sigla;
	
	private byte[] logo;

	//bi-directional many-to-one association to EstatusEmpresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estatus_empresa\"", insertable = false, updatable = false)
		})
	private EstatusEmpresa estatusEmpresa;

	//bi-directional many-to-one association to Sucursal
	@OneToMany(mappedBy="empresa")
	private List<Sucursal> sucursals;

	public Empresa() {
	}

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getCaracterRif() {
		return this.caracterRif;
	}

	public void setCaracterRif(String caracterRif) {
		this.caracterRif = caracterRif;
	}

	public String getDireccion() {
		return this.direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(String empresa) {
		this.empresa = empresa;
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

	public Integer getIdEstatusEmpresa() {
		return this.idEstatusEmpresa;
	}

	public void setIdEstatusEmpresa(Integer idEstatusEmpresa) {
		this.idEstatusEmpresa = idEstatusEmpresa;
	}

	public Integer getIdMunicipio() {
		return this.idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public Integer getRif() {
		return this.rif;
	}

	public void setRif(Integer rif) {
		this.rif = rif;
	}
	
	public String getSigla() {
		return this.sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public EstatusEmpresa getEstatusEmpresa() {
		return this.estatusEmpresa;
	}

	public void setEstatusEmpresa(EstatusEmpresa estatusEmpresa) {
		this.estatusEmpresa = estatusEmpresa;
	}

	public List<Sucursal> getSucursals() {
		return this.sucursals;
	}

	public void setSucursals(List<Sucursal> sucursals) {
		this.sucursals = sucursals;
	}

	public Sucursal addSucursal(Sucursal sucursal) {
		getSucursals().add(sucursal);
		sucursal.setEmpresa(this);

		return sucursal;
	}

	public Sucursal removeSucursal(Sucursal sucursal) {
		getSucursals().remove(sucursal);
		sucursal.setEmpresa(null);

		return sucursal;
	}

	public byte[] getLogo() {
		return logo;
	}

	public void setLogo(byte[] logo) {
		this.logo = logo;
	}

}