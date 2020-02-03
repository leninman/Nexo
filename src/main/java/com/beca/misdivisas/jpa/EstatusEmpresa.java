package com.beca.misdivisas.jpa;



import java.io.Serializable;
import javax.persistence.*;
import java.util.List;


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

	//bi-directional many-to-one association to Empresa
	@OneToMany(mappedBy="estatusEmpresa")
	private List<Empresa> empresas;

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

	public List<Empresa> getEmpresas() {
		return this.empresas;
	}

	public void setEmpresas(List<Empresa> empresas) {
		this.empresas = empresas;
	}

	public Empresa addEmpresa(Empresa empresa) {
		getEmpresas().add(empresa);
		empresa.setEstatusEmpresa(this);

		return empresa;
	}

	public Empresa removeEmpresa(Empresa empresa) {
		getEmpresas().remove(empresa);
		empresa.setEstatusEmpresa(null);

		return empresa;
	}

}