package com.beca.misdivisas.jpa;



import java.io.Serializable;
import javax.persistence.*;


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
}