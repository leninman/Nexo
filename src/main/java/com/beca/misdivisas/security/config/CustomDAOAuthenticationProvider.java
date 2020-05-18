package com.beca.misdivisas.security.config;

import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.beca.misdivisas.services.UsuarioService;



public final class CustomDAOAuthenticationProvider extends DaoAuthenticationProvider  {
	
	@Override
	public Authentication authenticate(Authentication authentication)
			throws AuthenticationException {
		
		UsuarioService service = (UsuarioService) this.getUserDetailsService();
		try {
			WebAuthenticationDetails details = (WebAuthenticationDetails) authentication.getDetails();
	        String userIp = details.getRemoteAddress();
	        
	        service.setIpOrigen(userIp);
	        
	        this.setUserDetailsService(service);
	        
	        Authentication result = super.authenticate(authentication);
			return result;
		} catch (AuthenticationException e) {
			
			service.verificarIntentos();
			
			throw e;
		}
		
	}
	

	/*@Override
	public boolean supports(Class<?> authentication) {
		
		return super.supports(authentication);
	}
*/

	
}