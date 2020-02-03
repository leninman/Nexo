package com.beca.misdivisas.model;

import java.math.BigDecimal;

public class DatosReporteMapa {
	private BigDecimal[] montos;
	private String[] meses;
	
	
	
	public BigDecimal[] getMontos() {
		return montos;
	}
	public void setMontos(BigDecimal[] montos) {
		this.montos = montos;
	}
	public String[] getMeses() {
		return meses;
	}
	public void setMeses(String[] meses) {
		this.meses = meses;
	}

}
