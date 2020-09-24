package com.beca.misdivisas.model;

import java.util.List;

import com.beca.misdivisas.jpa.Rol;

public class Usuario {
	
	private com.beca.misdivisas.jpa.Usuario usuario;
	private List<Rol> perfiles;
	
	
	public com.beca.misdivisas.jpa.Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(com.beca.misdivisas.jpa.Usuario usuario) {
		this.usuario = usuario;
	}
	public List<Rol> getPerfiles() {
		return perfiles;
	}
	public void setPerfiles(List<Rol> perfiles) {
		this.perfiles = perfiles;
	}

}
