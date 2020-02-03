package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;


/**
 * The persistent class for the "BLOQUE_IP_EMPRESA" database table.
 * 
 */
@Entity
@Table(name="\"BLOQUE_IP_EMPRESA\"", schema ="\"SEGURIDAD\"")
@NamedQuery(name="BloqueIpEmpresa.findAll", query="SELECT b FROM BloqueIpEmpresa b")
public class BloqueIpEmpresa implements Serializable {
	private static final long serialVersionUID = 1L;

	@Id
	@Column(name="\"idBloqueIpEmpresa\"")
	private Integer idBloqueIpEmpresa;

	@Column(name="\"idEmpresa\"")
	private Integer idEmpresa;

	@Column(name="\"ipFinal\"")
	private Long ipFinal;

	@Column(name="\"ipInicial\"")
	private Long ipInicial;
	
	//bi-directional many-to-one association to Empresa
	@ManyToOne
	@JoinColumns({@JoinColumn(name = "\"idEmpresa\"", insertable = false, updatable = false)
		})
	private Empresa empresa;
	

	public BloqueIpEmpresa() {
	}

	public Integer getIdBloqueIpEmpresa() {
		return this.idBloqueIpEmpresa;
	}

	public void setIdBloqueIpEmpresa(Integer idBloqueIpEmpresa) {
		this.idBloqueIpEmpresa = idBloqueIpEmpresa;
	}

	public Integer getIdEmpresa() {
		return this.idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Long getIpFinal() {
		return this.ipFinal;
	}

	public void setIpFinal(Long ipFinal) {
		this.ipFinal = ipFinal;
	}

	public Long getIpInicial() {
		return this.ipInicial;
	}

	public void setIpInicial(Long ipInicial) {
		this.ipInicial = ipInicial;
	}
	
	public Empresa getEmpresa() {
		return this.empresa;
	}

	public void setEmpresa(Empresa empresa) {
		this.empresa = empresa;
	}

}