package com.beca.misdivisas.services;

import java.util.ArrayList;
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

import com.beca.misdivisas.interfaces.IUsuarioRepo;
import com.beca.misdivisas.jpa.PerfilUsuario;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.util.Constantes;


@Service
public class UsuarioService implements UserDetailsService {

	@Autowired
	private IUsuarioRepo repo;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	private LogService logServ;
	
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
		
		if (username!=null && !username.isEmpty()) {
			us = repo.findByNombreUsuarioAndEstado(username.toUpperCase(),Constantes.ACTIVO);
				
			if (us!=null) {					
				session.removeAttribute(Constantes.USUARIO);
				us.setTipoUsuario(Constantes.USUARIO_EXTERNO);
				session.setAttribute(Constantes.USUARIO, us);	
				final List<Menu> menues = menuService.getMenu(us, Constantes.TIPO_MENU_S);
				menues.addAll(menuService.getMenu(us, Constantes.TIPO_MENU_U));
				session.removeAttribute(Constantes.USUARIO_MENUES);
				session.setAttribute(Constantes.USUARIO_MENUES, menues);
				session.setAttribute(Constantes.USUARIO_INTERNO, false);
				
				/*if(us.getUsuarioRols()!=null && !us.getUsuarioRols().isEmpty()) {			
					roles = new ArrayList<>();
					for (Iterator<UsuarioRol> iterator = us.getUsuarioRols().iterator(); iterator.hasNext();) {				
						UsuarioRol rol = iterator.next();
						roles.add(new SimpleGrantedAuthority(Constantes.ROL_PRE + rol.getRol().getRol()));
					}
					userDet = new User(us.getNombreUsuario(), us.getContrasena(), us.getHabilitado(), true, true, true, roles);
				} */
				if(us.getPerfilUsuarios()!=null && !us.getPerfilUsuarios().isEmpty()) {			
					roles = new ArrayList<>();
					for (Iterator<PerfilUsuario> iterator = us.getPerfilUsuarios().iterator(); iterator.hasNext();) {				
						PerfilUsuario perfil = iterator.next();
						roles.add(new SimpleGrantedAuthority(Constantes.ROL_PRE + perfil.getPerfil().getPerfil()));
					}
					userDet = new User(us.getNombreUsuario(), us.getContrasena(), us.getHabilitado(), true, true, true, roles);
				} 
			} else {
				session.setAttribute(Constantes.USUARIO_INTERNO, true);
				throw new UsernameNotFoundException("User " + username + " not found in bd");
			}
		 
		}else {
			throw new UsernameNotFoundException("Id de Usuario Vacio");
		}
		
		logServ.registrarLog(Constantes.OPCION_LOGIN, Constantes.OPCION_LOGIN, Constantes.LOGIN, true, this.getIpOrigen(), us);
		return userDet;
	}
	
	public void verificarIntentos() {
		
		HttpSession session = factory.getObject();
		
		int intentos;
		
		if(session.getAttribute(Constantes.USUARIO)!= null) {
			Usuario usuario = (Usuario) session.getAttribute(Constantes.USUARIO);
			if(session.getAttribute(Constantes.USER_NAME)!= null) {
				if(usuario.getNombreUsuario().equals(session.getAttribute(Constantes.USER_NAME))) {
					if (session.getAttribute(Constantes.INTENTOS) != null)
						intentos = (int) session.getAttribute(Constantes.INTENTOS) + 1;
					else
						intentos = 1;
				}
				else {
					session.removeAttribute(Constantes.USER_NAME);
					session.setAttribute(Constantes.USER_NAME, usuario.getNombreUsuario());
					intentos = 1;
				}
			}
			else {
				session.removeAttribute(Constantes.USER_NAME);
				session.setAttribute(Constantes.USER_NAME, usuario.getNombreUsuario());
				intentos = 1;
			}
			
			if(intentos == 3) {
				usuario.setHabilitado(false);
				repo.save(usuario);
			}
			else {
				session.removeAttribute(Constantes.INTENTOS);
				session.setAttribute(Constantes.INTENTOS, intentos);
			}
		}

	}

}
