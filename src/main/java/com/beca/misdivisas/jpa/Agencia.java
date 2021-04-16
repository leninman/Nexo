package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import java.sql.Timestamp;
import java.util.List;




/**
 * The persistent class for the "AGENCIA" database table.
 * 
 */
@Entity
@Table(name="\"AGENCIA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Agencia.findAll", query="SELECT a FROM Agencia a")
public class Agencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_agencia_idAgencia\"", sequenceName ="\"ALMACEN\".\"seq_agencia_idAgencia\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_agencia_idAgencia\"")
	@Column(name="\"id_agencia\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idAgencia;

	@Column(name="\"agencia\"")
	private String agencia;

	@Column(name="\"fecha_creacion\"")
	private Timestamp fechaCreacion;

	@Column(name="\"fecha_estatus\"")
	private Timestamp fechaEstatus; 

	@Column(name="\"id_estatus_agencia\"")
	private Integer idEstatusAgencia;

	@Column(name="\"id_municipio\"")
	private Integer idMunicipio;

	@Column(name="latitud")
	private String latitud;

	@Column(name="longitud")
	private String longitud;

	@Column(name="\"numero_agencia\"")
	private Integer numeroAgencia;
	
	private Boolean almacenamiento;
	
	private Boolean recaudacion;
	
	//bi-directional many-to-one association to EstatusAgencia
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_estatus_agencia\"", insertable = false, updatable = false)
		})
	private EstatusAgencia estatusAgencia;
	
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_municipio\"", insertable = false, updatable = false)
		})
	private Municipio municipio;


	//bi-directional many-to-one association to Remesa
	@OneToMany(mappedBy="agencia")
	private List<Remesa> remesas;

	public Agencia() {
	}

	public Integer getIdAgencia() {
		return this.idAgencia;
	}

	public void setIdAgencia(Integer idAgencia) {
		this.idAgencia = idAgencia;
	}

	public String getAgencia() {
		return this.agencia;
	}

	public void setAgencia(String agencia) {
		this.agencia = agencia;
	}

	public Timestamp getFechaCreacion() {
		return this.fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Timestamp getFechaEstatus() {
		return this.fechaEstatus;
	}

	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public Integer getIdEstatusAgencia() {
		return this.idEstatusAgencia;
	}

	public void setIdEstatusAgencia(Integer idEstatusAgencia) {
		this.idEstatusAgencia = idEstatusAgencia;
	}

	public Integer getIdMunicipio() {
		return this.idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public String getLatitud() {
		return this.latitud;
	}

	public void setLatitud(String latitud) {
		this.latitud = latitud;
	}

	public String getLongitud() {
		return this.longitud;
	}

	public void setLongitud(String longitud) {
		this.longitud = longitud;
	}

	public Integer getNumeroAgencia() {
		return this.numeroAgencia;
	}

	public void setNumeroAgencia(Integer numeroAgencia) {
		this.numeroAgencia = numeroAgencia;
	}
	
	public EstatusAgencia getEstatusAgencia() {
		return estatusAgencia;
	}

	public void setEstatusAgencia(EstatusAgencia estatusAgencia) {
		this.estatusAgencia = estatusAgencia;
	}

	public Boolean getAlmacenamiento() {
		return almacenamiento;
	}

	public void setAlmacenamiento(Boolean almacenamiento) {
		this.almacenamiento = almacenamiento;
	}
	
	public List<Remesa> getRemesas() {
		return this.remesas;
	}

	public void setRemesas(List<Remesa> remesas) {
		this.remesas = remesas;
	}

	public Remesa addRemesa(Remesa remesa) {
		getRemesas().add(remesa);
		remesa.setAgencia(this);

		return remesa;
	}

	public Remesa removeRemesa(Remesa remesa) {
		getRemesas().remove(remesa);
		remesa.setAgencia(null);

		return remesa;
	}
	
	public Municipio getMunicipio() {
		return municipio;
	}

	public void setMunicipio(Municipio municipio) {
		this.municipio = municipio;
	}

	public Boolean getRecaudacion() {
		return recaudacion;
	}

	public void setRecaudacion(Boolean recaudacion) {
		this.recaudacion = recaudacion;
	}
	

}