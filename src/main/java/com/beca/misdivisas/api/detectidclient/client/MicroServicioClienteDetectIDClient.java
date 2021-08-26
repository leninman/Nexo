package com.beca.misdivisas.api.detectidclient.client;

import org.json.JSONObject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdRequest;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseCRUD;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseGet;
import com.beca.misdivisas.api.generico.MicroservicioClient;
import com.google.gson.Gson;


@Service
public class MicroServicioClienteDetectIDClient extends MicroservicioClient implements IMicroServicioClienteDetectID {

	private static final Logger logger = LoggerFactory.getLogger(MicroServicioClienteDetectIDClient.class);

	@Value("${microservicio.detectid.cliente}")
	private String urlDetectId;

	
	@Override
	public ClientesDetectIdResponseGet detectIdGet(ClientesDetectIdRequest request) throws Exception {
		ClientesDetectIdResponseGet response = new ClientesDetectIdResponseGet();
		
		RestTemplate cliente = getTemplate();
		HttpHeaders headers = new HttpHeaders();

		JSONObject body = new JSONObject();
		Gson gson = new Gson();
		body = new JSONObject(gson.toJson(request));
		headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);
		try {
			response = cliente.postForObject(urlDetectId+"/get", entity, ClientesDetectIdResponseGet.class);

		} catch (RestClientException e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		logger.info(response.toString());
		return response;
	}

	@Override
	public ClientesDetectIdResponseCRUD detectIdCRUD(ClientesDetectIdRequest request, String operacion) throws Exception {
		ClientesDetectIdResponseCRUD response = new ClientesDetectIdResponseCRUD();
		
		RestTemplate cliente = getTemplate();
		HttpHeaders headers = new HttpHeaders();

		JSONObject body = new JSONObject();
		Gson gson = new Gson();
		body = new JSONObject(gson.toJson(request));
		headers = createHeaders(60*60*1); //segundos

		HttpEntity<String> entity = new HttpEntity<String>(body.toString(), headers);

		try {
			switch (operacion) {
			case "update":
				HttpEntity<ClientesDetectIdResponseCRUD> result;
				 result = cliente.exchange(urlDetectId+"/"+operacion, HttpMethod.PUT, entity, ClientesDetectIdResponseCRUD.class, "");
				 if(result.hasBody())
					 response= result.getBody();
				break;
				
			case "delete":
				cliente.delete(urlDetectId+"/"+operacion, entity);
				break;

			default:
				response = cliente.postForObject(urlDetectId+"/"+operacion, entity, ClientesDetectIdResponseCRUD.class);
				break;
			}

		} catch (RestClientException e) {
			logger.info(e.getLocalizedMessage());
			throw e;
		}
		logger.info(response.toString());
		return response;
	}


}
