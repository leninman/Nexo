package com.beca.misdivisas.api.generico.model;

public class ResultadoGenerico {
	private String codigo;
	private String descripcion;
	
	public String getCodigo() {
		return codigo;
	}
	public void setCodigo(String codigo) {
		this.codigo = codigo;
	}
	public String getDescripcion() {
		return descripcion;
	}
	public void setDescripcion(String descripcion) {
		this.descripcion = descripcion;
	}
	@Override
	public String toString() {
		return "ResultadoGenerico [codigo=" + codigo + ", descripcion=" + descripcion + "]";
	}
}
