package com.beca.misdivisas.util;

import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.Locale;

public class Util {

	
	public static String formatMonto(String montoIn) {
		Locale[] locales = { new Locale("de", "DE") };
		NumberFormat nf = NumberFormat.getNumberInstance(locales[0]);
		DecimalFormat df = (DecimalFormat) nf;
		df.applyPattern("###,###,###,##0.00");

		return df.format(Double.parseDouble(montoIn));
	}
	

}
