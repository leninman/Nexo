package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.Rol;

public interface IRolRepo extends JpaRepository<Rol, Integer> {
	
	@Transactional
	@Modifying
	@Query("update Rol r set r.estado=?2 where r.idRol=?1")
	void deleteByRolId(int id, String estado);
	
	Rol findByRol(String rol);
	List<Rol> findAll();
	List<Rol> findAllByOrderByIdEmpresaAsc();
	List<Rol> findByIdEmpresaAndEstado(int idEmpresa, String estado);
	@Query("select r from  Rol r, UsuarioRol ur where r.idRol = ur.idRol and  ur.idUsuario =?1 and r.estado=?2")
	List<Rol> findByIdUsuarioAndEstado(int idUsuario, String estado);
	
	@Query("select r from  Rol r where  r.idEmpresa = null and r.estado=?1")
	List<Rol> findByIdEmpresaNullAndEstado( String estado);
	
}
