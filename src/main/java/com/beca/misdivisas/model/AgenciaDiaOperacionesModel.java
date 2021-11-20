package com.beca.misdivisas.model;

import java.util.List;


public class AgenciaDiaOperacionesModel {
	
	private Integer idAgencia;
	
	private String agencia;
	
	private Integer nAgencia;
	
	private Integer idEstatusAgencia;
	
	private Boolean almacenamiento;	
	
	private Boolean recaudacion;

	private List<AgenciaFechaOperaciones> agenciaFechaOperaciones;

	public AgenciaDiaOperacionesModel(Integer idAgencia, String agencia, Integer nAgencia, Integer idEstatusAgencia, Boolean almacenamiento,
			Boolean recaudacion, List<AgenciaFechaOperaciones> agenciaFechaOperaciones) {
		super();
		this.idAgencia = idAgencia;
		this.agencia = agencia;
		this.nAgencia = nAgencia;
		this.idEstatusAgencia = idEstatusAgencia;
		this.almacenamiento = almacenamiento;
		this.recaudacion = recaudacion;
		this.agenciaFechaOperaciones = agenciaFechaOperaciones;
	}
	
	public AgenciaDiaOperacionesModel() {
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

	public Boolean getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(Boolean almacenamiento) {
		this.almacenamiento = almacenamiento;
	}

	public Boolean getRecaudacion() {
		return recaudacion;
	}

	public void setRecaudacion(Boolean recaudacion) {
		this.recaudacion = recaudacion;
	}

	public List<AgenciaFechaOperaciones> getAgenciaFechaOperaciones() {
		return agenciaFechaOperaciones;
	}

	public void setAgenciaFechaOperaciones(List<AgenciaFechaOperaciones> agenciaFechaOperaciones) {
		this.agenciaFechaOperaciones = agenciaFechaOperaciones;
	}

	public Integer getIdEstatusAgencia() {
		return idEstatusAgencia;
	}

	public void setIdEstatusAgencia(Integer idEstatusAgencia) {
		this.idEstatusAgencia = idEstatusAgencia;
	}

	
}
