package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Agencia;

public interface IAgenciaRepo extends JpaRepository<Agencia, Integer>{
	public List<Agencia> findByAlmacenamiento(Boolean almacenamiento);
	public List<Agencia> findByNumeroAgencia(Integer numeroAgencia);
	@Query("SELECT DISTINCT(a) FROM Agencia a where a.numeroAgencia = ?1 and a.idEstatusAgencia = ?2 and a.almacenamiento = TRUE")
	public Agencia findByNumeroAgenciaAct(Integer numeroAgencia, Integer estatus);
}
