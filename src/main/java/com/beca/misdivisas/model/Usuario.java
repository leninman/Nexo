package com.beca.misdivisas.model;

import java.util.ArrayList;
import java.util.List;
import com.beca.misdivisas.jpa.Perfil;

public class Usuario {
	private com.beca.misdivisas.jpa.Usuario usuario;
	private List<Perfil> perfiles;
	
	public Usuario() {
		this.usuario = new com.beca.misdivisas.jpa.Usuario();
		this.perfiles = new ArrayList<Perfil>();
	}

	
	public com.beca.misdivisas.jpa.Usuario getUsuario() {
		return usuario;
	}
	public void setUsuario(com.beca.misdivisas.jpa.Usuario usuario) {
		this.usuario = usuario;
	}
	public List<Perfil> getPerfiles() {
		return perfiles;
	}
	public void setPerfiles(List<Perfil> perfiles) {
		this.perfiles = perfiles;
	}

}
