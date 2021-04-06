package com.beca.misdivisas.model;

import java.sql.Timestamp;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

public class SucursalModel {
	private Integer idSucursal;
	private Integer idEmpresa;
	private Integer idEstatusSucursal;	
	private Integer idEstado;
	private Timestamp fechaEstatus;
	private Boolean acopio;

	@NotNull(message = "requerido")
	private Integer idMunicipio;

	@NotBlank(message = "requerido")
	private String latitud;

	@NotBlank(message = "requerido")
	private String longitud;

	@NotBlank(message = "requerido")
	private String nombre;

	public SucursalModel(Integer idSucursal, Integer idEmpresa, Integer idEstatusSucursal,
			Timestamp fechaEstatus, @NotNull(message = "requerido") Integer idMunicipio,
			@NotBlank(message = "requerido") String latitud, @NotBlank(message = "requerido") String longitud,
			@NotBlank(message = "requerido") String nombre, Boolean acopio) {
		this.idSucursal = idSucursal;
		this.idEmpresa = idEmpresa;
		this.idEstatusSucursal = idEstatusSucursal;
		this.fechaEstatus = fechaEstatus;
		this.idMunicipio = idMunicipio;
		this.latitud = latitud;
		this.longitud = longitud;
		this.nombre = nombre;
		this.acopio = acopio;
	}

	public Timestamp getFechaEstatus() {
		return fechaEstatus;
	}

	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public SucursalModel() {
	}

	public Integer getIdSucursal() {
		return idSucursal;
	}

	public void setIdSucursal(Integer idSucursal) {
		this.idSucursal = idSucursal;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Integer getIdEstatusSucursal() {
		return idEstatusSucursal;
	}

	public void setIdEstatusSucursal(Integer idEstatusSucursal) {
		this.idEstatusSucursal = idEstatusSucursal;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getLatitud() {
		return latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}
	

	public Boolean getAcopio() {
		return acopio == null ? false : acopio;
	}

	public void setAcopio(Boolean acopio) {
		this.acopio = acopio;
	}
}
