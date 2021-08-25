package com.beca.misdivisas.security.config;

import java.io.IOException;

import javax.servlet.Filter;
import javax.servlet.FilterChain;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.Cookie;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import org.springframework.stereotype.Component;

//@Component
public class TransactionFilter implements Filter {

    // JF No usaremos una cookie para controlar la sesion

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {
	/*	final HttpServletResponse httpResp = (HttpServletResponse) response;
        final HttpServletRequest httpReq = (HttpServletRequest) request;
        final long currTime = System.currentTimeMillis();
        final long expiryTime = currTime + httpReq.getSession().getMaxInactiveInterval() * 1000;
        
        
        Cookie cookie = new Cookie("sessionExpiry", "" + currTime);
        if (httpReq.getRemoteUser() != null) {
            cookie = new Cookie("sessionExpiry", "" + expiryTime);
        } 
        cookie.setPath(httpReq.getContextPath());
        
        httpResp.addCookie(cookie);
        chain.doFilter(request, response);
        */
	}
}
