package com.beca.misdivisas.model;

import javax.validation.constraints.NotBlank;

public class AutorizadoBeneficioTraspaso extends AutorizadoBanco {
	
	
	private String nombreEmpresa;
	
	public AutorizadoBeneficioTraspaso(Integer idAutorizado, Integer idTipoAutorizado, @NotBlank(message = "requerido") String caracterRifAutorizado,
			@NotBlank(message = "requerido") String rifAutorizado, String nombreEmpresa) {
		super(idAutorizado, idTipoAutorizado, caracterRifAutorizado, rifAutorizado);
		this.nombreEmpresa = nombreEmpresa;
	}
	
	
	public AutorizadoBeneficioTraspaso() {
		super();
	}

	
	public String getNombreEmpresa() {
		return nombreEmpresa;
	}
	public void setNombreEmpresa(String nombreEmpresa) {
		this.nombreEmpresa = nombreEmpresa;
	}
}
