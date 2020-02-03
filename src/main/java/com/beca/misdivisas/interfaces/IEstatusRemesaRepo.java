package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.EstatusRemesa;

public interface IEstatusRemesaRepo extends JpaRepository<EstatusRemesa, Integer> {
	@Query("SELECT er FROM EstatusRemesa er WHERE er.idEstatusRemesa = ?1")
	public EstatusRemesa findEstatusRemesaById(int id);
}
