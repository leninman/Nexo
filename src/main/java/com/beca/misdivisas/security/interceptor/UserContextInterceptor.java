package com.beca.misdivisas.security.interceptor;

import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;

import org.apache.commons.validator.routines.UrlValidator;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.servlet.handler.HandlerInterceptorAdapter;
import org.springframework.stereotype.Component;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.util.Constantes;

@Component
public class UserContextInterceptor extends HandlerInterceptorAdapter {

	private final List<String> FORM_PATHS = Arrays.asList("/listarTipoAutorizado", "/crearAutorizado",
			"/agregarAutorizadoBeneficiarioTraspaso", "/agregarAutorizadoEmpresaTransporte",
			"/agregarAutorizadoPersonaNatural", "/agregarAutorizadoPersonaJuridica", "/autorizadosListar",
			"/autorizadoEditar", "/autorizadoEliminar", "/solicitudesRetiro", "/nuevaSolicitudRetiro",
			"/crearSolicitudRetiro", "/editarSolicitudRetiro", "/anularSolicitudRetiro", "/getSolicitudRetiroValidar",
			"/getSolicitudRetiroAprobar", "/validarSolicitudRetiro", "/procesarSolicitudRetiro",
			"/getSolicitudRetiroProcesar", "/anularSolicitudesRetiro", "/rechazarSolicitudRetiro",
			"/anularSolicitudesRetiro", "/aprobarSolicitudRetiro", "/aprobarSolicitudesRetiro", "/listarTipoAutorizado");

	private static final List<String> OTROS_PATHS = Arrays.asList("/totalPorSucursal", "/index", "/grafico",
			"/mainBECA", "/usuarioChange", "/403", "/404", "/405", "/error", "/access-denied", "/errorPage");

	private static final List<String> REPORTES_URL = Arrays.asList("/remesa", "/remesaNoApta", "/irregularidades",
			"/remesabycartaporte", "/totalPorSucursal", "/remesaEntregaPendiente", "/remesasPendientes", "/reporteSolicitudRetiro");

	private static final List<String> MAPA_URLS = Arrays.asList("/sucursales", "/agencias");

	private static final List<String> USER_URLS = Arrays.asList("/usuarioMainAgregar", "/usuarioListar",
			"/resultadoCambio", "/usuarioEditar", "/usuarioAgregar", "/usuarioUpdate", "/usuarioEliminar", "/cert",
			"/usuarioChange");

	// private static final List<String> FORM_URLS = Arrays.asList("/envioEfectivo",
	// "/traspasoEfectivo", "/retiroEfectivo");

	private static final List<String> EMPRESA_URLS = Arrays.asList("/crearEmpresa", "/empresaListar", "/empresaEditar",
			"/empresaAgregar", "/empresaActualizar", "/municipios", "/sucursalHome", "/sucursalCrear",
			"/sucursalListar", "/sucursalEditar", "/sucursalAgregar", "/sucursalActualizar");
	
	private static final List<String> AGENCIA_URLS = Arrays.asList("/crearAgencia", "/agenciaListar", "/agenciaEditar",
			"/agenciaAgregar", "/agenciaActualizar", "/municipios", "/agencias");

	private static final List<String> PERFIL_URLS = Arrays.asList("/crearPerfilHome", "/crearPerfil",
			"/editarPerfilHome", "/editarPerfil", "/eliminarPerfil", "/perfiles");

