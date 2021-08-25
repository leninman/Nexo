package com.beca.misdivisas.api.cuentas.model;

import com.google.gson.annotations.SerializedName;

public class ConsultarCuentasRequest {
	@SerializedName("id_cliente")
	private String idCliente; //rif a consultar
	private String idConsumidor;
	@SerializedName("id_canal")
	private String idCanal;
	private String IpOrigen;
	private String idUsuario;
	private String idTerminal;
	
	public String getIdCliente() {
		return idCliente;
	}
	public void setIdCliente(String idCliente) {
		this.idCliente = idCliente;
	}
	public String getIdConsumidor() {
		return idConsumidor;
	}
	public void setIdConsumidor(String idConsumidor) {
		this.idConsumidor = idConsumidor;
	}
	public String getIdCanal() {
		return idCanal;
	}
	public void setIdCanal(String idCanal) {
		this.idCanal = idCanal;
	}
	public String getIpOrigen() {
		return IpOrigen;
	}
	public void setIpOrigen(String ipOrigen) {
		IpOrigen = ipOrigen;
	}
	public String getIdUsuario() {
		return idUsuario;
	}
	public void setIdUsuario(String idUsuario) {
		this.idUsuario = idUsuario;
	}
	public String getIdTerminal() {
		return idTerminal;
	}
	public void setIdTerminal(String idTerminal) {
		this.idTerminal = idTerminal;
	}
	
	@Override
	public String toString() {
		return "ConsultarCuentasRequest [idCliente=" + idCliente + ", idConsumidor=" + idConsumidor + ", idCanal="
				+ idCanal + ", IpOrigen=" + IpOrigen + ", idUsuario=" + idUsuario + ", idTerminal=" + idTerminal + "]";
	}

}
