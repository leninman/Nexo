package com.beca.misdivisas.controller;

import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;


import javax.servlet.http.HttpSession;


import org.apache.commons.lang3.time.DateUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.interfaces.IAgenciaDiaRepo;
import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IFeriadoRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.AgenciaDia;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.AgenciaDiaOperaciones;
import com.beca.misdivisas.model.AgenciaDiaOperacionesModel;
import com.beca.misdivisas.model.AgenciaFechaOperaciones;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class AgenciaDiaController {

	private final static DateFormat simpleDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;

	@Autowired
	private IAgenciaDiaRepo agenciaDiaRepository;

	@Autowired
	private IAgenciaRepo agenciaRepository;

	@Autowired
	private IFeriadoRepo feriadoRepository;

	@GetMapping("/agenciaDias")
	public String agenciaDia(Model model) {
		getAgenciaDias(model);
		return "mainAgenciaDias";

	}
	
	@GetMapping("/agenciaDiasResult")
	public String agenciaDiaResult(Model model) {
		getAgenciaDias(model);
		return Constantes.OP_AGENCIA_DIAS_VIEW;

	}

	@Transactional
	@PostMapping("agenciaDiaAgregar") 
	public String addAgenciaDia(@RequestParam("fechasHabilitadas") List<String> fechasHabilitadas) throws ParseException { 
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Date fechaInicial = new Date();
		Date fechaFin = DateUtils.addDays(fechaInicial, 10);
		agenciaDiaRepository.deleteByFecha(fechaInicial, fechaFin);
		
		List<AgenciaDia> agenciaDias = new ArrayList<>();
		for(String habilitada: fechasHabilitadas) {
			int slashPosition = habilitada.indexOf("-");
			int idAgencia = Integer.valueOf(habilitada.substring(0, slashPosition));
			String fecha = habilitada.substring(slashPosition + 1);
			Date date = new SimpleDateFormat("dd/MM/yyyy").parse(fecha);
			AgenciaDia ad = new AgenciaDia(idAgencia, date);
			agenciaDias.add(ad);
			
		}
		agenciaDiaRepository.saveAll(agenciaDias);	
		
	return "redirect:agenciaDiasResult?success";		 
	}
	
	public void getAgenciaDias(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		List<String> localDates = new ArrayList<>();

		Date fechaInicial = new Date();
		Calendar calendar = Calendar.getInstance();
		calendar.setTime(fechaInicial);
		calendar.set(Calendar.HOUR, 0);
		calendar.set(Calendar.MINUTE, 0);
		calendar.set(Calendar.SECOND, 0);
		calendar.set(Calendar.MILLISECOND, 0);
		calendar.add(Calendar.DATE, 0);
		calendar.set(Calendar.HOUR_OF_DAY, 0);
		fechaInicial = calendar.getTime();
		final List<String> listaFeriados = feriadoRepository.findAllFechaMayorQue(new Date()).stream()
				.map((feriado) -> simpleDateFormat.format(feriado.getFecha())).collect(Collectors.toList());
		int i = 0;
		while (i < 10) {
			Date newDate = DateUtils.addDays(fechaInicial, i);
			String fecha = simpleDateFormat.format(newDate);
			calendar.setTime(newDate);
			Boolean esFeriado = listaFeriados.contains(fecha);
			if ((calendar.get(Calendar.DAY_OF_WEEK) > 1 && calendar.get(Calendar.DAY_OF_WEEK) < 7) && !esFeriado) {
				localDates.add(fecha);
			}
			i++;
		}
		model.addAttribute("localDates", localDates);

		Date fechaFin = DateUtils.addDays(fechaInicial, 10);
		final List<AgenciaDia> agenciaDia = agenciaDiaRepository.findAllByFecha(fechaInicial, fechaFin);
		final List<Agencia> agencias = agenciaRepository.findByAlmacenamientoOrRecaudacion(true, true);
		List<AgenciaDiaOperacionesModel> agenciaDiaOperacionesList = new ArrayList<>();

		agencias.stream().forEach(ag -> {
			String descripcion = null;
			AgenciaDiaOperacionesModel agenciaDO = new AgenciaDiaOperacionesModel();
			if (ag.getAlmacenamiento() != true && ag.getRecaudacion() != false)
				descripcion = "(Rec)";
			else if (ag.getAlmacenamiento() != false && ag.getRecaudacion() != true)
				descripcion = "(Alm)";
			else if (ag.getAlmacenamiento() != false && ag.getRecaudacion() != false)
				descripcion = "(Alm y Rec)";
			agenciaDO.setAgencia(ag.getAgencia() + "  " + descripcion);
			agenciaDO.setIdAgencia(ag.getIdAgencia());
			agenciaDO.setnAgencia(ag.getNumeroAgencia());
			agenciaDO.setIdEstatusAgencia(ag.getIdEstatusAgencia());
			agenciaDO.setAlmacenamiento(ag.getAlmacenamiento());
			agenciaDO.setRecaudacion(ag.getRecaudacion());
			
			List<AgenciaFechaOperaciones> fechaOperaciones = new ArrayList<>();
			localDates.stream().forEach(date -> {
				boolean habilitado = agenciaDia.stream().filter(agd -> agd.getIdAgencia().equals(ag.getIdAgencia())
						&& simpleDateFormat.format(agd.getFecha()).equals(date)).count() > 0;
				fechaOperaciones.add(new AgenciaFechaOperaciones(date, habilitado));
			});

			agenciaDO.setAgenciaFechaOperaciones(fechaOperaciones);
			if(ag.getIdEstatusAgencia() != 2) {
			agenciaDiaOperacionesList.add(agenciaDO);
			}
		});

		AgenciaDiaOperaciones agenciaDiaOperaciones = new AgenciaDiaOperaciones();
		agenciaDiaOperaciones.setAgenciaDiaOperacionesModel(agenciaDiaOperacionesList);
		model.addAttribute(Constantes.AGENCIA_DIAS_MODEL, agenciaDiaOperaciones);

	}
}
