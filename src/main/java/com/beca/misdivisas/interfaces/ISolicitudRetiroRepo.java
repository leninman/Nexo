package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.SolicitudRetiro;

public interface ISolicitudRetiroRepo extends JpaRepository<SolicitudRetiro, Integer>{
	public SolicitudRetiro findById(int id);
	public List<SolicitudRetiro> findByIdEmpresa(int idEmpresa);
	public List<SolicitudRetiro> findByIdAutorizado(int idAutorizado);
	public List<SolicitudRetiro> findByIdEmpresaAndIdMoneda(int idEmpresa, int idMoneda);
}
