package com.beca.misdivisas.services;

import javax.mail.MessagingException;
import javax.mail.internet.MimeMessage;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.mail.javamail.MimeMessageHelper;
import org.springframework.scheduling.annotation.Async;
import org.springframework.stereotype.Service;

@Service
public class EmailService{
	
	private static final Logger LOGGER = LoggerFactory.getLogger(EmailService.class);

	@Autowired
	private JavaMailSender sender;
	
	@Async
	public void sendEmail(String email, String emailBody) {
		MimeMessage message = sender.createMimeMessage();
		MimeMessageHelper helper = new MimeMessageHelper(message);		
		try {
			helper.setTo(email);
			helper.setText(emailBody, true);
			helper.setSubject("OTP - Banco Exterior");
			sender.send(message);
			LOGGER.info("Mail enviado!");
		} catch (MessagingException e) {
			LOGGER.error("Hubo un error al enviar el mail: {}", e);
		}
	}	
}
