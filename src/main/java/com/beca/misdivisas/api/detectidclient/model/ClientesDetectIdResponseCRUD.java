package com.beca.misdivisas.api.detectidclient.model;

import com.beca.misdivisas.api.generico.model.Comision;
import com.beca.misdivisas.api.generico.model.Resultado;

public class ClientesDetectIdResponseCRUD {
	
	private Resultado resultado;
	private DatosClientesDetectIdCRUD datos;
	private Comision comision;
	

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

	public Comision getComision() {
		return comision;
	}

	public void setComision(Comision comision) {
		this.comision = comision;
	}

	@Override
	public String toString() {
		return "ClientesDetectIdResponseCRUD [resultado=" + resultado + ", datos=" + datos + ", comision=" + comision
				+ "]";
	}


}
