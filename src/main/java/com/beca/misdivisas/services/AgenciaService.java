package com.beca.misdivisas.services;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.util.Constantes;

@Service
public class AgenciaService {
	
	@Autowired
	private IAgenciaRepo agenciaRepo;
	
	
	public boolean estaActiva(int numAgencia) {
		
		
		List<Agencia> agencias = agenciaRepo.findByNumeroAgencia(numAgencia);
		
		if (agencias != null && agencias.size() == 1 && agencias.get(0) != null && agencias.get(0).getIdEstatusAgencia().equals(Constantes.EMPRESA_ACTIVA))
			return true;
		else
			return false;
	}

}
