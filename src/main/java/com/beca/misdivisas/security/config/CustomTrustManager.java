package com.beca.misdivisas.security.config;

import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.X509TrustManager;

public class CustomTrustManager implements X509TrustManager {

	X509Certificate[] certificate = null;
	
	public void checkClientTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		if (string.equalsIgnoreCase("")) {
			throw new CertificateException();
			}
	}

	public void checkServerTrusted(X509Certificate[] xcs, String string) throws CertificateException {
		if (string.equalsIgnoreCase("")) {
			throw new CertificateException();
			}
	}

	public X509Certificate[] getAcceptedIssuers() {
		return new java.security.cert.X509Certificate[0];
		//return certificate;
	}

}