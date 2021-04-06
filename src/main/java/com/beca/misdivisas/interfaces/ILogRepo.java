package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Log;

public interface ILogRepo extends JpaRepository<Log, Integer> {
	
	@Query("SELECT l FROM Log l WHERE l.idUsuario = ?1 and l.accion = ?2 and l.resultado = ?3 order by l.idLog desc")
	public List<Log> findLogByIdUsuarioAndAccionAndResultado(int idUsuario, String accion, boolean resultado);
	
	@Query("SELECT l FROM Log l WHERE l.nombreUsuario = ?1 and l.accion = ?2 and l.resultado = ?3 order by l.idLog desc")
	public List<Log> findLogByNombreUsuarioAndAccionAndResultado(String nombreUsuario, String accion, boolean resultado);

}
