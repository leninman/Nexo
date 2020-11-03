package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.UsuarioEquipoFrecuente;

public interface IUsuarioEquipoFrecuente extends JpaRepository<UsuarioEquipoFrecuente, Integer> {
	
	List<UsuarioEquipoFrecuente> findByIdUsuario(int idUsuario);

}
