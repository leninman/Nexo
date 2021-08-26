package com.beca.misdivisas.api.cuentas.client;

import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasRequest;
import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasResponse;

public interface IMicroServicioConsultarCuentas {

	public ConsultarCuentasResponse consultarCuentas(ConsultarCuentasRequest request) throws Exception;

	
}
