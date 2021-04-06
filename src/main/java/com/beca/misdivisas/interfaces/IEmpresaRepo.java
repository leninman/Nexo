package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Empresa;

public interface IEmpresaRepo extends JpaRepository<Empresa, Integer> {
	
	public Empresa findById(int id);
	
	@Query("SELECT logo FROM Empresa e WHERE e.idEmpresa = ?1")
	public byte[] getLogoByIdEmpresa(int idEmpresa);

	@Query("SELECT e FROM Empresa e WHERE e.idEstatusEmpresa = ?1 order by e.empresa asc")
	public List<Empresa> findAllActiveOrderByName(int estatus);
	
	@Query("SELECT e FROM Empresa e order by e.empresa asc")
	public List<Empresa> findAllOrderByName();
    
	public List<Empresa> findByCaracterRifAndRif(String caracterRif, Integer rif);

	public List<Empresa> findByRif(Integer rif);
	
	public List<Empresa> findBySigla(String sigla);
	
	public List<Empresa> findByIdEmpresaCoe(int idEmpresaCoe);
}
