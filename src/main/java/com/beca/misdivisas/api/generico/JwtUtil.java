package com.beca.misdivisas.api.generico;

import java.io.Serializable;

import java.security.Key;
import java.time.Instant;

import java.util.Date;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;

import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

@Component
public class JwtUtil implements Serializable {
	private static final long serialVersionUID = -2550185165626007488L;
	
	public String generarBearer(String secret, String iss, long exp) {
		
		SignatureAlgorithm algoritmoHS256 = SignatureAlgorithm.HS256;
		byte[] secretBytes = DatatypeConverter.parseBase64Binary(secret);
	    Key key = new SecretKeySpec(secretBytes, algoritmoHS256.getJcaName());
	    Instant timeFin = Instant.now().plusSeconds(exp);
	    String s = new String();
	
		try {
	
		s = Jwts.builder()
				.setHeaderParam("alg", "HS256")
				.setHeaderParam("typ", "JWT")
				.setIssuer(iss)
				.setExpiration((Date.from(Instant.ofEpochSecond(timeFin.getEpochSecond()))))
				.claim("iss", iss)
				.signWith(algoritmoHS256 , key)
				.compact();				
	
		} catch (Exception e) {			
			e.printStackTrace();
		}
		
		return s;
	}
}