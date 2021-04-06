package com.beca.misdivisas.jpa;



import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "ESTATUS_EMPRESA" database table.
 * 
 */
@Entity
@Table(name="\"ESTATUS_EMPRESA\"", schema ="\"ALMACEN\"")
@NamedQuery(name="EstatusEmpresa.findAll", query="SELECT e FROM EstatusEmpresa e")
public class EstatusEmpresa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"id_estatus_empresa\"")
	private Integer idEstatusEmpresa;

	@Column(name="\"estatus_empresa\"")
	private String estatusEmpresa;


	public EstatusEmpresa() {
	}

	public Integer getIdEstatusEmpresa() {
		return this.idEstatusEmpresa;
	}

	public void setIdEstatusEmpresa(Integer idEstatusEmpresa) {
		this.idEstatusEmpresa = idEstatusEmpresa;
	}

	public String getEstatusEmpresa() {
		return this.estatusEmpresa;
	}

	public void setEstatusEmpresa(String estatusEmpresa) {
		this.estatusEmpresa = estatusEmpresa;
	}
}