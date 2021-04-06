package com.beca.misdivisas.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Comparator;
import java.util.Date;
import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.apache.commons.lang3.math.NumberUtils;
import org.apache.tika.Tika;

import com.beca.misdivisas.interfaces.IAutorizadoRepo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroRepo;
import com.beca.misdivisas.interfaces.ISolicitudRetiroTrazaRepo;
import com.beca.misdivisas.interfaces.ITipoAutorizado;
import com.beca.misdivisas.interfaces.ITransportistaRepo;
import com.beca.misdivisas.jpa.Autorizado;
import com.beca.misdivisas.jpa.SolicitudRetiro;
import com.beca.misdivisas.jpa.SolicitudRetiroTraza;
import com.beca.misdivisas.jpa.TipoAutorizado;
import com.beca.misdivisas.jpa.Transportista;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.AutorizadoModel;
import com.beca.misdivisas.model.AutorizadoBanco;
import com.beca.misdivisas.model.AutorizadoPersonaJuridica;
import com.beca.misdivisas.model.AutorizadoPersonaNatural;
import com.beca.misdivisas.model.AutorizadoBeneficioTraspaso;
import com.beca.misdivisas.model.AutorizadoEmpresaTransporte;
import com.beca.misdivisas.model.TipoAutorizadoModel;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.AutorizadoUtils;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;
import com.beca.misdivisas.util.ValidationUtils;

@Controller
public class AutorizadoController {
	private final static List<Integer> ESTATUS_SOLICITUD_ACTIVA = Arrays.asList(1, 2, 4, 5);

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private LogService logServ;

	@Autowired
	private IAutorizadoRepo autorizadoRepository;

	@Autowired
	private ITransportistaRepo transportistaRepository;

	@Autowired
	private ITipoAutorizado tipoAutorizadoRepository;

	@Autowired
	private ISolicitudRetiroRepo solicitudRetiroRepo;

	@Autowired
	private ISolicitudRetiroTrazaRepo solicitudRetiroTrazaRepo;

	@Value("${ruta.img.autorizados}")
	private String rutaImg;

