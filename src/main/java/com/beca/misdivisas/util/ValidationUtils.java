package com.beca.misdivisas.util;

import java.io.IOException;
import org.apache.commons.validator.routines.EmailValidator;
import org.apache.tika.Tika;
import org.springframework.web.multipart.MultipartFile;

public class ValidationUtils {
	
	public static boolean validarRif(String rif) {
		boolean result = false;
		int digito = -1;
		int per = 0;
		rif = rif.toUpperCase();
		// Valido que la letra inicial sea correcta
		try {
			switch (rif.charAt(0)) {
			case 'V':
				per = 1;
				break;
			case 'E':
				per = 2;
				break;
			case 'J':
				per = 3;
				break;
			case 'P':
				per = 4;
				break;
			case 'G':
				per = 5;
				break;
			default:
				return false;
			}

			// Valido que el digito verificador sea valido
			if (per > 0) {
				int suma = (per * 4) + (Integer.parseInt(rif.substring(1, 2)) * 3)
						+ (Integer.parseInt(rif.substring(2, 3)) * 2) + (Integer.parseInt(rif.substring(3, 4)) * 7)
						+ (Integer.parseInt(rif.substring(4, 5)) * 6) + (Integer.parseInt(rif.substring(5, 6)) * 5)
						+ (Integer.parseInt(rif.substring(6, 7)) * 4) + (Integer.parseInt(rif.substring(7, 8)) * 3)
						+ (Integer.parseInt(rif.substring(8, 9)) * 2);

				int resto = suma % 11;

				if (resto > 1) {
					digito = 11 - resto;
				} else {
					digito = 0;
				}
			}

		} catch (Throwable ignored) {
		}
		result = (digito == Integer.parseInt(rif.substring(9)));
		return result;
	}
	
	public static boolean isValidImageType(MultipartFile image) throws IOException {
		if (image != null && image.getBytes().length > 0) {
			if (!image.getContentType().toLowerCase().equals("image/jpg")
					&& !image.getContentType().toLowerCase().equals("image/jpeg")
					&& !image.getContentType().toLowerCase().equals("image/gif")
					&& !image.getContentType().toLowerCase().equals("image/png")) {
				return false;
			}
			else {				
		            Tika defaultTika = new Tika();
		            String deteccion = defaultTika.detect(image.getBytes());
		           
		            if (!deteccion.toLowerCase().equals("image/jpg")
							&& !deteccion.toLowerCase().equals("image/jpeg")
							&& !deteccion.toLowerCase().equals("image/gif")
							&& !deteccion.toLowerCase().equals("image/png")) {
						return false;
					}			
			}
		}
		return true;
	}
	
	public static boolean isValidImageSize(MultipartFile image) throws IOException {
		if (image.getSize() > 1048576) {
			return false;
		}
		return true;
	}
	
	public static boolean isValidEmail(String email) {		
		return EmailValidator.getInstance().isValid(email);
	}

}
