package com.beca.misdivisas.security.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.Arrays;
import java.util.List;

import javax.servlet.http.HttpServletRequest;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.stereotype.Component;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.util.Constantes;

@Component
public class UserContextInterceptor extends HandlerInterceptorAdapter {

	private final List<String> REPORTES_PATHS = Arrays.asList("/reporte", "/reporteNoAptos", "/remesa", "/remesaNoApta",
			"/irregularidades", "/trackingRemesas", "/remesabycartaporte", "/remesasPendientes",
			"/remesaEntregaPendiente", "/reporteSucursal");
	private final List<String> MAPA_PATHS = Arrays.asList("/mapa", "/sucursales");
	private final List<String> USER_PATHS = Arrays.asList("/usuarioHome", "/changePassword", "/usuarioMainAgregar",
			"/usuarioListar", "/resultadoCambio", "/usuarioEditar", "/usuarioAgregar", "/usuarioUpdate",
			"/usuarioEliminar" , "/cert");
	private final List<String> FORM_PATHS = Arrays.asList("/EnvioEfectivo", "/TraspasoEfectivo", "/RetiroEfectivo");
	private final List<String> OTROS_PATHS = Arrays.asList("/totalPorSucursal", "/index", "/grafico", "/mainBECA",
			"/changePassword", "/usuarioChange", "/403", "/404", "/error", "/access-denied", "/errorPage");

	private final List<String> ROL_PATHS = Arrays.asList("/roleHome", "/createRoleHome", "/createRole", "/editRoleHome", "/editRole", "/deleteRole", "/gestionarRoles");
	
	private final List<String> CHANGE_PASSWORD_PATHS = Arrays.asList("/EnvioEfectivo", "/TraspasoEfectivo",
			"/RetiroEfectivo", "/main", "/changeCompany", "/mapa", "/reporte", "/reporteNoAptos", "/trackingRemesas",
			"/remesasPendientes", "/reporteSucursal", "/usuarioHome");

	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Value("${dias.vencimiento.clave}")
	private long vencimientoClave;

	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String servletPath = request.getServletPath();
		final int secondSlashIndex = servletPath.indexOf("/", 1);
		if (secondSlashIndex > -1) {
			servletPath = servletPath.substring(0, secondSlashIndex);
		}

		// ignora pagina de login
		if (servletPath.equalsIgnoreCase("/login") || servletPath.equalsIgnoreCase("/loginBECA")) {
			return true;
		}		
		
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);

		if (usuario == null) {
			if (servletPath.equalsIgnoreCase("/main")||servletPath.equalsIgnoreCase("/mainBECA")) {
				return true;
			}
			if (OTROS_PATHS.contains(servletPath)) {
				return true;
			}

			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		} else {
			// redireccionar usuario al change password cuando acceden por primera vez
			if (CHANGE_PASSWORD_PATHS.contains(servletPath)
					&& (usuario.getContrasena1() == null || usuario.getContrasena1().trim().equals(""))) {
				response.sendRedirect(request.getContextPath() + "/changePassword");
				return false;
			}
			//valida clave vencida
			if(!servletPath.equalsIgnoreCase("/changePassword") && !servletPath.equalsIgnoreCase("/usuarioChange")&& !servletPath.equalsIgnoreCase("error/error") && usuario.esClaveVencida(vencimientoClave)) {
				request.getSession().setAttribute(Constantes.CAMBIO_C, true);
				response.sendRedirect(request.getContextPath() + "/changePassword");
				return false;
			}
			
			// validacion de roles
			if (REPORTES_PATHS.contains(servletPath)
					&& usuario.hasAnyRol(Constantes.ROL_CONSULTOR, Constantes.ROL_ADMIN, Constantes.ROL_ADMIN_BECA)) {
				return true;
			}
			if (MAPA_PATHS.contains(servletPath)
					&& usuario.hasAnyRol(Constantes.ROL_CONSULTOR, Constantes.ROL_ADMIN, Constantes.ROL_ADMIN_BECA)) {
				return true;
			}
			if (USER_PATHS.contains(servletPath)
					&& usuario.hasAnyRol(Constantes.ROL_ADMIN, Constantes.ROL_ADMIN_BECA)) {
				return true;
			}
			if (FORM_PATHS.contains(servletPath) && usuario.hasAnyRol(Constantes.ROL_CONSULTOR, Constantes.ROL_ADMIN)) {
				return true;
			}
			if (servletPath.equals("/main")
					|| (servletPath.equals("/changeCompany") && usuario.hasAnyRol(Constantes.ROL_ADMIN_BECA))) {
				return true;
			}
			if (OTROS_PATHS.contains(servletPath)) {
				return true;
			}
			if(ROL_PATHS.contains(servletPath)) {
				return true;
			}

			response.sendRedirect(request.getContextPath() + "/main");
			return false;
		}
	}
}
