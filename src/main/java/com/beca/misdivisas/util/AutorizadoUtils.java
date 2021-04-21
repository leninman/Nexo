package com.beca.misdivisas.util;

import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.model.AutorizadoBeneficioTraspaso;
import com.beca.misdivisas.model.AutorizadoEmpresaTransporte;
import com.beca.misdivisas.model.AutorizadoPersonaJuridica;
import com.beca.misdivisas.model.AutorizadoPersonaNatural;

public class AutorizadoUtils {
	public static AutorizadoBeneficioTraspaso convertirAutorizadoToAutorizadoBeneficioTraspaso(Autorizado autorizado) {
		final AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso = new AutorizadoBeneficioTraspaso(
				autorizado.getIdAutorizado(), autorizado.getIdTipoAutorizado(),
				autorizado.getRifEmpresa().substring(0, 1), autorizado.getRifEmpresa().substring(1),
				autorizado.getNombreEmpresa());
		return autorizadoBeneficioTraspaso;
	}

	public static AutorizadoEmpresaTransporte convertirAutorizadoToAutorizadoEmpresaTransporte(Autorizado autorizado) {
		final AutorizadoEmpresaTransporte autorizadoEmpresaTransporte = new AutorizadoEmpresaTransporte(
				autorizado.getIdAutorizado(), autorizado.getIdTipoAutorizado(),
				autorizado.getTransportista().getRif().substring(0, 1), autorizado.getTransportista().getRif().substring(1),
				autorizado.getTransportista().getTransportista());
		
		return autorizadoEmpresaTransporte;
	}

	public static AutorizadoPersonaNatural convertirAutorizadoToAutorizadoPersonaNatural(Autorizado autorizado) {
		final AutorizadoPersonaNatural autorizadoPersonaNatural = new AutorizadoPersonaNatural(
				autorizado.getIdAutorizado(), autorizado.getIdTipoAutorizado(),
				(autorizado.getDocumentoIdentidad()!=null && !autorizado.getDocumentoIdentidad().isEmpty() ? autorizado.getDocumentoIdentidad().substring(0, 1): ""), 
				(autorizado.getDocumentoIdentidad()!=null && !autorizado.getDocumentoIdentidad().isEmpty() ? autorizado.getDocumentoIdentidad().substring(1): ""),
				autorizado.getNombreCompleto(), 
				(autorizado.getTelefonoMovil() != null && autorizado.getTelefonoMovil().length() > 4) ? autorizado.getTelefonoMovil().substring(0, 4) : "",
				(autorizado.getTelefonoMovil() != null && autorizado.getTelefonoMovil().length() > 5) ? autorizado.getTelefonoMovil().substring(4) : "", 
				autorizado.getEmail(), autorizado.getPlacaVehiculo(),
				autorizado.getMarcaModeloColorVehiculo(), autorizado.getImagenDocumento());

		autorizadoPersonaNatural.setImagenDocumento(autorizado.getImagenDocumento());

		return autorizadoPersonaNatural;
	}

	public static AutorizadoPersonaJuridica convertirAutorizadoToAutorizadoPersonaJuridica(Autorizado autorizado) {
		final AutorizadoPersonaJuridica autorizadoPersonaJuridica = new AutorizadoPersonaJuridica(
				autorizado.getIdAutorizado(), autorizado.getIdTipoAutorizado(),
				(autorizado.getDocumentoIdentidad()!=null && !autorizado.getDocumentoIdentidad().isEmpty() ? autorizado.getDocumentoIdentidad().substring(0, 1): ""), 
				(autorizado.getDocumentoIdentidad()!=null && !autorizado.getDocumentoIdentidad().isEmpty() ? autorizado.getDocumentoIdentidad().substring(1): ""),
				autorizado.getNombreCompleto(), 
				(autorizado.getRifEmpresa()!=null && !autorizado.getRifEmpresa().isEmpty() ? autorizado.getRifEmpresa().substring(0, 1): ""),
				(autorizado.getRifEmpresa()!=null && !autorizado.getRifEmpresa().isEmpty() ? autorizado.getRifEmpresa().substring(1): ""),			
				autorizado.getNombreEmpresa(),
				(autorizado.getTelefonoMovil() != null && autorizado.getTelefonoMovil().length() > 4) ? autorizado.getTelefonoMovil().substring(0, 4) : "",
				(autorizado.getTelefonoMovil() != null && autorizado.getTelefonoMovil().length() > 5) ? autorizado.getTelefonoMovil().substring(4) : "",
				autorizado.getEmail(), autorizado.getCargoEmpresa(), autorizado.getPlacaVehiculo(), autorizado.getMarcaModeloColorVehiculo());

		autorizadoPersonaJuridica.setImagenDocumento(autorizado.getImagenDocumento());
		autorizadoPersonaJuridica.setImagenDocumentoRif(autorizado.getImagenRif());
		autorizadoPersonaJuridica.setImagenDocumentoAdicional(autorizado.getImagenAdicional());

		return autorizadoPersonaJuridica;
	}

}
