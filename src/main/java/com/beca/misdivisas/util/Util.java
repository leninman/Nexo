package com.beca.misdivisas.util;

import java.text.DateFormat;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Locale;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.env.Environment;
import org.springframework.stereotype.Service;

import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

@Service
public class Util {
	@Autowired
	private Environment env;

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
			if (diaHabil == true)
				diffDays++;

			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static int getDiasHabiles(Calendar fechaInicial, Calendar fechaFinal) {
		int diffDays = -1;
		while (fechaInicial.before(fechaFinal)) {
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				diffDays++;
			}
			fechaInicial.add(Calendar.DATE, 1);
		}
		return diffDays;
	}

	public static String diaHabilPrevio(List<Date> listaFechasNoLaborables) {
		boolean hasta = true;
		DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
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

	public static boolean esLaborable(Calendar fechaInicial, List<Date> listaFechasNoLaborables) {

		if (listaFechasNoLaborables != null) {
			Date fechaNoLaborablecalendar = fechaInicial.getTime();
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY && fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				for (int i = 0; i < listaFechasNoLaborables.size(); i++) {
					if (fechaNoLaborablecalendar.equals(listaFechasNoLaborables.get(i))) {
						return false;
					}
				}				
			} else {
				return false;
			}
		} else {
			if (fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SUNDAY
					&& fechaInicial.get(Calendar.DAY_OF_WEEK) != Calendar.SATURDAY) {
				return true;
			} else {
				return false;
			}
		}
		return true;
	}
}
