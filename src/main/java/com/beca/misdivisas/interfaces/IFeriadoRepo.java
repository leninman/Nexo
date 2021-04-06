package com.beca.misdivisas.interfaces;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Feriado;


public interface IFeriadoRepo extends JpaRepository<Feriado, Integer> {
	
	public  List<Feriado> findByFecha(Date fecha);
	
	@Query("select distinct (f) from Feriado f "
			+ "where f.fecha > ?1 order by f.fecha asc")
	List<Feriado> findAllFechaMayorQue(Date fechaInicial);

}
