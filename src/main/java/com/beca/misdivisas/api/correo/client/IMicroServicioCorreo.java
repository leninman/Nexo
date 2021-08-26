package com.beca.misdivisas.api.correo.client;

import com.beca.misdivisas.api.correo.model.EnvioDeCorreoRequest;
import com.beca.misdivisas.api.correo.model.EnvioDeCorreoResponse;

public interface IMicroServicioCorreo {

	public EnvioDeCorreoResponse enviarCorreo(EnvioDeCorreoRequest request) throws Exception;
	
}
