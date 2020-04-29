package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Menu;


public interface IMenuRepo extends JpaRepository<Menu, Integer> {
	
	@Query("select distinct (m) from Menu m, MenuRol mr, Rol r, UsuarioRol ur, Usuario u where m.idMenu = mr.idMenu and mr.idRol = r.idRol and r.idRol = ur.idRol and ur.idUsuario = u.idUsuario and u.idUsuario = ?1 and m.nivel = ?2 order by m.idMenu asc")
	List<Menu> findByIdUsuario(int idUsuario, int nivel);
}
