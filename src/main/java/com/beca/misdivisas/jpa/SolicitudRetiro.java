package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;


/**
 * The persistent class for the "SOLICITUD_RETIRO" database table.
 * 
 */
@Entity
@Table(name="\"SOLICITUD_RETIRO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="SolicitudRetiro.findAll", query="SELECT s FROM SolicitudRetiro s")
public class SolicitudRetiro implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_solicitud_retiro_idSolicitud\"", sequenceName ="\"ALMACEN\".\"seq_solicitud_retiro_idSolicitud\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_solicitud_retiro_idSolicitud\"")
	@Column(name="\"id_solicitud\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idSolicitud;

	@Column(name="\"carta_porte\"")
	private String cartaPorte;

	/*
	 * @Column(name="\"clave_validacion\"") private String claveValidacion;
	 */

	//@Temporal(TemporalType.DATE)
	@Column(name="\"fecha_estimada\"")
	private Date fechaEstimada;

	@Column(name="monto")
	private BigDecimal monto;

	@Column(name="\"tipo_billete\"")
	private String tipoBillete;
	
	@Column(name="\"id_agencia\"")
	private Integer idAgencia;

	//bi-directional many-to-one association to Agencia
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_agencia\"", insertable = false, updatable = false)
	})
	private Agencia agencia;

	@Column(name="\"id_autorizado\"")
	private Integer idAutorizado;
	
	//bi-directional many-to-one association to Autorizado
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_autorizado\"", insertable = false, updatable = false)
	})
	private Autorizado autorizado;

	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;
	
	//bi-directional many-to-one association to Empresa
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_empresa\"", insertable = false, updatable = false)
	})
	private Empresa empresa;

	@Column(name="\"id_moneda\"")
	private Integer idMoneda;
	
	//bi-directional many-to-one association to Moneda
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_moneda\"", insertable = false, updatable = false)
	})
	private Moneda moneda;

	//bi-directional many-to-one association to SolicitudRetiroTraza
	@JsonBackReference
	@OneToMany(mappedBy="solicitudRetiro")
	private List<SolicitudRetiroTraza> solicitudRetiroTrazas;

	public SolicitudRetiro() {
	}

	public Integer getIdSolicitud() {
		return this.idSolicitud;
	}

	public void setIdSolicitud(Integer idSolicitud) {
		this.idSolicitud = idSolicitud;
	}

	public String getCartaPorte() {
		return this.cartaPorte;
	}

	public void setCartaPorte(String cartaPorte) {
		this.cartaPorte = cartaPorte;
	}

	/*
	 * public String getClaveValidacion() { return this.claveValidacion; }
	 * 
	 * public void setClaveValidacion(String claveValidacion) { this.claveValidacion
	 * = claveValidacion; }
	 */

	public Date getFechaEstimada() {
		return this.fechaEstimada;
	}

	public void setFechaEstimada(Date fechaEstimada) {
		this.fechaEstimada = fechaEstimada;
	}

	public BigDecimal getMonto() {
		return this.monto;
	}

	public void setMonto(BigDecimal monto) {
		this.monto = monto;
	}

	public String getTipoBillete() {
		return this.tipoBillete;
	}

	public void setTipoBillete(String tipoBillete) {
		this.tipoBillete = tipoBillete;
	}

	public Agencia getAgencia() {
		return this.agencia;
	}

	public void setAgencia(Agencia agencia) {
		this.agencia = agencia;
	}

	public Autorizado getAutorizado() {
		return this.autorizado;
	}

	public void setAutorizado(Autorizado autorizado) {
		this.autorizado = autorizado;
	}

	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

	public Moneda getMoneda() {
		return this.moneda;
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

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Integer getIdMoneda() {
		return idMoneda;
	}

	public void setIdMoneda(Integer idMoneda) {
		this.idMoneda = idMoneda;
	}

	public List<SolicitudRetiroTraza> getSolicitudRetiroTrazas() {
		return this.solicitudRetiroTrazas;
	}

	public void setSolicitudRetiroTrazas(List<SolicitudRetiroTraza> solicitudRetiroTrazas) {
		this.solicitudRetiroTrazas = solicitudRetiroTrazas;
	}

	public SolicitudRetiroTraza addSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().add(solicitudRetiroTraza);
		solicitudRetiroTraza.setSolicitudRetiro(this);

		return solicitudRetiroTraza;
	}

	public SolicitudRetiroTraza removeSolicitudRetiroTraza(SolicitudRetiroTraza solicitudRetiroTraza) {
		getSolicitudRetiroTrazas().remove(solicitudRetiroTraza);
		solicitudRetiroTraza.setSolicitudRetiro(null);

		return solicitudRetiroTraza;
	}

}