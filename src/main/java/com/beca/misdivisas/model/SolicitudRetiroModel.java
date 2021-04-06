package com.beca.misdivisas.model;

import java.io.Serializable;
import java.math.BigDecimal;

import javax.validation.constraints.NotEmpty;
import javax.validation.constraints.NotNull;

import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.jpa.Moneda;

public class SolicitudRetiroModel implements Serializable,Comparable<SolicitudRetiroModel>{

	private static final long serialVersionUID = 1L;
	private Integer idSolicitud;
	private String cartaPorte;

	@NotEmpty(message = "requerido")
	private String fechaEstimada;

	@NotNull(message = "requerido")
	private BigDecimal monto;

	private String tipoBillete;
	private Agencia agencia;
	private Autorizado autorizado;
	private Integer idEmpresa;
	private Moneda moneda;
	private Integer idAgencia;
	private Integer idAutorizado;
	private Integer idMoneda;
	private String estatus;
	private String nombreAutorizado;
	private String fechaStatus;
	private String nombreEmpresa;
	
	public SolicitudRetiroModel() {
	}

	public SolicitudRetiroModel(Integer idSolicitud) {
		super();
		this.idSolicitud = idSolicitud;
	}

	public SolicitudRetiroModel(Integer idSolicitud, String fechaEstimada, BigDecimal monto, String tipoBillete,
			Integer idAgencia, Integer idAutorizado, Integer idMoneda) {
		super();
		this.idSolicitud = idSolicitud;
		this.fechaEstimada = fechaEstimada;
		this.monto = monto;
		this.tipoBillete = tipoBillete;
		this.idAgencia = idAgencia;
		this.idAutorizado = idAutorizado;
		this.idMoneda = idMoneda;
	}

	public SolicitudRetiroModel(Integer idSolicitud, String cartaPorte, String fechaEstimada, BigDecimal monto,
			String tipoBillete, Agencia agencia, Autorizado autorizado, Moneda moneda, String nombreEmpresa) {
		this.idSolicitud = idSolicitud;
		this.cartaPorte = cartaPorte;
		this.fechaEstimada = fechaEstimada;
		this.monto = monto;
		this.tipoBillete = tipoBillete.equalsIgnoreCase("A") ? "Apto" : "No Apto";
		this.agencia = agencia;
		this.autorizado = autorizado;
		this.moneda = moneda;
		this.nombreEmpresa = nombreEmpresa;
	}

	public SolicitudRetiroModel(Integer idSolicitud, String fechaEstimada, BigDecimal monto, String tipoBillete,
			Agencia agencia, Autorizado autorizado, Moneda moneda, String estatus, String nombreEmpresa) {		
		this.idSolicitud = idSolicitud;
		this.fechaEstimada = fechaEstimada;
		this.monto = monto;
		this.tipoBillete = tipoBillete.equalsIgnoreCase("A") ? "Apto" : "No Apto";
		this.agencia = agencia;
		this.autorizado = autorizado;
		this.moneda = moneda;
		this.estatus = estatus;
		this.nombreEmpresa = nombreEmpresa;
		if (autorizado.getIdTipoAutorizado() == 3) {
			this.nombreAutorizado = autorizado.getRifEmpresa() + " - " + autorizado.getNombreEmpresa();
		} else if (autorizado.getIdTipoAutorizado() == 1) {
			this.nombreAutorizado = autorizado.getDocumentoIdentidad() + " - " + autorizado.getNombreCompleto();
		} else if (autorizado.getIdTipoAutorizado() == 2) {
			this.nombreAutorizado = autorizado.getDocumentoIdentidad() + " - " + autorizado.getNombreCompleto() + " - "
					+ autorizado.getRifEmpresa() + " - " + autorizado.getNombreEmpresa();
			;
		}
	}

	public SolicitudRetiroModel(Integer idSolicitud, String cartaPorte,
			@NotEmpty(message = "requerido") String fechaEstimada, @NotNull(message = "requerido") BigDecimal monto,
			String tipoBillete, Agencia agencia, Autorizado autorizado, Moneda moneda, String estatus,
			String fechaStatus, String nombreEmpresa) {
		this(idSolicitud, fechaEstimada, monto, tipoBillete, agencia, autorizado, moneda, estatus, nombreEmpresa);
		this.cartaPorte = cartaPorte;
		this.fechaStatus = fechaStatus;
	}

	public Integer getIdSolicitud() {
		return idSolicitud;
	}

	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
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

	public BigDecimal getMonto() {
		return monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getTipoBillete() {
		return tipoBillete;
	}

	public void setTipoBillete(String tipoBillete) {
		this.tipoBillete = tipoBillete;
	}

	public Agencia getAgencia() {
		return agencia;
	}

	public void setAgencia(Agencia agencia) {
		this.agencia = agencia;
	}

	public Autorizado getAutorizado() {
		return autorizado;
	}

	public void setAutorizado(Autorizado autorizado) {
		this.autorizado = autorizado;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Moneda getMoneda() {
		return moneda;
	}

	public void setMoneda(Moneda moneda) {
		this.moneda = moneda;
	}

	public Integer getIdAgencia() {
		return idAgencia;
	}

	public void setIdAgencia(Integer idAgencia) {
		this.idAgencia = idAgencia;
	}

	public Integer getIdAutorizado() {
		return idAutorizado;
	}

	public void setIdAutorizado(Integer idAutorizado) {
		this.idAutorizado = idAutorizado;
	}

	public Integer getIdMoneda() {
		return idMoneda;
	}

	public void setIdMoneda(Integer idMoneda) {
		this.idMoneda = idMoneda;
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
	
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}

	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
		
	@Override
	public int compareTo(SolicitudRetiroModel sr) {
		return this.idSolicitud.compareTo(sr.idSolicitud);
	}

	@Override
	public boolean equals(Object arg0) {		
		return this.idSolicitud == ((SolicitudRetiroModel)arg0).idSolicitud;				
				
	}
}
