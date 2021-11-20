package com.beca.misdivisas.controller;

import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.ldap.userdetails.LdapUserDetailsImpl;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.interfaces.IPerfilRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Perfil;
import com.beca.misdivisas.jpa.PerfilUsuario;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Login;
import com.beca.misdivisas.model.Menu;
import com.beca.misdivisas.security.config.CustomWebAuthenticationDetails;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class HomeController {

	@Autowired
	private IAgenciaRepo agenciaRepository;

	@Autowired
	private IEmpresaRepo empresaRepository;

	@Autowired
	private ILogRepo logRepository;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private MenuService menuService;

	@Value("${ldap.torre.dominio}")
	private String dominio;

	@Autowired
	private IPerfilRepo perfilRepo;
	
	String tipoVista;

	@GetMapping(value = "/")
	public String home() {
		return Constantes.LOGIN;
	}

	@GetMapping("/login")
	public String login(HttpServletRequest request) {
		if (!dominio.equalsIgnoreCase(Constantes.DOMINIO_PROD))
			request.setAttribute(Constantes.DOMINIO, false);
		else
			request.setAttribute(Constantes.DOMINIO, true);
		return Constantes.LOGIN;
	}

	@PostMapping(value = "/login")
	public String main() {
		return Constantes.MAIN;
	}

	@PostMapping(value = "/mainBECA")
	public String mainBECA(Login login, Model model) {

		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		List<Log> logs = null;
		
		List<Menu> menues = null;
		
		try {
			LdapUserDetailsImpl principal = (LdapUserDetailsImpl) SecurityContextHolder.getContext().getAuthentication()
					.getPrincipal();
			int idAgencia = 0;
            int numeroAgencia = ((CustomWebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication()
                    .getDetails()).getNumeroAgencia();
            final List<Agencia> agencias = agenciaRepository.findByNumeroAgencia(numeroAgencia);
            if (!agencias.isEmpty())
                idAgencia = agencias.get(0).getIdAgencia();
            usuario.setIdAgencia(idAgencia);
            
			String dn = principal.getDn();
			usuario.setNombreUsuario(principal.getUsername());
			usuario.setNombreCompleto(dn.substring(dn.indexOf("cn=") + 4, dn.indexOf(",")));
			usuario.setTipoUsuario(Constantes.USUARIO_INTERNO);
			usuario.setContrasena1(Constantes.CLAVE_LOCAL);

			if (login.getVista().equalsIgnoreCase(Constantes.TIPO_VISTA_GENERAL)) {
				menues = menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_TODOS);
				menues.addAll(menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_GENERAL));
				menues.addAll(menuService.getMenuBy(usuario, Constantes.TIPO_MENU_U, Constantes.TIPO_VISTA_GENERAL));

				logServ.registrarLog(Constantes.MAIN, Constantes.VISTA_GENERAL, Constantes.LOGIN_BECA, true,
						Util.getRemoteIp(request), usuario);
			} else {
				usuario.setEmpresa(empresaRepository.findById(login.getEmpresa().getIdEmpresa().intValue()));
				usuario.setIdEmpresa(login.getEmpresa().getIdEmpresa());

				menues = menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_TODOS);
				menues.addAll(menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_EMPRESA));
				menues.addAll(menuService.getMenuBy(usuario, Constantes.TIPO_MENU_U, Constantes.TIPO_VISTA_EMPRESA));

				logServ.registrarLog(Constantes.MAIN, Constantes.VISTA_EMPRESA, Constantes.LOGIN_BECA, true,
						Util.getRemoteIp(request), usuario);
			}

			factory.getObject().removeAttribute(Constantes.USUARIO);
			factory.getObject().setAttribute(Constantes.USUARIO, usuario);

			factory.getObject().removeAttribute(Constantes.USUARIO_MENUES);
			factory.getObject().setAttribute(Constantes.USUARIO_MENUES, menues);

			model.addAttribute(Constantes.MENUES, menues);

			com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
			usuarioModel.setUsuario(usuario);
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);

			if (usuario.getIdUsuario() == null) {
				logs = logRepository.findLogByNombreUsuarioAndAccionAndResultado(
						usuario.getNombreUsuario().toUpperCase(), Constantes.OPCION_LOGIN, true);
			} else {
				logs = logRepository.findLogByIdUsuarioAndAccionAndResultado(usuario.getIdUsuario(),
						Constantes.OPCION_LOGIN, true);
			}
			if (logs.size() > 1) {
				Date date = new Date();
				date.setTime(logs.get(1).getFecha().getTime());
				String ultimoIngreso = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMM).format(date);
				model.addAttribute(Constantes.ULTIMO_INGRESO, ultimoIngreso);
			}

		} catch (Exception e) {
			logServ.registrarLog(Constantes.MAIN, e.getLocalizedMessage(), Constantes.LOGIN_BECA, false,
					Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}

		return Constantes.MAIN;
	}

	@GetMapping(value = "/main")
	public String main(Login login, Model model) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		String retorno = Constantes.MAIN;
		HttpSession session = factory.getObject();
		
		
		
						
		List<Menu> menues = null;
		try {
			com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
			usuarioModel.setUsuario(usuario);
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);
			List<Log> logs = null;
			if (usuario == null) {

				LdapUserDetailsImpl principal = (LdapUserDetailsImpl) SecurityContextHolder.getContext()
						.getAuthentication().getPrincipal();
				usuario = new Usuario();
				String dn = principal.getDn();
				usuario.setNombreUsuario(principal.getUsername());
				int idAgencia = 0;
	            int numeroAgencia = ((CustomWebAuthenticationDetails) SecurityContextHolder.getContext().getAuthentication()
	                    .getDetails()).getNumeroAgencia();
	            final List<Agencia> agencias = agenciaRepository.findByNumeroAgencia(numeroAgencia);
	            if (!agencias.isEmpty())
	                idAgencia = agencias.get(0).getIdAgencia();
	            usuario.setIdAgencia(idAgencia);
				usuario.setNombreCompleto(dn.substring(dn.indexOf("cn=") + 4, dn.indexOf(",")));
				usuario.setTipoUsuario(Constantes.USUARIO_INTERNO);
				usuario.setContrasena1(Constantes.CLAVE_LOCAL);
				// usuario.setUsuarioRols(getRoles(principal));
				usuario.setPerfilUsuarios(getPerfiles(principal));

				factory.getObject().removeAttribute(Constantes.USUARIO);
				factory.getObject().setAttribute(Constantes.USUARIO, usuario);
				usuarioModel.setUsuario(usuario);
				model.addAttribute(Constantes.U_SUARIO, usuarioModel);
				menues = new ArrayList<Menu>();
				menues.addAll(menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_TODOS));
				if (tieneAccesoVista(usuario.getPerfilUsuarios(), Constantes.TIPO_VISTA_TODOS)
						|| (tieneAccesoVista(usuario.getPerfilUsuarios(), Constantes.TIPO_VISTA_GENERAL)
								&& tieneAccesoVista(usuario.getPerfilUsuarios(), Constantes.TIPO_VISTA_EMPRESA))) {
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_GENERAL));
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_U, Constantes.TIPO_VISTA_GENERAL));
					login.setEmpresas(empresaRepository.findAllActiveOrderByName(Constantes.EMPRESA_ACTIVA));

					retorno = Constantes.LOGIN_BECA;
				} else if (tieneAccesoVista(usuario.getPerfilUsuarios(), Constantes.TIPO_PERFIL_E)) {
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_EMPRESA));
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_U, Constantes.TIPO_VISTA_EMPRESA));
					tipoVista=Constantes.TIPO_VISTA_EMPRESA;
					model.addAttribute("tipoVista",tipoVista);
					login.setEmpresas(empresaRepository.findAllActiveOrderByName(Constantes.EMPRESA_ACTIVA));
					retorno = Constantes.LOGIN_BECA;

				} else if (tieneAccesoVista(usuario.getPerfilUsuarios(), Constantes.TIPO_VISTA_GENERAL)) {
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_S, Constantes.TIPO_VISTA_GENERAL));
					menues.addAll(
							menuService.getMenuBy(usuario, Constantes.TIPO_MENU_U, Constantes.TIPO_VISTA_GENERAL));

				}

				session.removeAttribute(Constantes.USUARIO_MENUES);
				session.setAttribute(Constantes.USUARIO_MENUES, menues);

			} else {

				if (usuario.getIdUsuario() == null) {
					logs = logRepository.findLogByNombreUsuarioAndAccionAndResultado(usuario.getNombreUsuario(),
							Constantes.OPCION_LOGIN, true);
				} else {
					logs = logRepository.findLogByIdUsuarioAndAccionAndResultado(usuario.getIdUsuario(),
							Constantes.OPCION_LOGIN, true);
				}
				if (logs.size() > 1) {
					Date date = new Date();
					date.setTime(logs.get(1).getFecha().getTime());
					String ultimoIngreso = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMM).format(date);
					model.addAttribute(Constantes.ULTIMO_INGRESO, ultimoIngreso);
				}
			}
			logServ.registrarLog(Constantes.MAIN, Constantes.MAIN, Constantes.OPCION_INICIO, true,
					Util.getRemoteIp(request), usuario);
			if (menues != null) {
				model.addAttribute(Constantes.MENUES, menues);
			}	
			else {
				model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
			}	

		} catch (Exception e) {
			logServ.registrarLog(retorno, e.getLocalizedMessage(), Constantes.OPCION_INICIO, false,
					Util.getRemoteIp(request), usuario);
			return Constantes.ERROR;
		}
		return retorno;
	}

	@GetMapping(value = "/cambiarEmpresa")
	public String cambioDeEmpresa(Login login, Model model) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);

		try {
			com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
			usuarioModel.setUsuario(usuario);
			model.addAttribute(Constantes.U_SUARIO, usuarioModel);

			login.setEmpresas(empresaRepository.findAllActiveOrderByName(Constantes.EMPRESA_ACTIVA));
			model.addAttribute(Constantes.LOGIN, login);
			String detalle = MessageFormat.format(Constantes.TEXTO_CAMBIO_EMPRESA, usuario.getEmpresa().getEmpresa());
			logServ.registrarLog(Constantes.CAMBIO_EMPRESA, detalle, Constantes.CAMBIO_EMPRESA, true,
					Util.getRemoteIp(request), usuario);

		} catch (Exception e) {
			logServ.registrarLog(Constantes.CAMBIO_EMPRESA, e.getLocalizedMessage(), Constantes.CAMBIO_EMPRESA, false,
					Util.getRemoteIp(request), usuario);
		}
		tipoVista=Constantes.TIPO_VISTA_EMPRESA;
		model.addAttribute("tipoVista",tipoVista);
		login.setEmpresas(empresaRepository.findAllActiveOrderByName(Constantes.EMPRESA_ACTIVA));

		return Constantes.LOGIN_BECA;
	}

	@GetMapping(value = "/grafico")
	public String grafico(Model model) {
		return Constantes.GRAFICO;
	}

	/*
	 * private List<UsuarioRol> getRoles(LdapUserDetailsImpl principal) {
	 * List<UsuarioRol> usuarioRols = new ArrayList<>(); Rol rol; UsuarioRol urol ;
	 * List<GrantedAuthority> authorities = (List<GrantedAuthority>)
	 * principal.getAuthorities();
	 * 
	 * for (GrantedAuthority grantedAuthority : authorities) { String nombreRol =
	 * grantedAuthority.getAuthority().replaceAll(Constantes.ROL_PRE,""); rol = new
	 * Rol(); rol.setRol(nombreRol); urol = new UsuarioRol(); urol.setRol(rol);
	 * usuarioRols.add(urol); } return usuarioRols; }
	 */

	private List<PerfilUsuario> getPerfiles(LdapUserDetailsImpl principal) {
		List<PerfilUsuario> perfilesUsuario = new ArrayList<>();
		Perfil perfil;
		PerfilUsuario perfilU;
		List<GrantedAuthority> authorities = (List<GrantedAuthority>) principal.getAuthorities();

		for (GrantedAuthority grantedAuthority : authorities) {
			String nombrePerfil = grantedAuthority.getAuthority().replaceAll(Constantes.ROL_PRE, "");
			perfil = perfilRepo.findByPerfilAndIdEmpresaNullAndEstado(nombrePerfil, Constantes.ACTIVO);
			perfilU = new PerfilUsuario();
			perfilU.setPerfil(perfil);
			perfilesUsuario.add(perfilU);
		}
		return perfilesUsuario;

	}

	private boolean tieneAccesoVista(List<PerfilUsuario> perfilUsuarios, String tipoVista) {
		boolean resp = false;
		for (PerfilUsuario perfilUsuario : perfilUsuarios) {
			if (perfilUsuario != null && perfilUsuario.getPerfil() != null
					&& !perfilUsuario.getPerfil().getTipoVista().isEmpty()
					&& perfilUsuario.getPerfil().getTipoVista().equals(tipoVista))
				resp = true;
		}

		return resp;

	}

}