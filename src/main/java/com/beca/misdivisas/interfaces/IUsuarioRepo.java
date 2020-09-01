package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Usuario;

public interface IUsuarioRepo extends JpaRepository<Usuario, Integer> {
	
	Usuario findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(String nombre, String estado);
	
	Usuario findById(int id);
	
	List<Usuario> findByNombreUsuarioIgnoreCaseAndIdEmpresa(String nombreUsuario, Integer idEmpresa);

	List<Usuario> findByIdEmpresaAndEstadoIgnoreCase(int idEmpresa, String estado);
	
	@Query("select distinct(u) from Usuario u,UsuarioRol ur, Rol r where u.idUsuario = ur.idUsuario and ur.idRol = r.idRol and u.idEmpresa = ?1 and u.estado = ?2 and r.rol not in (?3) order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaAndEstadoAndNotInRol(int idEmpresa, String estado, String usuarioRol);

	@Query("select distinct(u) from Usuario u,UsuarioRol ur, Rol r where u.idUsuario = ur.idUsuario and ur.idRol = r.idRol and u.idEmpresa = ?1 and u.estado = ?2 and r.idRol = ?3 order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaAndEstadoInRol(int idEmpresa, String estado, int rolId);

}
