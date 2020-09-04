package com.beca.misdivisas.util;

import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.Instant;
import java.util.Locale;
import java.util.concurrent.TimeUnit;

import javax.servlet.http.HttpServletRequest;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class Util {

	public static String formatMonto(String montoIn) {
		Locale[] locales = { new Locale("de", "DE") };
		NumberFormat nf = NumberFormat.getNumberInstance(locales[0]);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###,###,##0.00");

		return df.format(Double.parseDouble(montoIn));
	}

	public int diasHabiles(Calendar fechaInicial, Calendar fechaFinal, List<Date> listaFechasNoLaborables) {
		int diffDays = 0;
		boolean diaHabil = false;

		while (fechaInicial.before(fechaFinal) || fechaInicial.equals(fechaFinal)) {
			if (!listaFechasNoLaborables.isEmpty()) {
				for (Date date : listaFechasNoLaborables) {
					Date fechaNoLaborablecalendar = fechaInicial.getTime();
					if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
							&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY
							&& !fechaNoLaborablecalendar.equals(date)) {

						diaHabil = true;
					} else {
						diaHabil = false;
						break;
					}
				}
			} else {
				if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
						&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY)
					diffDays++;
			}
			if (diaHabil)
				diffDays++;

			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static int getDiasHabiles(Calendar fechaInicial, Calendar fechaFinal, List<Date> listaFechasNoLaborables) {
		int diffDays = 0;
		while (fechaInicial.before(fechaFinal)) {
			if (esLaborable(fechaInicial, listaFechasNoLaborables)) {
				diffDays++;
			}
			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static String diaHabilPrevio(List<Date> listaFechasNoLaborables) {
		boolean hasta = true;
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		Calendar cal = new GregorianCalendar();
		Date fecha;
		try {
			fecha = formato.parse(formato.format(new Date()));
			cal.setTime(fecha);
			cal.add(Calendar.DATE, -1);
			while (hasta) {
				if (esLaborable(cal, listaFechasNoLaborables)) {
					hasta = false;
				} else {
					cal.add(Calendar.DATE, -1);
				}
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}

		return formato.format(cal.getTime());
	}

	public static boolean esLaborable(Calendar fecha, List<Date> listaFechasNoLaborables) {

		fecha.set(Calendar.HOUR_OF_DAY, 0);
		fecha.set(Calendar.MINUTE, 0);
		fecha.set(Calendar.SECOND, 0);
		fecha.set(Calendar.MILLISECOND, 0);
		boolean result = true;

		if (listaFechasNoLaborables != null) {
			Date fechaC = fecha.getTime();
			
			if (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				for (int i = 0; i < listaFechasNoLaborables.size(); i++) {
					if (fechaC.equals(listaFechasNoLaborables.get(i))) {
						result = false;
					}
				}				
			} else {
				result = false;
			}
		} else {
			if (fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& fecha.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				result = true;
			} else {
				result = false;
			}
		}
		return result;
	}
	
	public List<Date> obtenerFeriados(String prop){
		DateFormat formato = new SimpleDateFormat("dd-MM-yyyy");
		String bancarios = prop;
		List<Date> fechas = new ArrayList<>();
		
		if(bancarios!=null && !bancarios.isEmpty()) {
			String [] bancariosSplit = bancarios.split(",");		
	
			for(int i =  0; i< bancariosSplit.length; i++) {
				try {				
					fechas.add(formato.parse(bancariosSplit[i]));
				} catch (ParseException e) {
					e.printStackTrace();
				}
			}
		}
		return fechas;
	}
	
	public static String getRemoteIp(HttpServletRequest request) {
		String ip="";
		if(request.getHeader("x-forwarded-for")!= null)
			ip= request.getHeader("x-forwarded-for");
		else
			ip= request.getRemoteAddr();
		
		return ip;
	}
	
	public static long getDateDiff(Timestamp date1, TimeUnit timeUnit) {
        Instant instante = Instant.now();
		Timestamp ahora = Timestamp.from(instante);
	    long diffInMillies = ahora.getTime() - date1.getTime();
	    return timeUnit.convert(diffInMillies,TimeUnit.MILLISECONDS);
	}
}
