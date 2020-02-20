package com.beca.misdivisas.controller;


import java.sql.Timestamp;
import java.util.Date;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Login;


@Controller
public class HomeController {
   
	@Autowired
	private IEmpresaRepo empresaRepository;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	@Autowired
	private ILogRepo logRepo;

	@Autowired
	private HttpServletRequest request;
	
    @RequestMapping(value="/")
    public String home(){
    	 return "login";
    }
    
    
    @GetMapping("/login")
    public String login(HttpServletRequest request) {
    	return "login";
    }
    
	@PostMapping(value="/login")
	public String main() {
		return "main";
	}
	
	@PostMapping(value="/mainBECA")
	public String mainBECA(Login login) {
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        
		Usuario usuario =  new Usuario();
		usuario.setNombreUsuario(auth.getName());
		usuario.setEmpresa(empresaRepository.findById(login.getEmpresa().getIdEmpresa().intValue()));
		usuario.setIdEmpresa(login.getEmpresa().getIdEmpresa());
		usuario.setTipoUsuario("Interno");
		usuario.setContrasena1("local");
		factory.getObject().removeAttribute("Usuario");
		factory.getObject().setAttribute("Usuario", usuario);
		
		registrarLog("Acceso al sistema", "Ingreso", "Login", true);

		return "main";
	}
	
	@GetMapping(value="/main")
	public String main_href(Login login, Model model) {
		
		if(factory.getObject().getAttribute("Usuario") != null) {
			if (((Usuario)factory.getObject().getAttribute("Usuario")).getContrasena1()!=null && !(((Usuario)factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals("")))
				return "main";
			else {
				Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
		    	model.addAttribute("usuario", usuario);
		        return "changePassword";
			}
		}
			
		else {
			login.setEmpresas(empresaRepository.findAll());
		    model.addAttribute("login", login);
		    
		    
			return "loginBECA";
		}
	}
	
	@GetMapping(value="/grafico")
	public String grafico(Model model) {
		
		return "grafico";
	}
	
    @GetMapping("/index")
    public String index() {
        return "index";
    }
    
    @GetMapping("/reporteG")
    public String reporte() {
        return "reporteGrafico";
    }
   
/*
    @GetMapping("/access-denied")
    public String accessDenied() {
        return "/error/access-denied";
    }
    
    @RequestMapping(value="/403")
    public String Error403(){
        return "403";
    }
*/
	public  void registrarLog(String accion, String detalle,  String opcion, boolean resultado) {
		Date date = new Date();
		Log audit = new Log();
		Authentication auth = SecurityContextHolder.getContext().getAuthentication();
		
		String ip = request.getRemoteAddr();
		HttpSession session = factory.getObject();
		Usuario us = (Usuario) session.getAttribute("Usuario");
		if(us!=null) {
			audit.setIdEmpresa(us.getIdEmpresa());
			audit.setIdUsuario(us.getIdUsuario());
			audit.setNombreUsuario(us.getNombreUsuario());
		}else {
			audit.setNombreUsuario(auth.getName());
		}
		audit.setFecha(new Timestamp(date.getTime()));
		audit.setIpOrigen(ip);
		audit.setAccion(accion);
		audit.setDetalle(detalle);
		audit.setOpcionMenu(opcion);
		audit.setResultado(true);
		logRepo.save(audit);
	}
	
}