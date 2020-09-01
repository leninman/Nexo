package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Empresa;

public interface IEmpresaRepo extends JpaRepository<Empresa, Integer> {
	
	public Empresa findById(int id);
	
	@Query("SELECT logo FROM Empresa e WHERE e.idEmpresa = ?1")
	public byte[] getLogoByIdEmpresa(int idEmpresa);


}
