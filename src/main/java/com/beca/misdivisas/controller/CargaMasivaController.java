package com.beca.misdivisas.controller;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.UnsupportedEncodingException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.net.MalformedURLException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.multipart.MultipartFile;


import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IAutorizadoRepo;
import com.beca.misdivisas.interfaces.IFeriadoRepo;
import com.beca.misdivisas.interfaces.IMonedaRepo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroRepo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroTrazaRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.jpa.Feriado;
import com.beca.misdivisas.jpa.Moneda;
import com.beca.misdivisas.jpa.SolicitudRetiro;
import com.beca.misdivisas.jpa.SolicitudRetiroTraza;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.SolicitudRetiroModel;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;



@Controller
public class CargaMasivaController {
	private static final String contentType = "application/vnd.openxmlformats-officedocument.spreadsheetml.sheet";
	@Autowired
	private HttpServletRequest request;
	
	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private IAgenciaRepo agenciaRepo;
	
	@Autowired
	private IFeriadoRepo feriadoRepo;
	
	@Autowired
	private IMonedaRepo monedaRepo;
	
	@Autowired
	private IAutorizadoRepo autorizadoRepo;

	@Autowired
	private ISolicitudRetiroRepo solicitudRetiroRepo;
	
	@Autowired
	private ISolicitudRetiroTrazaRepo solicitudRetiroTrazaRepo;
	
	@Value("${ruta.recursos}")
	private String rutaRecursos;

	/*
	 * @GetMapping("/cargaMasiva") public String cargaMasiva(Model model) {
	 * model.addAttribute(Constantes.MENUES,
	 * factory.getObject().getAttribute(Constantes.USUARIO_MENUES)); return
	 * "solicitudes/retiroEfectivo/retiroEfectivo"; }
	 */
	

	@GetMapping(path = "/editSolicitudRetiro", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public SolicitudRetiroModel editarSolicitudRetiro(@RequestParam("idSolicitud") Integer id, Model model) throws Exception {
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");
		int posicion = solicitudes.indexOf(new SolicitudRetiroModel(id));
		SolicitudRetiroModel solicitud = null;
		if(posicion>=0)
			solicitud = solicitudes.get(posicion);
		
		model.addAttribute(Constantes.CREAR, false);
		
		return solicitud;
	}
	
	@PostMapping("crearEditarSolicitudRetiro")
	public String crearEditarSolicitudRetiro(@Valid SolicitudRetiroModel solicitudRetiroModel, BindingResult result, Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, model.getAttribute(Constantes.CREAR));
			model.addAttribute("solicitudRetiro", solicitudRetiroModel);
			return Constantes.OP_SOLICITUD_RETIRO_FORM;
		}
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");

		if(solicitudes == null)
			solicitudes = new ArrayList<SolicitudRetiroModel>();

		//String  accion = (String) model.getAttribute("accion");
		if(!(boolean)model.getAttribute(Constantes.CREAR)) {
			int posicion = solicitudes.indexOf(new SolicitudRetiroModel(solicitudRetiroModel.getIdSolicitud()));
			
			SolicitudRetiroModel solicitud = solicitudes.get(posicion);
			solicitud.setIdEmpresa(usuario.getIdEmpresa());
			solicitud.setTipoBillete(solicitudRetiroModel.getTipoBillete());
			solicitud.setFechaEstimada(solicitudRetiroModel.getFechaEstimada());
			solicitud.setMonto(solicitudRetiroModel.getMonto());
			solicitud.setIdAgencia(solicitudRetiroModel.getIdAgencia());
			solicitud.setIdAutorizado(solicitudRetiroModel.getIdAutorizado());
			solicitud.setIdMoneda(solicitudRetiroModel.getIdMoneda());
			
			solicitudes.add(posicion, solicitud);			
			
		}else{
			solicitudRetiroModel.setIdEmpresa(usuario.getIdEmpresa());
			solicitudes.add(solicitudRetiroModel);
		}

		factory.getObject().removeAttribute("SolicitudesMasivas");
		factory.getObject().setAttribute("SolicitudesMasivas", solicitudes);


