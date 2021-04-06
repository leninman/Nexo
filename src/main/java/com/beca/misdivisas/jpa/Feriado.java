package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.Date;


/**
 * The persistent class for the "FERIADO" database table.
 * 
 */
@Entity
@Table(name="\"FERIADO\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Feriado.findAll", query="SELECT f FROM Feriado f")
public class Feriado implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name="\"ALMACEN\".\"seq_feriado_idFeriado\"", sequenceName ="\"ALMACEN\".\"seq_feriado_idFeriado\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"ALMACEN\".\"seq_feriado_idFeriado\"")
	@Column(name="\"id_feriado\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idFeriado;

	@Column(name="fecha")
	private Date fecha;

	public Feriado() {
	}

	public Integer getIdFeriado() {
		return this.idFeriado;
	}

	public void setIdFeriado(Integer idFeriado) {
		this.idFeriado = idFeriado;
	}

	public Date getFecha() {
		return this.fecha;
	}

	public void setFecha(Date fecha) {
		this.fecha = fecha;
	}

}