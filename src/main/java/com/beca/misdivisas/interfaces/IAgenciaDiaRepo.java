package com.beca.misdivisas.interfaces;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.AgenciaDia;


public interface IAgenciaDiaRepo extends JpaRepository<AgenciaDia, Integer>{
	
	@Query("SELECT DISTINCT (ad) FROM AgenciaDia ad WHERE ad.fecha >= ?1 AND ad.fecha <= ?2")
	public List<AgenciaDia> findAllByFecha(Date fechaInicial, Date fechaFin);
	
	@Query("SELECT ad FROM AgenciaDia ad WHERE ad.fecha >= ?1")
	public List<AgenciaDia> findAllFecha(Date fecha);
	
	@Transactional
	@Modifying
	@Query("DELETE FROM AgenciaDia ad WHERE ad.fecha >= ?1 AND ad.fecha <=?2")
	void deleteByFecha(Date fechaInicial, Date fechaFin);
	
}
