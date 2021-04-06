package com.beca.misdivisas.services;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IFeriadoRepo;
import com.beca.misdivisas.jpa.Feriado;

@Service
public class FeriadosService {
	@Autowired
	private IFeriadoRepo feriadoRepo;
	
	public List<Date> obtenerFeriados(){
		List<Date> fechas = new ArrayList<>();
		Calendar c = Calendar.getInstance();
		c.setTime(new Date());
		c.add(Calendar.DATE, -7); 
		List<Feriado> feriados = feriadoRepo.findAllFechaMayorQue(c.getTime());		
		for (Feriado feriado : feriados) {
			fechas.add(feriado.getFecha());
		}

		return fechas;
	}
}
