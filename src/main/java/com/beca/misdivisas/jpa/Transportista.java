package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "TRANSPORTISTA" database table.
 * 
 */
@Entity
@Table(name="\"TRANSPORTISTA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="Transportista.findAll", query="SELECT t FROM Transportista t")
public class Transportista implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_transportista\"")
	private Integer idTransportista;

	private String transportista;

	//bi-directional many-to-one association to Remesa
	@OneToMany(mappedBy="transportista")
	private List<Remesa> remesas;

	public Transportista() {
	}

	public Integer getIdTransportista() {
		return this.idTransportista;
	}

	public void setIdTransportista(Integer idTransportista) {
		this.idTransportista = idTransportista;
	}

	public String getTransportista() {
		return this.transportista;
	}

	public void setTransportista(String transportista) {
		this.transportista = transportista;
	}

	public List<Remesa> getRemesas() {
		return this.remesas;
	}

	public void setRemesas(List<Remesa> remesas) {
		this.remesas = remesas;
	}

	public Remesa addRemesa(Remesa remesa) {
		getRemesas().add(remesa);
		remesa.setTransportista(this);

		return remesa;
	}

	public Remesa removeRemesa(Remesa remesa) {
		getRemesas().remove(remesa);
		remesa.setTransportista(null);

		return remesa;
	}

}