		return "redirect:solicitudesListar?success";
	}
	
	
	@PostMapping("/cargaMasiva")
	public ResponseEntity<String> cargaMasivaPost(Model model, @RequestParam("file") MultipartFile file) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		
		String fileName = file.getOriginalFilename().toUpperCase();
	    if(fileName.endsWith(".TXT")) {
		String[] error = new String[2];
		List<String[]> errores = new ArrayList<String[]>();
		List<SolicitudRetiroModel> solicitudesExistentes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");

		List<SolicitudRetiroModel> solicitudes = new ArrayList<SolicitudRetiroModel>();
		try {
			if(file.isEmpty())
				return new ResponseEntity<String>("Archivo vacio", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
			
	        Tika tika = new Tika();  
	        String type = tika.detect(file.getInputStream());  
			if(!type.equals("text/plain") ) {
				return new ResponseEntity<String>("No es un archivo v\\u00E1lido", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
			}
			
			InputStream inputStream = file.getInputStream();
			InputStreamReader inputStreamReader = new InputStreamReader(inputStream, "UTF-8");
			System.lineSeparator();
			BufferedReader br = new BufferedReader(inputStreamReader);

			String linea = null;
			while ((linea = br.readLine()) != null) {
				String[] solicitud = linea.split("\\|");
				if (solicitud.length >= 7) {
					if (solicitud[0].isEmpty() || solicitud[0].length()>1 || (!solicitud[0].matches("[AN]"))) {//Tipo de billete
						error[0]=linea;
						error[1]="El campo tipo de billete esta vac\u00EDo o es incorrecto";
						errores.add(error);
					} else {
						if (solicitud[1].isEmpty()) {//Fecha Estimada del Retiro
							error[0]=linea;
							error[1]="El campo fecha estimada del retiro esta vac\u00EDo";
							errores.add(error);
						} else {
							if (solicitud[4].isEmpty()) {//Número de la Agencia
								error[0]=linea;
								error[1]="El campo n\u00FAmero de agencia esta vac\u00EDo";
								errores.add(error);
							} else {
								if (solicitud[5].isEmpty()) {//Moneda
									error[0]=linea;
									error[1]="El campo moneda esta vac\u00EDo";
									errores.add(error);
								} else {
									if (solicitud[6].isEmpty()) {//Monto
										error[0]=linea;
										error[1]="El campo monto esta vac\u00EDo";
										errores.add(error);

									} else {																
										try {
											SolicitudRetiroModel solicitudRetiro = new SolicitudRetiroModel();
											if(solicitudesExistentes != null)
												solicitudRetiro.setIdSolicitud(solicitudesExistentes.size() + solicitudes.size() +1);
											else
												solicitudRetiro.setIdSolicitud(solicitudes.size() +1);
											solicitudRetiro.setIdEmpresa(usuario.getIdEmpresa());											
											solicitudRetiro.setTipoBillete(solicitud[0]);											
											solicitudRetiro.setFechaEstimada(verificarFecha(solicitud[1]));	
											Autorizado autorizado = null;
											if(!solicitud[2].isEmpty()) {//Autorizado												
												if(solicitud[3].isEmpty()) {//personal natural en representacion Propia
													autorizado = autorizadoRepo.findByDocumentoIdentidad(Util.formatoDocumentoCompleto(solicitud[2].toUpperCase()), usuario.getIdEmpresa(), Constantes.ACTIVO, 1);
													if(autorizado == null) {
														error[0]=linea;
														error[1]="La c\u00E9dula del autorizado no se encuentra registrada en el sistema";
														errores.add(error);
													}
												}else {//personal natural en representacion de una empresa
													autorizado = autorizadoRepo.findByDocumentoIdentidadAndRifEmpresa(Util.formatoDocumentoCompleto(solicitud[2].toUpperCase()), usuario.getIdEmpresa(), solicitud[3].toUpperCase(), Constantes.ACTIVO);
													if(autorizado!=null)
														solicitudRetiro.setNombreAutorizado(autorizado.getDocumentoIdentidad() + " - " +autorizado.getNombreCompleto());
													else{
														error[0]=linea;
														error[1]="La informaci\u00F3n del autorizado es incorrecta";
														errores.add(error);
													}														
												}
											}else {//Autorizado empresa de transporte de valores 
												if(!solicitud[3].isEmpty()) {
													autorizado = autorizadoRepo.findByRifEmpresa(solicitud[3], usuario.getIdEmpresa(), Constantes.ACTIVO);													
												}else {
													error[0]=linea;
													error[1]="El rif de la empresa de transporte de valores es requerido";
													errores.add(error);
												}
											}
											if(autorizado != null) {											
												solicitudRetiro.setAutorizado(autorizado);
												solicitudRetiro.setIdAutorizado(autorizado.getIdAutorizado());
											}
											Agencia ags = agenciaRepo.findByNumeroAgenciaAct(Integer.parseInt(solicitud[4].trim()), Constantes.EMPRESA_ACTIVA);										
											if(ags!=null) { //Agencia
												solicitudRetiro.setIdAgencia(ags.getIdAgencia());
												solicitudRetiro.setAgencia(ags);
											}else {
												error[0]=linea;
												error[1]="El n\u00FAmero de agencia dado no se encuentra en el sistema";
												errores.add(error);
											}									
											
											List<Moneda> monedas = monedaRepo.findByCodigo(solicitud[5]);
											if(monedas != null && !monedas.isEmpty()) {//Moneda
												solicitudRetiro.setIdMoneda(monedas.get(0).getIdMoneda());
												solicitudRetiro.setMoneda(monedas.get(0));
											}else {
												error[0]=linea;
												error[1]="El tipo de moneda dado no se encuentra en el sistema";
												errores.add(error);
											}
											
											if(solicitud[6].length()>=2 && solicitud[6].length()<=15) {	//Monto
												if(solicitudRetiro.getMoneda()!=null && solicitudRetiro.getIdMoneda().equals(2)) {
													BigDecimal monto = new BigDecimal(solicitud[6].substring(0,solicitud[6].length()-2)).setScale(2, RoundingMode.UNNECESSARY);
													if (!esMultiploDe(monto, new BigDecimal(5))) {
														error[0]=linea;
														error[1]="Para la moneda seleccionada el monto deber ser m\u00FAltiplo de 5";
														errores.add(error);
													}else {
														solicitudRetiro.setMonto(new BigDecimal(solicitud[6].substring(0,solicitud[6].length()-2)).setScale(2, RoundingMode.UNNECESSARY));
													}
												}else {
													solicitudRetiro.setMonto(new BigDecimal(solicitud[6].substring(0,solicitud[6].length()-2)).setScale(2, RoundingMode.UNNECESSARY));
												}

											}else {
												error[0]=linea;
												error[1]="La longitud del campo monto debe ser mayor a 0 y menor o igual a 15";
												errores.add(error);
											}
											if(existeSolicitud(solicitudes, solicitudRetiro)||existeSolicitud(solicitudesExistentes, solicitudRetiro)) {
												error[0]=linea;
												error[1]="Solicitud duplicada";
												errores.add(error);
											}else {
												if(validaSolicitud(solicitudRetiro))
													solicitudes.add(solicitudRetiro);
											}
												
										} catch (ParseException e) {
											error[0]=linea;
											error[1]=e.getLocalizedMessage();
											errores.add(error);											
										}
									}
								}
							}
						}
					}
				} else {
					error[0]=linea;
					error[1]="El n\u00FAmero de campos no es el esperado";
					errores.add(error);
				}
				error = new String[2];
			}
			br.close();

		} catch (UnsupportedEncodingException e) {
			e.printStackTrace();
		} catch (IOException e) {
			e.printStackTrace();
		}
		model.addAttribute(Constantes.ERRORES, errores);
		if(solicitudesExistentes != null)
			solicitudesExistentes.addAll(solicitudes);
		else
			solicitudesExistentes = solicitudes;
			
		factory.getObject().removeAttribute("SolicitudesMasivas");		
		factory.getObject().setAttribute("SolicitudesMasivas", solicitudesExistentes);
		
		factory.getObject().removeAttribute("NroSolicitudesMasivas");
		factory.getObject().setAttribute("NroSolicitudesMasivas", solicitudes.size());
		
		factory.getObject().removeAttribute("ErrorSolicitudesMasivas");
		factory.getObject().setAttribute("ErrorSolicitudesMasivas", errores);
		
		return ResponseEntity.ok("Archivo procesado de forma exitosa");
	    }else
	    	return new ResponseEntity<String>("Extensi\\u00F3n inv\\u00E1lida", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
	    
	}
	
	@PostMapping("/eliminarSolicitudRetiro")
	public ResponseEntity<String> eliminarSolicitud(Model model, @RequestParam("id") int idSolicitud) {
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");
		SolicitudRetiroModel sr = new SolicitudRetiroModel(idSolicitud);
		int posicion = solicitudes.lastIndexOf(sr);
		if(posicion>-1)
		solicitudes.remove(posicion);		

		factory.getObject().removeAttribute("SolicitudesMasivas");
		factory.getObject().setAttribute("SolicitudesMasivas", solicitudes);
			
		return ResponseEntity.ok("Solicitud eliminada de forma exitosa");
	}
	
	private String verificarFecha(String fecha) throws ParseException {
		SimpleDateFormat in = new SimpleDateFormat(Constantes.FORMATO_FECHA_ddMMYYYY);
		SimpleDateFormat out = new SimpleDateFormat("dd-MM-yyyy");		
		Date f = null;
		in.setLenient(false);
		try {
			f = in.parse(fecha);	
		} catch (Exception e) {
			throw new ParseException("Fecha inv\u00E1lida", 1);
		}
		if (f.before(new Date()))
				throw new ParseException("Fecha caducada", 1);
		Calendar cal = Calendar.getInstance(); 
		cal.add(Calendar.MONTH, 4);
		if(f.after(cal.getTime()))
			throw new ParseException("La fecha debe corresponder con un d\u00EDa h\u00E1bil dentro de los pr\u00F3ximos 4 meses", 1);
		
		List<Feriado> feriado = feriadoRepo.findByFecha(f);
		if (feriado != null && !feriado.isEmpty())
			throw new ParseException("Es feriado", 1);

		return out.format(f);
	}
		
	public String getFromMonto(String montoIn) {
		StringBuilder str = new StringBuilder(montoIn);
		str.insert(str.length()-2, '.');
		return str.toString().replaceFirst("^0+(?!$)", "");
	}
	
	@GetMapping(path = "/solicitudesMasivas", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<SolicitudRetiroModel> getSolicitudesMasivas() {
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");
		return solicitudes;
	}
	
	@GetMapping(path = "/nroSolicitudesMasivas", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public int getNroSolicitudesMasivas() {
		int nro =  (int) factory.getObject().getAttribute("NroSolicitudesMasivas");
		return nro;
	}
	
	@GetMapping(path = "/erroresSolicitudesMasivas", produces = MediaType.APPLICATION_JSON_VALUE)
	@ResponseBody
	public List<String[]> getErroresSolicitudesMasivas() {
		List<String[]> errores = (List<String[]>) factory.getObject().getAttribute("ErrorSolicitudesMasivas");
		factory.getObject().removeAttribute("ErrorSolicitudesMasivas");
		return errores;
	}
	
	@PostMapping("/cargarSolicitudRetiro")
	public  ResponseEntity<String> cargarSolicitudRetiro(@Valid SolicitudRetiroModel solicitudRetiroModel, BindingResult result, Model model, @RequestParam("crearSolicitud") boolean  operacionCrear) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");
		if(solicitudes == null) 
			solicitudes = new ArrayList<SolicitudRetiroModel>();
		//model.addAttribute("crearSolicitud", true);
		SolicitudRetiroModel solicitud = new SolicitudRetiroModel();
		final List<Feriado> listaFeriados = feriadoRepo.findAll();
		DateFormat simpleDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_D_M_YYYY);
		final List<String> feriados = listaFeriados.stream().map((feriado) -> simpleDateFormat.format(feriado.getFecha())).collect(Collectors.toList());
		model.addAttribute("feriados", feriados);

		if(operacionCrear) {//crear
			solicitudRetiroModel.setIdSolicitud(solicitudes.size()+1);
			solicitudRetiroModel.setIdEmpresa(usuario.getIdEmpresa());			
			solicitudRetiroModel.setAutorizado(autorizadoRepo.findById(solicitudRetiroModel.getIdAutorizado()).get());
			solicitudRetiroModel.setAgencia(agenciaRepo.findById(solicitudRetiroModel.getIdAgencia()).get());
			solicitudRetiroModel.setMoneda(monedaRepo.findById(solicitudRetiroModel.getIdMoneda()).get());
			
			if(!existeSolicitud(solicitudes,solicitudRetiroModel))
				solicitudes.add(solicitudRetiroModel);
			else
				return new ResponseEntity<String>("Solicitud duplicada", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
		}else {//editar
			int index = solicitudes.indexOf(solicitudRetiroModel);
			if(index >=0) {
				solicitud = solicitudes.get(index);
				solicitud.setFechaEstimada(solicitudRetiroModel.getFechaEstimada());
				solicitud.setTipoBillete(solicitudRetiroModel.getTipoBillete());
				solicitud.setMonto(solicitudRetiroModel.getMonto());
				solicitud.setIdAgencia(solicitudRetiroModel.getIdAgencia());
				solicitud.setIdAutorizado(solicitudRetiroModel.getIdAutorizado());
				solicitud.setIdMoneda(solicitudRetiroModel.getIdMoneda());
				solicitud.setAutorizado(autorizadoRepo.findById(solicitudRetiroModel.getIdAutorizado()).get());
				solicitud.setAgencia(agenciaRepo.findById(solicitudRetiroModel.getIdAgencia()).get());
				solicitud.setMoneda(monedaRepo.findById(solicitudRetiroModel.getIdMoneda()).get());
				solicitudes.set(index, solicitud);
			}
		}
		
		if(solicitudRetiroModel.getMoneda()!=null && solicitudRetiroModel.getIdMoneda().equals(2)) {
			BigDecimal monto = solicitudRetiroModel.getMonto();
			if (!esMultiploDe(monto, new BigDecimal(5))) {
				return new ResponseEntity<String>("Para la moneda seleccionada el monto deber ser multiplo de 5", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
			}
		}		
		
		  if (result.hasErrors()) {			  
			  model.addAttribute("solicitudRetiro", solicitudRetiroModel); 
			  return new ResponseEntity<String>("Solicitud errada", new HttpHeaders(), HttpStatus.ALREADY_REPORTED);
		  }
		 
		model.addAttribute("solicitudRetiroModel", solicitud);
		factory.getObject().removeAttribute("SolicitudesMasivas");
		factory.getObject().setAttribute("SolicitudesMasivas", solicitudes);
		model.addAttribute(Constantes.CREAR, true);
		if(operacionCrear)
			return ResponseEntity.ok("Solicitud agregada");
		else
			return ResponseEntity.ok("Solicitud actualizada");
	}
	
	@Transactional
	@PostMapping("/crearSolicitudesDeRetiro")
	public String crearSolicitudesDeRetiro(Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		List<SolicitudRetiroModel> solicitudes = (List<SolicitudRetiroModel>) factory.getObject().getAttribute("SolicitudesMasivas");
		
		try {
			for (SolicitudRetiroModel solicitudRetiroModel : solicitudes) {
				final SolicitudRetiro solicitudRetiro = convertirSolicitudRetiroModelASolicitudRetiro(solicitudRetiroModel);
				
				final SolicitudRetiro newSolicitudRetiro = solicitudRetiroRepo.save(solicitudRetiro);
				
				final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
				solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
				if(usuario.getIdUsuario()==null)
					solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
				solicitudRetiroTraza.setIdSolicitud(newSolicitudRetiro.getIdSolicitud());
				solicitudRetiroTraza.setIdEstatusSolicitud(1);
				Date date = new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);
				solicitudRetiroTraza.setFecha(ts);
				
				solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);
				
				String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_CREAR,
						newSolicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
				logServ.registrarLog(Constantes.CREAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_GENERACION, true,
						Util.getRemoteIp(request), usuario);
			}
		} catch (Exception e) {
			factory.getObject().removeAttribute("SolicitudesMasivas");
			factory.getObject().removeAttribute("NroSolicitudesMasivas");
			
			return "redirect:solicitudesListar?error";
		}
		factory.getObject().removeAttribute("SolicitudesMasivas");
		factory.getObject().removeAttribute("NroSolicitudesMasivas");
		
		return "redirect:solicitudesListar?success";
	}
	
	private SolicitudRetiro convertirSolicitudRetiroModelASolicitudRetiro(
			final SolicitudRetiroModel solicitudRetiroModel) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		final SolicitudRetiro solicitudRetiro = new SolicitudRetiro();
		solicitudRetiro.setIdEmpresa(usuario.getIdEmpresa());
		final DateFormat dateFormat = new SimpleDateFormat("dd-MM-yyyy");
		solicitudRetiro.setFechaEstimada(
				dateFormat.parse(solicitudRetiroModel.getFechaEstimada() + Constantes.FORMATO_HORA_0));
		solicitudRetiro.setTipoBillete(solicitudRetiroModel.getTipoBillete());
		solicitudRetiro.setMonto(BigDecimal.valueOf(solicitudRetiroModel.getMonto().longValue()));
		solicitudRetiro.setIdAgencia(solicitudRetiroModel.getIdAgencia());
		solicitudRetiro.setIdAutorizado(solicitudRetiroModel.getIdAutorizado());
		solicitudRetiro.setIdMoneda(solicitudRetiroModel.getIdMoneda());
		return solicitudRetiro;
	}
	
	private boolean existeSolicitud(List<SolicitudRetiroModel> solicitudes,  SolicitudRetiroModel solicitud) {
		if(solicitudes != null)
		for (SolicitudRetiroModel s : solicitudes) {
			if(s.getFechaEstimada()!= null && solicitud.getFechaEstimada()!=null && solicitud.getFechaEstimada().equalsIgnoreCase(s.getFechaEstimada()))
				if(s.getIdAgencia()!= null && solicitud.getIdAgencia()!=null && solicitud.getIdAgencia().equals(s.getIdAgencia()))
					if(s.getIdAutorizado()!= null && solicitud.getIdAutorizado()!=null && solicitud.getIdAutorizado().equals(s.getIdAutorizado()))
						if(s.getIdMoneda()!= null && solicitud.getIdMoneda()!=null && solicitud.getIdMoneda().equals(s.getIdMoneda()))
							if(s.getMonto()!= null && solicitud.getMonto()!=null && solicitud.getMonto().setScale(0).equals(s.getMonto().setScale(0)))
								return true;
		}
		return false;
	}
	
	private boolean validaSolicitud(SolicitudRetiroModel s) {
		if(s.getAgencia()!=null && s.getAutorizado()!=null&&s.getMoneda()!=null && s.getMonto()!=null)
			return true;
		
		return false;
	}
	
	@GetMapping("/descarga/{fileName:.+}")
	public ResponseEntity<Resource> downloadFileFromLocal(@PathVariable String fileName) {
		Path path = Paths.get(rutaRecursos + fileName);
		Resource resource = null;
		try {
			resource = new UrlResource(path.toUri());
		} catch (MalformedURLException e) {
			e.printStackTrace();
		}
		return ResponseEntity.ok()
				.contentType(MediaType.parseMediaType(contentType))
				.header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + resource.getFilename() + "\"")
				.body(resource);
	}
	
	public static boolean esMultiploDe(final BigDecimal multiplo, final BigDecimal base){
	    if (multiplo.compareTo(base) == 0)
	        return true;
	    try {
	    	multiplo.divide(base, 0, BigDecimal.ROUND_UNNECESSARY);
	        return true;
	    }
	    catch(ArithmeticException e){
	        return false;
	    }
	}
}
