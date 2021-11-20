package com.beca.misdivisas.model;

import javax.validation.constraints.NotEmpty;

public class ValidarOtpModel {
	
	
	private String idSolicitud;
	
	//@NotEmpty(message = "requerido")
	private String otp;
	
	public ValidarOtpModel(String idSolicitud, String otp) {
		super();
		this.idSolicitud = idSolicitud;
		this.otp = otp;
	}
	public ValidarOtpModel() {
		
	}
	public String getIdSolicitud() {
		return idSolicitud;
	}
	public void setIdSolicitud(String idSolicitud) {
		this.idSolicitud = idSolicitud;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	

}
