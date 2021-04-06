package com.beca.misdivisas.model;

import javax.validation.constraints.NotBlank;

public class AutorizadoBanco {
	private Integer idAutorizado;
	
	private Integer idTipoAutorizado;

	@NotBlank(message = "requerido")
	private String rifAutorizado;
	
	@NotBlank(message = "requerido")
	private String caracterRifAutorizado;

	public Integer getIdAutorizado() {
		return idAutorizado;
	}

	public void setIdAutorizado(Integer idAutorizado) {
		this.idAutorizado = idAutorizado;
	}

	public String getRifAutorizado() {
		return rifAutorizado;
	}

	public void setRifAutorizado(String rifAutorizado) {
		this.rifAutorizado = rifAutorizado;
	}

	public String getCaracterRifAutorizado() {
		return caracterRifAutorizado;
	}

	public void setCaracterRifAutorizado(String caracterRifAutorizado) {
		this.caracterRifAutorizado = caracterRifAutorizado;
	}
	
	public AutorizadoBanco(Integer idAutorizado, Integer idTipoAutorizado, @NotBlank(message = "requerido") String caracterRifAutorizado,
			@NotBlank(message = "requerido") String rifAutorizado) {
		super();
		this.idAutorizado = idAutorizado;
		this.caracterRifAutorizado = caracterRifAutorizado;
		this.rifAutorizado = rifAutorizado;
		this.idTipoAutorizado = idTipoAutorizado;
	}
	
	public AutorizadoBanco() {		
	}
	
	public Integer getIdTipoAutorizado() {
		return idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}


}
