package com.beca.misdivisas.api.generico;

import java.util.List;

import com.beca.misdivisas.api.cuentas.model.CuentaConsultarCuentas;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseCRUD;
import com.beca.misdivisas.api.detectidclient.model.DatosClientesDetectIdGet;

public interface IMicroservicioService {
			
	public List<CuentaConsultarCuentas> consultarCuentas(String docCliente, String ipOrigen) throws Exception;
		
	public boolean enviarCorreo(String para,String asunto, String encabezado, String cuerpo, String pie, boolean esNotificacion, String ipOrigen) throws Exception;
	
	public ClientesDetectIdResponseCRUD detectIdCRUD(String nombre, String sharedKey, String telefono, String email, String ipOrigen, String operacion) throws Exception;
		
	public DatosClientesDetectIdGet obtenerClienteDetectId(String sharedKey, String telefono, String email, String ipOrigen) throws Exception;
	
	public String solicitarValidarOTP(String sharedKey, String otp, String ipOrigen, String proposito, String operacion) throws Exception;
		
	public boolean enviarOTP(String tipoDocumento, String nroDocumento, String nombre,String telefono, String correo, String proposito, String ipOrigen) throws Exception;
		
}
