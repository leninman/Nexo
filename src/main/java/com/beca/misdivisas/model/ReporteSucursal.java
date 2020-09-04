package com.beca.misdivisas.model;

import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;

import com.beca.misdivisas.util.Constantes;

public class ReporteSucursal implements Comparable<ReporteSucursal> {
	private String fecha;
	private String sucursal;
	private String referencia;
	private String concepto;
	private String debito;
	private String credito;
	private String saldo;
	private String moneda;

	public String getSucursal() {
		return sucursal;
	}

	public void setSucursal(String sucursal) {
		this.sucursal = sucursal;
	}

	public String getReferencia() {
		return referencia;
	}

	public void setReferencia(String referencia) {
		this.referencia = referencia;
	}

	public String getConcepto() {
		return concepto;
	}

	public void setConcepto(String concepto) {
		this.concepto = concepto;
	}

	public String getMoneda() {
		return moneda;
	}

	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getFecha() {
		return fecha;
	}

	public void setFecha(String fecha) {
		this.fecha = fecha;
	}

	public String getDebito() {
		return debito;
	}

	public void setDebito(String debito) {
		this.debito = debito;
	}

	public String getCredito() {
		return credito;
	}

	public void setCredito(String credito) {
		this.credito = credito;
	}

	public String getSaldo() {
		return saldo;
	}

	public void setSaldo(String saldo) {
		this.saldo = saldo;
	}

	@Override
	public int compareTo(ReporteSucursal s) {
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		Date date1, date2;
		try {
			if (getFecha() != null && s.getFecha() != null) {
				date1 = formato.parse(getFecha());
				date2 = formato.parse(s.getFecha());
				
				return date1.compareTo(date2);
			}
		} catch (ParseException e) {
			e.printStackTrace();
		}
		return 0;

	}

}
