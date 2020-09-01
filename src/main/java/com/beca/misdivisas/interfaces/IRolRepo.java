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
}
