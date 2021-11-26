package com.beca.misdivisas.api.generico;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.security.auth.login.CredentialException;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.api.correo.client.IMicroServicioCorreo;
import com.beca.misdivisas.api.correo.model.EnvioDeCorreoRequest;
import com.beca.misdivisas.api.correo.model.EnvioDeCorreoResponse;
import com.beca.misdivisas.api.cuentas.client.IMicroServicioConsultarCuentas;
import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasRequest;
import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasResponse;
import com.beca.misdivisas.api.cuentas.model.CuentaConsultarCuentas;
import com.beca.misdivisas.api.detectidclient.client.IMicroServicioClienteDetectID;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdRequest;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseCRUD;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseGet;
import com.beca.misdivisas.api.detectidclient.model.DatosClientesDetectIdCRUD;
import com.beca.misdivisas.api.detectidclient.model.DatosClientesDetectIdGet;
import com.beca.misdivisas.api.detectidotp.client.IMicroServicioOTPDetectID;
import com.beca.misdivisas.api.detectidotp.model.OtpRequest;
import com.beca.misdivisas.api.detectidotp.model.OtpResponse;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Service
public class MicroservicioService implements IMicroservicioService {
	private static final Logger logger = LoggerFactory.getLogger(MicroservicioService.class);
	
	@Autowired
	IMicroServicioCorreo apiCorreo;
	@Autowired
	IMicroServicioOTPDetectID apiOTPDetectID;
	@Autowired
	IMicroServicioConsultarCuentas apiCuentas;
	@Autowired
	IMicroServicioClienteDetectID apiClientesDetectID;
	
