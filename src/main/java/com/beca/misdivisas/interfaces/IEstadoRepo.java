package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Estado;

public interface IEstadoRepo extends JpaRepository<Estado, Integer>{

	@Query("SELECT e FROM Estado e order by e.estado asc")
	public List<Estado> findAll();
}
