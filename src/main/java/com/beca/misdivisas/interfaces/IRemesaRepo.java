package com.beca.misdivisas.interfaces;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Remesa;
import com.beca.misdivisas.model.ReporteSucursalMapa;

public interface IRemesaRepo extends JpaRepository<Remesa, Integer> {

	// @Query("SELECT r FROM Remesa r WHERE LOWER(p.lastName) = LOWER(:lastName)")
	public List<Remesa> findAll();
	// public List<Person> findByName(String lastName);

	@Query("SELECT r FROM Remesa r, Sucursal s  WHERE r.idSucursal = s.idSucursal and s.idSucursal = ?1")
	public List<Remesa> findRemesaBySucursalId(int id);

	@Query("SELECT r FROM Remesa r WHERE r.cartaPorte = ?1")
	public List<Remesa> findRemesaByCartaporte(String cartaPorte);

	@Query("SELECT r FROM Remesa r, Sucursal s WHERE r.idSucursal = s.idSucursal and s.idEmpresa=?1 and r.cartaPorte = ?2")
	public List<Remesa> findRemesaByCartaporteAndIdEmpresa(int idEmpresa, String cartaPorte);

	@Query("SELECT r FROM Remesa r, RemesaDetalle rd, Sucursal s WHERE r.idRemesa=rd.idRemesa and r.idSucursal = s.idSucursal and s.idEmpresa=?1 and r.cartaPorte = ?2")
	public List<Remesa> trackingRemesaByCartaporteAndIdEmpresa(int idEmpresa, String cartaPorte);

	@Query("SELECT r FROM Remesa r, Sucursal s, RemesaDetalle rd"
			+ " WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa and d.fecha between ?3 and ?4) and r.idOperacion not in (9,12) and rd.idEstatusRemesa in (2,4) and s.idEmpresa  = ?1 and rd.idMoneda = ?2   order by rd.fecha asc")
	public List<Remesa> findRemesaByEmpresaId(int idEmpresa, int moneda, Date fechaInicio, Date fechaFin);

	@Query("SELECT r FROM Remesa r, Sucursal s, Empresa e, RemesaDetalle rd, Pieza p"
			+ " WHERE r.idSucursal = s.idSucursal and s.idEmpresa=e.idEmpresa and s.idEmpresa=e.idEmpresa and r.idRemesa = p.idRemesa and rd.idRemesa = r.idRemesa  and e.idEmpresa  = ?1 and rd.idMoneda = ?2  and p.cantidadNoApta >0 and rd.fecha between ?3 and ?4  order by rd.fecha asc")
	public List<Remesa> findRemesaByEmpresaIdNoApto(int idEmpresa, int moneda, Date fechaInicio, Date fechaFin);

	@Query("SELECT r FROM Remesa r, Sucursal s,  RemesaDetalle rd"
			+ " WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and rd.idEstatusRemesa in (2,4) and r.idOperacion not in (9,12) and s.idEmpresa  = ?1 and rd.idMoneda = ?2  order by rd.fecha asc")
	public List<Remesa> getLasRemesaByMoneda(int idEmpresa, int moneda);

	@Query("SELECT SUM(rd.monto) FROM Remesa r, Sucursal s, RemesaDetalle rd"
			+ " WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and r.idOperacion not in (9,12) and s.idEmpresa  = ?1 and rd.idMoneda = ?2 and rd.idEstatusRemesa = ?3")
	public BigDecimal getLastRemesaByStatus(int idEmpresa, int moneda, int status);

	@Query("SELECT SUM(rd.monto) FROM Remesa r, Sucursal s, RemesaDetalle rd"
			+ " WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and s.idSucursal  = ?1 and rd.idMoneda = ?2 and rd.idEstatusRemesa = ?3 and r.idOperacion = ?4")
	public BigDecimal getTotalBySucursal(int idSucursal, int moneda, int status, int operacion);

	@Query("SELECT r FROM Remesa r, Sucursal s, RemesaDetalle rd WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa  and d.fecha between ?3 and ?4) and s.idEmpresa=?1 and r.cartaPorte = ?2 order by rd.fecha asc, rd.idEstatusRemesa asc")
	public List<Remesa> findRemesaByCartaporteAndIdEmpresaByDate(int idEmpresa, String cartaPorte, Date fechaInicio,
			Date fechaFin);

	@Query("SELECT New com.beca.misdivisas.model.ReporteSucursalMapa( SUM(rd.monto), EXTRACT(MONTH FROM rd.fecha), EXTRACT(YEAR FROM rd.fecha))  FROM Remesa r, Sucursal s, RemesaDetalle rd "
			+ "WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa = rd. idRemesa) "
			+ "and  s.idSucursal = ?1 and rd.idMoneda = ?2 and rd.idEstatusRemesa = ?3 and r.idOperacion = ?4 and rd.fecha > ?5 group by EXTRACT(MONTH FROM rd.fecha), EXTRACT(YEAR FROM rd.fecha)  order by EXTRACT(YEAR FROM rd.fecha) asc")
	public List<ReporteSucursalMapa> getTotalMensualBySucursal(int idSucursal, int moneda, int status, int idOperacion,
			Date fechaInicio);

	@Query("SELECT r FROM Remesa r, Sucursal s, RemesaDetalle rd WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.idEstatusRemesa=(SELECT MAX(d.idEstatusRemesa) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and r.idOperacion in (2,8) and rd.idEstatusRemesa =3 and s.idEmpresa=?1 order by rd.fecha asc")
	public List<Remesa> findRemesasPendientesByIdEmpresa(int idEmpresa);

	//@Query("SELECT MAX(fechan) FROM (SELECT MAX(rd.fecha) as fechan FROM Remesa r, Sucursal s, RemesaDetalle rd  WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.fecha=(SELECT MAX(d.fecha) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and s.idEmpresa  = ?1 ) f ")
	@Query("SELECT MAX(rd.fecha) as fechan FROM Remesa r, Sucursal s, RemesaDetalle rd  WHERE r.idSucursal = s.idSucursal and rd.idRemesa = r.idRemesa and rd.fecha=(SELECT MAX(d.fecha) FROM RemesaDetalle d WHERE d.idRemesa=rd.idRemesa) and s.idEmpresa  = ?1")
	public List<Remesa> getFechaDeCorte(int idEmpresa);

}
