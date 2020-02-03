package com.beca.misdivisas.model;

import java.math.BigDecimal;

public class ReporteSucursalMapa {

	private BigDecimal suma;
	private int mes;
	private int ano;
	
	public ReporteSucursalMapa(BigDecimal suma, int mes, int ano) {
		super();
		this.suma = suma;
		this.mes = mes;
		this.ano = ano;
	}
	public BigDecimal getSuma() {
		return suma;
	}
	public void setSuma(BigDecimal suma) {
		this.suma = suma;
	}
	public int getMes() {
		return mes;
	}
	public void setMes(int mes) {
		this.mes = mes;
	}
	public int getAno() {
		return ano;
	}
	public void setAno(int ano) {
		this.ano = ano;
	}
	
	
}
