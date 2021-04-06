package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Moneda;

public interface IMonedaRepo extends JpaRepository<Moneda, Integer>{
	List<Moneda> findByCodigo(String codigo);
}
