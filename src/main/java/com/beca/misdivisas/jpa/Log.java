package com.beca.misdivisas.jpa;

import java.io.Serializable;
import javax.persistence.*;
import java.sql.Timestamp;


/**
 * The persistent class for the "LOG" database table.
 * 
 */
@Entity
@Table(name="\"LOG\"", schema = "\"AUDITORIA\"")
@NamedQuery(name="Log.findAll", query="SELECT l FROM Log l")
public class Log implements Serializable {
	private static final long serialVersionUID = 1L;
	
	@Id
	@SequenceGenerator(name="\"AUDITORIA\".\"seq_log_idLog\"", sequenceName ="\"AUDITORIA\".\"seq_log_idLog\"", allocationSize = 1)
	@GeneratedValue(strategy=GenerationType.SEQUENCE, generator="\"AUDITORIA\".\"seq_log_idLog\"")
	@Column(name="\"id_log\"", unique = true, nullable = false, columnDefinition = "serial")
	private Integer idLog;

	private String accion;

	private String detalle;

	private Timestamp fecha;

	@Column(name="\"id_empresa\"")
	private Integer idEmpresa;

	@Column(name="\"id_usuario\"")
	private Integer idUsuario;

	@Column(name="\"ip_origen\"")
	private String ipOrigen;

	@Column(name="\"nombre_usuario\"")
	private String nombreUsuario;

	@Column(name="\"opcion_menu\"")
	private String opcionMenu;

	private Boolean resultado;

	public Log() {
	}

	public Integer getIdLog() {
		return this.idLog;
	}

	public void setIdLog(Integer idLog) {
		this.idLog = idLog;
	}

	public String getDetalle() {
		return this.detalle;
	}

	public void setDetalle(String detalle) {
		this.detalle = detalle;
	}

	public Timestamp getFecha() {
		return this.fecha;
	}

	public void setFecha(Timestamp fecha) {
		this.fecha = fecha;
	}

	public Boolean getResultado() {
		return this.resultado;
	}

	public void setResultado(Boolean resultado) {
		this.resultado = resultado;
	}

	public String getAccion() {
		return accion;
	}

	public void setAccion(String accion) {
		this.accion = accion;
	}

	public Integer getIdEmpresa() {
		return idEmpresa;
	}

	public void setIdEmpresa(Integer idEmpresa) {
		this.idEmpresa = idEmpresa;
	}

	public Integer getIdUsuario() {
		return idUsuario;
	}

	public void setIdUsuario(Integer idUsuario) {
		this.idUsuario = idUsuario;
	}

	public String getIpOrigen() {
		return ipOrigen;
	}

	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}

	public String getNombreUsuario() {
		return nombreUsuario;
	}

	public void setNombreUsuario(String nombreUsuario) {
		this.nombreUsuario = nombreUsuario;
	}

	public String getOpcionMenu() {
		return opcionMenu;
	}

	public void setOpcionMenu(String opcionMenu) {
		this.opcionMenu = opcionMenu;
	}

}