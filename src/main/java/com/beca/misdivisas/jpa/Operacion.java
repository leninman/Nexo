package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;

import com.fasterxml.jackson.annotation.JsonBackReference;

import java.util.List;


/**
 * The persistent class for the "OPERACION" database table.
 * 
 */
@Entity
@Table(name="\"OPERACION\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Operacion.findAll", query="SELECT o FROM Operacion o")
public class Operacion implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_operacion\"")
	private Integer idOperacion;

	@Column(name="\"id_tipo_operacion\"")
	private Integer idTipoOperacion;

	private String operacion;

	//bi-directional many-to-one association to TipoOperacion
	@JsonBackReference
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"id_tipo_operacion\"", insertable = false, updatable = false)
		})
	private TipoOperacion tipoOperacion;

	//bi-directional many-to-one association to Remesa
	@JsonBackReference
	@OneToMany(mappedBy="operacion")
	private List<Remesa> remesas;

	public Operacion() {
	}

	public Integer getIdOperacion() {
		return this.idOperacion;
	}

	public void setIdOperacion(Integer idOperacion) {
		this.idOperacion = idOperacion;
	}

	public Integer getIdTipoOperacion() {
		return this.idTipoOperacion;
	}

	public void setIdTipoOperacion(Integer idTipoOperacion) {
		this.idTipoOperacion = idTipoOperacion;
	}

	public String getOperacion() {
		return this.operacion;
	}

	public void setOperacion(String operacion) {
		this.operacion = operacion;
	}

	public TipoOperacion getTipoOperacion() {
		return this.tipoOperacion;
	}

	public void setTipoOperacion(TipoOperacion tipoOperacion) {
		this.tipoOperacion = tipoOperacion;
	}

	public List<Remesa> getRemesas() {
		return this.remesas;
	}

	public void setRemesas(List<Remesa> remesas) {
		this.remesas = remesas;
	}

	public Remesa addRemesa(Remesa remesa) {
		getRemesas().add(remesa);
		remesa.setOperacion(this);

		return remesa;
	}

	public Remesa removeRemesa(Remesa remesa) {
		getRemesas().remove(remesa);
		remesa.setOperacion(null);

		return remesa;
	}

}