package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Rol;

public interface IRolRepo extends JpaRepository<Rol, Integer> {
	
	//Rol findByRol(String rol);
	
	//List<Rol> findAll();
	
	/* List<Rol> findAllByOrderByIdEmpresaAsc(); */
	
	/* List<Rol> findByIdEmpresaAndEstado(int idEmpresa, String estado); */
	
	/*
	 * @Query("select r from  Rol r, UsuarioRol ur where r.idRol = ur.idRol and  ur.idUsuario =?1 and r.estado=?2"
	 * ) List<Rol> findByIdUsuarioAndEstado(int idUsuario, String estado);
	 */
	
	/*
	 * @Query("select r from  Rol r where  r.idEmpresa = null and r.estado=?1 and r.rol!=?2"
	 * ) List<Rol> findByIdEmpresaNullAndEstadoAndRol( String estado, String rol);
	 */
	
}
