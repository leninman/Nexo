package com.beca.misdivisas.model;

import java.util.List;

public class Locacion {
	private String sucursal;
	private Double latitud;
	private Double longitud;
	private String accion;
	private String logo;
	private int posicion;
	private List<String> saldos;
	
	public List<String> getSaldos() {
		return saldos;
	}
	public void setSaldos(List<String> saldos) {
		this.saldos = saldos;
	}
	public String getSucursal() {
		return sucursal;
	}
	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public Double getLatitud() {
		return latitud;
	}
	public void setLatitud(Double latitud) {
		this.latitud = latitud;
	}
	public Double getLongitud() {
		return longitud;
	}
	public void setLongitud(Double longitud) {
		this.longitud = longitud;
	}
	public String getAccion() {
		return accion;
	}
	public void setAccion(String accion) {
		this.accion = accion;
	}
	public String getLogo() {
		return logo;
	}
	public void setLogo(String logo) {
		this.logo = logo;
	}
	public int getPosicion() {
		return posicion;
	}
	public void setPosicion(int posicion) {
		this.posicion = posicion;
	}

}
