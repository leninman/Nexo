package com.beca.misdivisas.services;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Service
public class LogService {
	@Autowired
	private ILogRepo logRepo;
	
	@Autowired
	 private static final Logger logger = LoggerFactory.getLogger(LogService.class);
	
	public void registrar(HttpSession sesion, HttpServletRequest request,  ILogRepo logRepo) {
		Date date = new Date();
		Log audit = new Log();
		String ip = Util.getRemoteIp(request);
		Usuario us = (Usuario) sesion.getAttribute(Constantes.USUARIO);
		if (us != null) {
			audit.setIdEmpresa(us.getIdEmpresa());
			audit.setIdUsuario(us.getIdUsuario());
			audit.setNombreUsuario(us.getNombreUsuario());
		}
		audit.setFecha(new Timestamp(date.getTime()));
		audit.setIpOrigen(ip);
		audit.setAccion("Logout");
		audit.setDetalle("Logout");
		audit.setOpcionMenu("Cerrar Sesion");
		audit.setResultado(true);
		logRepo.save(audit);
	}
	
	public  void registrarLog(String accion, String detalle,  String opcion, String ip, Usuario us) {
		Date date = new Date();
		Log audit = new Log();
				
		audit.setFecha(new Timestamp(date.getTime()));
		audit.setIpOrigen(ip);
		audit.setAccion(accion);
		audit.setDetalle(detalle);
		audit.setIdEmpresa(us.getIdEmpresa());
		audit.setIdUsuario(us.getIdUsuario());
		audit.setNombreUsuario(us.getNombreUsuario());
		audit.setOpcionMenu(opcion);
		audit.setResultado(true);
		logRepo.save(audit);
		logger.info("Ip origen: "+ ip +" Accion:" +accion +" Detalle:"+ detalle + " Opcion:"+ opcion);
	}
}
