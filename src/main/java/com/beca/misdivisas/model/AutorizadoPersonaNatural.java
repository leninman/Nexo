package com.beca.misdivisas.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

public class AutorizadoPersonaNatural implements Serializable {
	private static final long serialVersionUID = 10253L;

	private Integer idAutorizado;

	private Integer idTipoAutorizado;

	@NotBlank(message = "requerido")
	private String tipoDocumentoAutorizado;

	@NotBlank(message = "requerido")
	private String documentoAutorizado;

	@NotBlank(message = "requerido")
	private String nombreAutorizado;

	@NotBlank(message = "requerido")
	private String telefono;

	private String prefijoTelefono;

	//@NotBlank(message = "requerido")
	private String email;

	private String placa;

	private String marcaModeloColor;

	private MultipartFile documentoImg;
	private String imagenDocumento;
	private String imgDocumentoAutorizado;

	public AutorizadoPersonaNatural(Integer idAutorizado, Integer idTipoAutorizado,
			@NotBlank(message = "requerido") String tipoDocumentoAutorizado,
			@NotBlank(message = "requerido") String documentoAutorizado,
			@NotBlank(message = "requerido") String nombreAutorizado, String prefijoTelefono,
			@NotBlank(message = "requerido") String telefono, @NotBlank(message = "requerido") String email,
			String placa, String marcaModeloColor, String documentoImg) {
		super();
		this.idAutorizado = idAutorizado;
		this.idTipoAutorizado = idTipoAutorizado;
		this.tipoDocumentoAutorizado = tipoDocumentoAutorizado;
		this.documentoAutorizado = documentoAutorizado;
		this.nombreAutorizado = nombreAutorizado;
		this.prefijoTelefono = prefijoTelefono;
		this.telefono = telefono;
		this.email = email;
		this.placa = placa;
		this.marcaModeloColor = marcaModeloColor;
		this.imagenDocumento =documentoImg; 
	}

	public AutorizadoPersonaNatural() {
	}

	public Integer getIdAutorizado() {
		return idAutorizado;
	}

	public void setIdAutorizado(Integer idAutorizado) {
		this.idAutorizado = idAutorizado;
	}

	public String getTipoDocumentoAutorizado() {
		return tipoDocumentoAutorizado;
	}

	public void setTipoDocumentoAutorizado(String tipoDocumentoAutorizado) {
		this.tipoDocumentoAutorizado = tipoDocumentoAutorizado;
	}

	public String getDocumentoAutorizado() {
		return documentoAutorizado;
	}

	public void setDocumentoAutorizado(String documentoAutorizado) {
		this.documentoAutorizado = documentoAutorizado;
	}

	public String getNombreAutorizado() {
		return nombreAutorizado;
	}

	public void setNombreAutorizado(String nombreAutorizado) {
		this.nombreAutorizado = nombreAutorizado;
	}

	public String getTelefono() {
		return telefono;
	}

	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getPlaca() {
		return placa;
	}

	public void setPlaca(String placa) {
		this.placa = placa;
	}

	public String getMarcaModeloColor() {
		return marcaModeloColor;
	}

	public void setMarcaModeloColor(String marcaModeloColor) {
		this.marcaModeloColor = marcaModeloColor;
	}

	public MultipartFile getDocumentoImg() {
		return documentoImg;
	}

	public void setDocumentoImg(MultipartFile documentoImg) {
		this.documentoImg = documentoImg;
	}

	public String getImagenDocumento() {
		return imagenDocumento;
	}

	public void setImagenDocumento(String imagenDocumento) {
		this.imagenDocumento = imagenDocumento;
	}

	public String getImgDocumentoAutorizado() {
		return imgDocumentoAutorizado;
	}

	public void setImgDocumentoAutorizado(String imgDocumentoAutorizado) {
		this.imgDocumentoAutorizado = imgDocumentoAutorizado;
	}

	public Integer getIdTipoAutorizado() {
		return idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}

	public String getPrefijoTelefono() {
		return prefijoTelefono;
	}

	public void setPrefijoTelefono(String prefijoTelefono) {
		this.prefijoTelefono = prefijoTelefono;
	}

}
