package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.UsuarioRol;

public interface IUsuarioRolRepo extends JpaRepository<UsuarioRol, Integer> {
	
	List<UsuarioRol> findByIdUsuario(int idUsuario);
}
