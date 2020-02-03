package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "TIPO_OPERACION" database table.
 * 
 */
@Entity
@Table(name="\"TIPO_OPERACION\"", schema ="\"ALMACEN\"")
@NamedQuery(name="TipoOperacion.findAll", query="SELECT t FROM TipoOperacion t")
public class TipoOperacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_tipo_operacion\"")
	private Integer idTipoOperacion;

	@Column(name="\"tipo_operacion\"")
	private String tipoOperacion;

	//bi-directional many-to-one association to Operacion
	@OneToMany(mappedBy="tipoOperacion")
	private List<Operacion> operacions;

	public TipoOperacion() {
	}

	public Integer getIdTipoOperacion() {
		return this.idTipoOperacion;
	}

	public void setIdTipoOperacion(Integer idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}

	public String getTipoOperacion() {
		return this.tipoOperacion;
	}

	public void setTipoOperacion(String tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public List<Operacion> getOperacions() {
		return this.operacions;
	}

	public void setOperacions(List<Operacion> operacions) {
		this.operacions = operacions;
	}

	public Operacion addOperacion(Operacion operacion) {
		getOperacions().add(operacion);
		operacion.setTipoOperacion(this);

		return operacion;
	}

	public Operacion removeOperacion(Operacion operacion) {
		getOperacions().remove(operacion);
		operacion.setTipoOperacion(null);

		return operacion;
	}

}