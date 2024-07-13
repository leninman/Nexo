package com.beca.misdivisas.controller;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasRequest;
import com.beca.misdivisas.api.cuentas.model.ConsultarCuentasResponse;
import com.beca.misdivisas.api.cuentas.model.CuentaConsultarCuentas;
import com.beca.misdivisas.interfaces.IFeriadoRepo;
import com.beca.misdivisas.jpa.Feriado;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.security.config.CustomActiveDirectoryLdapAuthenticationProvider;

import com.beca.misdivisas.util.Constantes;

@Controller
public class DepositoController {
	private final static DateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DD_MM_YYYY);
	private final static DateFormat longDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
	private final static DateFormat simpleDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_D_M_YYYY);
	@Autowired
	private IFeriadoRepo feriadoRepository;
	
	

	
	
	@Autowired
	private ObjectFactory<HttpSession> factory;
	
	
	
	@GetMapping("/depositos")
	public String listadoDepositos(Model modelo) {
		
		final int numagencia;
		
		
		//CustomActiveDirectoryLdapAuthenticationProvider.obtenerNumAgencia(null);

		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		
		
		return Constantes.OP_DEPOSITOS;
	}
	
	
	
	@GetMapping("/nuevoDeposito")
	public String nuevoDeposito(Model modelo) throws Exception {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.CREARDEPOSITO, true);
		return Constantes.OP_DEPOSITOS_FORM;
	}
	
		
	
	@GetMapping("/detalleDeposito")
	public String detalleDepositos(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.VERDEPOSITO, true);
		return Constantes.OP_DEPOSITOS_DETALLE_FORM;
	}
	
	@PostMapping("/autorizarReverso")
	public String autorizarReverso(Model model) {
		return "redirect:/detalleDeposito";		
	}
	
	
	@PostMapping("/ejecutarReverso")
	public String ejecutarReverso(Model model) {
		return "redirect:/depositos";
	}
	
	
	@GetMapping("/voucher")
	public String voucher(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.VERVOUCHER, true);
		return Constantes.VOUCHER_DEPOSITO;
	}
	
	@PostMapping("/agregarDeposito")
	public String agregarDeposito(Model model) {
		
		return "redirect:/voucher";
	}
	
	
	
	
	
}
