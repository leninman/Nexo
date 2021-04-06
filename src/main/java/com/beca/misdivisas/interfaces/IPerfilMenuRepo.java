package com.beca.misdivisas.interfaces;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.PerfilMenu;

public interface IPerfilMenuRepo extends JpaRepository<PerfilMenu, Integer> {
	@Query("select pm from PerfilMenu pm where pm.menu.idMenu=?1 and pm.perfil.idPerfil=?2")
	PerfilMenu findByIdMenuAndIdPerfil(int idMenu, int idPerfil);
	
	@Transactional
	@Modifying
	@Query("delete from PerfilMenu pm where pm.perfil.idPerfil=?1")
	void deleteByIdPerfil(int id);
}
