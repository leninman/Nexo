package com.beca.misdivisas.model;

public class TipoAutorizadoModel {
	
	private Integer idTipoAutorizado;

	private String tipoAutorizado;

	public TipoAutorizadoModel() {
	}

	public Integer getIdTipoAutorizado() {
		return idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}

	public String getTipoAutorizado() {
		return tipoAutorizado;
	}

	public void setTipoAutorizado(String tipoAutorizado) {
		this.tipoAutorizado = tipoAutorizado;
	}

	public TipoAutorizadoModel(Integer idTipoAutorizado, String tipoAutorizado) {
		super();
		this.idTipoAutorizado = idTipoAutorizado;
		this.tipoAutorizado = tipoAutorizado;
	}

}
