package com.beca.misdivisas.controller;

import java.util.regex.Pattern;

import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;

import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.util.Constantes;

@Controller
public class RetiroController {
	
	@Autowired
	private ObjectFactory<HttpSession> factory;

	
	
	
	@GetMapping("/retiros")
	public String listadoRetiros(Model modelo) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		
		return Constantes.OP_RETIROS;
	}
	
	@GetMapping("/nuevoRetiro")
	public String nuevoRetiro(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.CREARRETIRO, true);
		return Constantes.OP_RETIROS_FORM;
	}
	
	@GetMapping("/detalleRetiro")
	public String detalleDepositos(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.VERDEPOSITO, true);
		return Constantes.OP_RETIROS_DETALLE_FORM;
	}
	
	
	@PostMapping("/autorizarReversoRetiro")
	public String autorizarReverso(Model model) {
		return "redirect:/detalleDeposito";		
	}
	
	
	@PostMapping("/ejecutarReversoRetiro")
	public String ejecutarReverso(Model model) {
		return "redirect:/depositos";
	}
	
	
	@GetMapping("/voucherRetiro")
	public String voucherRetiro(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		modelo.addAttribute(Constantes.U_SUARIO, usuario);
		modelo.addAttribute(Constantes.VERVOUCHER, true);
		return Constantes.VOUCHER_RETIRO;
	}
	
	@PostMapping("/agregarRetiro")
	public String agregarRetiro(Model model) {
		
		return "redirect:/voucherRetiro";
	}

}
