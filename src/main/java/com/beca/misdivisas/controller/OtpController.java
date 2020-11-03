package com.beca.misdivisas.controller;

import java.sql.Timestamp;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Random;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

import com.beca.misdivisas.interfaces.IUsuarioEquipoFrecuente;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.jpa.UsuarioEquipoFrecuente;
import com.beca.misdivisas.services.EmailService;
import com.beca.misdivisas.services.MailContentBuilder;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class OtpController {

	@Autowired
	private IUsuarioEquipoFrecuente usuarioEquipoFrecuenteRepository;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private EmailService emailService;

	@Autowired
	private MailContentBuilder mailContentBuilder;
	
	@GetMapping("/enviarOtp")
	public void enviarOtpCode(HttpServletRequest request, HttpServletResponse response) throws Exception {
		response.sendRedirect(request.getContextPath() + "/otpForm");
	}

	@GetMapping(value = "/otpForm")
	public String otpForm(HttpServletRequest request) throws Exception {
		final String codigoOtp = generarCodigoOTP();
		request.getSession().setAttribute("generatedOtp", codigoOtp);		
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
	    String htmlBody = mailContentBuilder.build(usuario.getNombreCompleto(), codigoOtp);
		emailService.sendEmail("gregory.padron@gmail.com", htmlBody);
		return "otpForm";
	}

	@PostMapping(value = "/validarOtp")
	public void validarOtp(HttpServletRequest request, HttpServletResponse response, @RequestParam String otp,
			@RequestParam(required = false) boolean frecuente) throws Exception {
		HttpSession session = request.getSession();
		final String generatedOtp = (String) session.getAttribute("generatedOtp");
		if (otp.equals(generatedOtp)) {
			if (frecuente) {
				String remoteIp = Util.getRemoteIp(request);
				Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
				List<UsuarioEquipoFrecuente> equiposFrecuentes = usuarioEquipoFrecuenteRepository.findByIdUsuario(usuario.getIdUsuario());
				boolean esEquipoFrecuente = equiposFrecuentes.stream().anyMatch(uef -> uef.getDireccionIp().equalsIgnoreCase(remoteIp));
				
				if (!esEquipoFrecuente) {
					if (equiposFrecuentes.size() == 3) {
						Collections.sort(equiposFrecuentes, Comparator.comparingInt(UsuarioEquipoFrecuente::getIdUsuarioEquipoFrecuente)); 
						UsuarioEquipoFrecuente equipoFrecuente = equiposFrecuentes.get(0);
						usuarioEquipoFrecuenteRepository.delete(equipoFrecuente);
					}
					Date date = new Date();
					UsuarioEquipoFrecuente usuarioEquipoFrecuente = new UsuarioEquipoFrecuente();					
					usuarioEquipoFrecuente.setFechaCreacion(new Timestamp(date.getTime()));
					usuarioEquipoFrecuente.setDireccionIp(remoteIp);
					usuarioEquipoFrecuente.setIdUsuario(usuario.getIdUsuario());
					usuarioEquipoFrecuenteRepository.save(usuarioEquipoFrecuente);
				}
			}
			session.setAttribute("userIpValidated", true);
			response.sendRedirect(request.getContextPath() + "/main");
			return;
		} 
		response.sendRedirect(request.getContextPath() + "/login");

	}

	private static String generarCodigoOTP() {
		String numbers = "0123456789";
		Random rndm_method = new Random();
		int length = 8;
		char[] otp = new char[length];

		for (int i = 0; i < length; i++) {
			otp[i] = numbers.charAt(rndm_method.nextInt(numbers.length()));
		}
		return String.valueOf(otp);
	}
}
