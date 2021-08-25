package com.beca.misdivisas.api.generico.model;

public class Resultado {
    private String codStatus;
    private String status;
	private String codigo;
	private String descripcion;
    
    public String getCodStatus() {
		return codStatus;
	}

	public void setCodStatus(String codStatus) {
		this.codStatus = codStatus;
	}

	public String getStatus() {
		return status;
	}

	public void setStatus(String status) {
		this.status = status;
	}
	
	public Resultado() {
		super();
	}

	public Resultado(String codigo, String descripcion) {
		super();
		this.codigo = codigo;
		this.descripcion = descripcion;
	}

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
		return "Resultado [codStatus=" + codStatus + ", status=" + status + ", codigo=" + codigo + ", descripcion="
				+ descripcion + "]";
	}

}
