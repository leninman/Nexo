package com.beca.misdivisas.api.generico;

import java.security.KeyManagementException;
import java.security.NoSuchAlgorithmException;
import java.security.cert.CertificateException;
import java.security.cert.X509Certificate;

import javax.net.ssl.SSLContext;
import javax.net.ssl.TrustManager;
import javax.net.ssl.X509TrustManager;

import org.apache.http.conn.ssl.NoopHostnameVerifier;
import org.apache.http.impl.client.CloseableHttpClient;
import org.apache.http.impl.client.HttpClientBuilder;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.client.HttpComponentsClientHttpRequestFactory;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

//@Service
public class MicroservicioClient {
	private static final Logger logger = LoggerFactory.getLogger(MicroservicioClient.class);
	
	@Value("${client.jwt.secret}")
	private String secret;
	@Value("${client.jwt.key}")
	private String iss;
	@Value("${client.apikey}")
	private String apiKey;
	
	static String auth = "Authorization";
	static String bearer = "Bearer ";
	static String contentType = "Content-Type";
	static String accept = "Accept-Charset";
	static String xapiKey = "x-api-key";
	static String json = "application/json";
	static String utf8 = "UTF-8";
	
	
	public HttpHeaders createHeaders(long exp) {
		HttpHeaders headers = new HttpHeaders();
		headers.add(contentType, json);
		headers.add(accept, utf8);
		headers.setContentType(MediaType.APPLICATION_JSON);
		headers.set(xapiKey, apiKey);
		headers.set(auth, bearer + new JwtUtil().generarBearer(secret, iss, exp));

		return headers;
	}

	private static TrustManager  trustSelfSignedSSLWS() {
        X509Certificate[] xCertificate = null;
         return new X509TrustManager() {
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
                return xCertificate;
            }
        };
    }
	
	protected static RestTemplate getTemplate() {
		SSLContext sc;
		HttpComponentsClientHttpRequestFactory requestFactory = new HttpComponentsClientHttpRequestFactory();
		try {
			sc = SSLContext.getInstance("TLS");
	        sc.init(null, new TrustManager[] {trustSelfSignedSSLWS()}, null);
	        CloseableHttpClient httpClient = (HttpClientBuilder.create()).setSSLHostnameVerifier(NoopHostnameVerifier.INSTANCE).setSSLContext(sc).build();
	        requestFactory = new HttpComponentsClientHttpRequestFactory();       
	        requestFactory.setHttpClient(httpClient);	        
			/*
			 * requestFactory.setConnectionRequestTimeout(request.
			 * getConnectionRequestTimeout());
			 * requestFactory.setConnectTimeout(request.getConnectTimeout());
			 * requestFactory.setReadTimeout(request.getReadTimeout());
			 */
		} catch (NoSuchAlgorithmException e) {
			logger.info(e.getLocalizedMessage());
		} catch (KeyManagementException e) {
			logger.info(e.getLocalizedMessage());
		}

		return new RestTemplate(requestFactory);
	}

}
