package com.beca.misdivisas.api.detectidotp.model;

public class OtpRequest {
	
	private int idCanal;
	private String idSesion;
	private String idUsuario;
	private String sharedKey;
	private String channel;
	private String modulo;
	private String ip;
	private String otp;
	private String proposito;
	
	public int getIdCanal() {
		return idCanal;
	}
	public void setIdCanal(int idCanal) {
		this.idCanal = idCanal;
	}
	public String getIdSesion() {
		return idSesion;
	}
	public void setIdSesion(String idSesion) {
		this.idSesion = idSesion;
	}
	public String getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getSharedKey() {
		return sharedKey;
	}
	public void setSharedKey(String sharedKey) {
		this.sharedKey = sharedKey;
	}
	public String getChannel() {
		return channel;
	}
	public void setChannel(String channel) {
		this.channel = channel;
	}
	public String getModulo() {
		return modulo;
	}
	public void setModulo(String modulo) {
		this.modulo = modulo;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	public String getOtp() {
		return otp;
	}
	public void setOtp(String otp) {
		this.otp = otp;
	}
	public String getProposito() {
		return proposito;
	}
	public void setProposito(String proposito) {
		this.proposito = proposito;
	}
	
	@Override
	public String toString() {
		return "OtpRequest [idCanal=" + idCanal + ", idSesion=" + idSesion + ", idUsuario=" + idUsuario + ", sharedKey="
				+ sharedKey + ", channel=" + channel + ", modulo=" + modulo + ", ip=" + ip + ", otp=" + otp
				+ ", proposito=" + proposito + "]";
	}
    

}
