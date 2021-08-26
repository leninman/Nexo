package com.beca.misdivisas.api.cuentas.model;

import java.util.List;

public class DatosConsultarCuentas {
    private Integer totalCuentas;
    private List<CuentaConsultarCuentas> cuentas;
    
	public Integer getTotalCuentas() {
		return totalCuentas;
	}
	public void setTotalCuentas(Integer totalCuentas) {
		this.totalCuentas = totalCuentas;
	}
	public List<CuentaConsultarCuentas> getCuentas() {
		return cuentas;
	}
	public void setCuentas(List<CuentaConsultarCuentas> cuentas) {
		this.cuentas = cuentas;
	}
	
	@Override
	public String toString() {
		return "DatosConsultarCuentas [totalCuentas=" + totalCuentas + ", cuentas=" + cuentas + "]";
	}

}
