package com.beca.misdivisas.model;

public class AgenciaFechaOperaciones {
	
	private String dia;
	 
	private Boolean habilitada;	
	
	public AgenciaFechaOperaciones(String dia, Boolean habilitada) {
		super();
		this.dia = dia;
		this.habilitada = habilitada;
	}

	public String getDia() {
			return dia;
		}

		public void setDia(String dia) {
			this.dia = dia;
		}

		public Boolean getHabilitada() {
			return habilitada;
		}

		public void setHabilitada(Boolean habilitada) {
			this.habilitada = habilitada;
		}
}
