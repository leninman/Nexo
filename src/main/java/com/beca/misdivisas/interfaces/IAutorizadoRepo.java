package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import com.beca.misdivisas.jpa.Autorizado;

public interface IAutorizadoRepo extends JpaRepository<Autorizado, Integer>{
	
	public Autorizado findById(int id);
	public List<Autorizado> findByIdEmpresaAndEstadoOrderByIdTipoAutorizadoAscDocumentoIdentidadAsc(int id, String estado);
	public List<Autorizado> findByidTipoAutorizadoInAndEstadoAndIdEmpresaOrderByIdTipoAutorizadoAscDocumentoIdentidadAsc(List<Integer> tipos, String estado, int idEmpresa);
	public Autorizado findByidTipoAutorizadoAndIdEmpresaAndDocumentoIdentidadAndEstado(int tipo,int idEmpresa, String documentoId,String estado);
	
	public Autorizado findByDocumentoIdentidadAndIdEmpresaAndEstado(String docId, int idEmpesa, String estado );
	
	@Query("SELECT DISTINCT(a) FROM Autorizado a WHERE a.documentoIdentidad like ?1 and a.idEmpresa=?2 and a.estado = ?3 and idTipoAutorizado=?4")
	public Autorizado findByDocumentoIdentidad(String id, int idEmpresa, String estado, int tipoAutorizado);
	
	@Query("SELECT DISTINCT(a) FROM Autorizado a WHERE a.rifEmpresa like ?1 and a.idEmpresa=?2  and a.estado = ?3")
	public Autorizado findByRifEmpresa(String rif, int idEmpresa, String estado);
	
	@Query("SELECT DISTINCT(a) FROM Autorizado a WHERE a.documentoIdentidad like ?1 and a.idEmpresa=?2 and a.rifEmpresa like ?3 and a.estado = ?4")
	public Autorizado findByDocumentoIdentidadAndRifEmpresa(String id, int idEmpresa, String rif, String estado);
	
	public Autorizado findByIdEmpresaAndDocumentoIdentidadAndRifEmpresaAndIdTipoAutorizadoAndEstado(int idEmpresa, String documentoId, String rif,  int tipoAutorizado, String estado);
	
	public Autorizado findByIdEmpresaAndDocumentoIdentidadAndIdTipoAutorizadoAndEstado(int idEmpresa, String documentoId, int tipoAutorizado, String estado);
	
	public Autorizado findByIdTipoAutorizadoAndIdEmpresaAndIdTransportistaAndEstado(int idTipoAutorizado, int idEmpresa, int idTransportista, String estado);

}
