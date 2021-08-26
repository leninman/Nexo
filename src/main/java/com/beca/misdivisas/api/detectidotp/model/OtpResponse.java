package com.beca.misdivisas.api.detectidotp.model;

import com.beca.misdivisas.api.detectidclient.model.DatosClientesDetectIdCRUD;
import com.beca.misdivisas.api.generico.model.Resultado;

public class OtpResponse {
	private Resultado resultado;
	private DatosClientesDetectIdCRUD datos;
	
	public DatosClientesDetectIdCRUD getDatos() {
		return datos;
	}
	public void setDatos(DatosClientesDetectIdCRUD datos) {
		this.datos = datos;
	}
	public Resultado getResultado() {
		return resultado;
	}
	public void setResultado(Resultado resultado) {
		this.resultado = resultado;
	}
	@Override
	public String toString() {
		return "OtpResponse [resultado=" + resultado + ", datos=" + datos + "]";
	}	
	
	
}
