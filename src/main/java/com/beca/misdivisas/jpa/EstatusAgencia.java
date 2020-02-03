package com.beca.misdivisas.jpa;



import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


/**
 * The persistent class for the "ESTATUS_AGENCIA" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_AGENCIA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusAgencia.findAll", query="SELECT e FROM EstatusAgencia e")
public class EstatusAgencia implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_estatus_agencia\"")
	private Integer idEstatusAgencia;

	@Column(name="\"estatus_agencia\"")
	private String estatusAgencia;

	//bi-directional many-to-one association to Agencia
	@OneToMany(mappedBy="estatusAgencia")
	private List<Agencia> agencias;

	public EstatusAgencia() {
	}

	public Integer getIdEstatusAgencia() {
		return this.idEstatusAgencia;
	}

	public void setIdEstatusAgencia(Integer idEstatusAgencia) {
		this.idEstatusAgencia = idEstatusAgencia;
	}

	public String getEstatusAgencia() {
		return this.estatusAgencia;
	}

	public void setEstatusAgencia(String estatusAgencia) {
		this.estatusAgencia = estatusAgencia;
	}

	public List<Agencia> getAgencias() {
		return this.agencias;
	}

	public void setAgencias(List<Agencia> agencias) {
		this.agencias = agencias;
	}

	public Agencia addAgencia(Agencia agencia) {
		getAgencias().add(agencia);
		agencia.setEstatusAgencia(this);

		return agencia;
	}

	public Agencia removeAgencia(Agencia agencia) {
		getAgencias().remove(agencia);
		agencia.setEstatusAgencia(null);

		return agencia;
	}

}