package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import com.beca.misdivisas.jpa.UsuarioRol;

public interface IUsuarioRolRepo extends JpaRepository<UsuarioRol, Integer> {
	
	/* List<UsuarioRol> findByIdUsuario(int idUsuario); */
	
	/* @Transactional
	@Modifying
	@Query("delete from UsuarioRol r where r.rol.idRol=?1")
	void deleteByRolId(int id);
	
	@Transactional
	@Modifying
	@Query("delete from UsuarioRol r where r.usuario.idUsuario=?1")
	void deleteByUserId(int id);*/
}
