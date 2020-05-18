package com.beca.misdivisas.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.ResourceLoader;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.util.StreamUtils;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.interfaces.IRemesaRepo;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Log;
import com.beca.misdivisas.jpa.Sucursal;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.Locacion;
import com.beca.misdivisas.model.Menu;
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
	private ILogRepo logRepo;

	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private MenuService menuService;
	
	@Autowired
	 private static final Logger logger = LoggerFactory.getLogger(MapController.class);
	
	@Autowired
	ResourceLoader resourceLoader;
	
   @Value("${images.path}")
    private String pathImages;
	
	@GetMapping(value = "/mapa", produces = "application/json")
	public String mapa(Model model, HttpServletRequest request) {

		model.addAttribute("menus",getMenu());
		
		if (((Usuario)factory.getObject().getAttribute("Usuario")).getContrasena1()!=null && !(((Usuario)factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {
			
			registrarLog(Constantes.REPORTE_MAPA, Constantes.REPORTE_MAPA,Constantes.OPCION_MAPA, true);
			return "mapa";
    	}else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
	    	model.addAttribute("usuario", usuario);
	        return "changePassword";
		}
	}
	
	@RequestMapping(path = "/sucursales", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<Locacion> getAlllocation(HttpServletRequest request) {
		Locacion locacion = null;
		int i = 0;

		int id = ((Usuario)factory.getObject().getAttribute("Usuario")).getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		List<Sucursal> sucursales = empresa.getSucursals();
		List<Locacion> locaciones = new ArrayList<Locacion>();
		BigDecimal montoDolar = null;
		BigDecimal montoEuro = null;
		String descripcion = null;
		
		for (i = 0; i < sucursales.size(); i++) {
			montoDolar = getMontoTotalBySucursal(sucursales.get(i).getIdSucursal(),Constantes.USD);
			montoEuro = getMontoTotalBySucursal(sucursales.get(i).getIdSucursal(),Constantes.EUR);
			locacion = new Locacion();
			if(montoDolar!= null) 
				descripcion = Constantes.SIMBOLO_DOLAR + Util.formatMonto(montoDolar.toString());
			else
				descripcion = Constantes.SIMBOLO_DOLAR + "0,00";
			if(montoEuro!=null)
				descripcion += "<br>"+Constantes.SIMBOLO_EURO + Util.formatMonto(montoEuro.toString());
			else
				descripcion += "<br>"+Constantes.SIMBOLO_EURO + "0,00";
			if(montoDolar != BigDecimal.valueOf(0, 00) || montoEuro != BigDecimal.valueOf(0, 00)) {
				locacion.setSucursal(sucursales.get(i).getSucursal() + "<br>" + descripcion);
				locacion.setLatitud(Double.parseDouble(sucursales.get(i).getLatitud()));
				locacion.setLongitud(Double.parseDouble(sucursales.get(i).getLongitud()));
				locacion.setAccion(sucursales.get(i).getIdSucursal().toString());
				locacion.setLogo("images");////app/divisasapp/webapps/NexoDivisas/WEB-INF/classes/static/img/
				locacion.setPosicion(i);
				locaciones.add(locacion);
			}
		}
		String Detalle= "Consulta: Obtiene la lista de las sucursales de la empresa";
		registrarLog(Constantes.REPORTE_MAPA, Detalle,Constantes.OPCION_MAPA, true);
		return locaciones;
	}

	private BigDecimal getMontoTotalBySucursal(int idSucursal, int moneda) {
		BigDecimal montoTotal = BigDecimal.valueOf(0, 00);
		BigDecimal montoRecepcion = BigDecimal.valueOf(0, 00);
		BigDecimal montoSobrante = BigDecimal.valueOf(0, 00);
		BigDecimal montoFaltante = BigDecimal.valueOf(0, 00);
		
		montoRecepcion = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA, Constantes.RECEPCION_EFECTIVO);
		
		montoSobrante = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA, Constantes.DIFERENCIA_SOBRANTE);
		
		montoFaltante = remesaRepo.getTotalBySucursal(idSucursal, moneda, Constantes.ESTATUS_ENTREGADA, Constantes.DIFERENCIA_FALTANTE);
		
		if(montoRecepcion != null)
			montoTotal = montoRecepcion;
		if(montoSobrante != null)
			montoTotal = montoTotal.add(montoSobrante);
		if(montoFaltante != null)
			montoTotal = montoTotal.subtract(montoFaltante);
		
		return montoTotal;
	}
	
	public  void registrarLog(String accion, String detalle,  String opcion, boolean resultado) {
		Date date = new Date();
		Log audit = new Log();
		
		String ip = request.getRemoteAddr();
		HttpSession session = factory.getObject();
		Usuario us = (Usuario) session.getAttribute("Usuario");
		
		audit.setFecha(new Timestamp(date.getTime()));
		audit.setIpOrigen(ip);
		audit.setAccion(accion);
		audit.setDetalle(detalle);
		audit.setIdEmpresa(us.getIdEmpresa());
		audit.setIdUsuario(us.getIdUsuario());
		audit.setNombreUsuario(us.getNombreUsuario());
		audit.setOpcionMenu(opcion);
		audit.setResultado(true);
		logRepo.save(audit);
		
		logger.info("Ip origen: "+ ip +" Accion:" +accion +" Detalle:"+ detalle + " Opcion:"+ opcion);		
	}
	
	@GetMapping(value = "/images")
	public ResponseEntity<byte[]> fromClasspathAsResEntity() throws IOException {
		int id = ((Usuario)factory.getObject().getAttribute("Usuario")).getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		
		
		Resource imageFile = resourceLoader.getResource("file:///"+ pathImages + empresa.getCaracterRif() + empresa.getRif() + ".jpg");

		byte[] imageBytes = StreamUtils.copyToByteArray(imageFile.getInputStream());

		return ResponseEntity.ok().contentType(MediaType.IMAGE_JPEG).body(imageBytes);
	}
	
	public List<Menu> getMenu() {
		List<Menu> menu = null;

		if (request.isUserInRole(Constantes.ADMIN_BECA)) {
			menu = menuService.loadMenuByRolName(Constantes.ADMIN_BECA);

		} else {
			menu = menuService.loadMenuByUserId(((Usuario) factory.getObject().getAttribute("Usuario")).getIdUsuario());
		}

		return menu;
	}
}
