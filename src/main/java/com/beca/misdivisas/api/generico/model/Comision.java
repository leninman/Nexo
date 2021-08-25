package com.beca.misdivisas.api.generico.model;

public class Comision {
	
	private String moneda;
	private Double monto;

	public Comision() {
		this.moneda = "VES";
		this.monto = 0.00;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}
	public Double getMonto() {
		return monto;
	}
	public void setMonto(Double monto) {
		this.monto = monto;
	}
	
	@Override
	public String toString() {
		return "Comision [moneda=" + moneda + ", monto=" + monto + "]";
	}
	
}
