package com.beca.misdivisas.controller;

import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.json.JSONObject;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Usuario;

@Controller
public class LogController {
	
	@Autowired
	private ILogRepo logRepo;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;


	@RequestMapping(value = "/RegistrarLog", method = RequestMethod.POST)
	@ResponseBody
	public  void registrarLog(@RequestBody String req,HttpServletRequest request) {
		Date date = new Date();
		Log audit = new Log();
		
		JSONObject jsonObject = new JSONObject(req);
		
		String accion= jsonObject.getString("accion");
		String detalle = jsonObject.getString("detalle");
		String opcion = jsonObject.getString("opcion");
		
		String ip = request.getRemoteAddr();
		HttpSession session = factory.getObject();
		Usuario us = (Usuario) session.getAttribute("Usuario");
		
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
	}
}
