package com.beca.misdivisas.services;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.api.generico.IMicroservicioService;

@Service
public class SolicitudRetiroService {
	
	private static final Logger logger = LoggerFactory.getLogger(SolicitudRetiroService.class);
	
	@Autowired
	private IMicroservicioService microservicioService;
	
	public boolean enviarCorreoAutorizado(String para,String asunto, String encabezado, String cuerpo, String pie, boolean esNotificacion, String ipOrigen) {
		
		try {
			return microservicioService.enviarCorreo(para, asunto, encabezado, cuerpo, pie, esNotificacion, ipOrigen);			
		}catch (Exception e) {
			logger.info(e.getLocalizedMessage());
			return false;
		}
	}

}
