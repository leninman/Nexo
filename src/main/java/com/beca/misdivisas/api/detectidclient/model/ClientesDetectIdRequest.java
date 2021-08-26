package com.beca.misdivisas.api.detectidclient.model;

public class ClientesDetectIdRequest {
	
	private String ip;
	private String ipOrigen;
	private int idCanal;
	private String idSesion;
	private String idCliente;
	private String sharedKey;
	private String businessDescription;
	private String email;
	private String telefono;
	
	public String getIpOrigen() {
		return ipOrigen;
	}
	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}
	
	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}
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
	public String getBusinessDescription() {
		return businessDescription;
	}
	public void setBusinessDescription(String businessDescription) {
		this.businessDescription = businessDescription;
	}
	public String getEmail() {
		return email;
	}
	public void setEmail(String email) {
		this.email = email;
	}
	public String getTelefono() {
		return telefono;
	}
	public void setTelefono(String telefono) {
		this.telefono = telefono;
	}
	public String getSharedKey() {
		return sharedKey;
	}
	public void setSharedKey(String sharedKey) {
		this.sharedKey = sharedKey;
	}
	public String getIp() {
		return ip;
	}
	public void setIp(String ip) {
		this.ip = ip;
	}
	
	@Override
	public String toString() {
		return "ClientesDetectIdRequest [ip=" + ip + ", ipOrigen=" + ipOrigen + ", idCanal=" + idCanal + ", idSesion="
				+ idSesion + ", idCliente=" + idCliente + ", sharedKey=" + sharedKey + ", businessDescription="
				+ businessDescription + ", email=" + email + ", telefono=" + telefono + "]";
	}

}
