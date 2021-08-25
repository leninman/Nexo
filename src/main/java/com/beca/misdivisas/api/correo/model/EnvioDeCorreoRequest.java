package com.beca.misdivisas.api.correo.model;

public class EnvioDeCorreoRequest {
	private String desde;
	private String para;
	private String asunto;
	private String encabezado;
	private String cuerpo;
	private String pie;
	private String idCliente;
	private int idCanal;
	private String idSesion;
	private String ipOrigen;
	private boolean esNotification;
	
	public EnvioDeCorreoRequest() {
		super();
	}
			
	public EnvioDeCorreoRequest(String desde, String para, String asunto, String encabezado, String cuerpo, String pie,
			boolean esNotification) {
		super();
		this.desde = desde;
		this.para = para;
		this.asunto = asunto;
		this.encabezado = encabezado;
		this.cuerpo = cuerpo;
		this.pie = pie;
		this.esNotification = esNotification;
	}
	
	public String getPara() {
		return para;
	}
	public void setPara(String para) {
		this.para = para;
	}
	public String getAsunto() {
		return asunto;
	}
	public void setAsunto(String asunto) {
		this.asunto = asunto;
	}
	public String getEncabezado() {
		return encabezado;
	}
	public void setEncabezado(String encabezado) {
		this.encabezado = encabezado;
	}
	public String getCuerpo() {
		return cuerpo;
	}
	public void setCuerpo(String cuerpo) {
		this.cuerpo = cuerpo;
	}
	public String getPie() {
		return pie;
	}
	public void setPie(String pie) {
		this.pie = pie;
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
	public String getIpOrigen() {
		return ipOrigen;
	}
	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}
	public boolean isNotificacion() {
		return esNotification;
	}
	public void setNotificacion(boolean notificacion) {
		this.esNotification = notificacion;
	}
	public String getDesde() {
		return desde;
	}
	public void setDesde(String desde) {
		this.desde = desde;
	}

	@Override
	public String toString() {
		return "EnvioDeCorreoRequest [desde=" + desde + ", para=" + para + ", asunto=" + asunto + ", encabezado="
				+ encabezado + ", cuerpo=" + cuerpo + ", pie=" + pie + ", idCliente=" + idCliente + ", idCanal="
				+ idCanal + ", idSesion=" + idSesion + ", ipOrigen=" + ipOrigen + ", esNotification=" + esNotification
				+ "]";
	}

}
