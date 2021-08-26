package com.beca.misdivisas.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IRemesaRepo;
import com.beca.misdivisas.jpa.Agencia;

import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Sucursal;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Locacion;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class MapController {
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private IEmpresaRepo empresaRepo;

	@Autowired
	private IAgenciaRepo agenciaRepo;

	@Autowired
	private IRemesaRepo remesaRepo;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private LogService logServ;

	@GetMapping(value = "/mapa", produces = "application/json")
	public String mapa(Model model, HttpServletRequest request) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.TIPO_MAPA, "sucursales");
		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, Constantes.TEXTO_REPORTE_MAPA,
				Constantes.TEXTO_REPORTE_MAPA, true, Util.getRemoteIp(request), usuario);

		return Constantes.MAPA;
	}

	@GetMapping(path = "/sucursales", produces = "application/json")
	@ResponseBody
	public List<Locacion> getAlllocation(HttpServletRequest request) {
		Locacion locacion = null;
		List<Locacion> locaciones = new ArrayList<>();
		BigDecimal montoDolar = null;
		BigDecimal montoEuro = null;
		String descripcion = null;
		int i = 0;
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);

		int id = usuario.getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		List<Sucursal> sucursales = empresa.getSucursals();

		for (i = 0; i < sucursales.size(); i++) {
			montoDolar = getMontoTotalBySucursal(sucursales.get(i).getIdSucursal(), Constantes.USD);
			montoEuro = getMontoTotalBySucursal(sucursales.get(i).getIdSucursal(), Constantes.EUR);
			locacion = new Locacion();
			if (montoDolar != null)
				descripcion = Constantes.SIMBOLO_DOLAR + Util.formatMonto(montoDolar.toString());
			else
				descripcion = Constantes.SIMBOLO_DOLAR + "0,00";
			if (montoEuro != null)
				descripcion += "<br>" + Constantes.SIMBOLO_EURO + Util.formatMonto(montoEuro.toString());
			else
				descripcion += "<br>" + Constantes.SIMBOLO_EURO + "0,00";
			// if (montoDolar != BigDecimal.valueOf(0, 00) || montoEuro !=
			// BigDecimal.valueOf(0, 00)) {
			locacion.setSucursal(sucursales.get(i).getSucursal() + "<br>" + descripcion);
			locacion.setLatitud(Double.parseDouble(sucursales.get(i).getLatitud()));
			locacion.setLongitud(Double.parseDouble(sucursales.get(i).getLongitud()));
			locacion.setAccion(sucursales.get(i).getIdSucursal().toString());
			if (empresa.getLogo() != null)
				locacion.setLogo(Constantes.IMAGES);
			else
				locacion.setLogo("img/sucursal.png");

			locacion.setPosicion(i);
			locaciones.add(locacion);	
			// }
		}

		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, Constantes.SUCURSALES_EMPRESA,
				Constantes.TEXTO_REPORTE_MAPA, true, Util.getRemoteIp(request), usuario);

		return locaciones;
	}

	private BigDecimal getMontoTotalBySucursal(int idSucursal, int moneda) {
		BigDecimal montoTotal = BigDecimal.valueOf(0, 00);
		BigDecimal montoRecepcion = BigDecimal.valueOf(0, 00);
		BigDecimal montoSobrante = BigDecimal.valueOf(0, 00);
		BigDecimal montoFaltante = BigDecimal.valueOf(0, 00);

		montoRecepcion = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
				Constantes.RECEPCION_EFECTIVO);

		montoSobrante = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
				Constantes.DIFERENCIA_SOBRANTE);

		montoFaltante = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_ENTREGADA,
				Constantes.DIFERENCIA_FALTANTE);

		if (montoRecepcion != null)
			montoTotal = montoRecepcion;
		if (montoSobrante != null)
			montoTotal = montoTotal.add(montoSobrante);
		if (montoFaltante != null)
			montoTotal = montoTotal.subtract(montoFaltante);

		return montoTotal;
	}

	@GetMapping(value = "/images")
	public ResponseEntity<byte[]> fromClasspathAsResEntity() {
		int id = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO)).getIdEmpresa();
		byte[] logo = empresaRepo.getLogoByIdEmpresa(id);
		if (logo == null || logo.length <= 0) {
			Resource imageFile = resourceLoader.getResource("classpath:static/img/sucursal.png");
			try {
				logo = StreamUtils.copyToByteArray(imageFile.getInputStream());
			} catch (IOException e) {
				e.printStackTrace();
			}
		}

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(logo);
	}

	@GetMapping(value = "/mapaAgencias", produces = "application/json")
	public String mapaAgencia(Model model, HttpServletRequest request) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		model.addAttribute(Constantes.U_SUARIO, usuarioModel);
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.TIPO_MAPA, "agencias" );
		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, Constantes.TEXTO_REPORTE_MAPA,
				Constantes.TEXTO_REPORTE_MAPA, true, Util.getRemoteIp(request), usuario);

		return Constantes.MAPA;
	}

	
	
	
	
	@GetMapping(path = "/agencias", produces = "application/json")
	@ResponseBody
	public List<Locacion> getAgenciaLocation(HttpServletRequest request) {
		Locacion locacion = null;
		List<Locacion> locaciones = new ArrayList<>();
		String descripcion = null;
		int i = 0;

		final List<Agencia> agencias = agenciaRepo.findByAlmacenamientoOrRecaudacion(true,true);
		for (final Agencia agencia : agencias) {
			if (agencia.getLatitud() != null && agencia.getLongitud() != null && !agencia.getLatitud().isEmpty()
					&& !agencia.getLongitud().isEmpty() && agencia.getIdEstatusAgencia() != 2) {
			
				if(agencia.getAlmacenamiento() != true && agencia.getRecaudacion() !=false)
					descripcion = "Recaudación"; else
				if(agencia.getAlmacenamiento() != false && agencia.getRecaudacion() !=true)
					descripcion = "Almacenamiento"; else
				if(agencia.getAlmacenamiento() != false && agencia.getRecaudacion() !=false)
					descripcion = "Almacenamiento y Recaudación"; 
				locacion = new Locacion();
				locacion.setSucursal(agencia.getAgencia() + "<br>" + agencia.getNumeroAgencia() + "<br>" + descripcion);
				locacion.setLatitud(Double.parseDouble(agencia.getLatitud()));
				locacion.setLongitud(Double.parseDouble(agencia.getLongitud()));
				locacion.setAccion(agencia.getIdAgencia().toString());
				locacion.setLogo("img/imageBE.png");
				locacion.setPosicion(i);
				locaciones.add(locacion);
				i++;
			}
		}
		return locaciones;
	}
}
