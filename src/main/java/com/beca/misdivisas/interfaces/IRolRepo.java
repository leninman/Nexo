package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Rol;

public interface IRolRepo extends JpaRepository<Rol, Integer> {
	
	Rol findByRol(String rol);
}
