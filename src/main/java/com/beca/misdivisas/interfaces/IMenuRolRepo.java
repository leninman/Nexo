package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.MenuRol;

public interface IMenuRolRepo extends JpaRepository<MenuRol, Integer> {
	@Query("select mr from MenuRol mr where idMenu=?1 and idRol=?2")
	MenuRol findByIdmenuAndIdRol(int idMenu, int idRol);
	
	@Transactional
	@Modifying
	@Query("delete from MenuRol where idRol=?1")
	void deleteByRolId(int id);
}
