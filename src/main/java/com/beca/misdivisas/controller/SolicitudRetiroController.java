package com.beca.misdivisas.controller;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.StringTokenizer;
import java.util.stream.Collectors;

import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdRequest;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseCRUD;
import com.beca.misdivisas.api.detectidclient.model.ClientesDetectIdResponseGet;
import com.beca.misdivisas.api.detectidclient.model.DatosClientesDetectIdGet;
import com.beca.misdivisas.api.detectidotp.model.OtpRequest;
import com.beca.misdivisas.api.detectidotp.model.OtpResponse;
import com.beca.misdivisas.api.generico.IMicroservicioService;
import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IAutorizadoRepo;
import com.beca.misdivisas.interfaces.IEstatusSolicitudRetiroRepo;
import com.beca.misdivisas.interfaces.IFeriadoRepo;
import com.beca.misdivisas.interfaces.IMonedaRepo;
import com.beca.misdivisas.interfaces.IMotivoRechazo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroRepo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroTrazaRepo;
import com.beca.misdivisas.interfaces.ITransportistaRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.jpa.EstatusSolicitudRetiro;
import com.beca.misdivisas.jpa.Feriado;
import com.beca.misdivisas.jpa.Moneda;
import com.beca.misdivisas.jpa.MotivoRechazo;
import com.beca.misdivisas.jpa.SolicitudRetiro;
import com.beca.misdivisas.jpa.SolicitudRetiroTraza;
import com.beca.misdivisas.jpa.Transportista;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.AutorizadoBeneficioTraspaso;
import com.beca.misdivisas.model.AutorizadoEmpresaTransporte;
import com.beca.misdivisas.model.AutorizadoModel;
import com.beca.misdivisas.model.AutorizadoPersonaJuridica;
import com.beca.misdivisas.model.AutorizadoPersonaNatural;
import com.beca.misdivisas.model.ReporteSolicitudRetiro;
import com.beca.misdivisas.model.SolicitudRetiroModel;
import com.beca.misdivisas.model.SolicitudTrazaModel;
import com.beca.misdivisas.model.ValidarOtpModel;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.SolicitudRetiroService;
import com.beca.misdivisas.util.AutorizadoUtils;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class SolicitudRetiroController {
	private final static DateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DD_MM_YYYY);
	private final static DateFormat longDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
	private final static DateFormat simpleDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_D_M_YYYY);

	private final static List<Integer> tiposAutorizado = Arrays.asList(1, 2, 3);

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;

	@Autowired
	private IAutorizadoRepo autorizadoRepository;

	@Autowired
	private ISolicitudRetiroRepo solicitudRetiroRepo;

	@Autowired
	private IAgenciaRepo agenciaRepo;

	@Autowired
	private IMonedaRepo monedaRepo;

	@Autowired
	private ISolicitudRetiroTrazaRepo solicitudRetiroTrazaRepo;

	@Autowired
	private IMotivoRechazo motivoRechazoRepository;

	@Autowired
	private IFeriadoRepo feriadoRepository;

	@Autowired
	private IEstatusSolicitudRetiroRepo estatusSolicitudRetiroRepo;

	@Autowired
	private ITransportistaRepo transportistaRepo;

	// ocasiona error al desplegar en el TomEE
	// @PersistenceContext
