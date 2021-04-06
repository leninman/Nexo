package com.beca.misdivisas.model;

import java.io.Serializable;

import javax.validation.constraints.NotBlank;

import org.springframework.web.multipart.MultipartFile;

public class AutorizadoPersonaJuridica implements Serializable {
	private static final long serialVersionUID = 10252L;

	private Integer idAutorizado;

	private Integer idTipoAutorizado;

	@NotBlank(message = "requerido")
	private String tipoDocumentoAutorizado;

	@NotBlank(message = "requerido")
	private String documentoAutorizado;

	@NotBlank(message = "requerido")
	private String nombreAutorizado;

	@NotBlank(message = "requerido")
	private String caracterRifEmpresa;

	@NotBlank(message = "requerido")
	private String rifEmpresa;

	@NotBlank(message = "requerido")
	private String nombreEmpresa;

	@NotBlank(message = "requerido")
	private String telefono;
	private String prefijoTelefono;

	//@NotBlank(message = "requerido")
	private String email;

	@NotBlank(message = "requerido")
	private String cargo;

	private String placa;

	private String marcaModeloColor;

	private MultipartFile documentoImg;
	private String imagenDocumento;
	private String imgDocumentoAutorizado;
	private MultipartFile documentoAdicionalImg;
	private String imagenDocumentoAdicional;
	private String imgDocumentoAdicionalAutorizado;
	private MultipartFile documentoRifImg;
	private String imagenDocumentoRif;
	private String imgDocumentoRifAutorizado;

	public AutorizadoPersonaJuridica() {
	}

	public AutorizadoPersonaJuridica(Integer idAutorizado, Integer idTipoAutorizado,
			@NotBlank(message = "requerido") String tipoDocumentoAutorizado,
			@NotBlank(message = "requerido") String documentoAutorizado,
			@NotBlank(message = "requerido") String nombreAutorizado,
			@NotBlank(message = "requerido") String caracterRifEmpresa,
			@NotBlank(message = "requerido") String rifEmpresa, @NotBlank(message = "requerido") String nombreEmpresa,
			String prefijoTelefono, @NotBlank(message = "requerido") String telefono,
			@NotBlank(message = "requerido") String email, @NotBlank(message = "requerido") String cargo, String placa,
			String marcaModeloColor) {

		super();
		this.idAutorizado = idAutorizado;
		this.idTipoAutorizado = idTipoAutorizado;
		this.tipoDocumentoAutorizado = tipoDocumentoAutorizado;
		this.documentoAutorizado = documentoAutorizado;
		this.nombreAutorizado = nombreAutorizado;
		this.caracterRifEmpresa = caracterRifEmpresa;
		this.rifEmpresa = rifEmpresa;
		this.nombreEmpresa = nombreEmpresa;
		this.telefono = telefono;
		this.prefijoTelefono = prefijoTelefono;
		this.email = email;
		this.placa = placa;
		this.marcaModeloColor = marcaModeloColor;
		this.cargo = cargo;
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

	public String getCaracterRifEmpresa() {
		return caracterRifEmpresa;
	}

	public void setCaracterRifEmpresa(String caracterRifEmpresa) {
		this.caracterRifEmpresa = caracterRifEmpresa;
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

	public MultipartFile getDocumentoAdicionalImg() {
		return documentoAdicionalImg;
	}

	public void setDocumentoAdicionalImg(MultipartFile documentoAdicionalImg) {
		this.documentoAdicionalImg = documentoAdicionalImg;
	}

	public String getImagenDocumentoAdicional() {
		return imagenDocumentoAdicional;
	}

	public void setImagenDocumentoAdicional(String imagenDocumentoAdicional) {
		this.imagenDocumentoAdicional = imagenDocumentoAdicional;
	}

	public String getImgDocumentoAdicionalAutorizado() {
		return imgDocumentoAdicionalAutorizado;
	}

	public void setImgDocumentoAdicionalAutorizado(String imgDocumentoAdicionalAutorizado) {
		this.imgDocumentoAdicionalAutorizado = imgDocumentoAdicionalAutorizado;
	}

	public String getImagenDocumentoRif() {
		return imagenDocumentoRif;
	}

	public void setImagenDocumentoRif(String imagenDocumentoRif) {
		this.imagenDocumentoRif = imagenDocumentoRif;
	}

	public String getImgDocumentoRifAutorizado() {
		return imgDocumentoRifAutorizado;
	}

	public void setImgDocumentoRifAutorizado(String imgDocumentoRifAutorizado) {
		this.imgDocumentoRifAutorizado = imgDocumentoRifAutorizado;
	}

	public Integer getIdTipoAutorizado() {
		return idTipoAutorizado;
	}

	public void setIdTipoAutorizado(Integer idTipoAutorizado) {
		this.idTipoAutorizado = idTipoAutorizado;
	}

	public String getCargo() {
		return cargo;
	}

	public void setCargo(String cargo) {
		this.cargo = cargo;
	}

	public String getPrefijoTelefono() {
		return prefijoTelefono;
	}

	public void setPrefijoTelefono(String prefijoTelefono) {
		this.prefijoTelefono = prefijoTelefono;
	}

	public MultipartFile getDocumentoRifImg() {
		return documentoRifImg;
	}

	public void setDocumentoRifImg(MultipartFile documentoRifImg) {
		this.documentoRifImg = documentoRifImg;
	}

}
