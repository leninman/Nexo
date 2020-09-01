package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.UsuarioRol;

public interface IUsuarioRolRepo extends JpaRepository<UsuarioRol, Integer> {
	
	List<UsuarioRol> findByIdUsuario(int idUsuario);
	
	@Transactional
	@Modifying
	@Query("delete from UsuarioRol where idRol=?1")
	void deleteByRolId(int id);
}
