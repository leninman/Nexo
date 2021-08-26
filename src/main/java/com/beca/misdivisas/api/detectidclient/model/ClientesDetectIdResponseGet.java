package com.beca.misdivisas.api.detectidclient.model;


import com.beca.misdivisas.api.generico.model.Comision;
import com.beca.misdivisas.api.generico.model.Resultado;

public class ClientesDetectIdResponseGet {
	private Resultado resultado;
	private DatosClientesDetectIdGet datos;	
	private Comision comision;
	
	public Resultado getResultado() {
		return resultado;
	}
	public void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}
	public DatosClientesDetectIdGet getDatos() {
		return datos;
	}
	public void setDatos(DatosClientesDetectIdGet datos) {
		this.datos = datos;
	}
	public Comision getComision() {
		return comision;
	}
	public void setComision(Comision comision) {
		this.comision = comision;
	}
	
	@Override
	public String toString() {
		return "ClientesDetectIdResponseGet [resultado=" + resultado + ", datos=" + datos + ", comision=" + comision
				+ "]";
	}
}
