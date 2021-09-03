package com.beca.misdivisas.jpa;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;

/**
 * The persistent class for the "AGENCIA_DIA" database table.
 * 
 */
@Entity
@Table(name = "\"AGENCIA_DIA\"", schema = "\"ALMACEN\"")
@NamedQuery(name = "AgenciaDia.findAll", query = "SELECT ad FROM AgenciaDia ad")
public class AgenciaDia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "\"ALMACEN\".\"seq_agencia_dia_idAgenciaDia\"", sequenceName = "\"ALMACEN\".\"seq_agencia_dia_idAgenciaDia\"", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "\"ALMACEN\".\"seq_agencia_dia_idAgenciaDia\"")
	@Column(name = "\"id_agencia_dia\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idAgenciaDia;

	@Column(name = "\"id_agencia\"")
	private Integer idAgencia;

	@Column(name = "\"fecha\"")
	private Date fecha;
	
	//bi-directional one-to-one association to Agencia
	/*	@OneToOne
		@JoinColumns({@JoinColumn(name = "\"agencia\"", insertable = false, updatable = false)
			})
		private Agencia agencia;*/

	public AgenciaDia() {
	}

	public AgenciaDia(Integer idAgencia, Date fecha) {
		super();
		this.idAgencia = idAgencia;
		this.fecha = fecha;
	}

	public Integer getIdAgenciaDia() {
		return idAgenciaDia;
	}

	public void setIdAgenciaDia(Integer idAgenciaDia) {
		this.idAgenciaDia = idAgenciaDia;
	}

	public Integer getIdAgencia() {
		return idAgencia;
	}

	public void setIdAgencia(Integer idAgencia) {
		this.idAgencia = idAgencia;
	}

	public Date getFecha() {
		return fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}
