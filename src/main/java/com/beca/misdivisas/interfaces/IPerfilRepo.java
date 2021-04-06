package com.beca.misdivisas.interfaces;

import java.sql.Timestamp;
import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.transaction.annotation.Transactional;

import com.beca.misdivisas.jpa.Perfil;

public interface IPerfilRepo extends JpaRepository<Perfil, Integer> {
	@Transactional
	@Modifying
	@Query("update Perfil p set p.estado=?2, p.fechaActualizacion=?3 where p.idPerfil=?1")
	void updateByIdPerfil(int id, String estado, Timestamp fechaActualizacion);
	
	List<Perfil> findByIdEmpresa(Integer idEmpresa);
	
	List<Perfil> findByIdEmpresaAndEstadoOrderByPerfilAsc(int idEmpresa, String estado);
	
	@Query("SELECT distinct(p) FROM Perfil p where p.idEmpresa=?1 and p.estado=?2 and p.tipoPerfil=?3 and p.editable=?4 and upper(p.perfil) not like ?5 order by p.perfil asc")
	List<Perfil> findByIdEmpresaEstadoTipoEditable(int idEmpresa, String estado, String tipo, boolean editable, String perfilExcluido);	
			
	@Query("SELECT distinct(p) FROM Perfil p where p.idEmpresa is NULL and p.estado = ?1 order by p.perfil asc")
	List<Perfil> findByIdEmpresaNullAndEstadoOrderByPerfilAsc(String estado);
	
	@Query("SELECT distinct(p) FROM Perfil p where p.perfil = ?1 and p.idEmpresa=null and p.estado = ?2")
	Perfil findByPerfilAndIdEmpresaNullAndEstado(String nombre, String estado);	
	
	@Query("SELECT distinct(p) FROM Perfil p ")//INNER JOIN perfilUsuario pu ON p.idPerfil = pu.perfil.idPerfil where pu.usuario.idUsuario =?1 and p.idEmpresa=?2 and  p.estado=?3
	List<Perfil> findByIdUsuarioAndIdEmpresaAndEstado(int idUsuario, int idEmpresa, String estado);
	
	@Query(nativeQuery = true, value = "SELECT \"SEGURIDAD\".f_crear_perfiles_nueva_empresa(:idEmpresa)")
	public boolean crearPerfilesNuevaEmpresa(@Param("idEmpresa") int idEmpresa);
	
	@Query("SELECT distinct(p) FROM Perfil p where UPPER(p.perfil) = ?1 and p.idEmpresa=?2 and p.estado = ?3")
	Perfil findByNombrePerfilAndIdEmpresaEstado(String nombre, int idEmpresa, String estado);
	
	@Query("SELECT distinct(p) FROM Perfil p where UPPER(p.perfil) = ?1 and p.idEmpresa is NULL and p.estado = ?2")
	Perfil findByNombrePerfilInternoAndEstado(String nombre, String estado);

}
