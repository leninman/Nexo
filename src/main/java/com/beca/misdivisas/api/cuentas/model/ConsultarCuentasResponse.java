package com.beca.misdivisas.api.cuentas.model;

import com.beca.misdivisas.api.generico.model.Comision;
import com.beca.misdivisas.api.generico.model.ResultadoGenerico;

public class ConsultarCuentasResponse {
	
	private ResultadoGenerico resultado;
	private DatosConsultarCuentas datos;
	private Comision comision;
	
	public ResultadoGenerico getResultado() {
		return resultado;
	}
	public void setResultado(ResultadoGenerico resultado) {
		this.resultado = resultado;
	}
	public DatosConsultarCuentas getDatos() {
		return datos;
	}
	public void setDatos(DatosConsultarCuentas datos) {
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
		return "ConsultarCuentasResponse [resultado=" + resultado + ", datos=" + datos + ", comision=" + comision + "]";
	}
	
}
