package com.beca.misdivisas.api.correo.model;

import com.beca.misdivisas.api.generico.model.Resultado;

public class EnvioDeCorreoResponse {
	
	private Resultado resultado;

	public Resultado getResultado() {
		return resultado;
	}

	public void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}

	@Override
	public String toString() {
		return "EnvioDeCorreoResponse [resultado=" + resultado.toString() + "]";
	}
}
