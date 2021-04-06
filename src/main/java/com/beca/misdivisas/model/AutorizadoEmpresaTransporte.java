package com.beca.misdivisas.model;

import javax.validation.constraints.NotBlank;

public class AutorizadoEmpresaTransporte extends AutorizadoBanco {
	
	
	private String nombreAutorizado;
	private int idEmpresa;
	private int idTransportista;
	
	public AutorizadoEmpresaTransporte(Integer idAutorizado, Integer idTipoAutorizado, @NotBlank(message = "requerido") String caracterRifAutorizado,
			@NotBlank(message = "requerido") String rifAutorizado, String nombreAutorizado) {
		super(idAutorizado, idTipoAutorizado, caracterRifAutorizado, rifAutorizado);
		this.nombreAutorizado = nombreAutorizado;
	}
	
	public AutorizadoEmpresaTransporte() {
		super();
	}

	
	public String getNombreAutorizado() {
		return nombreAutorizado;
	}
	public void setNombreAutorizado(String nombreAutorizado) {
		this.nombreAutorizado = nombreAutorizado;
	}

	public int getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(int idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public int getIdTransportista() {
		return idTransportista;
	}

	public void setIdTransportista(int idTransportista) {
		this.idTransportista = idTransportista;
	}
	
}
