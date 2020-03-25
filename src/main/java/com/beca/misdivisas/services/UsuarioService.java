package com.beca.misdivisas.services;

import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.jpa.UsuarioRol;

@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private IUsuarioRepo repo;
	
	@Autowired
	private ILogRepo logRepo;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	private String ipOrigen;

	public String getIpOrigen() {
		return ipOrigen;
	}

	public void setIpOrigen(String ipOrigen) {
		this.ipOrigen = ipOrigen;
	}
	
	@Override
	public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
		UserDetails userDet = null;
		List<GrantedAuthority> roles = null;
		Usuario us = null;
		
		HttpSession session = factory.getObject();
				
		if (username!=null && username.length() > 0) {
			us = repo.findByNombreUsuarioIgnoreCaseAndEstadoIgnoreCase(username,"A");
			if (us!=null) {				
					
					session.removeAttribute("Usuario");
					us.setTipoUsuario("Externo");
					session.setAttribute("Usuario", us);				
					
					Date date = new Date();
					
					Log audit = new Log();
					audit.setFecha(new Timestamp(date.getTime()));
					audit.setIpOrigen(this.getIpOrigen());
					audit.setAccion("Ingreso");
					audit.setDetalle("Acceso al sistema");
					audit.setIdEmpresa(us.getIdEmpresa());
					audit.setIdUsuario(us.getIdUsuario());
					audit.setNombreUsuario(us.getNombreUsuario());
					audit.setOpcionMenu("Login");
					audit.setResultado(true);
					logRepo.save(audit);

			}
		}else
			throw new UsernameNotFoundException("Id de Usuario Vacio"); 
		
		if(us!=null && us.getUsuarioRols()!=null && !us.getUsuarioRols().isEmpty()) {
			
			roles = new ArrayList<>();
			for (Iterator<UsuarioRol> iterator = us.getUsuarioRols().iterator(); iterator.hasNext();) {
				
				UsuarioRol rol = (UsuarioRol) iterator.next();
				roles.add(new SimpleGrantedAuthority("ROLE_" + rol.getRol().getRol()));
			}
			userDet = new User(us.getNombreCompleto(), us.getContrasena(), us.getHabilitado(), true, true, true, roles);
		} 
		
		if(us==null) {
			
			throw new UsernameNotFoundException(
					"User " + username + " not found in bd");
		}
		
		return userDet;
	}
	
	public void verificarIntentos() {
		
		HttpSession session = factory.getObject();
		
		int intentos;
		
		if(session.getAttribute("Usuario")!= null) {
			Usuario usuario = (Usuario) session.getAttribute("Usuario");
			if(session.getAttribute("username")!= null) {
				if(usuario.getNombreUsuario().equals((String)session.getAttribute("username"))) {
					if (session.getAttribute("intentos") != null)
						intentos = (int) session.getAttribute("intentos") + 1;
					else
						intentos = 1;
				}
				else {
					session.removeAttribute("username");
					session.setAttribute("username", usuario.getNombreUsuario());
					intentos = 1;
				}
			}
			else {
				session.removeAttribute("username");
				session.setAttribute("username", usuario.getNombreUsuario());
				intentos = 1;
			}
			
			if(intentos == 3) {
				usuario.setHabilitado(false);
				repo.save(usuario);
			}
			else {
				session.removeAttribute("intentos");
				session.setAttribute("intentos", intentos);
			}
		}

	}

}
