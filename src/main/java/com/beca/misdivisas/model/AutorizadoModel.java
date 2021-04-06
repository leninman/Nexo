package com.beca.misdivisas.model;

public class AutorizadoModel {
	private Integer idAutorizado;
	private String tipoAutorizado;
	private String rifEmpresa;
	private String nombreEmpresa;
	private String docIdentidad;
	private String nombreAutorizado;
	
	public AutorizadoModel(Integer idAutorizado, String tipoAutorizado, String rifEmpresa, String nombreEmpresa,
			String docIdentidad, String nombreAutorizado) {
		super();
		this.idAutorizado = idAutorizado;
		this.tipoAutorizado = tipoAutorizado;
		this.rifEmpresa = rifEmpresa;
		this.nombreEmpresa = nombreEmpresa;
		this.docIdentidad = docIdentidad;
		this.nombreAutorizado = nombreAutorizado;
	}
	
	
	public AutorizadoModel(Integer idAutorizado, String nombreAutorizado) {
		super();
		this.idAutorizado = idAutorizado;
		this.nombreAutorizado = nombreAutorizado;
	}


	public Integer getIdAutorizado() {
		return idAutorizado;
	}
	public void setIdAutorizado(Integer idAutorizado) {
		this.idAutorizado = idAutorizado;
	}
	public String getTipoAutorizado() {
		return tipoAutorizado;
	}
	public void setTipoAutorizado(String tipoAutorizado) {
		this.tipoAutorizado = tipoAutorizado;
	}
	public String getRifEmpresa() {
		return rifEmpresa;
	}
	public void setRifEmpresa(String rifEmpresa) {
		this.rifEmpresa = rifEmpresa;
	}
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
	public String getDocIdentidad() {
		return docIdentidad;
	}
	public void setDocIdentidad(String docIdentidad) {
		this.docIdentidad = docIdentidad;
	}
	public String getNombreAutorizado() {
		return nombreAutorizado;
	}
	public void setNombreAutorizado(String nombreAutorizado) {
		this.nombreAutorizado = nombreAutorizado;
	}
	
	

}
