package com.beca.misdivisas.model;

import java.sql.Timestamp;
import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;



public class AgenciaModel {
	private Integer idAgencia;
	
	@NotBlank(message = "requerido")
	private String agencia;
	private Integer nAgencia;
	
	private String latitud;
	private String longitud;

	private String fechaCreacion;
	private String fechaEstatus;
	private Integer idEstatusAgencia;	

	@NotNull(message = "requerido")
	private Integer idMunicipio;
	
	private Integer idEstado;
	
	private Boolean almacenamiento;	
	
	private Boolean recaudacion;	
	
	private String estatus;
	
	private String municipio;
	
	private String estado;


	public AgenciaModel(Integer idAgencia, String agencia, String latitud, String longitud, Integer idMunicipio, String estatus, Integer nAgencia, String fechaCreacion, String fechaEstatus, Boolean almacenamiento, Boolean recaudacion) {		

		this.idAgencia = idAgencia;
		this.agencia = agencia;
		this.nAgencia = nAgencia;
		this.longitud = longitud;
		this.latitud = latitud;
		this.fechaCreacion = fechaCreacion;
		this.fechaEstatus = fechaEstatus;
		this.estatus = estatus;
		this.idEstatusAgencia = idEstatusAgencia;
		this.idEstado = idEstado;
		this.idMunicipio = idMunicipio;
		this.almacenamiento = almacenamiento;
		this.recaudacion = recaudacion;
		
	}

	public AgenciaModel() {
	}

	public Integer getIdAgencia() {
		return idAgencia;
	}

	public void setIdAgencia(Integer idAgencia) {
		this.idAgencia = idAgencia;
	}

	public String getAgencia() {
		return agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public Integer getnAgencia() {
		return nAgencia;
	}

	public void setnAgencia(Integer nAgencia) {
		this.nAgencia = nAgencia;
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

	public String getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(String fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public String getFechaEstatus() {
		return fechaEstatus;
	}

	public void setFechaEstatus(String fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public Integer getIdEstatusAgencia() {
		return idEstatusAgencia;
	}

	public void setIdEstatusAgencia(Integer idEstatusAgencia) {
		this.idEstatusAgencia = idEstatusAgencia;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public Boolean getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(Boolean almacenamiento) {
		this.almacenamiento = almacenamiento;
	}
	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	
	public String getMunicipio() {
		return municipio;
	}

	public void setMunicipio(String municipio) {
		this.municipio = municipio;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public String getEstado() {
		return estado;
	}

	public void setEstado(String estado) {
		this.estado = estado;
	}

	public Boolean getRecaudacion() {
		return recaudacion;
	}

	public void setRecaudacion(Boolean recaudacion) {
		this.recaudacion = recaudacion;
	}

}
