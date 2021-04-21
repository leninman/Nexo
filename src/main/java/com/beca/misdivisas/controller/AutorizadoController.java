package com.beca.misdivisas.controller;

import java.awt.image.BufferedImage;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Base64;
import java.util.Date;
import java.util.Iterator;
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

		final List<Autorizado> autorizados = autorizadoRepository.findByIdEmpresaAndEstadoOrderByIdTipoAutorizadoAscDocumentoIdentidadAsc(usuario.getIdEmpresa(), "A");
		final List<AutorizadoModel> listaAutorizados = new ArrayList<AutorizadoModel>();
		
		for (Iterator<Autorizado> iterator = autorizados.iterator(); iterator.hasNext();) {
			Autorizado autorizado = (Autorizado) iterator.next();
			if(autorizado.getTipoAutorizado().getIdTipoAutorizado().equals(3)) {
				Optional<Transportista> transportista = transportistaRepository.findById(autorizado.getIdTransportista());		
				listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getTipoAutorizado().getTipoAutorizado(), transportista.get().getRif(),
						transportista.get().getTransportista(), autorizado.getDocumentoIdentidad(),
						autorizado.getNombreCompleto()));
			}
			else {
			listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
					autorizado.getTipoAutorizado().getTipoAutorizado(), autorizado.getRifEmpresa(),
					autorizado.getNombreEmpresa(), autorizado.getDocumentoIdentidad(),
					autorizado.getNombreCompleto()));
			}
			autorizado = null;
		}

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
		/*case 4:
			final AutorizadoBeneficioTraspaso autorizadoBeneficioTraspaso = new AutorizadoBeneficioTraspaso();
			autorizadoBeneficioTraspaso.setIdTipoAutorizado(tipoAutorizacion);
			model.addAttribute("autorizadoBeneficioTraspaso", autorizadoBeneficioTraspaso);
			return Constantes.OP_AUTORIZADOS_BENEF_TRASPASO;*/
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

	/*
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
	}*/

	@PostMapping("agregarAutorizadoEmpresaTransporte")
	public String agregarAutorizadoEmpresaTransporte(@Valid AutorizadoEmpresaTransporte autorizadoEmpresaTransporte,
			BindingResult result, Model model, @RequestParam("crear") Boolean accionCrear) throws Exception {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		autorizadoEmpresaTransporte.setIdEmpresa(usuario.getIdEmpresa());
		validarAutorizado(autorizadoEmpresaTransporte, result, accionCrear);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.CREAR, accionCrear);
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
		autorizado.setRifEmpresa(null);
		autorizado.setNombreEmpresa(null);
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
			BindingResult result, Model model, @RequestParam("crear") Boolean accionCrear) throws Exception {

		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.CREAR, accionCrear);
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		validarAutorizadoPersonaNatural(autorizadoPersonaNatural, result, accionCrear, usuario.getIdEmpresa());
		if (result.hasErrors()) {
			model.addAttribute("autorizadoPersonaNatural", autorizadoPersonaNatural);
			if (autorizadoPersonaNatural.getImgDocumentoAutorizado() != null
					&& !autorizadoPersonaNatural.getImgDocumentoAutorizado().isEmpty()) {
				autorizadoPersonaNatural.setImagenDocumento(autorizadoPersonaNatural.getImgDocumentoAutorizado());
				autorizadoPersonaNatural.setDocumentoImg(new MockMultipartFile("documentoImg",
						Base64.getDecoder().decode(autorizadoPersonaNatural.getImgDocumentoAutorizado().getBytes())));
			}else {
				if(!accionCrear) {
					Autorizado a = autorizadoRepository.findById(autorizadoPersonaNatural.getIdAutorizado().intValue());					
						try {
							if(a!= null)
								if(a.getImagenDocumento()!=null && !a.getImagenDocumento().isEmpty())
									autorizadoPersonaNatural.setImagenDocumento(Util.obtenerArchivoStr(rutaImg+a.getImagenDocumento(), "documentoImg"));
						} catch (Exception e) {
							return "redirect:"+Constantes.ERROR;
						}
				}
			}

			return Constantes.OP_AUTORIZADOS_PER_NATURAL;
		}

		try {
			Autorizado autorizadoToUpdate = new Autorizado();
			if (autorizadoPersonaNatural.getIdAutorizado() != null) {
				autorizadoToUpdate = autorizadoRepository.findById(autorizadoPersonaNatural.getIdAutorizado()).get();
			}

			final Autorizado autorizado = convertirAutorizadoPersonaNaturalToAutorizado(autorizadoPersonaNatural, autorizadoToUpdate);
			autorizado.setIdEmpresa(usuario.getIdEmpresa());
			autorizado.setEstado(Constantes.ACTIVO);
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);

			if (accionCrear) {
				autorizado.setFechaCreacion(ts);
			} else {
				autorizado.setFechaCreacion(autorizadoToUpdate.getFechaCreacion());
			}

			try {
				autorizado.setFechaActualizacion(ts);
				autorizadoRepository.save(autorizado);
			} catch (Exception e) {
				return Constantes.ERRORES;
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
		} catch (Exception e) {
			return Constantes.ERRORES;
		}

		return "redirect:autorizadosListar?success";
	}

	@PostMapping("agregarAutorizadoPersonaJuridica")
	public String agregarAutorizadoPersonaJuridica(@Valid AutorizadoPersonaJuridica autorizadoPersonaJuridica,
			BindingResult result, Model model, @RequestParam("crear") Boolean accionCrear) throws Exception {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.CREAR, accionCrear);
		validarAutorizadoPersonaJuridica(autorizadoPersonaJuridica, result, accionCrear, usuario.getIdEmpresa());

		if (result.hasErrors()) {
			Autorizado a = null;
			model.addAttribute("autorizadoPersonaJuridica", autorizadoPersonaJuridica);
			if(!accionCrear)
				a = autorizadoRepository.findById(autorizadoPersonaJuridica.getIdAutorizado().intValue());
			if (autorizadoPersonaJuridica.getImgDocumentoAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoAutorizado().isEmpty()) {
				autorizadoPersonaJuridica.setImagenDocumento(autorizadoPersonaJuridica.getImgDocumentoAutorizado());
				autorizadoPersonaJuridica.setDocumentoImg(new MockMultipartFile("documentoImg",
						Base64.getDecoder().decode(autorizadoPersonaJuridica.getImgDocumentoAutorizado().getBytes())));
			}else if(!accionCrear) {									
				try {
					if(a!= null)
						if(a.getImagenDocumento()!=null && !a.getImagenDocumento().isEmpty())
							autorizadoPersonaJuridica.setImagenDocumento(Util.obtenerArchivoStr(rutaImg+a.getImagenDocumento(), "documentoImg"));
				} catch (Exception e) {					
				}
			}
			if (autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().isEmpty()) {
				autorizadoPersonaJuridica
						.setImagenDocumentoAdicional(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado());
				autorizadoPersonaJuridica
						.setDocumentoAdicionalImg(new MockMultipartFile("documentoAdicionalImg", Base64.getDecoder()
								.decode(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().getBytes())));

			}else if(!accionCrear) {									
				try {
					if(a!= null)
						if(a.getImagenAdicional()!=null && !a.getImagenAdicional().isEmpty())
							autorizadoPersonaJuridica.setImagenDocumentoAdicional(Util.obtenerArchivoStr(rutaImg+a.getImagenAdicional(), "documentoAdicionalImg"));
				} catch (Exception e) {					
				}
			}
			if (autorizadoPersonaJuridica.getImgDocumentoRifAutorizado() != null
					&& !autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().isEmpty()) {
				autorizadoPersonaJuridica
						.setImagenDocumentoRif(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado());
				autorizadoPersonaJuridica.setDocumentoRifImg(new MockMultipartFile("documentoRifImg", Base64
						.getDecoder().decode(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().getBytes())));
			}else if(!accionCrear) {									
				try {
					if(a!= null)
						if(a.getImagenRif()!=null && !a.getImagenRif().isEmpty())
							autorizadoPersonaJuridica.setImagenDocumentoRif(Util.obtenerArchivoStr(rutaImg+a.getImagenRif(), "documentoRifImg"));
				} catch (Exception e) {					
				}
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
				Optional<Transportista> transportista = transportistaRepository.findById(autorizado.getIdTransportista());		
				autorizado.setRifEmpresa(transportista.get().getRif());
				autorizado.setNombreEmpresa(transportista.get().getTransportista());
				
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
					
					if(autorizado.getImagenDocumento()!=null && !autorizado.getImagenDocumento().isEmpty()){
						String imagenDocumento = Util.obtenerArchivoStr(rutaImg + autorizado.getImagenDocumento(), "documentoImg");
						autorizadoPersonaNatural.setImagenDocumento(imagenDocumento);
						autorizadoPersonaNatural.setImgDocumentoAutorizado(imagenDocumento);
						
					}

				} catch (Exception e) {
					return Constantes.ERRORES;
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
				} catch (Exception e) {
					return Constantes.ERRORES;
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
		
		try {
			if (autorizadoAEliminar == null) {
				return "redirect:autorizados?error";
	
			} else {
				final boolean tieneSolicitudActiva = tieneSolicitudActiva(id);
				if (tieneSolicitudActiva) {
					redirectAttributes.addFlashAttribute("solicitudActiva", "solicitudActiva");
					return "redirect:autorizados";
				}
				
				if(autorizadoAEliminar.getImagenDocumento()!= null && !autorizadoAEliminar.getImagenDocumento().isEmpty())
					Util.eliminarArchivo(rutaImg, autorizadoAEliminar.getImagenDocumento());
				if(autorizadoAEliminar.getImagenRif()!= null && !autorizadoAEliminar.getImagenRif().isEmpty())
					Util.eliminarArchivo(rutaImg, autorizadoAEliminar.getImagenRif());
				if(autorizadoAEliminar.getImagenAdicional()!= null && !autorizadoAEliminar.getImagenAdicional().isEmpty())
					Util.eliminarArchivo(rutaImg, autorizadoAEliminar.getImagenAdicional());
				Date date = new Date();
				long time = date.getTime();
				Timestamp ts = new Timestamp(time);
				autorizadoAEliminar.setEstado("I");
				autorizadoAEliminar.setFechaActualizacion(ts);
				autorizadoRepository.save(autorizadoAEliminar);
				String detalle = MessageFormat.format(Constantes.ACCION_EDITAR_AUTORIZADO, "Autorizado Persona Juridica",
						autorizadoAEliminar.getIdAutorizado(), autorizadoAEliminar.getIdTipoAutorizado(),
						usuario.getIdUsuario(), usuario.getNombreUsuario());
				logServ.registrarLog(Constantes.ELIMINAR_AUTORIZADO, detalle, Constantes.AUTORIZADO_PERSONA_JURIDICA, true,
						Util.getRemoteIp(request), usuario);
				return "redirect:autorizados?success";
			}
		} catch (IOException e) {
			return "redirect:autorizados?error";
		}
	}
	
	
	@GetMapping("/autorizadosListar")
	public String autorizadosListar(Model model) {
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		List<Autorizado> autorizados = autorizadoRepository.findByIdEmpresaAndEstadoOrderByIdTipoAutorizadoAscDocumentoIdentidadAsc(usuario.getIdEmpresa(),
				Constantes.ACTIVO);
		List<AutorizadoModel> listaAutorizados = new ArrayList<AutorizadoModel>();
		
		for (Iterator<Autorizado> iterator = autorizados.iterator(); iterator.hasNext();) {
			Autorizado autorizado = (Autorizado) iterator.next();
			if(autorizado.getTipoAutorizado().getIdTipoAutorizado().equals(3)) {
				Optional<Transportista> transportista = transportistaRepository.findById(autorizado.getIdTransportista());		
				//autorizado.setRifEmpresa(transportista.get().getRif());
				//autorizado.setNombreEmpresa(transportista.get().getTransportista());
				listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
						autorizado.getTipoAutorizado().getTipoAutorizado(), transportista.get().getRif(),
						transportista.get().getTransportista(), autorizado.getDocumentoIdentidad(),
						autorizado.getNombreCompleto()));
			}
			else {
			
				listaAutorizados.add(new AutorizadoModel(autorizado.getIdAutorizado(),
					autorizado.getTipoAutorizado().getTipoAutorizado(), autorizado.getRifEmpresa(),
					autorizado.getNombreEmpresa(), autorizado.getDocumentoIdentidad(),
					autorizado.getNombreCompleto()));
			}
			autorizado = null;
		}

		model.addAttribute("autorizados", listaAutorizados);

		return Constantes.OP_AUTORIZADOS_LISTA;
	}

	/*private void validarAutorizado(AutorizadoBanco autorizadoBanco, BindingResult result) throws Exception {
		if (autorizadoBanco.getCaracterRifAutorizado() != null && !autorizadoBanco.getRifAutorizado().isEmpty()) {
			if (!autorizadoBanco.getRifAutorizado().matches("[0-9]{9}")) {
				result.rejectValue("rifAutorizado", "", "debe tener 9 d\u00EDgitos");
			} else if (!ValidationUtils
					.validarRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado())) {
				result.rejectValue("rifAutorizado", "", "inv\u00E1lido");
			}
		}

	}*/

	private void validarAutorizado(AutorizadoEmpresaTransporte autorizadoBanco, BindingResult result, boolean accionCrear) throws Exception {
		if (autorizadoBanco.getCaracterRifAutorizado() != null && !autorizadoBanco.getRifAutorizado().isEmpty()) {
			if (!autorizadoBanco.getRifAutorizado().matches("[0-9]{9}")) {
				result.rejectValue("rifAutorizado", "", "debe tener 9 d\u00EDgitos");
			} else if (!ValidationUtils
					.validarRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado())) {
				result.rejectValue("rifAutorizado", "", "inv\u00E1lido");
			}
			List<Transportista> transportistas = transportistaRepository
					.findByRif(autorizadoBanco.getCaracterRifAutorizado() + autorizadoBanco.getRifAutorizado());
			if (transportistas == null || transportistas.isEmpty()) {
				result.rejectValue("rifAutorizado", "", "Transportista no autorizado por el banco");
			}
			// buscar por tipoAutorizado, idEmpresa, id_transportista, estado
			if (transportistas != null && transportistas.size() > 0) {
				autorizadoBanco.setIdTransportista(transportistas.get(0).getIdTransportista());
				if(accionCrear) {
					Autorizado au = autorizadoRepository.findByIdTipoAutorizadoAndIdEmpresaAndIdTransportistaAndEstado(autorizadoBanco.getIdTipoAutorizado(), autorizadoBanco.getIdEmpresa(), transportistas.get(0).getIdTransportista(), "A");
					if(au != null) {
						result.rejectValue("rifAutorizado", "", "Ya existe este autorizado");
					}
				}
			}
		}
	}

	private void validarAutorizadoPersonaNatural(AutorizadoPersonaNatural autorizado, BindingResult result, boolean creando, int idEmpresa)
			throws Exception {
		
		if(autorizado.getNombreAutorizado() != null &&  !autorizado.getNombreAutorizado().trim().isEmpty()) {			
			autorizado.setNombreAutorizado(autorizado.getNombreAutorizado().trim());
				
			if(!autorizado.getNombreAutorizado().matches("[a-zA-Z\\ \\Á\\á\\É\\é\\Í\\í\\Ó\\ó\\Ú\\ú\\Ñ\\ñ]*")) {
				result.rejectValue("nombreAutorizado", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getNombreAutorizado().length() > 100) {
				result.rejectValue("nombreAutorizado", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}

			if ((autorizado.getDocumentoImg() != null && autorizado.getDocumentoImg().getBytes().length > 0) || autorizado.getImagenDocumento() == null || autorizado.getImagenDocumento().isEmpty()) {
				if (autorizado.getDocumentoImg() == null || autorizado.getDocumentoImg().getBytes().length == 0) {
					
					if (autorizado.getImgDocumentoAutorizado() == null || autorizado.getImgDocumentoAutorizado().isEmpty()) {
						
						result.rejectValue("documentoImg", "", "requerido");
					}
					
				} else {
					if (!ValidationUtils.isValidImageType(autorizado.getDocumentoImg())) {
						result.rejectValue("documentoImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
					} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoImg())) {
						result.rejectValue("documentoImg", "", "no puede ser mayor a 1 MB");
					}
				}
			}
		//}

		if (!NumberUtils.isDigits(autorizado.getDocumentoAutorizado())) {
			result.rejectValue("documentoAutorizado", "", "debe contener solo n\u00FAmeros");
		}else {
			if(creando) {
				Autorizado a = autorizadoRepository.findByIdEmpresaAndDocumentoIdentidadAndIdTipoAutorizadoAndEstado(idEmpresa, autorizado.getTipoDocumentoAutorizado() + Util.formatoDocumentoIdentidad(autorizado.getDocumentoAutorizado()),autorizado.getIdTipoAutorizado(), Constantes.ACTIVO);
				if(a!=null) {
					result.rejectValue("documentoAutorizado", "", "Ya existen estos datos para un autorizado");
				}
			}
		}

		if (autorizado.getTelefono() != null && !autorizado.getTelefono().isEmpty()) {
			if (!autorizado.getTelefono().matches("[0-9]{7}")) {
				result.rejectValue("telefono", "", "debe tener 7 d\u00EDgitos");
			}
		}

		if (autorizado.getEmail() != null && !autorizado.getEmail().isEmpty()
				&& !ValidationUtils.isValidEmail(autorizado.getEmail())) {
			result.rejectValue("email", "", "inv\u00E1lido");
		}
		
		if(autorizado.getMarcaModeloColor() != null &&  !autorizado.getMarcaModeloColor().trim().isEmpty()) {			
			autorizado.setMarcaModeloColor(autorizado.getMarcaModeloColor().trim());
				
			if (!autorizado.getMarcaModeloColor().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("marcaModeloColor", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getMarcaModeloColor().length() > 100) {
				result.rejectValue("marcaModeloColor", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
		
		if(autorizado.getPlaca() != null &&  !autorizado.getPlaca().trim().isEmpty()) {			
			autorizado.setPlaca(autorizado.getPlaca().trim());
				
			if (!autorizado.getPlaca().matches("[a-zA-Z0-9\\-]*")) {
				result.rejectValue("placa", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getPlaca().length() > 10) {
				result.rejectValue("placa", "", "debe tener una longitud m\u00E1xima de 10 caracteres");
			}
		}
		
		if(result.hasErrors()) {
			autorizado.setImagenDocumento(null);
			autorizado.setImgDocumentoAutorizado(null);
		}
		
	}

	private void validarAutorizadoPersonaJuridica(AutorizadoPersonaJuridica autorizado, BindingResult result, boolean creando, int idEmpresa)
			throws Exception {	
		
		if(autorizado.getNombreAutorizado() != null &&  !autorizado.getNombreAutorizado().trim().isEmpty()) {			
			autorizado.setNombreAutorizado(autorizado.getNombreAutorizado().trim());
				
			if(!autorizado.getNombreAutorizado().matches("[a-zA-Z\\ \\Á\\á\\É\\é\\Í\\í\\Ó\\ó\\Ú\\ú\\Ñ\\ñ]*")) {
				result.rejectValue("nombreAutorizado", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getNombreAutorizado().length() > 100) {
				result.rejectValue("nombreAutorizado", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
		
		if (autorizado.getTelefono() != null && !autorizado.getTelefono().isEmpty()) {
			if (!autorizado.getTelefono().matches("[0-9]{7}")) {
				result.rejectValue("telefono", "", "debe tener 7 d\u00EDgitos");
			}
		}
		
		if (autorizado.getEmail() != null && !autorizado.getEmail().isEmpty()
				&& !ValidationUtils.isValidEmail(autorizado.getEmail())) {
			result.rejectValue("email", "", "inv\u00E1lido");
		}
		
		if(autorizado.getMarcaModeloColor() != null &&  !autorizado.getMarcaModeloColor().trim().isEmpty()) {			
			autorizado.setMarcaModeloColor(autorizado.getMarcaModeloColor().trim());
				
			if (!autorizado.getMarcaModeloColor().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("marcaModeloColor", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getMarcaModeloColor().length() > 100) {
				result.rejectValue("marcaModeloColor", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
		
		if(autorizado.getPlaca() != null &&  !autorizado.getPlaca().trim().isEmpty()) {			
			autorizado.setPlaca(autorizado.getPlaca().trim());
				
			if (!autorizado.getPlaca().matches("[a-zA-Z0-9\\-]*")) {
				result.rejectValue("placa", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getPlaca().length() > 10) {
				result.rejectValue("placa", "", "debe tener una longitud m\u00E1xima de 10 caracteres");
			}
		}
		
		if(autorizado.getNombreEmpresa() != null &&  !autorizado.getNombreEmpresa().trim().isEmpty()) {			
			autorizado.setNombreEmpresa(autorizado.getNombreEmpresa().trim());
				
			if (!autorizado.getNombreEmpresa().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("nombreEmpresa", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getNombreEmpresa().length() > 100) {
				result.rejectValue("nombreEmpresa", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
			
		if(autorizado.getCargo() != null &&  !autorizado.getCargo().trim().isEmpty()) {			
			autorizado.setCargo(autorizado.getCargo().trim());
			
			if (!autorizado.getCargo().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("cargo", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (autorizado.getCargo().length() > 50) {
				result.rejectValue("cargo", "", "debe tener una longitud m\u00E1xima de 50 caracteres");
			}
		}
			
	
		
		if (autorizado.getCaracterRifEmpresa() != null && !autorizado.getRifEmpresa().isEmpty()) {
			if (!autorizado.getRifEmpresa().matches("[0-9]{9}")) {
				result.rejectValue("rifEmpresa", "", "debe tener 9 d\u00EDgitos");
			} else if (!ValidationUtils.validarRif(autorizado.getCaracterRifEmpresa() + autorizado.getRifEmpresa())) {
				result.rejectValue("rifEmpresa", "", "inv\u00E1lido");
			}
		}
		if (autorizado.getDocumentoAutorizado().isEmpty() || !NumberUtils.isDigits(autorizado.getDocumentoAutorizado())) {
			result.rejectValue("documentoAutorizado", "", "debe contener solo n\u00FAmeros");
		}	
	
		
		//if(creando || !creando && !autorizado.getImgDocumentoAutorizado().isEmpty()) {
			if (autorizado.getImagenDocumento() == null || autorizado.getImagenDocumento().isEmpty() || (autorizado.getDocumentoImg() != null && autorizado.getDocumentoImg().getBytes().length > 0)) {
				if (autorizado.getDocumentoImg() == null || autorizado.getDocumentoImg().getBytes().length == 0) {
					
					if (autorizado.getImgDocumentoAutorizado() == null || autorizado.getImgDocumentoAutorizado().isEmpty()) {
							
						result.rejectValue("documentoImg", "", "requerido");
					}
					
				} else {
					if (!ValidationUtils.isValidImageType(autorizado.getDocumentoImg())) {
						result.rejectValue("documentoImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
					} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoImg())) {
						result.rejectValue("documentoImg", "", "no puede ser mayor a 1 MB");
					}
				}
			}
		//}
		
		if(creando || !creando && !autorizado.getImgDocumentoAdicionalAutorizado().isEmpty()) {
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
		}

		if (autorizado.getImagenDocumentoRif() == null || autorizado.getImagenDocumentoRif().isEmpty() || (autorizado.getDocumentoRifImg() != null && autorizado.getDocumentoRifImg().getBytes().length > 0)) {
				if (autorizado.getDocumentoRifImg() == null || autorizado.getDocumentoRifImg().getBytes().length == 0) {
					if (autorizado.getImgDocumentoRifAutorizado() == null || autorizado.getImgDocumentoRifAutorizado().isEmpty()) {
												
						result.rejectValue("documentoRifImg", "", "requerido");
					}
				} else {
					if (!ValidationUtils.isValidImageType(autorizado.getDocumentoRifImg())) {
						result.rejectValue("documentoRifImg", "", "debe tener el formato JPG, GIF, BMP o PNG");
					} else if (!ValidationUtils.isValidImageSize(autorizado.getDocumentoRifImg())) {
						result.rejectValue("documentoRifImg", "", "no puede ser mayor a 1 MB");
					}
				}
			}

		if(creando) {
			if (autorizado.getDocumentoAutorizado() != null && ! autorizado.getDocumentoAutorizado().isEmpty()) {
				Autorizado a = autorizadoRepository.findByIdEmpresaAndDocumentoIdentidadAndRifEmpresaAndIdTipoAutorizadoAndEstado(idEmpresa, autorizado.getTipoDocumentoAutorizado() + Util.formatoDocumentoIdentidad(autorizado.getDocumentoAutorizado()), autorizado.getCaracterRifEmpresa()+autorizado.getRifEmpresa(),autorizado.getIdTipoAutorizado(), Constantes.ACTIVO);
				if(a!=null) {
					result.rejectValue("documentoAutorizado", "", "Ya existen estos datos para un autorizado");
					result.rejectValue("rifEmpresa", "", "Ya existen estos datos para un autorizado");
				}
			}
		}

		if(result.hasErrors()) {
			autorizado.setImagenDocumento(null);
			autorizado.setImgDocumentoAutorizado(null);
			autorizado.setImagenDocumentoRif(null);
			autorizado.setImgDocumentoRifAutorizado(null);
			autorizado.setImagenDocumentoAdicional(null);
			autorizado.setImgDocumentoAdicionalAutorizado(null);
		}
	}

	/*private Autorizado convertirAutorizadoBeneficioTraspasoToAutorizado(AutorizadoBeneficioTraspaso solictudTraspaso)
			throws IOException {
		final Autorizado autorizado = new Autorizado();

		if (solictudTraspaso.getIdAutorizado() != null)
			autorizado.setIdAutorizado(solictudTraspaso.getIdAutorizado());

		autorizado.setRifEmpresa(solictudTraspaso.getCaracterRifAutorizado() + solictudTraspaso.getRifAutorizado());
		autorizado.setNombreEmpresa(solictudTraspaso.getNombreEmpresa());
		autorizado.setIdTipoAutorizado(solictudTraspaso.getIdTipoAutorizado());
		return autorizado;
	}*/

	private Autorizado convertirAutorizadoEmpresaTransporteToAutorizado(
			AutorizadoEmpresaTransporte autorizadoEmpresaTransporte) throws IOException {
		final Autorizado autorizado = new Autorizado();

		if (autorizadoEmpresaTransporte.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoEmpresaTransporte.getIdAutorizado());
		autorizado.setIdTipoAutorizado(autorizadoEmpresaTransporte.getIdTipoAutorizado());
		autorizado.setIdEmpresa(autorizadoEmpresaTransporte.getIdEmpresa());
		autorizado.setIdTransportista(autorizadoEmpresaTransporte.getIdTransportista());
		return autorizado;
	}

	private Autorizado convertirAutorizadoPersonaNaturalToAutorizado(AutorizadoPersonaNatural autorizadoPersonaNatural,
			Autorizado autorizadoToUpdate) throws Exception {
		final Autorizado autorizado = new Autorizado();

		Tika defaultTika = new Tika();
		if (autorizadoPersonaNatural.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoPersonaNatural.getIdAutorizado());
		
		autorizado.setIdTipoAutorizado(autorizadoPersonaNatural.getIdTipoAutorizado());
		autorizado.setDocumentoIdentidad(autorizadoPersonaNatural.getTipoDocumentoAutorizado() + Util.formatoDocumentoIdentidad(autorizadoPersonaNatural.getDocumentoAutorizado()));
		autorizado.setNombreCompleto(autorizadoPersonaNatural.getNombreAutorizado());		
		autorizado.setTelefonoMovil(autorizadoPersonaNatural.getPrefijoTelefono() + autorizadoPersonaNatural.getTelefono());
		autorizado.setEmail(autorizadoPersonaNatural.getEmail());
		autorizado.setMarcaModeloColorVehiculo(autorizadoPersonaNatural.getMarcaModeloColor());
		autorizado.setPlacaVehiculo(autorizadoPersonaNatural.getPlaca());

		/*
		 * String imagenDocumento = autorizadoToUpdate.getImagenDocumento() != null ?
		 * Util.obtenerArchivoStr(rutaImg + autorizadoToUpdate.getImagenDocumento(),
		 * "documentoImg") : null; if
		 * (autorizadoPersonaNatural.getImgDocumentoAutorizado()!=null &&
		 * !autorizadoPersonaNatural.getImgDocumentoAutorizado().equals(imagenDocumento)
		 * ) { if (autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename() !=
		 * null &&
		 * !autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename().isEmpty())
		 * { imgBuffered =
		 * Util.convertFile(autorizadoPersonaNatural.getImgDocumentoAutorizado().
		 * getBytes(), 'D'); String deteccion =
		 * defaultTika.detect(autorizadoPersonaNatural.getDocumentoImg().getBytes());
		 * String extension = deteccion.split("/")[1];
		 * autorizado.setImagenDocumento(Util.saveFile(imgBuffered, 'D', extension,
		 * rutaImg)); } } else if (imagenDocumento != null) {
		 * autorizado.setImagenDocumento(autorizadoToUpdate.getImagenDocumento()); }
		 */

		if (autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename() != null && !autorizadoPersonaNatural.getDocumentoImg().getOriginalFilename().isEmpty()) {
			    String deteccion = defaultTika.detect(autorizadoPersonaNatural.getDocumentoImg().getBytes());
			    String extension = deteccion.split("/")[1];
			autorizado.setImagenDocumento(Util.saveFile(Util.convertFile(autorizadoPersonaNatural.getImgDocumentoAutorizado().getBytes(), 'D'), 'D', extension, rutaImg));
			Util.eliminarArchivo(rutaImg, autorizadoToUpdate.getImagenDocumento());
		}else {
			autorizado.setImagenDocumento(autorizadoToUpdate.getImagenDocumento());
		}
		return autorizado;
	}

	private Autorizado convertirAutorizadoPersonaJuridicaToAutorizado(
			AutorizadoPersonaJuridica autorizadoPersonaJuridica, boolean creando, Autorizado autorizadoToUpdate)
			throws Exception {
		final Autorizado autorizado = new Autorizado();
		BufferedImage img;
		if (autorizadoPersonaJuridica.getIdAutorizado() != null)
			autorizado.setIdAutorizado(autorizadoPersonaJuridica.getIdAutorizado());

		autorizado.setDocumentoIdentidad(autorizadoPersonaJuridica.getTipoDocumentoAutorizado()
				+ Util.formatoDocumentoIdentidad(autorizadoPersonaJuridica.getDocumentoAutorizado()));
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
		if (!creando) {
			a = autorizadoRepository.findById((int) autorizadoPersonaJuridica.getIdAutorizado());
			if (autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenDocumento(Util.saveFile(img, 'D', extension, rutaImg));
				Util.eliminarArchivo(rutaImg, a.getImagenDocumento());
			} else {
				autorizado.setImagenDocumento(a.getImagenDocumento());
			}

			if (autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().getBytes(), 'R');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoRifImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenRif(Util.saveFile(img, 'R', extension, rutaImg));
				Util.eliminarArchivo(rutaImg, a.getImagenRif());
			} else {
				autorizado.setImagenRif(a.getImagenRif());
			}
			if (autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().getBytes(), 'A');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoAdicionalImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenAdicional(Util.saveFile(img, 'A', extension, rutaImg));
				Util.eliminarArchivo(rutaImg, a.getImagenAdicional());
			} else {
				autorizado.setImagenAdicional(a.getImagenAdicional());
			}
		} else {

			if (autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAutorizado().getBytes(), 'D');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenDocumento(Util.saveFile(img, 'D', extension, rutaImg));
			}
			if (autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoRifImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoRifAutorizado().getBytes(), 'R');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoRifImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenRif(Util.saveFile(img, 'R', extension, rutaImg));
			}
			if (autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename() != null
					&& !autorizadoPersonaJuridica.getDocumentoAdicionalImg().getOriginalFilename().isEmpty()) {
				img = Util.convertFile(autorizadoPersonaJuridica.getImgDocumentoAdicionalAutorizado().getBytes(), 'A');
				String deteccion = defaultTika.detect(autorizadoPersonaJuridica.getDocumentoAdicionalImg().getBytes());
				String extension = deteccion.split("/")[1];
				autorizado.setImagenAdicional(Util.saveFile(img, 'A', extension, rutaImg));
			}
		}

		return autorizado;
	}

	private boolean tieneSolicitudActiva(Integer idAutotirado) {
		final List<SolicitudRetiro> solicitudes = solicitudRetiroRepo.findByIdAutorizado(idAutotirado);
		final List<Integer> solicitudesIds = solicitudes.stream().map(SolicitudRetiro::getIdSolicitud)
				.collect(Collectors.toList());
		final List<SolicitudRetiroTraza> solicitudTrazas = solicitudRetiroTrazaRepo.findByIdSolicitudInOrderByIdSolicitudRetiroTrazaDesc(solicitudesIds);
		
		boolean tieneSolicitudActiva = false;
		
		for (Iterator<SolicitudRetiroTraza> iterator = solicitudTrazas.iterator(); iterator.hasNext();) {
			SolicitudRetiroTraza solicitudRetiroTraza = (SolicitudRetiroTraza) iterator.next();
			tieneSolicitudActiva = ESTATUS_SOLICITUD_ACTIVA.contains(solicitudRetiroTraza.getIdEstatusSolicitud());
			if (tieneSolicitudActiva)
				break;
			
		}
		

		return tieneSolicitudActiva;
	}

}