package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.MenuRol;

public interface IMenuRolRepo extends JpaRepository<MenuRol, Integer> {
	@Query("select mr from MenuRol mr where idMenu=?1 and idRol=?2")
	MenuRol findByIdmenuAndIdRol(int idMenu, int idRol);
}
