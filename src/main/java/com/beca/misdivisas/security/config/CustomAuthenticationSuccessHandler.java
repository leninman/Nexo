package com.beca.misdivisas.security.config;

import java.io.IOException;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.security.core.Authentication;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.AuthenticationSuccessHandler;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Controller;

import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;

@Component
@Controller
public class CustomAuthenticationSuccessHandler implements AuthenticationSuccessHandler {

	private static final int SESSION_TIMEOUT = 180;
	private ILogRepo logServ;	
	
	public CustomAuthenticationSuccessHandler(ILogRepo logServ) {
		super();
		this.logServ = logServ;
	}


	@Override
	public void onAuthenticationSuccess(HttpServletRequest request, HttpServletResponse response, Authentication authentication) throws IOException, ServletException {
		HttpSession session = request.getSession();
		session.setMaxInactiveInterval(SESSION_TIMEOUT);
		boolean esInterno = (boolean) session.getAttribute(Constantes.USUARIO_INTERNO);
		if(esInterno) {
			UserDetails userDet = (UserDetails) authentication.getPrincipal();
			LogService logServ = new LogService();
			Usuario us=new Usuario();		
			us.setNombreUsuario(userDet.getUsername());
			logServ.registrarLogin(us, request, this.logServ, Constantes.OPCION_LOGIN, Constantes.LOGIN);
		}
		response.sendRedirect(request.getContextPath() + "/main");
	}
}
