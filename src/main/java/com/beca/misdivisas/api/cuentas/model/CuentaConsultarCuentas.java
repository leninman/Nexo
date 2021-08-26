package com.beca.misdivisas.api.cuentas.model;

import java.io.Serializable;
import java.math.BigDecimal;

public class CuentaConsultarCuentas implements Serializable{
	private static final long serialVersionUID = 6750971390807144974L;
	private int id;
	private String numero;
    private String tipo;
    private String estatus;
    private String moneda;
    private BigDecimal saldo;
    private String signo;
    
	public String getNumero() {
		return numero;
	}
	public void setNumero(String numero) {
		this.numero = numero;
	}
	public String getTipo() {
		return tipo;
	}
	public void setTipo(String tipo) {
		this.tipo = tipo;
	}
	public String getMoneda() {
		return moneda;
	}
	public void setMoneda(String moneda) {
		this.moneda = moneda;
	}

	public String getSigno() {
		return signo;
	}
	public void setSigno(String signo) {
		this.signo = signo;
	}
	public BigDecimal getSaldo() {
		return saldo;
	}
	public void setSaldo(BigDecimal saldo) {
		this.saldo = saldo;
	}
	public String getEstatus() {
		return estatus;
	}
	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	
	@Override
	public String toString() {
		return "CuentaConsultarCuentas [id=" + id + ", numero=" + numero + ", tipo=" + tipo + ", estatus=" + estatus
				+ ", moneda=" + moneda + ", saldo=" + saldo + ", signo=" + signo + "]";
	} 

}
