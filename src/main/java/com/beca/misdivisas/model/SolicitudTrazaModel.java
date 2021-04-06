package com.beca.misdivisas.model;

public class SolicitudTrazaModel {
	private String fecha;
	private String nombreUsuario;
	private String estatus;
	
	
	public SolicitudTrazaModel(String fecha, String nombreUsuario, String estatus) {
		super();
		this.fecha = fecha;
		this.nombreUsuario = nombreUsuario;
		this.estatus = estatus;
	}
	public String getFecha() {
		return fecha;
	}
	public void setFecha(String fecha) {
		this.fecha = fecha;
	}
	public String getNombreUsuario() {
		return nombreUsuario;
	}
	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	
}
