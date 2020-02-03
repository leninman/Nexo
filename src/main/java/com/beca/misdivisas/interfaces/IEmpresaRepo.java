package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Empresa;

public interface IEmpresaRepo extends JpaRepository<Empresa, Integer> {
	
	public Empresa findById(int id);

}