	@Value("${api.usuario}")
	private String usuarioMS;
	@Value("${api.canal}")
	private String canal;	
	@Value("${correo.remitente}")
	private String remitente;
	@Value("${canal.detectid}")
	private String canalDetectid;
	@Value("${modulo.detectid}")
	private String moduloDetectid;
	
		
	public List<CuentaConsultarCuentas> consultarCuentas(String docCliente, String ipOrigen) throws Exception{
		List<CuentaConsultarCuentas> cuentas = null;
		
		ConsultarCuentasRequest cuentasRequest = new ConsultarCuentasRequest();
		cuentasRequest.setIdCanal(canal);
		cuentasRequest.setIdCliente(docCliente);
		cuentasRequest.setIdConsumidor(usuarioMS);
		//cuentasRequest.setIdTerminal();
		cuentasRequest.setIdUsuario(usuarioMS);
		cuentasRequest.setIpOrigen(ipOrigen);
		ConsultarCuentasResponse response;
		
		try {
			response = apiCuentas.consultarCuentas(cuentasRequest);
			
			if (response != null) {
				if (response.getResultado().getCodigo().equals("0000")) {
					cuentas = response.getDatos().getCuentas();
					cuentas = cuentas.stream().filter(x -> x.getEstatus().equalsIgnoreCase(Constantes.ACTIVA)).collect(Collectors.toList());
					int i=0;
					for (CuentaConsultarCuentas cta : cuentas) {
						cta.setId(i);
						i++;
					}
				} else {
					throw new Exception("Respuesta con error " + response.getResultado().getCodigo());
				}
			} else {
				throw new Exception("Respuesta nula");
			}
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			e.printStackTrace();
			throw e;
		}
		return cuentas;
	}
	
		
	public boolean enviarCorreo(String para,String asunto, String encabezado, String cuerpo, String pie, boolean esNotificacion, String ipOrigen) throws Exception{
		EnvioDeCorreoRequest request= new EnvioDeCorreoRequest();
		DateFormat formatoFecha = new SimpleDateFormat(Constantes.FORMATO_FECHA_YYYYMMDDHHMMSS);
		
		request.setAsunto(asunto);
		request.setCuerpo(cuerpo);
		request.setDesde(remitente);
		request.setEncabezado(encabezado);
		request.setIdCanal(Integer.parseInt(canal));
		request.setNotificacion(esNotificacion);
		request.setPara(para);
		request.setPie(pie);
		
		request.setIdCliente(usuarioMS);
		request.setIdSesion(formatoFecha.format(new Date()));
		request.setIpOrigen(ipOrigen);		
		
		EnvioDeCorreoResponse response=null;
		try {
			response = apiCorreo.enviarCorreo(request);			
		}catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			return false;
		}
		return true;
	}
	
	
	public ClientesDetectIdResponseCRUD detectIdCRUD(String nombre, String sharedKey, String telefono, String email, String ipOrigen, String operacion) throws Exception{
		DateFormat formatoFecha = new SimpleDateFormat(Constantes.FORMATO_FECHA_YYYYMMDDHHMMSS);
		ClientesDetectIdRequest request = new ClientesDetectIdRequest();
		
		request.setIdCanal(Integer.parseInt(canal));
		request.setIdSesion(formatoFecha.format(new Date()));
		request.setIp(ipOrigen);
		request.setIpOrigen(ipOrigen);
		request.setSharedKey(sharedKey);
		request.setEmail(email);
		request.setTelefono(telefono);
		request.setBusinessDescription(nombre);
		request.setIdCliente(canalDetectid);
		
		ClientesDetectIdResponseCRUD response = null;
		
		try {
			response = apiClientesDetectID.detectIdCRUD(request, operacion);			
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		return response;
	}
	
	public DatosClientesDetectIdGet obtenerClienteDetectId(String sharedKey, String telefono, String email, String ipOrigen) throws Exception{
		DateFormat formatoFecha = new SimpleDateFormat(Constantes.FORMATO_FECHA_YYYYMMDDHHMMSS);
		ClientesDetectIdRequest request = new ClientesDetectIdRequest();
		request.setIdCanal(Integer.parseInt(canal));
		request.setIdSesion(formatoFecha.format(new Date()));
		request.setIp(ipOrigen);
		request.setIpOrigen(ipOrigen);
		request.setSharedKey(sharedKey);
		request.setEmail(email);
		request.setTelefono(telefono);
		request.setIdCliente(canalDetectid);
		ClientesDetectIdResponseGet response = null;
		
		try {
			response = apiClientesDetectID.detectIdGet(request);
			if(response!=null && response.getResultado() != null && response.getResultado().getCodigo().equalsIgnoreCase("0000")) {
				if(response.getDatos() != null)
					return response.getDatos();
			}			
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		return null;
	}
	
	public String solicitarValidarOTP(String sharedKey, String otp, String ipOrigen, String proposito, String operacion) throws Exception{
		DateFormat formatoFecha = new SimpleDateFormat(Constantes.FORMATO_FECHA_YYYYMMDDHHMMSS);
		OtpRequest request = new OtpRequest();
		request.setIdCanal(Integer.parseInt(canal));
		request.setIdSesion(formatoFecha.format(new Date()));
		request.setIdUsuario(canalDetectid);
		request.setSharedKey(sharedKey);
		request.setChannel(canalDetectid);
		request.setModulo(moduloDetectid);
		request.setIp(ipOrigen);
		request.setProposito(proposito);
		String resultado ="Exito";
		if(otp != null)
			request.setOtp(otp);
		
		OtpResponse response;
		
		try {
			response = apiOTPDetectID.crearValidarOTP(request, operacion);
			if(response!=null && response.getResultado() != null) {
				switch (response.getResultado().getCodigo()) {
				case "0000"://exitoso
					resultado = "000";
					break;
				case "902"://
					resultado = "902"; //"OTP invalido"
					break;
				case "904":
					resultado = "904"; //"Excedio el numero de intentos"
					break;
				case "938":
					resultado = "938"; //"OTP Expirado"
					break;
				default:
					break;
				}				
			}						
		} catch (Exception e) {
			if(e.getLocalizedMessage().contains("902"))
				return "902";
			else if(e.getLocalizedMessage().contains("904"))
				return "904";
			else
				throw e;
		}
		
		return resultado;
	}
	
	
	public boolean enviarOTP(String tipoDocumento, String nroDocumento, String nombre,String telefono, String correo, String proposito, String ipOrigen) throws Exception{
		boolean existe= false, generado= false, creado = false;
		ClientesDetectIdResponseCRUD response=null;
		DatosClientesDetectIdCRUD datos;
		String resp;
		try {
			try {
				response = detectIdCRUD(nombre, tipoDocumento+nroDocumento, telefono, correo, ipOrigen, Constantes.GET);
				if(response!= null && response.getResultado()!=null && response.getResultado().getCodigo().equalsIgnoreCase("0000")) {
					existe=true;
					if(response.getDatos()!=null) {
						datos=response.getDatos();
						if(!datos.getTelefono().equalsIgnoreCase(telefono)||!datos.getEmail().equalsIgnoreCase(correo)) {
							response = detectIdCRUD(nombre, tipoDocumento+nroDocumento, telefono, correo, ipOrigen, Constantes.UPDATE);
						}
					}					
				}
			} catch (Exception e) {
				logger.info(e.getLocalizedMessage());
				if(e.getMessage().contains("404")) {
					existe = false;
				}
			}			
			if(!existe)
				response = detectIdCRUD(nombre, tipoDocumento+nroDocumento,telefono, correo, ipOrigen, Constantes.CREATE);
	
			resp = solicitarValidarOTP(tipoDocumento+nroDocumento, null, ipOrigen, proposito, Constantes.GENERAR);
			generado=resp.equalsIgnoreCase("Exito");
		} catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		return generado;
	}
}