	@GetMapping(value = "/autorizados")
	public String autorizados(Model modelo, @ModelAttribute("solicitudActiva") final String solicitudActiva) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.ID_EMPRESA, usuario.getEmpresa().getRif());

		final List<Autorizado> autorizados = autorizadoRepository.findByIdEmpresaAndEstado(usuario.getIdEmpresa(), "A");
		final List<AutorizadoModel> listaAutorizados = new ArrayList<AutorizadoModel>();
		autorizados.stream()
				.forEach(autorizado -> listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getTipoAutorizado().getTipoAutorizado(), autorizado.getRifEmpresa(),
						autorizado.getNombreEmpresa(), autorizado.getDocumentoIdentidad(),
						autorizado.getNombreCompleto())));
		modelo.addAttribute("autorizados", listaAutorizados);

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.OP_AUTORIZADOS, Constantes.OPCION_AUTORIZACION, Constantes.OP_SOLICITUD, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.OP_AUTORIZADOS;
	}

	@GetMapping(value = "/listarTipoAutorizado")
	public String listarTipoAutorizado(Model modelo) {
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		final List<TipoAutorizado> tipoAutorizadoList = tipoAutorizadoRepository.findAll();

		final List<TipoAutorizadoModel> tiposAutorizados = new ArrayList<TipoAutorizadoModel>();
		tipoAutorizadoList.stream().forEach(tipo -> tiposAutorizados
				.add(new TipoAutorizadoModel(tipo.getIdTipoAutorizado(), tipo.getTipoAutorizado())));
		modelo.addAttribute(Constantes.TIPOS_AUTORIZADO, tiposAutorizados);

		final TipoAutorizadoModel tipoAutorizadoModel = new TipoAutorizadoModel();
		modelo.addAttribute(Constantes.TIPO_AUTORIZADO_MODEL, tipoAutorizadoModel);

		return Constantes.OP_AUTORIZADOS_CREAR;
	}

	@PostMapping("crearAutorizado")
	public String crearAutorizado(@Valid TipoAutorizadoModel tipoAutorizacionModel, Model model) throws Exception {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		final int tipoAutorizacion = tipoAutorizacionModel.getIdTipoAutorizado();
		model.addAttribute("tipoAutorizacion", tipoAutorizacion);
		model.addAttribute(Constantes.CREAR, true);
		switch (tipoAutorizacion) {
		case 3:
			final AutorizadoEmpresaTransporte autorizadoEmpresaTransporte = new AutorizadoEmpresaTransporte();
			autorizadoEmpresaTransporte.setIdTipoAutorizado(tipoAutorizacion);
			model.addAttribute("autorizadoEmpresaTransporte", autorizadoEmpresaTransporte);
			return Constantes.OP_AUTORIZADOS_EMP_TRANSPORTE;
		case 4:
			final AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso = new AutorizadoBeneficioTraspaso();
			autorizadoBeneficioTraspaso.setIdTipoAutorizado(tipoAutorizacion);
			model.addAttribute("autorizadoBeneficioTraspaso", autorizadoBeneficioTraspaso);
			return Constantes.OP_AUTORIZADOS_BENEF_TRASPASO;
		case 1:
			final AutorizadoPersonaNatural autorizadoPersonaNatural = new AutorizadoPersonaNatural();
			autorizadoPersonaNatural.setIdTipoAutorizado(tipoAutorizacion);
			model.addAttribute("autorizadoPersonaNatural", autorizadoPersonaNatural);
			return Constantes.OP_AUTORIZADOS_PER_NATURAL;
		case 2:
			final AutorizadoPersonaJuridica autorizadoPersonaJuridica = new AutorizadoPersonaJuridica();
			autorizadoPersonaJuridica.setIdTipoAutorizado(tipoAutorizacion);
			model.addAttribute("autorizadoPersonaJuridica", autorizadoPersonaJuridica);
			return Constantes.OP_AUTORIZADOS_PER_JURIDICA;
		default:
			return "redirect:autorizados";
		}
	}

	@PostMapping("agregarAutorizadoBeneficiarioTraspaso")
	public String agregarAutorizadoBeneficioTraspaso(@Valid AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso,
			BindingResult result, Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		validarAutorizado(autorizadoBeneficioTraspaso, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, true);
			model.addAttribute("autorizadoBeneficioTraspaso", autorizadoBeneficioTraspaso);
			return Constantes.OP_AUTORIZADOS_BENEF_TRASPASO;
		}

		final Autorizado autorizado = convertirAutorizadoBeneficioTraspasoToAutorizado(autorizadoBeneficioTraspaso);
		autorizado.setIdEmpresa(usuario.getIdEmpresa());
		autorizado.setEstado("A");
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		if (autorizadoBeneficioTraspaso.getIdAutorizado() == null) {
			autorizado.setFechaCreacion(ts);
		} else {
			final Autorizado autorizadoToUpdate = autorizadoRepository
					.findById(autorizadoBeneficioTraspaso.getIdAutorizado()).get();
			autorizado.setFechaCreacion(autorizadoToUpdate.getFechaCreacion());
		}

		autorizado.setFechaActualizacion(ts);
		autorizadoRepository.save(autorizado);

		if (autorizadoBeneficioTraspaso.getIdAutorizado() == null) {
			String detalle = MessageFormat.format(Constantes.ACCION_CREAR_AUTORIZADO, "Beneficiario Traspaso",
					Constantes.OP_CREAR, usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.CREAR_AUTORIZADO, detalle, Constantes.BENEFICIARIO_TRASPASO, true,
					Util.getRemoteIp(request), usuario);
		} else {
			String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO, "Beneficiario Traspaso",
					autorizadoBeneficioTraspaso.getIdAutorizado(), Constantes.OP_EDICION, usuario.getIdUsuario(),
					usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.EDITAR_AUTORIZADO, detalle, Constantes.BENEFICIARIO_TRASPASO, true,
					Util.getRemoteIp(request), usuario);
		}

		return "redirect:autorizadosListar?success";
	}

	@PostMapping("agregarAutorizadoEmpresaTransporte")
	public String agregarAutorizadoEmpresaTransporte(@Valid AutorizadoEmpresaTransporte autorizadoEmpresaTransporte,
			BindingResult result, Model model) throws Exception {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		autorizadoEmpresaTransporte.setIdEmpresa(usuario.getIdEmpresa());
		validarAutorizado(autorizadoEmpresaTransporte, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, true);
			model.addAttribute("autorizadoEmpresaTransporte", autorizadoEmpresaTransporte);
			return Constantes.OP_AUTORIZADOS_EMP_TRANSPORTE;
		}

		final Autorizado autorizado = convertirAutorizadoEmpresaTransporteToAutorizado(autorizadoEmpresaTransporte);
		autorizado.setEstado(Constantes.ACTIVO);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		if (autorizadoEmpresaTransporte.getIdAutorizado() == null) {
			autorizado.setFechaCreacion(ts);
		} else {
			final Autorizado autorizadoToUpdate = autorizadoRepository
					.findById(autorizadoEmpresaTransporte.getIdAutorizado()).get();
			autorizado.setFechaCreacion(autorizadoToUpdate.getFechaCreacion());
		}
		autorizado.setFechaActualizacion(ts);
		autorizadoRepository.save(autorizado);

		if (autorizadoEmpresaTransporte.getIdAutorizado() == null) {
			String detalle = MessageFormat.format(Constantes.ACCION_CREAR_AUTORIZADO, "Empresa de Transporte",
					Constantes.OP_CREAR, usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.CREAR_AUTORIZADO, detalle, Constantes.EMPRESA_TRANSPORTE, true,
					Util.getRemoteIp(request), usuario);
		} else {
			String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO, "Empresa de Transporte",
					autorizadoEmpresaTransporte.getIdAutorizado(), Constantes.OP_EDICION, usuario.getIdUsuario(),
					usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.EDITAR_AUTORIZADO, detalle, Constantes.EMPRESA_TRANSPORTE, true,
					Util.getRemoteIp(request), usuario);
		}

		return "redirect:autorizadosListar?success";
	}

	@PostMapping("agregarAutorizadoPersonaNatural")
	public String agregarAutorizadoPersonaNatural(@Valid AutorizadoPersonaNatural autorizadoPersonaNatural,
			BindingResult result, Model model) throws Exception {

		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		validarAutorizadoPersonaNatural(autorizadoPersonaNatural, result, usuario.getIdEmpresa());
		if (result.hasErrors()) {
			if (autorizadoPersonaNatural.getIdAutorizado() == null) {
				model.addAttribute(Constantes.CREAR, true);
			} else {
				model.addAttribute(Constantes.CREAR, false);
			}

			model.addAttribute("autorizadoPersonaNatural", autorizadoPersonaNatural);
			if (autorizadoPersonaNatural.getImgDocumentoAutorizado() != null
					&& !autorizadoPersonaNatural.getImgDocumentoAutorizado().isEmpty()) {
				autorizadoPersonaNatural.setImagenDocumento(autorizadoPersonaNatural.getImgDocumentoAutorizado());
				autorizadoPersonaNatural.setDocumentoImg(new MockMultipartFile("documentoImg",
						Base64.getDecoder().decode(autorizadoPersonaNatural.getImgDocumentoAutorizado().getBytes())));
			}
			return Constantes.OP_AUTORIZADOS_PER_NATURAL;

		}

		Autorizado autorizadoToUpdate = new Autorizado();
		if (autorizadoPersonaNatural.getIdAutorizado() != null) {
			autorizadoToUpdate = autorizadoRepository.findById(autorizadoPersonaNatural.getIdAutorizado()).get();
		}

		final Autorizado autorizado = convertirAutorizadoPersonaNaturalToAutorizado(autorizadoPersonaNatural,
				autorizadoToUpdate);
		autorizado.setIdEmpresa(usuario.getIdEmpresa());
		autorizado.setEstado(Constantes.ACTIVO);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		if (autorizadoPersonaNatural.getIdAutorizado() == null) {
			autorizado.setFechaCreacion(ts);
		} else {
			autorizado.setFechaCreacion(autorizadoToUpdate.getFechaCreacion());
		}

		try {
			autorizado.setFechaActualizacion(ts);

			autorizadoRepository.save(autorizado);
		} catch (Exception e) {

		}

		if (autorizadoPersonaNatural.getIdAutorizado() == null) {
			final String detalle = MessageFormat.format(Constantes.ACCION_CREAR_AUTORIZADO,
					"Aurotizado Persona Natural", Constantes.OP_CREAR, usuario.getIdUsuario(),
					usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.CREAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_NATURAL, true,
					Util.getRemoteIp(request), usuario);
		} else {
			final String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO,
					"Aurotizado Persona Natural", autorizadoPersonaNatural.getIdAutorizado(), Constantes.OP_EDICION,
					usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.EDITAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_NATURAL, true,
					Util.getRemoteIp(request), usuario);
		}

		return "redirect:autorizadosListar?success";
	}

	@PostMapping("agregarAutorizadoPersonaJuridica")
	public String agregarAutorizadoPersonaJuridica(@Valid AutorizadoPersonaJuridica autorizadoPersonaJuridica,
			BindingResult result, Model model, @RequestParam("crear") Boolean accionCrear) throws Exception {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));

		validarAutorizadoPersonaJuridica(autorizadoPersonaJuridica, result, accionCrear, usuario.getIdEmpresa());
		if (result.hasErrors()) {

			model.addAttribute(Constantes.CREAR, accionCrear);

			if (autorizadoPersonaJuridica.getIdAutorizado() == null) {
				model.addAttribute(Constantes.CREAR, true);
			} else {
				model.addAttribute(Constantes.CREAR, false);
			}
			model.addAttribute("autorizadoPersonaJuridica", autorizadoPersonaJuridica);
			if (autorizadoPersonaJuridica.getImgDocumentoAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoAutorizado().isEmpty()) {
				autorizadoPersonaJuridica.setImagenDocumento(autorizadoPersonaJuridica.getImgDocumentoAutorizado());
				autorizadoPersonaJuridica.setDocumentoImg(new MockMultipartFile("documentoImg",
						Base64.getDecoder().decode(autorizadoPersonaJuridica.getImgDocumentoAutorizado().getBytes())));
			}
			if (autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().isEmpty()) {
				autorizadoPersonaJuridica
						.setImagenDocumentoAdicional(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado());
				autorizadoPersonaJuridica
						.setDocumentoAdicionalImg(new MockMultipartFile("documentoAdicionalImg", Base64.getDecoder()
								.decode(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().getBytes())));

			}
			if (autorizadoPersonaJuridica.getImgDocumentoRifAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().isEmpty()) {
				autorizadoPersonaJuridica
						.setImagenDocumentoRif(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado());
				autorizadoPersonaJuridica.setDocumentoRifImg(new MockMultipartFile("documentoRifImg", Base64
						.getDecoder().decode(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().getBytes())));
			}

			return Constantes.OP_AUTORIZADOS_PER_JURIDICA;
		}

		Autorizado autorizadoToUpdate = new Autorizado();
		if (autorizadoPersonaJuridica.getIdAutorizado() != null) {
			autorizadoToUpdate = autorizadoRepository.findById(autorizadoPersonaJuridica.getIdAutorizado()).get();
		}

		final Autorizado autorizado = convertirAutorizadoPersonaJuridicaToAutorizado(autorizadoPersonaJuridica,
				accionCrear, autorizadoToUpdate);
		autorizado.setIdEmpresa(usuario.getIdEmpresa());
		autorizado.setEstado(Constantes.ACTIVO);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		if (autorizadoPersonaJuridica.getIdAutorizado() == null) {
			autorizado.setFechaCreacion(ts);

		} else {
			autorizado.setFechaCreacion(autorizadoToUpdate.getFechaCreacion());
		}

		autorizado.setFechaActualizacion(ts);
		autorizadoRepository.save(autorizado);

		if (autorizadoPersonaJuridica.getIdAutorizado() == null) {
			String detalle = MessageFormat.format(Constantes.ACCION_CREAR_AUTORIZADO, "Aurotizado Persona Juridica",
					Constantes.OP_CREAR, usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.CREAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_JURIDICA, true,
					Util.getRemoteIp(request), usuario);
		} else {
			String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO, "Aurotizado Persona Juridica",
					autorizadoPersonaJuridica.getIdAutorizado(), Constantes.OP_EDICION, usuario.getIdUsuario(),
					usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.EDITAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_JURIDICA, true,
					Util.getRemoteIp(request), usuario);
		}

		return "redirect:autorizadosListar?success";
	}

	@PostMapping("autorizadoEditar")
	public String autorizadoEditar(@RequestParam("idAutorizado") int id, Model model) {
		Autorizado autorizado = autorizadoRepository.findById(id);
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		if (autorizado == null) {
			return "redirect:autorizadosListar?error";
		} else {
			final int tipoAutorizacion = autorizado.getIdTipoAutorizado();
			model.addAttribute("tipoAutorizacion", tipoAutorizacion);
			model.addAttribute(Constantes.CREAR, false);
			switch (tipoAutorizacion) {
			case 3:
				final AutorizadoEmpresaTransporte autorizadoEmpresaTransporte = AutorizadoUtils
						.convertirAutorizadoToAutorizadoEmpresaTransporte(autorizado);
				model.addAttribute("autorizadoEmpresaTransporte", autorizadoEmpresaTransporte);
				return Constantes.OP_AUTORIZADOS_EMP_TRANSPORTE;
			case 4:
				final AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso = AutorizadoUtils
						.convertirAutorizadoToAutorizadoBeneficioTraspaso(autorizado);
				model.addAttribute("autorizadoBeneficioTraspaso", autorizadoBeneficioTraspaso);
				return Constantes.OP_AUTORIZADOS_BENEF_TRASPASO;
			case 1:
				final AutorizadoPersonaNatural autorizadoPersonaNatural = AutorizadoUtils
						.convertirAutorizadoToAutorizadoPersonaNatural(autorizado);
				try {
					String imagenDocumento = Util.obtenerArchivoStr(rutaImg + autorizado.getImagenDocumento(),
							"documentoImg");
					autorizadoPersonaNatural.setImagenDocumento(imagenDocumento);
					autorizadoPersonaNatural.setImgDocumentoAutorizado(imagenDocumento);
				} catch (IOException e) {

				}
				model.addAttribute("autorizadoPersonaNatural", autorizadoPersonaNatural);
				return Constantes.OP_AUTORIZADOS_PER_NATURAL;
			case 2:
				final AutorizadoPersonaJuridica autorizadoPersonaJuridica = AutorizadoUtils
						.convertirAutorizadoToAutorizadoPersonaJuridica(autorizado);
				try {
					if (autorizado.getImagenDocumento() != null && !autorizado.getImagenDocumento().isEmpty()) {
						String imagenDocumento = Util.obtenerArchivoStr(rutaImg + autorizado.getImagenDocumento(),
								"documentoImg");
						autorizadoPersonaJuridica.setImagenDocumento(imagenDocumento);
						autorizadoPersonaJuridica.setImgDocumentoAutorizado(imagenDocumento);
					}
					if (autorizado.getImagenRif() != null && !autorizado.getImagenRif().isEmpty()) {
						String imagenRif = Util.obtenerArchivoStr(rutaImg + autorizado.getImagenRif(),
								"documentoRifImg");
						autorizadoPersonaJuridica.setImagenDocumentoRif(imagenRif);
						autorizadoPersonaJuridica.setImgDocumentoRifAutorizado(imagenRif);

					}
					if (autorizado.getImagenAdicional() != null && !autorizado.getImagenAdicional().isEmpty()) {
						String imagenAdicional = Util.obtenerArchivoStr(rutaImg + autorizado.getImagenAdicional(),
								"documentoAdicionalImg");
						autorizadoPersonaJuridica.setImagenDocumentoAdicional(imagenAdicional);
						autorizadoPersonaJuridica.setImgDocumentoAdicionalAutorizado(imagenAdicional);
					}
				} catch (IOException e) {

				}
				model.addAttribute("autorizadoPersonaJuridica", autorizadoPersonaJuridica);
				return Constantes.OP_AUTORIZADOS_PER_JURIDICA;
			default:
				return "redirect:autorizados";
			}
		}
	}

	@PostMapping("autorizadoEliminar")
	public String autorizadoEliminar(@RequestParam("idAutorizado") int id, Model model,
			RedirectAttributes redirectAttributes) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		Autorizado autorizadoAEliminar = autorizadoRepository.findById(id);
		if (autorizadoAEliminar == null) {
			return "redirect:autorizados?error";

		} else {
			final boolean tieneSolicitudActiva = tieneSolicitudActiva(id);
			if (tieneSolicitudActiva) {
				redirectAttributes.addFlashAttribute("solicitudActiva", "solicitudActiva");
				return "redirect:autorizados";
			}

			autorizadoAEliminar.setEstado("I");
			autorizadoRepository.save(autorizadoAEliminar);
			String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO, "Autorizado Persona Juridica",
					autorizadoAEliminar.getIdAutorizado(), autorizadoAEliminar.getIdTipoAutorizado(),
					usuario.getIdUsuario(), usuario.getNombreUsuario());
			logServ.registrarLog(Constantes.ELIMINAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_JURIDICA, true,
					Util.getRemoteIp(request), usuario);
			return "redirect:autorizados?success";
		}
	}

	@GetMapping("/autorizadosListar")
	public String autorizadosListar(Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		final List<Autorizado> autorizados = autorizadoRepository.findByIdEmpresaAndEstado(usuario.getIdEmpresa(),
				Constantes.ACTIVO);
		final List<AutorizadoModel> listaAutorizados = new ArrayList<AutorizadoModel>();
		autorizados.stream()
				.forEach(autorizado -> listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getTipoAutorizado().getTipoAutorizado(), autorizado.getRifEmpresa(),
						autorizado.getNombreEmpresa(), autorizado.getDocumentoIdentidad(),
						autorizado.getNombreCompleto())));
		model.addAttribute("autorizados", listaAutorizados);

		return Constantes.OP_AUTORIZADOS_LISTA;
	}

	private void validarAutorizado(AutorizadoBanco autorizadoBanco, BindingResult result) throws Exception {
		if (autorizadoBanco.getCaracterRifAutorizado() != null && !autorizadoBanco.getRifAutorizado().isEmpty()) {
			if (!autorizadoBanco.getRifAutorizado().matches("[0-9]{9}")) {
				result.rejectValue("rifAutorizado", "", "debe tener 9 digitos");
			} else if (!ValidationUtils
					.validarRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado())) {
				result.rejectValue("rifAutorizado", "", "inválido");
			}
		}

	}

	private void validarAutorizado(AutorizadoEmpresaTransporte autorizadoBanco, BindingResult result) throws Exception {
		if (autorizadoBanco.getCaracterRifAutorizado() != null && !autorizadoBanco.getRifAutorizado().isEmpty()) {
			if (!autorizadoBanco.getRifAutorizado().matches("[0-9]{9}")) {
				result.rejectValue("rifAutorizado", "", "debe tener 9 digitos");
			} else if (!ValidationUtils
					.validarRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado())) {
				result.rejectValue("rifAutorizado", "", "inválido");
			}
			List<Transportista> transportistas = transportistaRepository
					.findByRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado());
			if (transportistas == null || transportistas.isEmpty()) {
				result.rejectValue("rifAutorizado", "", "Transportista no autorizado por el banco");
			}
			// buscar por tipoAutorizado, idEmpresa, id_transportista, estado
			if (transportistas != null && transportistas.size() > 0) {
				autorizadoBanco.setIdTransportista(transportistas.get(0).getIdTransportista());
				Autorizado au = autorizadoRepository.findByIdTipoAutorizadoAndIdEmpresaAndIdTransportistaAndEstado(
						autorizadoBanco.getIdTipoAutorizado(), autorizadoBanco.getIdEmpresa(),
						transportistas.get(0).getIdTransportista(), "A");
				if (au != null) {
					result.rejectValue("rifAutorizado", "", "Ya existe este autorizado");
				}
			}
		}
	}

	private void validarAutorizadoPersonaNatural(AutorizadoPersonaNatural autorizado, BindingResult result,
			int idEmpresa) throws Exception {
		if (autorizado.getImgDocumentoAutorizado().isEmpty()
				|| (autorizado.getDocumentoImg() != null && autorizado.getDocumentoImg().getBytes().length > 0)) {
			if (autorizado.getDocumentoImg() == null || autorizado.getDocumentoImg().getBytes().length == 0) {
				result.rejectValue("documentoImg", "", "requerido");
			} else {
				if (!ValidationUtils.isValidImageType(autorizado.getDocumentoImg())) {
					result.rejectValue("documentoImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
				} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoImg())) {
					result.rejectValue("documentoImg", "", "no puede ser mayor a 1 MB");
				}
			}
		}
		if (!NumberUtils.isDigits(autorizado.getDocumentoAutorizado())) {
			result.rejectValue("documentoAutorizado", "", "debe contener solo números");
		}

		if (autorizado.getTelefono() != null && !autorizado.getTelefono().isEmpty()) {
			if (!autorizado.getTelefono().matches("[0-9]{7}")) {
				result.rejectValue("telefono", "", "debe tener 7 digitos");
			}
		}

		if (autorizado.getEmail() != null && !autorizado.getEmail().isEmpty()
				&& !ValidationUtils.isValidEmail(autorizado.getEmail())) {
			result.rejectValue("email", "", "inválido");
		}
	}

	private void validarAutorizadoPersonaJuridica(AutorizadoPersonaJuridica autorizado, BindingResult result,
			boolean creando, int idEmpresa) throws Exception {
		if (autorizado.getTelefono() != null && !autorizado.getTelefono().isEmpty()) {
			if (!autorizado.getTelefono().matches("[0-9]{7}")) {
				result.rejectValue("telefono", "", "debe tener 7 digitos");
			}
		}

		if (autorizado.getCaracterRifEmpresa() != null && !autorizado.getRifEmpresa().isEmpty()) {
			if (!autorizado.getRifEmpresa().matches("[0-9]{9}")) {
				result.rejectValue("rifEmpresa", "", "debe tener 9 digitos");
			} else if (!ValidationUtils.validarRif(autorizado.getCaracterRifEmpresa() + autorizado.getRifEmpresa())) {
				result.rejectValue("rifEmpresa", "", "inválido");
			}
		}
		
		if (autorizado.getDocumentoAutorizado().isEmpty()
				|| !NumberUtils.isDigits(autorizado.getDocumentoAutorizado())) {
			result.rejectValue("documentoAutorizado", "", "debe contener solo numeros");
		}
		
		if (autorizado.getImgDocumentoAutorizado().isEmpty()
				|| (autorizado.getDocumentoImg() != null && autorizado.getDocumentoImg().getBytes().length > 0)) {
			if (autorizado.getDocumentoImg() == null || autorizado.getDocumentoImg().getBytes().length == 0) {
				result.rejectValue("documentoImg", "", "requerido");
			} else {
				if (!ValidationUtils.isValidImageType(autorizado.getDocumentoImg())) {
					result.rejectValue("documentoImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
				} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoImg())) {
					result.rejectValue("documentoImg", "", "no puede ser mayor a 1 MB");
				}
			}
		}

		if (autorizado.getImgDocumentoAdicionalAutorizado().isEmpty() || (autorizado.getDocumentoAdicionalImg() != null
				&& autorizado.getDocumentoAdicionalImg().getBytes().length > 0)) {
			if ((autorizado.getDocumentoAdicionalImg() != null
					&& autorizado.getDocumentoAdicionalImg().getBytes().length > 0)) {
				if (!ValidationUtils.isValidImageType(autorizado.getDocumentoAdicionalImg())) {
					result.rejectValue("documentoAdicionalImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
				} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoAdicionalImg())) {
					result.rejectValue("documentoAdicionalImg", "", "no puede ser mayor a 1 MB");
				}
			}
		}

		if (autorizado.getImgDocumentoRifAutorizado().isEmpty()
				|| (autorizado.getDocumentoRifImg() != null && autorizado.getDocumentoRifImg().getBytes().length > 0)) {
			if (autorizado.getDocumentoRifImg() == null || autorizado.getDocumentoRifImg().getBytes().length == 0) {
				result.rejectValue("documentoRifImg", "", "requerido");
			} else {
				if (!ValidationUtils.isValidImageType(autorizado.getDocumentoRifImg())) {
					result.rejectValue("documentoRifImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
				} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoRifImg())) {
					result.rejectValue("documentoRifImg", "", "no puede ser mayor a 1 MB");
				}
			}
		}
		if (creando) {
			Autorizado a = autorizadoRepository
					.findByIdEmpresaAndDocumentoIdentidadAndRifEmpresaAndIdTipoAutorizadoAndEstado(idEmpresa,
							autorizado.getTipoDocumentoAutorizado() + autorizado.getDocumentoAutorizado(),
							autorizado.getCaracterRifEmpresa() + autorizado.getRifEmpresa(),
							autorizado.getIdTipoAutorizado(), Constantes.ACTIVO);
			if (a != null) {
				result.rejectValue("documentoAutorizado", "", "Ya existe esta combinación para un autorizado");
				result.rejectValue("rifEmpresa", "", "Ya existe esta combinación para un autorizado");
			}
		}
	}

	private Autorizado convertirAutorizadoBeneficioTraspasoToAutorizado(AutorizadoBeneficioTraspaso solictudTraspaso)
			throws IOException {
		final Autorizado autorizado = new Autorizado();

		if (solictudTraspaso.getIdAutorizado() != null)
			autorizado.setIdAutorizado(solictudTraspaso.getIdAutorizado());

		autorizado.setRifEmpresa(solictudTraspaso.getCaracterRifAutorizado() + solictudTraspaso.getRifAutorizado());
		autorizado.setNombreEmpresa(solictudTraspaso.getNombreEmpresa());
		autorizado.setIdTipoAutorizado(solictudTraspaso.getIdTipoAutorizado());
		return autorizado;
	}

	private Autorizado convertirAutorizadoEmpresaTransporteToAutorizado(
			AutorizadoEmpresaTransporte autorizadoEmpresaTransporte) throws IOException {
		final Autorizado autorizado = new Autorizado();

		if (autorizadoEmpresaTransporte.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoEmpresaTransporte.getIdAutorizado());
		autorizado.setIdTipoAutorizado(autorizadoEmpresaTransporte.getIdTipoAutorizado());
		autorizado.setIdEmpresa(autorizadoEmpresaTransporte.getIdEmpresa());
		autorizado.setIdTransportista(autorizadoEmpresaTransporte.getIdTransportista());
		// autorizado.setIdTransportista(autorizadoEmpresaTransporte.get);
		// autorizado.setRifEmpresa(autorizadoEmpresaTransporte.getCaracterRifAutorizado()
		// + autorizadoEmpresaTransporte.getRifAutorizado());
		// autorizado.setNombreEmpresa(autorizadoEmpresaTransporte.getNombreAutorizado());

		return autorizado;
	}

	private Autorizado convertirAutorizadoPersonaNaturalToAutorizado(AutorizadoPersonaNatural autorizadoPersonaNatural,
			Autorizado autorizadoToUpdate) throws Exception {
		final Autorizado autorizado = new Autorizado();
		BufferedImage imgBuffered = null;
		Tika defaultTika = new Tika();
		if (autorizadoPersonaNatural.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoPersonaNatural.getIdAutorizado());

		autorizado.setDocumentoIdentidad(autorizadoPersonaNatural.getTipoDocumentoAutorizado()
				+ autorizadoPersonaNatural.getDocumentoAutorizado());
		autorizado.setNombreCompleto(autorizadoPersonaNatural.getNombreAutorizado());
		autorizado.setIdTipoAutorizado(autorizadoPersonaNatural.getIdTipoAutorizado());
		autorizado.setTelefonoMovil(
				autorizadoPersonaNatural.getPrefijoTelefono() + autorizadoPersonaNatural.getTelefono());
		autorizado.setEmail(autorizadoPersonaNatural.getEmail());
		autorizado.setMarcaModeloColorVehiculo(autorizadoPersonaNatural.getMarcaModeloColor());
		autorizado.setPlacaVehiculo(autorizadoPersonaNatural.getPlaca());

		String imagenDocumento = autorizadoToUpdate.getImagenDocumento() != null
				? Util.obtenerArchivoStr(rutaImg + autorizadoToUpdate.getImagenDocumento(), "documentoImg")
				: null;
		if (autorizadoPersonaNatural.getImgDocumentoAutorizado() != null
				&& !autorizadoPersonaNatural.getImgDocumentoAutorizado().equals(imagenDocumento)) {
			if (autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename() != null
					&& !autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename().isEmpty()) {
				imgBuffered = Util.convertFile(autorizadoPersonaNatural.getImgDocumentoAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaNatural.getDocumentoImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenDocumento(Util.saveFile(imgBuffered, 'D', extension, rutaImg));
			}
		} else if (imagenDocumento != null) {
			autorizado.setImagenDocumento(autorizadoToUpdate.getImagenDocumento());
		}
		return autorizado;
	}

	private Autorizado convertirAutorizadoPersonaJuridicaToAutorizado(
			AutorizadoPersonaJuridica autorizadoPersonaJuridica, boolean creando, Autorizado autorizadoToUpdate)
			throws Exception {
		final Autorizado autorizado = new Autorizado();
		BufferedImage imgBuffered = null;
		if (autorizadoPersonaJuridica.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoPersonaJuridica.getIdAutorizado());

		autorizado.setDocumentoIdentidad(autorizadoPersonaJuridica.getTipoDocumentoAutorizado()
				+ autorizadoPersonaJuridica.getDocumentoAutorizado());
		autorizado.setNombreCompleto(autorizadoPersonaJuridica.getNombreAutorizado());
		autorizado.setRifEmpresa(
				autorizadoPersonaJuridica.getCaracterRifEmpresa() + autorizadoPersonaJuridica.getRifEmpresa());
		autorizado.setNombreEmpresa(autorizadoPersonaJuridica.getNombreEmpresa());
		autorizado.setIdTipoAutorizado(autorizadoPersonaJuridica.getIdTipoAutorizado());
		autorizado.setTelefonoMovil(
				autorizadoPersonaJuridica.getPrefijoTelefono() + autorizadoPersonaJuridica.getTelefono());
		autorizado.setEmail(autorizadoPersonaJuridica.getEmail());
		autorizado.setMarcaModeloColorVehiculo(autorizadoPersonaJuridica.getMarcaModeloColor());
		autorizado.setPlacaVehiculo(autorizadoPersonaJuridica.getPlaca());
		autorizado.setCargoEmpresa(autorizadoPersonaJuridica.getCargo());

		Autorizado a;
		Tika defaultTika = new Tika();
		
		String imagenDocumento = autorizadoToUpdate.getImagenDocumento() != null
				? Util.obtenerArchivoStr(rutaImg + autorizadoToUpdate.getImagenDocumento(), "documentoImg")
				: null;
		if (autorizadoPersonaJuridica.getImgDocumentoAutorizado() != null
				&& !autorizadoPersonaJuridica.getImgDocumentoAutorizado().equals(imagenDocumento)) {
			if (autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename().isEmpty()) {
				imgBuffered = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenDocumento(Util.saveFile(imgBuffered, 'D', extension, rutaImg));
			}
		} else if (imagenDocumento != null) {
			autorizado.setImagenDocumento(autorizadoToUpdate.getImagenDocumento());
		}
		
		String imagenRif = autorizadoToUpdate.getImagenRif() != null
				? Util.obtenerArchivoStr(rutaImg + autorizadoToUpdate.getImagenRif(), "documentoRifImg")
				: null;
		if (autorizadoPersonaJuridica.getImgDocumentoRifAutorizado() != null
				&& !autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().equals(imagenRif)) {
			if (autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename().isEmpty()) {
				imgBuffered = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoRifImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenRif(Util.saveFile(imgBuffered, 'D', extension, rutaImg));
			}
		} else if (imagenRif != null) {
			autorizado.setImagenRif(autorizadoToUpdate.getImagenRif());
		}
		
		String imagenAdicional = autorizadoToUpdate.getImagenAdicional() != null
				? Util.obtenerArchivoStr(rutaImg + autorizadoToUpdate.getImagenAdicional(), "documentoAdicionalImg")
				: null;
		if (autorizadoPersonaJuridica.getImgDocumentoRifAutorizado() != null
				&& !autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().equals(imagenAdicional)) {
			if (autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename().isEmpty()) {
				imgBuffered = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoAdicionalImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenAdicional(Util.saveFile(imgBuffered, 'D', extension, rutaImg));
			}
		} else if (imagenAdicional != null) {
			autorizado.setImagenAdicional(autorizadoToUpdate.getImagenAdicional());
		}

		return autorizado;
	}

	private boolean tieneSolicitudActiva(Integer idAutotirado) {
		final List<SolicitudRetiro> solicitudes = solicitudRetiroRepo.findByIdAutorizado(idAutotirado);
		final List<Integer> solicitudesIds = solicitudes.stream().map(SolicitudRetiro::getIdSolicitud)
				.collect(Collectors.toList());
		final List<SolicitudRetiroTraza> solicitudTrazas = solicitudRetiroTrazaRepo.findByIdSolicitudIn(solicitudesIds);
		final boolean tieneSolicitudActiva = solicitudesIds.stream()
				.anyMatch((idSolicitud) -> ESTATUS_SOLICITUD_ACTIVA
						.contains(solicitudTrazas.stream().filter((traza) -> traza.getIdSolicitud() == idSolicitud)
								.sorted(Comparator.comparing(SolicitudRetiroTraza::getIdSolicitud).reversed())
								.findFirst().get().getIdEstatusSolicitud()));

		return tieneSolicitudActiva;
	}

}