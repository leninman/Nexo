package com.beca.misdivisas.controller;

import java.math.BigDecimal;
import java.util.ArrayList;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IRemesaRepo;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Sucursal;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Locacion;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class MapController {
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private IEmpresaRepo empresaRepo;

	@Autowired
	private IRemesaRepo remesaRepo;

	@Autowired
	ResourceLoader resourceLoader;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private MenuService menuService;

	@GetMapping(value = "/mapa", produces = "application/json")
	public String mapa(Model model, HttpServletRequest request) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		model.addAttribute(Constantes.U_SUARIO, usuario);
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, Constantes.TEXTO_REPORTE_MAPA, Constantes.TEXTO_REPORTE_MAPA,
				Util.getRemoteIp(request), usuario);

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
			if (montoDolar != BigDecimal.valueOf(0, 00) || montoEuro != BigDecimal.valueOf(0, 00)) {
				locacion.setSucursal(sucursales.get(i).getSucursal() + "<br>" + descripcion);
				locacion.setLatitud(Double.parseDouble(sucursales.get(i).getLatitud()));
				locacion.setLongitud(Double.parseDouble(sucursales.get(i).getLongitud()));
				locacion.setAccion(sucursales.get(i).getIdSucursal().toString());
				locacion.setLogo("images");
				locacion.setPosicion(i);
				locaciones.add(locacion);
			}
		}
		String Detalle = "Consulta: Obtiene la lista de las sucursales de la empresa";
		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, Detalle, Constantes.TEXTO_REPORTE_MAPA, Util.getRemoteIp(request),
				usuario);

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
		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(empresaRepo.getLogoByIdEmpresa(id));
	}
}
