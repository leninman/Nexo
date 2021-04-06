package com.beca.misdivisas.model;

import java.text.DecimalFormat;
import org.apache.commons.lang3.StringUtils;

import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.jpa.Moneda;

public class ReporteSolicitudRetiro {
	private static final DecimalFormat formatter = new DecimalFormat("#,###.00");

	private String cartaPorte;
	private String fechaEstimada;
	private String monto;
	private String tipoBillete;
	private String estatus;
	private String nombreAutorizado;
	private String fechaStatus;
	private String nombreAgencia;
	private String nombreMoneda;
	private String tipoAutorizado;
	private String usuario;

	public ReporteSolicitudRetiro(Integer idSolicitud, String cartaPorte, String fechaEstimada, Integer monto,
			String tipoBillete, Agencia agencia, Autorizado autorizado, Moneda moneda, String estatus,
			String fechaStatus, String usuario) {
		
		this.fechaEstimada = fechaEstimada;
		this.monto = formatter.format(monto);
		this.tipoBillete = tipoBillete.equalsIgnoreCase("A") ? "Apto" : "No Apto";
		
		this.nombreAgencia = StringUtils.leftPad(String.valueOf(agencia.getNumeroAgencia()), 3, "0") + "-"
				+ agencia.getAgencia();
		
		if (autorizado.getIdTipoAutorizado() == 3) {
			
			this.nombreAutorizado = autorizado.getRifEmpresa() + " - " + autorizado.getNombreEmpresa();
		
		} else if (autorizado.getIdTipoAutorizado() == 1) {
			this.nombreAutorizado = autorizado.getDocumentoIdentidad() + " - " + autorizado.getNombreCompleto();
		
		} else if (autorizado.getIdTipoAutorizado() == 2) {
			this.nombreAutorizado = autorizado.getDocumentoIdentidad() + " - " + autorizado.getNombreCompleto() + " - "
					+ autorizado.getRifEmpresa() + " - " + autorizado.getNombreEmpresa();
			;
		}
		this.nombreMoneda = moneda.getMoneda();
		this.estatus = estatus;
		this.tipoAutorizado = autorizado.getTipoAutorizado().getTipoAutorizado();
		this.cartaPorte = cartaPorte;
		this.fechaStatus = fechaStatus;
		this.usuario = usuario;
	}

	public String getCartaPorte() {
		return cartaPorte;
	}

	public void setCartaPorte(String cartaPorte) {
		this.cartaPorte = cartaPorte;
	}

	public String getFechaEstimada() {
		return fechaEstimada;
	}

	public void setFechaEstimada(String fechaEstimada) {
		this.fechaEstimada = fechaEstimada;
	}

	public String getMonto() {
		return monto;
	}

	public void setMonto(String monto) {
		this.monto = monto;
	}

	public String getTipoBillete() {
		return tipoBillete;
	}

	public void setTipoBillete(String tipoBillete) {
		this.tipoBillete = tipoBillete;
	}

	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}

	public String getNombreAutorizado() {
		return nombreAutorizado;
	}

	public void setNombreAutorizado(String nombreAutorizado) {
		this.nombreAutorizado = nombreAutorizado;
	}

	public String getFechaStatus() {
		return fechaStatus;
	}

	public void setFechaStatus(String fechaStatus) {
		this.fechaStatus = fechaStatus;
	}

	public String getNombreAgencia() {
		return nombreAgencia;
	}

	public void setNombreAgencia(String nombreAgencia) {
		this.nombreAgencia = nombreAgencia;
	}

	public String getNombreMoneda() {
		return nombreMoneda;
	}

	public void setNombreMoneda(String nombreMoneda) {
		this.nombreMoneda = nombreMoneda;
	}

	public String getTipoAutorizado() {
		return tipoAutorizado;
	}

	public void setTipoAutorizado(String tipoAutorizado) {
		this.tipoAutorizado = tipoAutorizado;
	}
	
	public String getUsuario() {
		return usuario;
	}

	public void setUsuario(String usuario) {
		this.usuario = usuario;
	}

}