//	private EntityManager entityManager;

	@Autowired
	private IMicroservicioService microServicioService;

	@Autowired
	private SolicitudRetiroService solicitudRetiroService;

	@Value("${ruta.img.autorizados}")
	private String rutaImg;

	@GetMapping(value = "/solicitudesRetiro")
	public String getSolicitudesRetiro(Model modelo) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		factory.getObject().removeAttribute("SolicitudesMasivas");
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		modelo.addAttribute(Constantes.SOLICTUD_RETIRO_ACCION_FROM, Constantes.OP_GENERACION);

		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(usuario.getIdEmpresa(), 1, 2);
		modelo.addAttribute("solicitudesRetiro", listaSolicitudes);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.LISTAR_SOLICITUD_RETIRO_EFECTIVO,
				MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_GENERACION, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_SOLICITUDES_RETIRO;
	}

	@GetMapping("nuevaSolicitudRetiro")
	public String nuevaSolicitudRetiro(Model model) throws Exception {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		setModelData(model, usuario.getIdEmpresa());

		model.addAttribute(Constantes.CREAR, true);
		// model.addAttribute("crearSolicitud", true);
		final List<Feriado> listaFeriados = feriadoRepository.findAllFechaMayorQue(new Date());
		final List<String> feriados = listaFeriados.stream()
				.map((feriado) -> simpleDateFormat.format(feriado.getFecha())).collect(Collectors.toList());
		model.addAttribute("feriados", feriados);

		final SolicitudRetiroModel solicitudRetiroModel = new SolicitudRetiroModel();
		model.addAttribute("solicitudRetiroModel", solicitudRetiroModel);
		return Constantes.OP_SOLICITUD_RETIRO_FORM;
	}

	@PostMapping("crearSolicitudRetiro")
	public String crearSolicitudRetiro(@Valid SolicitudRetiroModel solicitudRetiroModel, BindingResult result,
			Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, true);
			model.addAttribute("solicitudRetiro", solicitudRetiroModel);
			return Constantes.OP_SOLICITUD_RETIRO_FORM;
		}

		final SolicitudRetiro solicitudRetiro = convertirSolicitudRetiroModelASolicitudRetiro(solicitudRetiroModel);

		final SolicitudRetiro newSolicitudRetiro = solicitudRetiroRepo.save(solicitudRetiro);

		final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
		solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
		if (usuario.getIdUsuario() == null)
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

		return "redirect:solicitudesListar?success";
	}

	@GetMapping("editarSolicitudRetiro")
	public String editarSolicitudRetiro(@RequestParam("idSolicitud") int id, Model model) throws Exception {
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:autorizadosListar?error";
		} else {
			setModelData(model, solicitudRetiro.getIdEmpresa());
			model.addAttribute(Constantes.CREAR, false);
			final SolicitudRetiroModel solicitudRetiroModel = convertirSolicitudRetiroASolicitudRetiroModel(
					solicitudRetiro);
			model.addAttribute("solicitudRetiroModel", solicitudRetiroModel);
			return Constantes.OP_SOLICITUD_RETIRO_FORM;
		}
	}

	@PostMapping("actualizarSolicitudRetiro")
	public String actualizarSolicitudRetiro(@Valid SolicitudRetiroModel solicitudRetiroModel, BindingResult result,
			Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		// validarAutorizado(autorizadoBeneficioTraspaso, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, false);
			model.addAttribute("solicitudRetiro", solicitudRetiroModel);
			return Constantes.OP_SOLICITUD_RETIRO_FORM;
		}

		final SolicitudRetiro solicitudRetiro = convertirSolicitudRetiroModelASolicitudRetiro(solicitudRetiroModel);
		solicitudRetiro.setIdSolicitud(solicitudRetiroModel.getIdSolicitud());
		solicitudRetiroRepo.save(solicitudRetiro);

		final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
		solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
		if (usuario.getIdUsuario() == null)
			solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
		solicitudRetiroTraza.setIdSolicitud(solicitudRetiroModel.getIdSolicitud());
		solicitudRetiroTraza.setIdEstatusSolicitud(2);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		solicitudRetiroTraza.setFecha(ts);

		solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);
		String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_EDICION,
				solicitudRetiroModel.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.EDICION_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_GENERACION, true,
				Util.getRemoteIp(request), usuario);

		return "redirect:solicitudesListar?success";
	}

	@PostMapping("anularSolicitudRetiro")
	public String anularSolicitudRetiro(@RequestParam("idSolicitud") int id,
			@ModelAttribute("accionFrom") String accionFrom, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			if (accionFrom.equalsIgnoreCase(Constantes.OP_GENERACION))
				return "redirect:solicitudesRetiro?error";
			else
				return "redirect:solicitudesRetiroAprobar?error";

		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
			if (usuario.getIdUsuario() == null)
				solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setIdEstatusSolicitud(3);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_ANULAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.ANULAR_SOLICITUD_RETIRO_EFECTIVO, detalle, accionFrom, true,
					Util.getRemoteIp(request), usuario);

			if (accionFrom.equalsIgnoreCase(Constantes.OP_GENERACION))
				return "redirect:solicitudesRetiro?success";
			else
				return "redirect:solicitudesRetiroAprobar?success";

		}
	}

	@PostMapping("anularSolicitudesRetiro")
	public String anularSolicitudRetiro(@RequestParam("idsSolicitudAnular") String idsSolicitudAnular, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		List<Integer> ids = getIdsList(idsSolicitudAnular);

		final List<SolicitudRetiro> solicitudesRetiro = solicitudRetiroRepo.findAllById(ids);
		if (solicitudesRetiro.isEmpty() || solicitudesRetiro.size() != ids.size()) {
			return "redirect:solicitudesRetiroAprobar?error";
		} else {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);

			final List<SolicitudRetiroTraza> solicitudesRetiroTraza = solicitudesRetiro.stream()
					.map(solicitud -> new SolicitudRetiroTraza(solicitud.getIdSolicitud(), 3, usuario.getIdUsuario(),
							ts, usuario.getIdUsuario() == null ? usuario.getNombreUsuario() : null))
					.collect(Collectors.toList());

			solicitudRetiroTrazaRepo.saveAll(solicitudesRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_ANULAR,
					idsSolicitudAnular, usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.ANULAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_GENERACION, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:solicitudesRetiroAprobar?success";
		}
	}

	@PostMapping("cancelarSolicitudRetiro")
	public String cancelarSolicitudRetiro(@RequestParam("idSolicitud") int id,
			@ModelAttribute("accionFrom") String accionFrom, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			if (accionFrom.equalsIgnoreCase(Constantes.OP_ENTREGA))
				return "redirect:solicitudesRetiroEntregar?error";
			else
				return "redirect:solicitudesRetiroProcesar?error";

		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
			if (usuario.getIdUsuario() == null)
				solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setIdEstatusSolicitud(10);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_CANCELAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.CANCELAR_SOLICITUD_RETIRO_EFECTIVO, detalle, accionFrom, true,
					Util.getRemoteIp(request), usuario);

			if (accionFrom.equalsIgnoreCase(Constantes.OP_ENTREGA))
				return "redirect:solicitudesRetiroEntregar?success";
			else
				return "redirect:solicitudesRetiroProcesar?success";

		}
	}

	@GetMapping("/solicitudesListar")
	public String listarSolicitudes(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(usuario.getIdEmpresa(), 1, 2);
		model.addAttribute("solicitudesRetiro", listaSolicitudes);
		return Constantes.OP_SOLICITUDES_RETIRO_LISTA;
	}

	private List<SolicitudRetiroModel> getSolicitudesRetiro(Integer idEmpresa, Integer estatusA, Integer estatusB) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		List<SolicitudRetiro> solicitudes = new ArrayList<SolicitudRetiro>();
		if (usuario.getIdAgencia() == null || (usuario.getIdAgencia() != null && estatusA != 7)) {
			solicitudes = idEmpresa != null ? solicitudRetiroRepo.findByIdEmpresa(idEmpresa)
					: solicitudRetiroRepo.findAll();
		} else {
			int idAgencia = usuario.getIdAgencia();
			solicitudes = solicitudRetiroRepo.findByIdAgencia(idAgencia);
		}
		final List<SolicitudRetiroModel> listaSolicitudes = new ArrayList<SolicitudRetiroModel>();
		final List<Integer> solicitudesIds = solicitudes.stream().map(SolicitudRetiro::getIdSolicitud)
				.collect(Collectors.toList());
		final List<SolicitudRetiroTraza> solicitudTrazas = solicitudRetiroTrazaRepo.findByIdSolicitudIn(solicitudesIds);

		solicitudes.stream().forEach((solicitud) -> {
			final Optional<SolicitudRetiroTraza> solicitudTraza = solicitudTrazas.stream()
					.filter(traza -> traza.getIdSolicitud().equals(solicitud.getIdSolicitud()))
					.sorted(new SortByIdTraza()).findFirst();
			if (solicitudTraza.isPresent() && (solicitudTraza.get().getIdEstatusSolicitud().equals(estatusA)
					|| (estatusB != null && solicitudTraza.get().getIdEstatusSolicitud().equals(estatusB)))) {

				if (solicitud.getAutorizado().getIdTipoAutorizado().intValue() == 3) {
//					entityManager.detach(solicitud.getAutorizado());
					Optional<Transportista> transportista = transportistaRepo
							.findById(solicitud.getAutorizado().getIdTransportista());
					solicitud.getAutorizado().setRifEmpresa(transportista.get().getRif());
					solicitud.getAutorizado().setNombreEmpresa(transportista.get().getTransportista());
				}

				listaSolicitudes.add(new SolicitudRetiroModel(solicitud.getIdSolicitud(), solicitud.getCartaPorte(),
						dateFormat.format(solicitud.getFechaEstimada()), solicitud.getMonto(),
						solicitud.getTipoBillete(), solicitud.getAgencia(), solicitud.getAutorizado(),
						solicitud.getMoneda(), solicitudTraza.get().getEstatusSolicitudRetiro().getEstatusSolicitud(),
						solicitud.getEmpresa().getEmpresa()));
			}
		});

		return listaSolicitudes;
	}

	private List<ReporteSolicitudRetiro> getReporteSolicitudesRetiro(Integer idEmpresa, Date fechaInicio, Date fechaFin,
			Integer idMoneda, Integer estatus) {
		final List<SolicitudRetiro> solicitudes = solicitudRetiroRepo.findByIdEmpresaAndIdMoneda(idEmpresa, idMoneda);
		final List<ReporteSolicitudRetiro> listaSolicitudes = new ArrayList<ReporteSolicitudRetiro>();
		final List<Integer> solicitudesIds = solicitudes.stream().map(SolicitudRetiro::getIdSolicitud)
				.collect(Collectors.toList());
		final List<SolicitudRetiroTraza> solicitudTrazas = estatus.intValue() == -1
				? solicitudRetiroTrazaRepo.findByIdSolicitudInAndFecha(solicitudesIds, fechaInicio, fechaFin)
				: solicitudRetiroTrazaRepo.findByIdSolicitudInAndFechaAndIdEstatus(solicitudesIds, fechaInicio,
						fechaFin, estatus);
		solicitudes.stream().forEach((solicitud) -> {
			final Optional<SolicitudRetiroTraza> solicitudTraza = solicitudTrazas.stream()
					.filter(traza -> traza.getIdSolicitud().equals(solicitud.getIdSolicitud()))
					.sorted(new SortByIdTraza()).findFirst();
			if (solicitudTraza.isPresent()) {
				final SolicitudRetiroTraza traza = solicitudTraza.get();
				if (solicitud.getAutorizado().getIdTipoAutorizado().intValue() == 3) {
					Optional<Transportista> transportista = transportistaRepo
							.findById(solicitud.getAutorizado().getIdTransportista());
					solicitud.getAutorizado().setRifEmpresa(transportista.get().getRif());
					solicitud.getAutorizado().setNombreEmpresa(transportista.get().getTransportista());
				}
				String estado = (traza.getMotivoRechazo() == null || traza.getMotivoRechazo().getMotivo() == null
						|| traza.getMotivoRechazo().getMotivo().trim().equals(""))
								? traza.getEstatusSolicitudRetiro().getEstatusSolicitud()
								: traza.getEstatusSolicitudRetiro().getEstatusSolicitud() + " - ("
										+ traza.getMotivoRechazo().getMotivo() + ")";

				String usuario = (traza.getIdUsuario() == null || traza.getUsuario() == null) ? traza.getCodigoUsuario()
						: traza.getUsuario().getNombreCompleto();

				listaSolicitudes.add(new ReporteSolicitudRetiro(solicitud.getIdSolicitud(), solicitud.getCartaPorte(),
						dateFormat.format(solicitud.getFechaEstimada()), solicitud.getMonto().intValue(),
						solicitud.getTipoBillete(), solicitud.getAgencia(), solicitud.getAutorizado(),
						solicitud.getMoneda(), estado, longDateFormat.format(traza.getFecha()), usuario));
			}
		});
		return listaSolicitudes;
	}

	@GetMapping(value = "/solicitudesRetiroAprobar")
	public String getSolicitudesRetiroAProcesar(Model modelo) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		modelo.addAttribute(Constantes.SOLICTUD_RETIRO_ACCION_FROM, Constantes.OP_APROBACION);

		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(usuario.getIdEmpresa(), 1, 2);

		modelo.addAttribute("solicitudesRetiro", listaSolicitudes);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.LISTAR_SOLICITUD_RETIRO_EFECTIVO,
				MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_APROBACION, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_SOLICITUDES_RETIRO_APROBAR;
	}

	@GetMapping(value = "/solicitudesRetiroValidar")
	public String getSolicitudesRetiroAValidar(Model modelo) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		// modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());
		modelo.addAttribute(Constantes.SOLICTUD_RETIRO_ACCION_FROM, Constantes.OP_VALIDACION);
		modelo.addAttribute("accion", Constantes.OP_VALIDAR);

		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(null, 4, null);

		modelo.addAttribute("solicitudesRetiro", listaSolicitudes);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.LISTAR_SOLICITUD_RETIRO_EFECTIVO,
				MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_VALIDACION, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_SOLICITUDES_RETIRO_VALIDAR_PROCESAR;
	}

	@GetMapping(value = "/solicitudesRetiroProcesar")
	public String solicitudesRetiroProcesar(Model modelo, @ModelAttribute("cartaPorte") final String cartaPorte) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		/*
		 * if(usuario.getEmpresa()!=null) modelo.addAttribute(Constantes.ID_EMPRESA,
		 * usuario.getEmpresa().getRif());
		 */
		modelo.addAttribute(Constantes.SOLICTUD_RETIRO_ACCION_FROM, Constantes.OP_PROCESAMIENTO);
		modelo.addAttribute("accion", Constantes.OP_PROCESAR);

		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(null, 5, null);

		modelo.addAttribute("solicitudesRetiro", listaSolicitudes);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.LISTAR_SOLICITUD_RETIRO_EFECTIVO,
				MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_PROCESAMIENTO, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_SOLICITUDES_RETIRO_VALIDAR_PROCESAR;
	}

	@GetMapping(value = "/solicitudesRetiroEntregar")
	public String getSolicitudesRetiroAEntregar(Model modelo) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		/* modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif()); */
		modelo.addAttribute(Constantes.SOLICTUD_RETIRO_ACCION_FROM, Constantes.OP_ENTREGA);

		final List<SolicitudRetiroModel> listaSolicitudes = getSolicitudesRetiro(null, 7, null);

		modelo.addAttribute("solicitudesRetiro", listaSolicitudes);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.LISTAR_SOLICITUD_RETIRO_EFECTIVO,
				MessageFormat.format(Constantes.OPCION_STR_EFECTIVO, "Retiro"), Constantes.OP_ENTREGA, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_SOLICITUDES_RETIRO_PROCESAR_ENTREGAR;
	}

	@PostMapping("aprobarSolicitudRetiro")
	public String aprobarSolicitudRetiro(@RequestParam("idSolicitudAprobar") int id, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroAprobar?error";
		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setIdUsuario(usuario.getIdUsuario());
			if (usuario.getIdUsuario() == null)
				solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setIdEstatusSolicitud(4);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_APROBAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.APROBAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_APROBACION, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:solicitudesRetiroAprobar?success";
		}
	}

	@PostMapping("validarSolicitudRetiro")
	public String validarSolicitudRetiro(@RequestParam("idSolicitud") int id, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroValidar?error";
		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdEstatusSolicitud(5);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_VALIDAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.VALIDAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_VALIDACION, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:solicitudesRetiroValidar?success";
		}
	}

	@PostMapping("aprobarSolicitudesRetiro")
	public String aprobarSolicitudesRetiro(@RequestParam("idsSolicitudAprobar") String idsSolicitudAprobar,
			Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		List<Integer> ids = getIdsList(idsSolicitudAprobar);

		final List<SolicitudRetiro> solicitudesRetiro = solicitudRetiroRepo.findAllById(ids);
		if (solicitudesRetiro.isEmpty() || solicitudesRetiro.size() != ids.size()) {
			return "redirect:solicitudesRetiroAprobar?error";
		} else {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);

			final List<SolicitudRetiroTraza> solicitudesRetiroTraza = solicitudesRetiro.stream()
					.map(solicitud -> new SolicitudRetiroTraza(solicitud.getIdSolicitud(), 4, usuario.getIdUsuario(),
							ts, usuario.getIdUsuario() == null ? usuario.getNombreUsuario() : null))
					.collect(Collectors.toList());

			solicitudRetiroTrazaRepo.saveAll(solicitudesRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_APROBAR,
					idsSolicitudAprobar, usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.APROBAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_APROBACION, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:solicitudesRetiroAprobar?success";
		}
	}

	@PostMapping("procesarSolicitudRetiro")
	public String procesarSolicitudRetiro(@RequestParam("idSolicitud") int id, Model model,
			RedirectAttributes redirectAttributes) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroProcesar?error";
		} else {

			final String cartaPorte = Util.generarCartaPorte();

			solicitudRetiro.setCartaPorte(cartaPorte);
			solicitudRetiroRepo.save(solicitudRetiro);

			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setIdEstatusSolicitud(7);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_PROCESAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.PROCESAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_PROCESAMIENTO,
					true, Util.getRemoteIp(request), usuario);

			redirectAttributes.addFlashAttribute("cartaPorte", cartaPorte);
			if (solicitudRetiro.getAutorizado() != null && solicitudRetiro.getAutorizado().getEmail() != null
					&& !solicitudRetiro.getAutorizado().getEmail().trim().equals("")) {
				String cuerpo = "";
				DateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
				cuerpo = MessageFormat.format(Constantes.CUERPO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO,
						Util.formatoFecha(Constantes.FORMATO_FECHA_EMAIL), cartaPorte,
						solicitudRetiro.getEmpresa().getEmpresa(),
						dateFormat.format(solicitudRetiro.getFechaEstimada()),
						solicitudRetiro.getAgencia().getAgencia());

				solicitudRetiroService.enviarCorreoAutorizado(solicitudRetiro.getAutorizado().getEmail(),
						Constantes.ASUNTO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO,
						Constantes.ENCABEZADO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO
								+ solicitudRetiro.getAutorizado().getNombreCompleto(),
						cuerpo, Constantes.PIE_CORREO_SOLICITUD_PROCESADA_AUTORIZADO, false, Util.getRemoteIp(request));
			}
			return "redirect:solicitudesRetiroProcesar";
		}
	}

	@GetMapping("getSolicitudRetiroAprobar")
	public String getSolicitudRetiroAprobar(@RequestParam("idSolicitud") int id, Model model) throws Exception {
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiro?error";
		} else {
			setDatosSolicitudModel(solicitudRetiro, model);
			return Constantes.OP_SOLICITUD_RETIRO_VIEW;
		}
	}

	@GetMapping("/getSolicitudRetiroValidar")
	public String getSolicitudRetiroValidar(@RequestParam("idSolicitud") int id, Model model) throws Exception {
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiro?error";
		} else {
			setDatosSolicitudModel(solicitudRetiro, model);
			return Constantes.OP_SOLICITUD_RETIRO_APROBAR_VIEW;
		}
	}

	@GetMapping("/getSolicitudRetiroProcesar")
	public String getSolicitudRetiroProcesar(@RequestParam("idSolicitud") int id, Model model) throws Exception {
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroProcesar?error";
		} else {
			setDatosSolicitudModel(solicitudRetiro, model);
			return Constantes.OP_SOLICITUD_RETIRO_VALIDADAS_VIEW;
		}
	}

	@GetMapping("/getSolicitudRetiroEntregar")
	public String getSolicitudRetiroEntregar(@RequestParam("idSolicitud") int id, Model model) throws Exception {
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroEntregar?error";
		} else {
			setDatosSolicitudModel(solicitudRetiro, model);
			return Constantes.OP_SOLICITUD_RETIRO_ENTREGADAS_VIEW;
		}
	}

	@GetMapping("/generarOtpEntregaSolicitudRetiro")
	public String generarOtpEntregaSolicitudRetiro(@RequestParam("idSolicitud") String id, Model model)
			throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		ValidarOtpModel validarOtpModel = new ValidarOtpModel();
		String idSolicitud = id;
		int idS = Integer.parseInt(idSolicitud);
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(idS);

		if (solicitudRetiro.getAutorizado() != null && solicitudRetiro.getAutorizado().getIdTipoAutorizado() != 3) {
			String nombre = solicitudRetiro.getAutorizado().getNombreCompleto() != null
					? solicitudRetiro.getAutorizado().getNombreCompleto().trim()
					: "";
			String telefono = solicitudRetiro.getAutorizado().getTelefonoMovil() != null
					? solicitudRetiro.getAutorizado().getTelefonoMovil().trim()
					: "";
			String email = solicitudRetiro.getAutorizado().getEmail() != null
					? solicitudRetiro.getAutorizado().getEmail().trim()
					: "";

			microServicioService.enviarOTP(
					solicitudRetiro.getAutorizado().getDocumentoIdentidad().trim().substring(0, 1),
					solicitudRetiro.getAutorizado().getDocumentoIdentidad().trim().substring(1), nombre, telefono,
					email, "retirar la solicitud", Util.getRemoteIp(request));
		}		
		validarOtpModel.setIdSolicitud(id);
		model.addAttribute("validarOtpModel", validarOtpModel);
		
		String detalle = MessageFormat.format(Constantes.DETALLE_GENERAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO,
				solicitudRetiro.getIdSolicitud(), solicitudRetiro.getAutorizado().getDocumentoIdentidad());
		logServ.registrarLog(Constantes.GENERAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO, detalle,
				Constantes.OP_ENTREGA, true, Util.getRemoteIp(request), usuario);
		
		return "modals/validarOtpModal";
		
	}

	@PostMapping("/validarOtpEntregaSolicitudRetiro")
	public String validarOtpEntregaSolicitudRetiro(@Valid ValidarOtpModel validarOtpModel, BindingResult result,
			Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		boolean invalidOtp = false;
		boolean excessOtp = false;
		boolean expiredOtp = false;
		int idS = Integer.parseInt(validarOtpModel.getIdSolicitud());
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(idS);

		String resultado = microServicioService.solicitarValidarOTP(
				solicitudRetiro.getAutorizado().getDocumentoIdentidad().trim(), validarOtpModel.getOtp(),
				Util.getRemoteIp(request), "retirar la solicitud", Constantes.VALIDAR);
		
		String detalle = MessageFormat.format(Constantes.DETALLE_VALIDAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO,
				solicitudRetiro.getIdSolicitud(), solicitudRetiro.getAutorizado().getDocumentoIdentidad());
		
		if (resultado.equals("902")) {
			invalidOtp = true;
			model.addAttribute("validarOtpModel", validarOtpModel);
			model.addAttribute("error", true);
			model.addAttribute("invalidOtp", invalidOtp);
			// model.addAttribute("resultado", resultado);
			logServ.registrarLog(Constantes.VALIDAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO, "OTP invalido: " + detalle, Constantes.OP_ENTREGA, false,
					Util.getRemoteIp(request), usuario);
			return "modals/validarOtpModal";
		} else if (resultado.equals("904")) {
			excessOtp = true;
			model.addAttribute("validarOtpModel", validarOtpModel);
			model.addAttribute("error", true);
			model.addAttribute("excessOtp", excessOtp);
			// model.addAttribute("resultado", resultado);
			logServ.registrarLog(Constantes.VALIDAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO, "Exceso de intentos OTP: " + detalle, Constantes.OP_ENTREGA, false,
					Util.getRemoteIp(request), usuario);
			return "modals/validarOtpModal";
		} else if (resultado.equals("938")) {
			expiredOtp = true;
			model.addAttribute("validarOtpModel", validarOtpModel);
			model.addAttribute("error", true);
			model.addAttribute("expiredOtp", expiredOtp);
			// model.addAttribute("resultado", resultado);
			logServ.registrarLog(Constantes.VALIDAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO, "OTP expirado: " + detalle, Constantes.OP_ENTREGA, false,
					Util.getRemoteIp(request), usuario);
			return "modals/validarOtpModal";
		}
		
		logServ.registrarLog(Constantes.VALIDAR_OTP_ENTREGA_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_ENTREGA, true,
				Util.getRemoteIp(request), usuario);
		final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
		solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
		solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
		solicitudRetiroTraza.setIdEstatusSolicitud(9);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);
		solicitudRetiroTraza.setFecha(ts);
		solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);
	
		String detalle1 = MessageFormat.format(Constantes.DETALLE_ENTREGA_SOLICITUD_RETIRO_EFECTIVO,
				solicitudRetiro.getIdSolicitud());
		logServ.registrarLog(Constantes.ENTREGAR_SOLICITUD_RETIRO_EFECTIVO, detalle1, Constantes.OP_ENTREGA, true,
				Util.getRemoteIp(request), usuario);
		return "modals/pruebaOTP";
	}

	@PostMapping("/entregarSolicitudRetiroEmpresaTransportista")
	public String entregarSolicitudRetiroEmpresaTransportista(@RequestParam("idSolicitud") int id, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			return "redirect:solicitudesRetiroEntregar?error";
		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdEstatusSolicitud(9);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.DETALLE_ENTREGA_SOLICITUD_RETIRO_EFECTIVO,
					solicitudRetiro.getIdSolicitud());
			logServ.registrarLog(Constantes.ENTREGAR_SOLICITUD_RETIRO_EFECTIVO, detalle, Constantes.OP_ENTREGA, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:solicitudesRetiroEntregar?success";
		}
	}

	private void setDatosSolicitudModel(SolicitudRetiro solicitudRetiro, Model model) throws IOException {

		final SolicitudRetiroModel solicitudRetiroModel = new SolicitudRetiroModel(solicitudRetiro.getIdSolicitud(),
				solicitudRetiro.getCartaPorte(), dateFormat.format(solicitudRetiro.getFechaEstimada()),
				solicitudRetiro.getMonto(), solicitudRetiro.getTipoBillete(), solicitudRetiro.getAgencia(),
				solicitudRetiro.getAutorizado(), solicitudRetiro.getMoneda(),
				solicitudRetiro.getEmpresa().getEmpresa());
		final SolicitudRetiroTraza solicitudTraza = solicitudRetiroTrazaRepo
				.findByIdSolicitud(solicitudRetiro.getIdSolicitud()).stream().sorted(new SortByIdTraza()).findFirst()
				.get();

		final SolicitudTrazaModel trazaModel = new SolicitudTrazaModel(longDateFormat.format(solicitudTraza.getFecha()),
				solicitudTraza.getUsuario() != null ? solicitudTraza.getUsuario().getNombreCompleto()
						: solicitudTraza.getCodigoUsuario(),
				solicitudTraza.getEstatusSolicitudRetiro().getEstatusSolicitud());
		model.addAttribute("solicitudRetiro", solicitudRetiroModel);
		model.addAttribute("traza", trazaModel);

		final List<MotivoRechazo> motivosRechazo = motivoRechazoRepository.findAll();
		model.addAttribute("motivosRechazo", motivosRechazo);

		final Autorizado autorizado = solicitudRetiro.getAutorizado();
		final int tipoAutorizacion = autorizado.getIdTipoAutorizado();
		model.addAttribute("tipoAutorizacion", tipoAutorizacion);
		model.addAttribute(Constantes.CREAR, false);
		switch (tipoAutorizacion) {
		case 3:
			final AutorizadoEmpresaTransporte autorizadoEmpresaTransporte = AutorizadoUtils
					.convertirAutorizadoToAutorizadoEmpresaTransporte(autorizado);

			model.addAttribute("autorizado", autorizadoEmpresaTransporte);
			break;
		case 4:
			final AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso = AutorizadoUtils
					.convertirAutorizadoToAutorizadoBeneficioTraspaso(autorizado);
			model.addAttribute("autorizado", autorizadoBeneficioTraspaso);
			break;
		case 1:
			final AutorizadoPersonaNatural autorizadoPersonaNatural = AutorizadoUtils
					.convertirAutorizadoToAutorizadoPersonaNatural(autorizado);
			if (autorizado.getImagenDocumento() != null && !autorizado.getImagenDocumento().isEmpty())
				autorizadoPersonaNatural.setImagenDocumento(
						Util.obtenerArchivoStr(rutaImg + autorizado.getImagenDocumento(), "documentoImg"));
			model.addAttribute("autorizado", autorizadoPersonaNatural);

			break;
		case 2:
			final AutorizadoPersonaJuridica autorizadoPersonaJuridica = AutorizadoUtils
					.convertirAutorizadoToAutorizadoPersonaJuridica(autorizado);
			if (autorizado.getImagenDocumento() != null && !autorizado.getImagenDocumento().isEmpty())
				autorizadoPersonaJuridica.setImagenDocumento(
						Util.obtenerArchivoStr(rutaImg + autorizado.getImagenDocumento(), "documentoImg"));
			if (autorizado.getImagenRif() != null && !autorizado.getImagenRif().isEmpty())
				autorizadoPersonaJuridica.setImagenDocumentoRif(
						Util.obtenerArchivoStr(rutaImg + autorizado.getImagenRif(), "documentoRifImg"));
			if (autorizado.getImagenAdicional() != null && !autorizado.getImagenAdicional().isEmpty())
				autorizadoPersonaJuridica.setImagenDocumentoAdicional(
						Util.obtenerArchivoStr(rutaImg + autorizado.getImagenAdicional(), "documentoAdicionalImg"));
			model.addAttribute("autorizado", autorizadoPersonaJuridica);
			break;
		}
	}

	@PostMapping("rechazarSolicitudRetiro")
	public String rechazarSolicitudRetiro(@RequestParam("idSolicitud") int id,
			@RequestParam("accionFrom") String accionFrom, @RequestParam("idMotivo") int idMotivo, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final SolicitudRetiro solicitudRetiro = solicitudRetiroRepo.findById(id);
		if (solicitudRetiro == null) {
			if (accionFrom.equalsIgnoreCase(Constantes.OP_VALIDACION))
				return "redirect:solicitudesRetiroValidar?error";
			else
				return "redirect:solicitudesRetiroProcesar?error";
		} else {
			final SolicitudRetiroTraza solicitudRetiroTraza = new SolicitudRetiroTraza();
			solicitudRetiroTraza.setCodigoUsuario(usuario.getNombreUsuario());
			solicitudRetiroTraza.setIdSolicitud(solicitudRetiro.getIdSolicitud());
			solicitudRetiroTraza.setIdEstatusSolicitud(6);
			solicitudRetiroTraza.setIdMotivoRechazo(idMotivo);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			solicitudRetiroTraza.setFecha(ts);
			solicitudRetiroTrazaRepo.save(solicitudRetiroTraza);

			String detalle = MessageFormat.format(Constantes.ACCION_SOLICITUD_RETIRO_EFECTIVO, Constantes.OP_RECHAZAR,
					solicitudRetiro.getIdSolicitud(), usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.RECHAZAR_SOLICITUD_RETIRO_EFECTIVO, detalle, accionFrom, true,
					Util.getRemoteIp(request), usuario);

			if (accionFrom.equalsIgnoreCase(Constantes.OP_VALIDACION))
				return "redirect:solicitudesRetiroValidar?success";
			else
				return "redirect:solicitudesRetiroProcesar?success";

		}
	}

	@GetMapping(value = "reporteSolicitudRetiro")
	public String reporte(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		final List<EstatusSolicitudRetiro> estatus = estatusSolicitudRetiroRepo.findByIdEstatusSolicitud();
		modelo.addAttribute("estatusSolicitud", estatus);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(usuario.getEmpresa().getCaracterRif(), usuario.getEmpresa().getRif()) + " "
						+ usuario.getEmpresa().getEmpresa());
		logServ.registrarLog(Constantes.REPORTE_SOLICITUD_RETIRO, Constantes.REPORTE_SOLICITUD_RETIRO,
				Constantes.REPORTE_SOLICITUD_RETIRO, true, Util.getRemoteIp(request), usuario);
		return Constantes.REPORTE_SOLICITUD_RETIRO;
	}

	@GetMapping(path = "/detalleSolicitudesRetiro/{fechaI}/{fechaF}/{moneda}/{estatus}", produces = "application/json")
	@ResponseBody
	public List<ReporteSolicitudRetiro> getDetalleSolicitudesRetiro(@PathVariable String fechaI,
			@PathVariable String fechaF, @PathVariable String moneda, @PathVariable String estatus) {
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);

		List<ReporteSolicitudRetiro> reporte = new ArrayList<>();

		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		try {
			reporte = getReporteSolicitudesRetiro(usuario.getIdEmpresa(),
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959), Integer.valueOf(moneda),
					Integer.valueOf(estatus));
		} catch (ParseException e) {
			logServ.registrarLog(Constantes.POSICION_CONSOLIDADA, e.getLocalizedMessage(),
					Constantes.POSICION_CONSOLIDADA, false, Util.getRemoteIp(request), usuario);
		}
		String detalle = MessageFormat.format(Constantes.CONSULTA_POR_PARAMETROS, fechaI, fechaF, moneda);
		logServ.registrarLog(Constantes.POSICION_CONSOLIDADA, detalle, Constantes.POSICION_CONSOLIDADA, true,
				Util.getRemoteIp(request), usuario);

		return reporte;
	}

	private SolicitudRetiro convertirSolicitudRetiroModelASolicitudRetiro(
			final SolicitudRetiroModel solicitudRetiroModel) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		final SolicitudRetiro solicitudRetiro = new SolicitudRetiro();
		solicitudRetiro.setIdEmpresa(usuario.getIdEmpresa());
		final DateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
		solicitudRetiro.setFechaEstimada(
				dateFormat.parse(solicitudRetiroModel.getFechaEstimada() + Constantes.FORMATO_HORA_0));
		solicitudRetiro.setTipoBillete(solicitudRetiroModel.getTipoBillete());
		solicitudRetiro.setMonto(BigDecimal.valueOf(solicitudRetiroModel.getMonto().longValue()));
		solicitudRetiro.setIdAgencia(solicitudRetiroModel.getIdAgencia());
		solicitudRetiro.setIdAutorizado(solicitudRetiroModel.getIdAutorizado());
		solicitudRetiro.setIdMoneda(solicitudRetiroModel.getIdMoneda());
		return solicitudRetiro;
	}

	private SolicitudRetiroModel convertirSolicitudRetiroASolicitudRetiroModel(final SolicitudRetiro solicitudRetiro)
			throws Exception {
		final SolicitudRetiroModel solicitudRetiroModel = new SolicitudRetiroModel(solicitudRetiro.getIdSolicitud(),
				dateFormat.format(solicitudRetiro.getFechaEstimada()),
				solicitudRetiro.getMonto().setScale(0, BigDecimal.ROUND_FLOOR), solicitudRetiro.getTipoBillete(),
				solicitudRetiro.getAgencia().getIdAgencia(), solicitudRetiro.getAutorizado().getIdAutorizado(),
				solicitudRetiro.getMoneda().getIdMoneda());

		return solicitudRetiroModel;
	}

	private List<Integer> getIdsList(String idsSolicitudes) {
		String idSolicitudesAprobar = idsSolicitudes.substring(1);
		idSolicitudesAprobar = idSolicitudesAprobar.substring(0, idSolicitudesAprobar.length() - 1);

		final StringTokenizer idSolicitudes = new StringTokenizer(idSolicitudesAprobar, "][");
		final List<Integer> ids = new ArrayList<Integer>();
		while (idSolicitudes.hasMoreTokens()) {
			ids.add(Integer.valueOf(idSolicitudes.nextToken()));
		}
		return ids;
	}

	private void setModelData(Model model, int idEmpresa) throws Exception {
		final List<Autorizado> autorizados = autorizadoRepository
				.findByidTipoAutorizadoInAndEstadoAndIdEmpresaOrderByIdTipoAutorizadoAscDocumentoIdentidadAsc(
						tiposAutorizado, Constantes.ACTIVO, idEmpresa);
		final List<AutorizadoModel> autorizadosBanco = autorizados.stream().map(autorizado -> {
			if (autorizado.getIdTipoAutorizado() == 3) {

				Optional<Transportista> transportista = transportistaRepo.findById(autorizado.getIdTransportista());

				return new AutorizadoModel(autorizado.getIdAutorizado(),
						transportista.get().getRif() + " " + transportista.get().getTransportista());
			} else if (autorizado.getIdTipoAutorizado() == 1) {
				return new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getDocumentoIdentidad() + " " + autorizado.getNombreCompleto());
			} else if (autorizado.getIdTipoAutorizado() == 2) {
				return new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getDocumentoIdentidad() + " " + autorizado.getNombreCompleto() + " "
								+ autorizado.getRifEmpresa() + " " + autorizado.getNombreEmpresa());
			} else {
				return new AutorizadoModel(autorizado.getIdAutorizado(), autorizado.getNombreCompleto());
			}
		}).collect(Collectors.toList());
		model.addAttribute("autorizados", autorizadosBanco);
		final List<Moneda> monedas = monedaRepo.findAll();
		model.addAttribute("monedas", monedas);
		final List<Agencia> agencias = agenciaRepo.findByAlmacenamiento(true);
		model.addAttribute("agencias", agencias);
	}
}

class SortByIdTraza implements Comparator<SolicitudRetiroTraza> {
	public int compare(SolicitudRetiroTraza a, SolicitudRetiroTraza b) {
		return b.getIdSolicitudRetiroTraza() - a.getIdSolicitudRetiroTraza();
	}
}
