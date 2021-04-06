package com.beca.misdivisas.security.config;

import javax.servlet.http.HttpServletRequest;


import org.springframework.security.web.authentication.WebAuthenticationDetails;

import com.beca.misdivisas.util.Util;

public class CustomWebAuthenticationDetails extends WebAuthenticationDetails {

	private static final long serialVersionUID = 1L;

	private String ipOrigen;
	
	private boolean peticionInterna;

    public CustomWebAuthenticationDetails(HttpServletRequest request) {
        super(request);
        ipOrigen = Util.getRemoteIp(request);
        
        if(request.getHeader("x-forwarded-for")!= null)
        	peticionInterna = false;
        else
        	peticionInterna = true;
    }

    public String getIpOrigen() {
        return ipOrigen;
    }
    
    public boolean isPeticionInterna() {
    	return peticionInterna;
    }
}