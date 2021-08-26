package com.beca.misdivisas.api.correo.client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.beca.misdivisas.api.correo.model.EnvioDeCorreoRequest;
import com.beca.misdivisas.api.correo.model.EnvioDeCorreoResponse;
import com.beca.misdivisas.api.generico.MicroservicioClient;
import com.google.gson.Gson;

@Service
public class MicroServicioCorreoClient extends MicroservicioClient implements IMicroServicioCorreo {
	private static final Logger logger = LoggerFactory.getLogger(MicroServicioCorreoClient.class);

	
	@Value("${microservicio.enviar.correo}")
	private String urlEnviarCorreo;

	
	@Override
	public EnvioDeCorreoResponse enviarCorreo(EnvioDeCorreoRequest request) throws Exception {
		EnvioDeCorreoResponse response = new EnvioDeCorreoResponse();

		RestTemplate cliente = getTemplate();
		HttpHeaders headers = new HttpHeaders();

		JSONObject body = new JSONObject();
		Gson gson = new Gson();
		body = new JSONObject(gson.toJson(request));
		headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

		try {
			response = cliente.postForObject(urlEnviarCorreo, entity, EnvioDeCorreoResponse.class);

		} catch (RestClientException e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		return response;
	}

}
