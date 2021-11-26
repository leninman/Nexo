package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Agencia;

public interface IAgenciaRepo extends JpaRepository<Agencia, Integer>{
	
	@Query("SELECT a FROM Agencia a WHERE a.almacenamiento = ?1 and a.idEstatusAgencia=1 order by a.numeroAgencia asc")
	public List<Agencia> findByAlmacenamiento(Boolean almacenamiento);
	
	@Query("SELECT a FROM Agencia a WHERE a.almacenamiento = ?1 or a.recaudacion = ?2 and a.idEstatusAgencia=1 order by a.numeroAgencia asc")
	public List<Agencia> findByAlmacenamientoOrRecaudacion(Boolean almacenamiento, Boolean recaudacion);
	
	public List<Agencia> findByNumeroAgencia(Integer numeroAgencia);
	
	@Query("SELECT DISTINCT(a) FROM Agencia a where a.numeroAgencia = ?1 and a.idEstatusAgencia = ?2 and a.almacenamiento = TRUE")
	public Agencia findByNumeroAgenciaAct(Integer numeroAgencia, Integer estatus);
	
	public Agencia findById(int id);
	
	@Query("SELECT a FROM Agencia a WHERE a.numeroAgencia = ?1 order by a.idEstatusAgencia asc")
	public List<Agencia> findAllActiveOrderByName(Integer idEstatusAgencia);
	
	@Query("SELECT a FROM Agencia a order by a.idAgencia asc")
	public List<Agencia> findAllOrderByName();
	
	/*
	 * public List<Agencia> findByName(String agencia);
	 * 
	 * public List<Agencia> findByNumber(Integer numeroAgencia);
	 * 
	 * public List<Agencia> findByIdStatus(Integer idEstatusAgencia);
	 * 
	 * public List<Agencia> findByIdMunicipio(Integer idMunicipio);
	 */
}
