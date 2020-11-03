package com.beca.misdivisas.model;

import java.sql.Timestamp;
import java.util.Base64;

import javax.validation.constraints.NotBlank;
import javax.validation.constraints.NotNull;

import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class EmpresaModel {
	private Integer idEmpresa;
	private String caracterRif;
	private String direccion;

	@NotBlank(message = "requerido")
	private String nombre;

	private Timestamp fechaCreacion;
	private Timestamp fechaEstatus;
	private Integer idEstatusEmpresa;

	@NotNull(message = "requerido")
	private Integer idMunicipio;

	private Integer idEstado;
	private Integer idEmpresaCoe;

	@NotBlank(message = "requerido")
	private String rif;
	
	@NotBlank(message = "requerido")
	private String sigla;

	private MultipartFile logo;

	private String imagenLogo;
	
	private String estatus;
	
	private String logoEmpresa;

	
	public String getLogoEmpresa() {
		return logoEmpresa;
	}

	public void setLogoEmpresa(String logoEmpresa) {
		this.logoEmpresa = logoEmpresa;
	}

	public EmpresaModel(Integer idEmpresa, String caracterRif, String direccion, String nombre, Timestamp fechaCreacion,
			Timestamp fechaEstatus, Integer idEstatusEmpresa, Integer idMunicipio, Integer idEmpresaCoe, String rif,
			String sigla, byte[] logo) {

		this.idEmpresa = idEmpresa;
		this.caracterRif = caracterRif;
		this.direccion = direccion;
		this.nombre = nombre;
		this.fechaCreacion = fechaCreacion;
		this.fechaEstatus = fechaEstatus;
		this.idEstatusEmpresa = idEstatusEmpresa;
		this.idMunicipio = idMunicipio;
		this.idEmpresaCoe = idEmpresaCoe;
		this.rif = rif;
		this.sigla = sigla;
		if (logo != null) {
			this.imagenLogo = Base64.getEncoder().encodeToString(logo);
			this.logoEmpresa = imagenLogo;
			MockMultipartFile mockMultipartFile = new MockMultipartFile("logo", logo);
			this.logo = mockMultipartFile;
		}
	}

	public EmpresaModel() {
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public String getCaracterRif() {
		return caracterRif;
	}

	public void setCaracterRif(String caracterRif) {
		this.caracterRif = caracterRif;
	}

	public String getDireccion() {
		return direccion;
	}

	public void setDireccion(String direccion) {
		this.direccion = direccion;
	}

	public String getNombre() {
		return nombre;
	}

	public void setNombre(String nombre) {
		this.nombre = nombre;
	}

	public Timestamp getFechaCreacion() {
		return fechaCreacion;
	}

	public void setFechaCreacion(Timestamp fechaCreacion) {
		this.fechaCreacion = fechaCreacion;
	}

	public Timestamp getFechaEstatus() {
		return fechaEstatus;
	}

	public void setFechaEstatus(Timestamp fechaEstatus) {
		this.fechaEstatus = fechaEstatus;
	}

	public Integer getIdEstatusEmpresa() {
		return idEstatusEmpresa;
	}

	public void setIdEstatusEmpresa(Integer idEstatusEmpresa) {
		this.idEstatusEmpresa = idEstatusEmpresa;
	}

	public Integer getIdMunicipio() {
		return idMunicipio;
	}

	public void setIdMunicipio(Integer idMunicipio) {
		this.idMunicipio = idMunicipio;
	}

	public Integer getIdEstado() {
		return idEstado;
	}

	public void setIdEstado(Integer idEstado) {
		this.idEstado = idEstado;
	}

	public Integer getIdEmpresaCoe() {
		return idEmpresaCoe;
	}

	public void setIdEmpresaCoe(Integer idEmpresaCoe) {
		this.idEmpresaCoe = idEmpresaCoe;
	}

	public String getRif() {
		return rif;
	}

	public void setRif(String rif) {
		this.rif = rif;
	}

	public String getSigla() {
		return sigla;
	}

	public void setSigla(String sigla) {
		this.sigla = sigla;
	}

	public MultipartFile getLogo() {
		return logo;
	}

	public void setLogo(MultipartFile logo) {
		this.logo = logo;
	}

	public String getImagenLogo() {
		return imagenLogo;
	}

	public void setImagenLogo(String imagenLogo) {
		this.imagenLogo = imagenLogo;
	}
	
	public String getEstatus() {
		return estatus;
	}

	public void setEstatus(String estatus) {
		this.estatus = estatus;
	}
}
