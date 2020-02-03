package com.beca.misdivisas.model;

public class Saldo {

	private Double monto;
	private int moneda;

	public Saldo(int moneda) {
		super();
		this.moneda = moneda;
	}

	public Double getMonto() {
		return monto;
	}

	public void setMonto(Double monto) {
		this.monto = monto;
	}

	public int getMoneda() {
		return moneda;
	}

	public void setMoneda(int moneda) {
		this.moneda = moneda;
	}

	@Override
	public boolean equals(Object o) {
		if (this == o)
			return true;
		if (o == null || getClass() != o.getClass())
			return false;
		Saldo saldo = (Saldo) o;
		return moneda == saldo.moneda;
	}

}
