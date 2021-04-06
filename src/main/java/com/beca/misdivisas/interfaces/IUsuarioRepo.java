package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Usuario;

public interface IUsuarioRepo extends JpaRepository<Usuario, Integer> {
	
	Usuario findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(String nombre, String estado);
	
	@Query("select distinct (u) from Usuario u,Empresa e where u.idEmpresa = e.idEmpresa and e.idEstatusEmpresa = 1 and upper(u.nombreUsuario) like ?1 and u.estado=?2")
	Usuario findByNombreUsuarioAndEstado(String nombre, String estado);

	/*@Query("select distinct (u) from Usuario u where  u.idUsuario in(select ur.usuario.idUsuario from UsuarioRol ur where ur.rol.idRol=?3) and u.estado=?2 and u.idEmpresa=?1")
	Usuario findByEmpresaAndEstadoAndRol(int idEmpresa, String estado, int rol);*/

	Usuario findById(int id);
	
	List<Usuario> findByNombreUsuarioIgnoreCaseAndIdEmpresa(String nombreUsuario, Integer idEmpresa);

	List<Usuario> findByIdEmpresaAndEstadoIgnoreCaseOrderByNombreUsuarioAsc(int idEmpresa, String estado);
	
	/*@Query("select distinct (u) from Usuario u where u.idUsuario not in (select ur.usuario.idUsuario from UsuarioRol ur where ur.rol.idRol in (select r.idRol from  Rol r where r.rol = ?3)) and u.idEmpresa = ?1 and u.estado = ?2 order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaAndEstadoAndNotInRol(int idEmpresa, String estado, String usuarioRol);*/
	
	@Query("select distinct(u) from Usuario u inner join PerfilUsuario pu on u.idUsuario = pu.usuario.idUsuario inner join Perfil p on pu.perfil.idPerfil = p.idPerfil where u.idEmpresa = ?1 and u.estado =?2 and p.perfil not like ?3 order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaAndEstadoNotInPerfil(int idEmpresa, String estado, String perfil);	

	/*@Query("select distinct(u) from Usuario u,UsuarioRol ur, Rol r where u.idUsuario = ur.usuario.idUsuario and ur.rol.idRol = r.idRol and u.idEmpresa = ?1 and u.estado = ?2 and r.idRol = ?3 order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaAndEstadoInRol(int idEmpresa, String estado, int rolId);*/
	
	@Query("select distinct(u) from Usuario u inner join PerfilUsuario pu on u.idUsuario=pu.usuario.idUsuario inner join Perfil p on pu.perfil.idPerfil = p.idPerfil where u.idEmpresa = ?1 and p.idPerfil = ?2 and u.estado = 'A' order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaInPerfil(int idEmpresa, int  idPerfil);
	
	@Query("select distinct(u) from Usuario u inner join PerfilUsuario pu on u.idUsuario=pu.usuario.idUsuario inner join Perfil p on pu.perfil.idPerfil = p.idPerfil where u.idEmpresa = ?1 and p.perfil = ?2 and u.estado = 'A' order by u.nombreUsuario asc")
	List<Usuario> findAllByEmpresaInPerfil(int idEmpresa, String  nombrePerfil);
	
	@Query("select distinct(u) from Usuario u inner join PerfilUsuario pu on u.idUsuario=pu.usuario.idUsuario inner join Perfil p on pu.perfil.idPerfil = p.idPerfil where u.idEmpresa = ?1 and p.idPerfil = ?2 and u.estado = 'A' order by u.nombreUsuario asc")
	List<Usuario> listarUsuarioByIdEmpresaAndIdPerfil(int idEmpresa, int idPerfil);

} 
