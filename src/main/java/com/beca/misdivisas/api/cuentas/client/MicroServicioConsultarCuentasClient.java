package com.beca.misdivisas.api.cuentas.client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasRequest;
import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasResponse;
import com.beca.misdivisas.api.generico.MicroservicioClient;
import com.google.gson.Gson;


@Service
public class MicroServicioConsultarCuentasClient extends MicroservicioClient implements IMicroServicioConsultarCuentas {

	private static final Logger logger = LoggerFactory.getLogger(MicroServicioConsultarCuentasClient.class);

	@Value("${microservicio.consultar.cuentas}")
	private String urlConsultaCtas;

	
	@Override
	public ConsultarCuentasResponse consultarCuentas(ConsultarCuentasRequest request) {
		ConsultarCuentasResponse response = new ConsultarCuentasResponse();
        RestTemplate cliente = getTemplate();
		HttpHeaders headers = new HttpHeaders();

		JSONObject body = new JSONObject();
		Gson gson = new Gson();
		body = new JSONObject(gson.toJson(request));

		headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		try {
			response = cliente.postForObject(urlConsultaCtas, entity, ConsultarCuentasResponse.class);
		} catch (RestClientException e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		logger.info(response.toString());
		return response;
	}
	

}
