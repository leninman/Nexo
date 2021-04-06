package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Menu;


public interface IMenuRepo extends JpaRepository<Menu, Integer> {
	

	Menu findById(int idMenu);
	
	//Menu findByIdAndIdPadre(int idMenu, int idMenuPadre);

	@Query("select distinct (m) from Menu m INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil INNER JOIN PerfilUsuario pu ON p.idPerfil=pu.perfil.idPerfil  where  pu.usuario.idUsuario = ?1  order by m.nivel, m.orden asc") 
	List<Menu> findByIdUsuario(int idUsuario, int nivel);
	
	//En las disponibles saldrán las opciones hoja con la secuencia de sus nodos padres en el nombre, se filtraran aquellas que cumplan con 
	//tipo_menu = S, accion != null, tipo_vista= E, visible_externo = S,  visible_solo_admin = N y estado = A
	@Query("select distinct (m) from Menu m where m.tipoMenu= ?1 and m.accion is not null and m.tipoVista = ?2 and m.visibleExterno = true and m.visibleSoloAdmin = ?3 and m.nivel in (2,3) and m.estado = 'A' order by m.idMenu, m.nivel, m.orden asc") 
	List<Menu> findByTipoMenuExterno(String tipoMenu, String tipoVista, boolean visibleSoloAdmin);

	@Query("select distinct (m) from Menu m where m.tipoMenu= ?1 and m.accion is not null and m.tipoVista = ?2 and m.visibleInterno = true and m.visibleSoloAdmin = ?3 and m.nivel in (2,3) and m.estado = 'A' order by m.idMenu, m.nivel, m.orden asc") 
	List<Menu> findByTipoMenuInterno(String tipoMenu, String tipoVista, boolean visibleSoloAdmin);

	
	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN Rol r ON m.rol.idRol=r.idRol "
			+ "where r.idRol = ?1 and m.nivel = ?2 order by m.idMenu asc")
	List<Menu> findByRolId(int rolId, int nivel);
  
	@Query("select distinct (m) from Menu m INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "INNER JOIN PerfilUsuario pu ON p.idPerfil=pu.perfil.idPerfil "
			+ "INNER JOIN Usuario u ON pu.usuario.idUsuario = u.idUsuario "
			+ "where u.idUsuario = ?1 and u.idEmpresa = ?2  and m.tipoMenu=?3 and m.estado='A' order by m.idMenu,m.nivel,m.orden asc")
	List<Menu> findByIdUsuarioAndIdEmpresaAndTipo(int idUsuario, int idEmpresa, String tipo);

	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where p.perfil = ?1  and m.nivel = ?2 and m.estado=?3 order by m.nivel, m.orden asc")
	List<Menu> findByNombrePerfil(String nombrePerfil, int nivel, String estado);
	
	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where p.perfil = ?1  and m.estado=?2 and m.tipoMenu=?3 order by m.nivel, m.orden asc")
	List<Menu> findByNombrePerfilAndEstado(String nombrePerfil, String estado, String tipo);
	
	
	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where p.perfil = ?1  and m.estado=?2 and m.tipoMenu=?3 order by m.nivel, m.orden asc")
	List<Menu> findByNombrePerfil(String nombrePerfil, String estado, String tipo);
	
	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where p.perfil = ?1  and m.estado=?2 and m.tipoMenu=?3 and p.idEmpresa is null order by m.nivel, m.orden asc")
	List<Menu> findByNombrePerfilAndIdEmpresaNullAndEstadoAndTipo(String nombrePerfil, String estado, String tipo);
	
	@Query("SELECT DISTINCT (m) FROM Menu m INNER JOIN PerfilMenu pm on m.idMenu=pm.menu.idMenu WHERE pm.perfil.idPerfil=?1 and m.estado='A'")
	List<Menu> findByIdPerfil(int idPerfil);
	

	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where m.estado=?1 and m.tipoMenu=?2 and p.idEmpresa is null and m.tipoVista=?3 and m.visibleSoloAdmin=?4 order by m.nivel, m.orden asc")
	List<Menu> findByIdEmpresaNullAndEstadoAndTipoVista( String estado, String tipo, String tipoVista, boolean visibleSoloAdmin);

	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where m.estado=?1 and m.tipoMenu=?2 and p.idEmpresa is null and m.tipoVista=?3  order by m.nivel, m.orden asc")
	List<Menu> findByIdEmpresaNullAndEstadoAndTipoVista( String estado, String tipo, String tipoVista);
	
	@Query("select distinct (m) from Menu m "
			+ "INNER JOIN PerfilMenu pm on m.idMenu = pm.menu.idMenu "
			+ "INNER JOIN Perfil p on  pm.perfil.idPerfil= p.idPerfil "
			+ "where p.perfil=?1 and m.estado=?2 and m.tipoMenu=?3 and p.idEmpresa is null and m.tipoVista=?4  order by m.nivel, m.orden asc")
	List<Menu> findByIdEmpresaNullAndEstadoAndTipoVista(String nombrePerfil, String estado, String tipo, String tipoVista);

}
