package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.PerfilUsuario;

public interface IPerfilUsuarioRepo extends JpaRepository<PerfilUsuario, Integer> {
	
	List<PerfilUsuario> findByUsuarioIdUsuario(int idUsuario);
	
	@Transactional
	@Modifying
	@Query("delete from PerfilUsuario p where p.perfil.idPerfil=?1")
	void deleteByIdPerfil(int id);
	
	@Transactional
	@Modifying
	@Query("delete from PerfilUsuario p where p.usuario.idUsuario=?1")
	void deleteByUserId(int id);
}
