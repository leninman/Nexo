package com.beca.misdivisas.api.detectidclient.client;

import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdRequest;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseCRUD;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseGet;

public interface IMicroServicioClienteDetectID {

	public ClientesDetectIdResponseGet detectIdGet(ClientesDetectIdRequest request) throws Exception;
	
	public ClientesDetectIdResponseCRUD detectIdCRUD(ClientesDetectIdRequest request, String operacion) throws Exception;

	
}
