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

		JSONObject body = new JSONObject();
	    Gson gson = new Gson();
	    body = new JSONObject(gson.toJson(request));
	    headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		try {
			response = cliente.postForObject(urlOtp+"/"+operacion, entity, OtpResponse.class);
		} catch (RestClientException e) {
			logger.info(e.getLocalizedMessage());
			//throw e;
			return response;
		}
		logger.info(response.toString());
		return response;
	}

}
