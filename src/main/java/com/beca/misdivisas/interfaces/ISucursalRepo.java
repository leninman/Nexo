package com.beca.misdivisas.interfaces;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Remesa;
import com.beca.misdivisas.jpa.Sucursal;

public interface ISucursalRepo extends JpaRepository<Sucursal, Integer> {

	public Optional<Sucursal> findById(Integer id);

	@Query("SELECT r FROM Remesa r, Sucursal s  WHERE r.idSucursal = s.idSucursal and s.idSucursal = 1")
	public List<Remesa> findRemesaBySucursalId();
	
	@Query("SELECT s FROM Empresa e, Sucursal s  WHERE e.idEmpresa = s.idEmpresa and e.idEmpresa = ?1")
	public List<Sucursal> findSucursalByEmpId(int id);

}
