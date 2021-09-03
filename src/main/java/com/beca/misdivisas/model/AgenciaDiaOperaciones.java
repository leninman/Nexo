package com.beca.misdivisas.model;

import java.util.ArrayList;
import java.util.List;

public class AgenciaDiaOperaciones {
	
	
	private List<String> habilitadas = new ArrayList<>();
	
	private	List<AgenciaDiaOperacionesModel> agenciaDiaOperacionesModel;

	public List<AgenciaDiaOperacionesModel> getAgenciaDiaOperacionesModel() {
		return agenciaDiaOperacionesModel;
	}

	public void setAgenciaDiaOperacionesModel(List<AgenciaDiaOperacionesModel> agenciaDiaOperacionesModel) {
		this.agenciaDiaOperacionesModel = agenciaDiaOperacionesModel;
	}

	public List<String> getHabilitadas() {
		return habilitadas;
	}

	public void setHabilitadas(List<String> habilitadas) {
		this.habilitadas = habilitadas;
	}

}
