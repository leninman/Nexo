package com.beca.misdivisas.services;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.thymeleaf.TemplateEngine;
import org.thymeleaf.context.Context;

@Service
public class MailContentBuilder {
 
    private TemplateEngine templateEngine;
 
    @Autowired
    public MailContentBuilder(TemplateEngine templateEngine) {
        this.templateEngine = templateEngine;
    }
 
    public String build(String nombreUsuario, String codigoOtp) {
        Context context = new Context();
        context.setVariable("nombreUsuario", nombreUsuario);
        context.setVariable("codigoOtp", codigoOtp);
        return templateEngine.process("otpEmail", context);
    }
 
}
