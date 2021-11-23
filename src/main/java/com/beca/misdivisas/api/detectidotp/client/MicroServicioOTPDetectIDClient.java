package com.beca.misdivisas.api.detectidotp.client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.beca.misdivisas.api.detectidotp.model.OtpRequest;
import com.beca.misdivisas.api.detectidotp.model.OtpResponse;
import com.beca.misdivisas.api.generico.MicroservicioClient;
import com.beca.misdivisas.api.generico.model.Resultado;
import com.beca.misdivisas.util.Constantes;
import com.google.gson.Gson;


@Service
public class MicroServicioOTPDetectIDClient extends MicroservicioClient implements IMicroServicioOTPDetectID {

	private static final Logger logger = LoggerFactory.getLogger(MicroServicioOTPDetectIDClient.class);

	@Value("${microservicio.otp}")
	private String urlOtp;

	
	@Override
	public OtpResponse crearValidarOTP(OtpRequest request, String operacion) throws Exception {
		OtpResponse response = new OtpResponse();
        RestTemplate cliente = getTemplate();
		HttpHeaders headers = new HttpHeaders();
		Resultado resultado = new Resultado();

		JSONObject body = new JSONObject();
	    Gson gson = new Gson();
	    body = new JSONObject(gson.toJson(request));
	    headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		try {
			response = cliente.postForObject(urlOtp+"/"+operacion, entity, OtpResponse.class);
		} catch (RestClientException e) {
			
			if(e.getLocalizedMessage().contains("902")) {
				resultado.setCodigo("902");
				response.setResultado(resultado);
				return response;
			}else if(e.getLocalizedMessage().contains("904")||e.getLocalizedMessage().contains("422")) {
				resultado.setCodigo("904");
				response.setResultado(resultado);
				return response;
			}
			if(e.getLocalizedMessage().contains("938")) {
				resultado.setCodigo("938");
				response.setResultado(resultado);
				return response;
			}	
			else
				throw e;
			/*logger.info(e.getLocalizedMessage());
			throw e;
			return response;*/
		}
		logger.info(response.toString());
		return response;
	}

}
