package com.beca.misdivisas.model;

import java.util.List;

import com.beca.misdivisas.jpa.Usuario;

public class Perfil {
	
	private int idPerfil;
	private String nombrePerfil;
	private String tipoPerfil;
	private String tipoVista;
	private boolean editable;

	private String[] opciones;
	private List<Usuario> usuarios;
	public int getIdPerfil() {
		return idPerfil;
	}
	public void setIdPerfil(int idPerfil) {
		this.idPerfil = idPerfil;
	}
	public String getNombrePerfil() {
		return nombrePerfil;
	}
	public void setNombrePerfil(String nombrePerfil) {
		this.nombrePerfil = nombrePerfil;
	}
	public String[] getOpciones() {
		return opciones;
	}
	public void setOpciones(String[] opciones) {
		this.opciones = opciones;
	}
	public List<Usuario> getUsuarios() {
		return usuarios;
	}
	public void setUsuarios(List<Usuario> usuarios) {
		this.usuarios = usuarios;
	}
	public String getTipoVista() {
		return tipoVista;
	}
	public void setTipoVista(String tipoVista) {
		this.tipoVista = tipoVista;
	}
	public String getTipoPerfil() {
		return tipoPerfil;
	}
	public void setTipoPerfil(String tipoPerfil) {
		this.tipoPerfil = tipoPerfil;
	}
	public boolean isEditable() {
		return editable;
	}
	public void setEditable(boolean editable) {
		this.editable = editable;
	}	

}
