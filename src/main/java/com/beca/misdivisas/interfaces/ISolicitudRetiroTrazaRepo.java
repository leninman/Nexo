package com.beca.misdivisas.interfaces;

import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.SolicitudRetiroTraza;

public interface ISolicitudRetiroTrazaRepo extends JpaRepository<SolicitudRetiroTraza, Integer> {
	
	@Query("SELECT t FROM SolicitudRetiroTraza t where t.idSolicitudRetiroTraza in (SELECT	max(s.idSolicitudRetiroTraza) "
			+ "FROM	SolicitudRetiroTraza s WHERE s.idSolicitud in (?1) group by s.idSolicitud)")
	public List<SolicitudRetiroTraza> findByIdSolicitudInOrderByIdSolicitudRetiroTrazaDesc(List<Integer> solicitudIds);

	public List<SolicitudRetiroTraza> findByIdSolicitudIn(List<Integer> solicitudIds);
	
	public List<SolicitudRetiroTraza> findByIdSolicitud(Integer solicitudIds);

	@Query("SELECT t FROM SolicitudRetiroTraza t where t.idSolicitudRetiroTraza in (SELECT	max(s.idSolicitudRetiroTraza) "
			+ "FROM	SolicitudRetiroTraza s WHERE s.idSolicitud in (?1) and s.fecha >= ?2 and s.fecha <= ?3 group by s.idSolicitud)")
	public List<SolicitudRetiroTraza> findByIdSolicitudInAndFecha(List<Integer> solicitudIds, Date fechaInicio,
			Date fechaFin);

	@Query("SELECT t FROM SolicitudRetiroTraza t where t.idSolicitudRetiroTraza in (SELECT	max(s.idSolicitudRetiroTraza) "
			+ "FROM	SolicitudRetiroTraza s WHERE s.idSolicitud in (?1) and s.fecha >= ?2 and s.fecha <= ?3 and s.idEstatusSolicitud = ?4 group by s.idSolicitud)")
	public List<SolicitudRetiroTraza> findByIdSolicitudInAndFechaAndIdEstatus(List<Integer> solicitudIds,
			Date fechaInicio, Date fechaFin, int idEstatus);

}
