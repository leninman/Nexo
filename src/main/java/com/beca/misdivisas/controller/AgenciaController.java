package com.beca.misdivisas.controller;

import java.io.ByteArrayOutputStream;
import java.io.IOException;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.SimpleDateFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.imageio.ImageIO;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.apache.tika.Tika;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IAgenciaRepo;
import com.beca.misdivisas.interfaces.IEstadoRepo;
import com.beca.misdivisas.interfaces.IMunicipioRepo;
import com.beca.misdivisas.jpa.Agencia;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Estado;
import com.beca.misdivisas.jpa.Municipio;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.AgenciaModel;
import com.beca.misdivisas.model.EmpresaModel;
import com.beca.misdivisas.model.MunicipioModel;
import com.beca.misdivisas.model.SucursalModel;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class AgenciaController {

	private final static DateFormat dateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DD_MM_YYYY);
	private final static DateFormat longDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
	private final static DateFormat simpleDateFormat = new SimpleDateFormat(Constantes.FORMATO_FECHA_D_M_YYYY);

	@Autowired
	private ObjectFactory<HttpSession> factory;
	private static final Pattern CARACTERES_ESPECIALES_PATTERN = Pattern.compile("[$&+:;=?@#|/{}\\\\]");

	@Autowired
	private IAgenciaRepo agenciaRepository;

	@Autowired
	private IEstadoRepo estadoRepository;

	@Autowired
	private IMunicipioRepo municipioRepository;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LogService logServ;

	@GetMapping("/agenciaHome")
	public String getAgencias(Model model) {

		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		final List<Agencia> agencia = agenciaRepository.findAllOrderByName();
		List<AgenciaModel> agenciaModel = agencia.stream().map((age) -> convertirAgenciaListToAgenciaModel(age))
				.collect(Collectors.toList());

		if (agenciaModel.isEmpty())
			agenciaModel = null;
		model.addAttribute(Constantes.AGENCIAS, agenciaModel);

		return "mainAgencias";
	}

	private AgenciaModel convertirAgenciaListToAgenciaModel(Agencia agencia) {

		AgenciaModel agenciaModel = new AgenciaModel(agencia.getIdAgencia(), agencia.getAgencia(), agencia.getLatitud(),
				agencia.getLongitud(), agencia.getIdMunicipio(), agencia.getEstatusAgencia().getEstatusAgencia(),
				agencia.getNumeroAgencia(), dateFormat.format(agencia.getFechaCreacion()),
				dateFormat.format(agencia.getFechaEstatus()), agencia.getAlmacenamiento(), agencia.getRecaudacion());

		agenciaModel.setEstatus(agencia.getEstatusAgencia().getEstatusAgencia());
		agenciaModel.setMunicipio(agencia.getMunicipio().getMunicipio());
		agenciaModel.setEstado(agencia.getMunicipio().getEstado().getEstado());
		return agenciaModel;

	}

	@GetMapping("/crearAgencia")
	public String crearAgencia(Model model) {
		AgenciaModel agenciaModel = new AgenciaModel();
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute(Constantes.ESTADOS, estados);
		model.addAttribute(Constantes.CREAR, true);
		model.addAttribute(Constantes.AGENCIA_MODEL, agenciaModel);
		return "agencia/agencias";
	}

	@PostMapping("agenciaAgregar")
	public String addAgencia(@Valid AgenciaModel agenciaModel, BindingResult result, Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarAgencia(agenciaModel, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute(Constantes.ESTADOS, estados);
			if (agenciaModel.getIdEstado() != null && agenciaModel.getIdEstado().intValue() != -1) {
				final List<Municipio> municipios = municipioRepository.findAllByIdEstado(agenciaModel.getIdEstado());
				model.addAttribute("municipios", municipios);
			}
			model.addAttribute(Constantes.CREAR, true);

			model.addAttribute(Constantes.AGENCIA_MODEL, agenciaModel);
			return "agencia/agencias";
		}

		Agencia agencia = convertirAgenciaModelToAgencia(agenciaModel);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		agencia.setFechaCreacion(ts);
		agencia.setFechaEstatus(ts);
		Agencia agenciaNueva = agenciaRepository.save(agencia);

		return "redirect:agenciaListar?success";
	}

	@GetMapping("/agenciaListar")
	public String agenciaListar(Model model) {
		final List<Agencia> agencias = agenciaRepository.findAllOrderByName();
		List<AgenciaModel> agenciasModel = agencias.stream().map((age) -> convertirAgenciaListToAgenciaModel(age))
				.collect(Collectors.toList());
		if (agenciasModel.isEmpty())
			agenciasModel = null;
		model.addAttribute(Constantes.AGENCIAS, agenciasModel);
		return "agencia/listaAgencias";
	}

	private Agencia convertirAgenciaModelToAgencia(AgenciaModel agenciaModel) throws Exception {
		Agencia agencia = new Agencia();

		if (agenciaModel.getIdAgencia() != null)
			agencia.setIdAgencia(agenciaModel.getIdAgencia());

		agencia.setAgencia(agenciaModel.getAgencia());
		agencia.setNumeroAgencia(agenciaModel.getnAgencia());
		agencia.setLatitud(agenciaModel.getLatitud());
		agencia.setLongitud(agenciaModel.getLongitud());
		agencia.setIdMunicipio(agenciaModel.getIdMunicipio());
		agencia.setIdEstatusAgencia(agenciaModel.getIdEstatusAgencia());
		agencia.setAlmacenamiento(agenciaModel.getAlmacenamiento());
		if (agenciaModel.getRecaudacion() == null)
			agencia.setRecaudacion(false);
		else
			agencia.setRecaudacion(agenciaModel.getRecaudacion());

		return agencia;
	}

	@GetMapping(path = "/agenciaEditar/{idAgencia}")
	public String editarAgencia(@PathVariable("idAgencia") int idAgencia, Model model) {
		Agencia agencia = agenciaRepository.findById(idAgencia);
		if (agencia == null) {
			return "redirect:agenciaListar?error";
		} else {
			AgenciaModel agenciaModel = convertirAgenciaToAgenciaModel(agencia);
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute(Constantes.ESTADOS, estados);
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(agenciaModel.getIdEstado());
			model.addAttribute(Constantes.MUNICIPIOS, municipios);
			model.addAttribute(Constantes.AGENCIA_MODEL, agenciaModel);
			model.addAttribute(Constantes.EDITAR, true);
			return "agencia/agencias";
		}
	}

	@PostMapping("agenciaActualizar")
	public String actualizarAgencia(@Valid AgenciaModel agenciaModel, BindingResult result, Model model)
			throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarAgencia(agenciaModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(agenciaModel.getIdEstado());
			model.addAttribute(Constantes.MUNICIPIOS, municipios);
			model.addAttribute(Constantes.ESTADOS, estados);
			model.addAttribute(Constantes.EDITAR, true);

			model.addAttribute(Constantes.AGENCIA_MODEL, agenciaModel);
			return "agencia/agencias";
		}

		final Agencia agenciaToUpdate = agenciaRepository.findById(agenciaModel.getIdAgencia()).get();
		Agencia agencia = convertirAgenciaModelToAgencia(agenciaModel);

		if (!agencia.getIdEstatusAgencia().equals(agenciaToUpdate.getIdEstatusAgencia())) {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			agencia.setFechaEstatus(ts);
		} else {
			agencia.setFechaEstatus(agenciaToUpdate.getFechaEstatus());
		}

		agencia.setFechaCreacion(agenciaToUpdate.getFechaCreacion());

		agenciaRepository.save(agencia);

		String detalle = MessageFormat.format(Constantes.ACCION_AGENCIA, Constantes.OP_EDICION, agencia.getAgencia(),
				agencia.getIdAgencia(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.EDICION_AGENCIA, detalle, Constantes.AGENCIAS, true, Util.getRemoteIp(request),
				usuario);
		return "redirect:agenciaListar?success";
	}

	private AgenciaModel convertirAgenciaToAgenciaModel(Agencia agencia) {

		AgenciaModel agenciaModel = new AgenciaModel(agencia.getIdAgencia(), agencia.getAgencia(), agencia.getLatitud(),
				agencia.getLongitud(), agencia.getIdMunicipio(), agencia.getEstatusAgencia().getEstatusAgencia(),
				agencia.getNumeroAgencia(), dateFormat.format(agencia.getFechaCreacion()),
				dateFormat.format(agencia.getFechaEstatus()), agencia.getAlmacenamiento(), agencia.getRecaudacion());

		int idEstado = municipioRepository.findById(agencia.getIdMunicipio()).get().getIdEstado();
		agenciaModel.setIdEstado(idEstado);
		return agenciaModel;
	}

	private void validarAgencia(AgenciaModel agenciaModel, BindingResult result) {

		if (agenciaModel.getAgencia() != null && !agenciaModel.getAgencia().trim().isEmpty()) {

			agenciaModel.setAgencia(agenciaModel.getAgencia().trim());

			if (!agenciaModel.getAgencia().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("agencia", "", "contiene caracteres especiales no v\u00E1lidos");
			} else if (agenciaModel.getAgencia().length() > 100) {
				result.rejectValue("agencia", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
		if (agenciaModel.getnAgencia() != null) {
			result.rejectValue("nAgencia", "", "requerido");
		}
		if (agenciaModel.getIdMunicipio() != null && agenciaModel.getIdMunicipio().intValue() == -1) {
			result.rejectValue("idMunicipio", "", "requerido");
		}

		if (agenciaModel.getLatitud() != null && !agenciaModel.getLatitud().trim().isEmpty()) {

			agenciaModel.setLatitud(agenciaModel.getLatitud().trim());

			if (!agenciaModel.getLatitud().matches("^[-]?\\d+[\\.]?\\d*$")
					|| Double.parseDouble(agenciaModel.getLatitud()) > 90
					|| Double.parseDouble(agenciaModel.getLatitud()) < -90)
				result.rejectValue("latitud", "", "invalida");
		}

		if (agenciaModel.getLongitud() != null && !agenciaModel.getLongitud().trim().isEmpty()) {

			agenciaModel.setLongitud(agenciaModel.getLongitud().trim());

			if (!agenciaModel.getLongitud().matches("^[-]?\\d+[\\.]?\\d*$")
					|| Double.parseDouble(agenciaModel.getLongitud()) > 180
					|| Double.parseDouble(agenciaModel.getLongitud()) < -180)
				result.rejectValue("longitud", "", "invalida");
		}

	}
}
