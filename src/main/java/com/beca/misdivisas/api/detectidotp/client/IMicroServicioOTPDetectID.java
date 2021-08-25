package com.beca.misdivisas.api.detectidotp.client;

import com.beca.misdivisas.api.detectidotp.model.OtpRequest;
import com.beca.misdivisas.api.detectidotp.model.OtpResponse;

public interface IMicroServicioOTPDetectID {

	public OtpResponse crearValidarOTP(OtpRequest request, String operacion) throws Exception;

	
}
