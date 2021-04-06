package com.beca.misdivisas.security.config;


import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import com.beca.misdivisas.services.UsuarioService;



public final class CustomDAOAuthenticationProvider extends DaoAuthenticationProvider  {
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		UsuarioService service = (UsuarioService) this.getUserDetailsService();
		try {
	        CustomWebAuthenticationDetails details = (CustomWebAuthenticationDetails) authentication.getDetails();
	        service.setIpOrigen(details.getIpOrigen());
	        this.setUserDetailsService(service);
	        super.setHideUserNotFoundExceptions(false);//Con esto se sabe si el error se debio a que el usuario no existe en BD
	        Authentication result = super.authenticate(authentication);	        
			return result;
		} catch (AuthenticationException e) {			
			service.verificarIntentos();
			
			throw e;
		}		
	}
}