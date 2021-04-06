package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Municipio;

public interface IMunicipioRepo extends JpaRepository<Municipio, Integer> {
	
	@Query("SELECT m FROM Municipio m WHERE m.idEstado = ?1 order by m.municipio asc")
	public List<Municipio> findAllByIdEstado(int idEstado);
}