	private static final List<String> CHANGE_PASSWORD_PATHS = Arrays.asList("/cambiarContrasena");

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(UserContextInterceptor.class);

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Value("${dias.vencimiento.clave}")
	private long vencimientoClave;

/*	@Value("${ldap.domain}")
	private String dominio;
*/
	@Override
	public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler)
			throws Exception {

		String servletPath = request.getServletPath();
		final int secondSlashIndex = servletPath.indexOf("/", 1);
		boolean isErrorPage = false;
		if (secondSlashIndex > -1) {
			isErrorPage = servletPath.endsWith("/errorPage");
			if (!isErrorPage) {
				servletPath = servletPath.substring(0, secondSlashIndex);
			}
		}

		UrlValidator urlValidator = new UrlValidator(UrlValidator.ALLOW_LOCAL_URLS);
		final String requestUrl = request.getRequestURL().toString();
		if (isErrorPage || !urlValidator.isValid(requestUrl)) {
			logger.error("El response fue enviado al errorPage al tratar de acceder a una Url invalida" + requestUrl);
			response.sendRedirect(request.getContextPath() + "/errorPage");
			return false;
		}

		// ignora pagina de login
		if (servletPath.equalsIgnoreCase("/login") || servletPath.equalsIgnoreCase("/loginBECA")) {
			return true;
		}

		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);

		if (usuario == null) {
			if (servletPath.equalsIgnoreCase("/main") || servletPath.equalsIgnoreCase("/mainBECA")
					|| servletPath.contentEquals("/preMain")) {
				return true;
			}
			if (OTROS_PATHS.contains(servletPath)) {
				return true;
			}

			response.sendRedirect(request.getContextPath() + "/login");
			return false;
		} else {
			// redireccionar usuario al change password cuando acceden por primera vez
			if (!CHANGE_PASSWORD_PATHS.contains(servletPath) && !servletPath.equalsIgnoreCase("/usuarioChange")
					&& (usuario.getContrasena1() == null || usuario.getContrasena1().trim().equals(""))) {
				response.sendRedirect(request.getContextPath() + "/cambiarContrasena");
				return false;
			}
			// valida clave vencida
			if (!servletPath.equalsIgnoreCase("/cambiarContrasena") && !servletPath.equalsIgnoreCase("/usuarioChange")
					&& !servletPath.equalsIgnoreCase("/error") && usuario.esClaveVencida(vencimientoClave)) {
				request.getSession().setAttribute(Constantes.CAMBIO_C, true);
				response.sendRedirect(request.getContextPath() + "/cambiarContrasena");
				return false;
			}
			//request.getSession().setAttribute(Constantes.DOMINIO_PROD, dominio);
			final List<Menu> menues = ((List<Menu>) factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
			final List<String> urlMenues = getMenuPath(menues);
			urlMenues.addAll(menues.stream().flatMap(menu -> menu.getSubMenu().stream())
					.filter((menu) -> menu.getAccion() != null).map(menu -> "/".concat(menu.getAccion()))
					.collect(Collectors.toList()));
			// validacion de roles
			if (urlMenues.contains(servletPath)) {
				return true;
			}

			if (urlMenues.stream().anyMatch((menu) -> REPORTES_URL.contains(menu))) {
				return true;
			}
			if (urlMenues.stream().anyMatch((menu) -> MAPA_URLS.contains(menu)) && MAPA_URLS.contains(servletPath)) {
				return true;
			}
			if (urlMenues.stream().anyMatch((menu) -> USER_URLS.contains(menu)) && USER_URLS.contains(servletPath)) {
				return true;
			}
			if (FORM_PATHS.contains(servletPath)){
				return true;
			}

			if (servletPath.equals("/main") || servletPath.equals("/preMain")
					|| (servletPath.equals("/cambiarEmpresa") && usuario.getAdmin())) {

				return true;
			}
			if (urlMenues.stream().anyMatch((menu) -> EMPRESA_URLS.contains(menu))
					|| EMPRESA_URLS.contains(servletPath)) {
				return true;
			}
			if (urlMenues.stream().anyMatch((menu) -> AGENCIA_URLS.contains(menu))
					|| AGENCIA_URLS.contains(servletPath)) {
				return true;
			}
			if (OTROS_PATHS.contains(servletPath)) {
				return true;
			}

			if (PERFIL_URLS.contains(servletPath)) {
				return true;

			}
			if (servletPath.equals("/gestionarPerfiles") && usuario.getAdmin())
				return true;

			response.sendRedirect(request.getContextPath() + "/main");
			return false;
		}
	}

	private List<String> getMenuPath(List<Menu> menues) {
		List<String> urlMenues = new ArrayList<String>();
		for (Menu menu : menues) {
			if (menu.getSubMenu() != null) {
				for (Menu sub : menu.getSubMenu()) {
					if (sub.getSubMenu() != null) {
						for (Menu sub1 : sub.getSubMenu()) {
							if (sub1.getAccion() != null) {
								urlMenues.add("/" + sub1.getAccion());
							}
						}
						if (sub.getAccion() != null) {
							urlMenues.add("/" + sub.getAccion());
						}
					}
				}
				if (menu.getAccion() != null) {
					urlMenues.add("/" + menu.getAccion());
				}
			}

		}
		return urlMenues;

	}
}
