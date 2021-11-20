package com.beca.misdivisas.interfaces;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.EstatusSolicitudRetiro;
import com.beca.misdivisas.jpa.SolicitudRetiroTraza;

public interface IEstatusSolicitudRetiroRepo extends JpaRepository<EstatusSolicitudRetiro, Integer>{
	
	
	@Query("SELECT t FROM EstatusSolicitudRetiro t where t.idEstatusSolicitud  != 8")
	public List<EstatusSolicitudRetiro> findByIdEstatusSolicitud( );

}
