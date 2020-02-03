package com.beca.misdivisas.interfaces;

import java.util.List;

import org.springframework.data.jpa.repository.JpaRepository;

import com.beca.misdivisas.jpa.Usuario;

public interface IUsuarioRepo extends JpaRepository<Usuario, Integer> {
	
	Usuario findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(String nombre, String estado);
	
	Usuario findById(int id);
	
	List<Usuario> findByNombreUsuarioIgnoreCaseAndIdEmpresa(String nombreUsuario, Integer idEmpresa);

	List<Usuario> findByIdEmpresaAndEstadoIgnoreCase(int idEmpresa, String estado);

}
