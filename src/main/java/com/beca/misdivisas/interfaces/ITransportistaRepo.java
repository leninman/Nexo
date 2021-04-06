package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Transportista;

public interface ITransportistaRepo extends JpaRepository<Transportista, Integer>{	
	public List<Transportista> findByRif(String rif);
}
