package com.beca.misdivisas.controller;

import java.io.IOException;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import javax.validation.Valid;

import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IEstadoRepo;
import com.beca.misdivisas.interfaces.IMunicipioRepo;
import com.beca.misdivisas.interfaces.ISucursalRepo;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Estado;
import com.beca.misdivisas.jpa.Municipio;
import com.beca.misdivisas.jpa.Sucursal;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.EmpresaModel;
import com.beca.misdivisas.model.MunicipioModel;
import com.beca.misdivisas.model.SucursalModel;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.services.MenuService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class EmpresaController {

	@Autowired
	private ObjectFactory<HttpSession> factory;
	private static final Pattern CARACTERES_ESPECIALES_PATTERN = Pattern.compile("[$&+:;=?@#|/{}\\\\]");

	@Autowired
	private IEmpresaRepo empresaRepository;

	@Autowired
	private ISucursalRepo sucursalRepository;

	@Autowired
	private IEstadoRepo estadoRepository;

	@Autowired
	private IMunicipioRepo municipioRepository;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private LogService logServ;
	
	@Autowired
	private MenuService menuService;

	@GetMapping("/empresaHome")
	public String getEmpresas(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		final List<Empresa> empresas = empresaRepository.findAllOrderByName();
		List<EmpresaModel> emoresasModel = empresas.stream().map((emp) -> convertirEmpresaListToEmpresaModel(emp))
				.collect(Collectors.toList());
		if (emoresasModel.isEmpty())
			emoresasModel = null;
		model.addAttribute(Constantes.EMPRESAS, emoresasModel);
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_EMPRESAS, Constantes.TEXTO_ADMINISTRAR_EMPRESAS,
				Constantes.TEXTO_ADMINISTRAR_EMPRESAS, Util.getRemoteIp(request), usuario);

		return "mainEmpresas";
	}

	@GetMapping("/crearEmpresa")
	public String crearEmpresaForm(Model model) {
		EmpresaModel empresaModel = new EmpresaModel();
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute("estados", estados);
		model.addAttribute("crear", true);
		model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
		return "empresa/empresas";
	}

	@PostMapping("empresaAgregar")
	public String addEmpresa(@Valid EmpresaModel empresaModel, BindingResult result, Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		validarEmpresa(empresaModel, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute("estados", estados);
			if (empresaModel.getIdEstado() != null && empresaModel.getIdEstado() != -1) {
				final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
				model.addAttribute("municipios", municipios);
			}
			model.addAttribute("crear", true);
			if (empresaModel.getLogoEmpresa() != null && !empresaModel.getLogoEmpresa().isEmpty()) {
				empresaModel.setImagenLogo(empresaModel.getLogoEmpresa());
				empresaModel.setLogo(new MockMultipartFile("logo",
						Base64.getDecoder().decode(empresaModel.getLogoEmpresa().getBytes())));
			}
			model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
			return "empresa/empresas";
		}

		Empresa empresa = convertirEmpresaModelToEmpresa(empresaModel);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		empresa.setFechaCreacion(ts);
		empresa.setFechaEstatus(ts);
		empresaRepository.save(empresa);

		String detalle = MessageFormat.format(Constantes.ACCION_EMPRESA, Constantes.OP_CREAR,
				empresa.getEmpresa(), usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_EMPRESAS, detalle, Constantes.OP_CREAR,
				Util.getRemoteIp(request), usuario);

		return "redirect:empresaListar?success";
	}

	@GetMapping("/empresaListar")
	public String empresaListar(Model model) {
		final List<Empresa> empresas = empresaRepository.findAllOrderByName();
		List<EmpresaModel> emoresasModel = empresas.stream().map((emp) -> convertirEmpresaListToEmpresaModel(emp))
				.collect(Collectors.toList());
		if (emoresasModel.isEmpty())
			emoresasModel = null;
		model.addAttribute(Constantes.EMPRESAS, emoresasModel);
		return "empresa/listaEmpresas";
	}

	@GetMapping(path = "/empresaEditar/{idEmpresa}")
	public String editarEmpresa(@PathVariable("idEmpresa") int idEmpresa, Model model) {
		Empresa empresa = empresaRepository.findById(idEmpresa);
		if (empresa == null) {
			return "redirect:empresaListar?error";
		} else {
			EmpresaModel empresaModel = convertirEmpresaToEmpresaModel(empresa);
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute("estados", estados);
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
			model.addAttribute("municipios", municipios);
			model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
			model.addAttribute("editar", true);
			return "empresa/empresas";
		}
	}

	@PostMapping("empresaActualizar")
	public String actualizarEmpresa(@Valid EmpresaModel empresaModel, BindingResult result, Model model)
			throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		validarEmpresa(empresaModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
			model.addAttribute("municipios", municipios);
			model.addAttribute("estados", estados);
			model.addAttribute("editar", true);
			if (empresaModel.getLogoEmpresa() != null && !empresaModel.getLogoEmpresa().isEmpty()) {
				empresaModel.setImagenLogo(empresaModel.getLogoEmpresa());
				empresaModel.setLogo(new MockMultipartFile("logo",
						Base64.getDecoder().decode(empresaModel.getLogoEmpresa().getBytes())));
			}
			model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
			return "empresa/empresas";
		}

		final Empresa empresaToUpdate = empresaRepository.findById(empresaModel.getIdEmpresa()).get();
		Empresa empresa = convertirEmpresaModelToEmpresa(empresaModel);

		if (empresa.getIdEstatusEmpresa() != empresaToUpdate.getIdEstatusEmpresa()) {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			empresa.setFechaEstatus(ts);
		} else {
			empresa.setFechaEstatus(empresaToUpdate.getFechaEstatus());
		}

		if (empresa.getLogo().length == 0 && empresaToUpdate.getLogo().length > 0)
			empresa.setLogo(empresaToUpdate.getLogo());

		empresa.setFechaCreacion(empresaToUpdate.getFechaCreacion());

		empresaRepository.save(empresa);

		String detalle = MessageFormat.format(Constantes.ACCION_EMPRESA, Constantes.OP_EDICION,
				empresa.getEmpresa(), usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_EMPRESAS, detalle, Constantes.OP_EDICION,
				Util.getRemoteIp(request), usuario);
		return "redirect:empresaListar?success";
	}

	//////// SUCURSAL API
	//////// /////////////////////////////////////////////////////////////////////////////////
	@GetMapping(path = "/sucursalHome/{idEmpresa}")
	public String getSucursales(@PathVariable("idEmpresa") int idEmpresa, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		List<Sucursal> sucursales = sucursalRepository.findSucursalByEmpId(idEmpresa);
		Empresa empresa = empresaRepository.findById(idEmpresa);
		model.addAttribute("sigla", empresa.getSigla());
		model.addAttribute("idEmpresa", idEmpresa);
		if (sucursales.isEmpty())
			sucursales = null;
		model.addAttribute(Constantes.SUCURSALES, sucursales);
		model.addAttribute("listar", true);

		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_SUCURSALES, Constantes.TEXTO_ADMINISTRAR_SUCURSALES,
				Constantes.TEXTO_ADMINISTRAR_SUCURSALES, Util.getRemoteIp(request), usuario);

		return "empresa/listaSucursales";
	}

	@GetMapping(path = "/sucursalCrear/{idEmpresa}")
	public String crearSucursalForm(@PathVariable("idEmpresa") int idEmpresa, Model model) {
		SucursalModel sucursalModel = new SucursalModel();
		sucursalModel.setIdEmpresa(idEmpresa);
		final List<Sucursal> sucursalesConAcopio = sucursalRepository.findSucursalByEmpIdAndAcopio(idEmpresa, true);
		boolean permitirAcopio = sucursalesConAcopio.isEmpty();
		model.addAttribute("permitirAcopio", permitirAcopio);
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute("estados", estados);
		model.addAttribute("crear", true);
		model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);

		return "empresa/sucursales";
	}

	@PostMapping("sucursalAgregar")
	public String addSucursal(@Valid SucursalModel sucursalModel, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		validarSucursal(sucursalModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute("estados", estados);
			if (sucursalModel.getIdEstado() != null && sucursalModel.getIdEstado() != -1) {
				final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
				model.addAttribute("municipios", municipios);
			}
			model.addAttribute("crear", true);
			final List<Sucursal> sucursalesConAcopio = sucursalRepository.findSucursalByEmpIdAndAcopio(sucursalModel.getIdEmpresa(), true);
			boolean permitirAcopio = sucursalesConAcopio.isEmpty();
			model.addAttribute("permitirAcopio", permitirAcopio);
			model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);
			return "empresa/sucursales";
		}

		Sucursal sucursal = convertirSucursalModelToSucursal(sucursalModel);
		Date date = new Date();
		long time = date.getTime();
		Timestamp ts = new Timestamp(time);

		sucursal.setFechaCreacion(ts);
		sucursal.setFechaEstatus(ts);
		sucursalRepository.save(sucursal);

		String detalle = MessageFormat.format(Constantes.ACCION_SUCURSAL, Constantes.OP_CREAR,
				sucursal.getSucursal(), usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_SUCURSALES, detalle, Constantes.OP_CREAR,
				Util.getRemoteIp(request), usuario);

		return "redirect:sucursalListar/" + sucursal.getIdEmpresa() + "?success";
	}

	@GetMapping(path = "/sucursalListar/{idEmpresa}")
	public String sucursalListar(@PathVariable("idEmpresa") int idEmpresa, Model model) {
		final List<Sucursal> sucursales = sucursalRepository.findSucursalByEmpId(idEmpresa);
		Empresa empresa = empresaRepository.findById(idEmpresa);
		model.addAttribute("sigla", empresa.getSigla());
		model.addAttribute("idEmpresa", idEmpresa);
		model.addAttribute(Constantes.SUCURSALES, sucursales);
		model.addAttribute("listar", true);
		return "empresa/listaSucursales";
	}

	@GetMapping(path = "/sucursalEditar/{idSucursal}")
	public String sucursalEditar(@PathVariable("idSucursal") int idSucursal, Model model) {
		Sucursal sucursal = sucursalRepository.findById(idSucursal).orElse(null);
		SucursalModel sucursalModel = convertirSucursalToSucursalModel(sucursal);
		final List<Sucursal> sucursalesConAcopio = sucursalRepository
				.findSucursalByEmpIdAndAcopio(sucursal.getIdEmpresa(), true);
		boolean permitirAcopio = sucursalesConAcopio.isEmpty();
		model.addAttribute("permitirAcopio", permitirAcopio);
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute("estados", estados);
		final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
		model.addAttribute("municipios", municipios);
		model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);
		model.addAttribute("editar", true);
		return "empresa/sucursales";
	}

	@PostMapping("sucursalActualizar")
	public String actualizarSucursal(@Valid SucursalModel sucursalModel, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, menuService.getMenu(usuario.getIdUsuario()));
		validarSucursal(sucursalModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
			model.addAttribute("municipios", municipios);
			model.addAttribute("estados", estados);
			final List<Sucursal> sucursalesConAcopio = sucursalRepository.findSucursalByEmpIdAndAcopio(sucursalModel.getIdEmpresa(), true);
			boolean permitirAcopio = sucursalesConAcopio.isEmpty();
			model.addAttribute("permitirAcopio", permitirAcopio);
			model.addAttribute("editar", true);
			model.addAttribute(Constantes.EMPRESA_MODEL, sucursalModel);
			return "empresa/sucursales";
		}

		Sucursal sucursalToUpdate = sucursalRepository.findById(sucursalModel.getIdSucursal()).get();
		Sucursal sucursal = convertirSucursalModelToSucursal(sucursalModel);

		if (sucursal.getIdEstatusSucursal() != sucursalToUpdate.getIdEstatusSucursal()) {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			sucursal.setFechaEstatus(ts);
		} else {
			sucursal.setFechaEstatus(sucursalToUpdate.getFechaEstatus());
		}
		sucursal.setFechaCreacion(sucursalToUpdate.getFechaCreacion());
		sucursal.setIdEmpresa(sucursalToUpdate.getIdEmpresa());

		sucursalRepository.save(sucursal);

		String detalle = MessageFormat.format(Constantes.ACCION_SUCURSAL, Constantes.OP_EDICION,
				sucursal.getSucursal(), usuario.getIdUsuario());
		logServ.registrarLog(Constantes.TEXTO_ADMINISTRAR_SUCURSALES, detalle, Constantes.OP_EDICION,
				Util.getRemoteIp(request), usuario);

		return "redirect:sucursalListar/" + sucursal.getIdEmpresa() + "?success";
	}

	@GetMapping(path = "/municipios/{idEstado}", produces = "application/json")
	@ResponseBody
	public List<MunicipioModel> getMunicipios(@PathVariable int idEstado) {
		final List<Municipio> municipios = municipioRepository.findAllByIdEstado(idEstado);
		final List<MunicipioModel> municipiosModel = municipios.stream()
				.map((mun) -> new MunicipioModel(mun.getIdMunicipio(), mun.getMunicipio()))
				.collect(Collectors.toList());
		return municipiosModel;
	}

	private EmpresaModel convertirEmpresaToEmpresaModel(Empresa empresa) {
		String rif = empresa.getRif() > 0 ? StringUtils.leftPad(String.valueOf(empresa.getRif()), 9, "0") : null;
		EmpresaModel empresaModel = new EmpresaModel(empresa.getIdEmpresa(), empresa.getCaracterRif(),
				empresa.getDireccion(), empresa.getEmpresa(), empresa.getFechaCreacion(), empresa.getFechaEstatus(),
				empresa.getIdEstatusEmpresa(), empresa.getIdMunicipio(), empresa.getIdEmpresaCoe(), rif,
				empresa.getSigla(), empresa.getLogo());

		int idEstado = municipioRepository.findById(empresa.getIdMunicipio()).get().getIdEstado();
		empresaModel.setIdEstado(idEstado);

		return empresaModel;
	}

	private EmpresaModel convertirEmpresaListToEmpresaModel(Empresa empresa) {
		String rif = StringUtils.leftPad(String.valueOf(empresa.getRif()), 9, "0");
		EmpresaModel empresaModel = new EmpresaModel(empresa.getIdEmpresa(), empresa.getCaracterRif(),
				empresa.getDireccion(), empresa.getEmpresa(), empresa.getFechaCreacion(), empresa.getFechaEstatus(),
				empresa.getIdEstatusEmpresa(), empresa.getIdMunicipio(), empresa.getIdEmpresaCoe(), rif,
				empresa.getSigla(), empresa.getLogo());

		empresaModel.setEstatus(empresa.getEstatusEmpresa().getEstatusEmpresa());
		return empresaModel;
	}

	private Empresa convertirEmpresaModelToEmpresa(EmpresaModel empresaModel) throws IOException {
		Empresa empresa = new Empresa();
		if (empresa.getIdEmpresa() != null)
			empresa.setIdEmpresa(empresaModel.getIdEmpresa());

		empresa.setIdEmpresa(empresaModel.getIdEmpresa());
		empresa.setCaracterRif(empresaModel.getCaracterRif());
		empresa.setDireccion(empresaModel.getDireccion());
		empresa.setEmpresa(empresaModel.getNombre());
		empresa.setIdEstatusEmpresa(empresaModel.getIdEstatusEmpresa());
		empresa.setIdMunicipio(empresaModel.getIdMunicipio());
		empresa.setIdEmpresaCoe(empresaModel.getIdEmpresaCoe());
		empresa.setRif(Integer.valueOf(empresaModel.getRif()));
		empresa.setSigla(empresaModel.getSigla());
		if (empresaModel.getLogoEmpresa() != null)
			empresa.setLogo(Base64.getDecoder().decode(empresaModel.getLogoEmpresa().getBytes()));

		return empresa;
	}

	private SucursalModel convertirSucursalToSucursalModel(Sucursal sucursal) {
		SucursalModel sucursalModel = new SucursalModel(sucursal.getIdSucursal(), sucursal.getIdEmpresa(),
				sucursal.getIdEstatusSucursal(), sucursal.getFechaEstatus(), sucursal.getIdMunicipio(),
				sucursal.getLatitud(), sucursal.getLongitud(), sucursal.getSucursal(), sucursal.getAcopio());

		int idEstado = municipioRepository.findById(sucursal.getIdMunicipio()).get().getIdEstado();
		sucursalModel.setIdEstado(idEstado);

		return sucursalModel;
	}

	private Sucursal convertirSucursalModelToSucursal(SucursalModel sucursalModel) {
		Sucursal sucursal = new Sucursal();
		if (sucursalModel.getIdSucursal() != null)
			sucursal.setIdSucursal(sucursalModel.getIdSucursal());
		sucursal.setSucursal(sucursalModel.getNombre());
		sucursal.setIdEstatusSucursal(sucursalModel.getIdEstatusSucursal());
		sucursal.setIdMunicipio(sucursalModel.getIdMunicipio());
		sucursal.setLatitud(sucursalModel.getLatitud());
		sucursal.setLongitud(sucursalModel.getLongitud());
		sucursal.setIdEmpresa(sucursalModel.getIdEmpresa());
		sucursal.setAcopio(sucursalModel.getAcopio());
		return sucursal;
	}

	private void validarEmpresa(EmpresaModel empresaModel, BindingResult result) throws Exception {
		if (empresaModel.getSigla() != null && !empresaModel.getSigla().isEmpty()) {
			if (!empresaModel.getSigla().matches("[a-zA-Z]{3}")) {
				result.rejectValue("sigla", "", "debe tener 3 carácteres");
			} else {
				final List<Empresa> empresasBySigla = empresaRepository.findBySigla(empresaModel.getSigla());
				if (empresasBySigla != null
						&& empresasBySigla.stream().anyMatch(emp -> emp.getSigla().equals(empresaModel.getSigla())
								&& emp.getIdEmpresa() != empresaModel.getIdEmpresa())) {
					result.rejectValue("sigla", "", "ya existe esta sigla");
				}
			}
		}

		if (empresaModel.getRif() != null && !empresaModel.getRif().isEmpty()) {
			if (!empresaModel.getRif().matches("[0-9]{9}")) {
				result.rejectValue("rif", "", "debe tener 9 digitos");
			} else {
				final List<Empresa> empresasByRif = empresaRepository.findByRif(Integer.valueOf(empresaModel.getRif()));
				if (empresasByRif != null
						&& empresasByRif.stream().anyMatch(emp -> emp.getRif().equals(empresaModel.getRif())
								&& emp.getIdEmpresa() != empresaModel.getIdEmpresa())) {
					result.rejectValue("rif", "", "ya existe este rif");
				}
			}
		}

		if (empresaModel.getNombre() != null && !empresaModel.getNombre().isEmpty()
				&& (empresaModel.getNombre().length() > 100
						|| CARACTERES_ESPECIALES_PATTERN.matcher(empresaModel.getNombre()).find())) {
			result.rejectValue("nombre", "",
					"debe tener una longitud máxima de 100 sin caracteres especiales como $&+,:;=?@#|/{}\\");
		}

		if (empresaModel.getDireccion() != null && !empresaModel.getDireccion().isEmpty()
				&& (empresaModel.getDireccion().length() > 100
						|| CARACTERES_ESPECIALES_PATTERN.matcher(empresaModel.getDireccion()).find())) {
			result.rejectValue("direccion", "",
					"debe tener una longitud máxima de 100 sin caracteres especiales como $&+,:;=?@#|/{}\\");
		}

		if (empresaModel.getIdEmpresaCoe() != null) {
			if (!String.valueOf(empresaModel.getIdEmpresaCoe()).matches("[0-9]*")) {
				result.rejectValue("idEmpresaCoe", "", "debe tener solo digitos");
			} else {
				final List<Empresa> empresasByCoe = empresaRepository
						.findByIdEmpresaCoe(empresaModel.getIdEmpresaCoe());
				if (empresasByCoe != null && empresasByCoe.stream()
						.anyMatch(emp -> emp.getIdEmpresaCoe().equals(empresaModel.getIdEmpresaCoe())
								&& emp.getIdEmpresa() != empresaModel.getIdEmpresa())) {
					result.rejectValue("idEmpresaCoe", "", "ya existe este COE");
				}
			}
		}

		if (empresaModel.getIdMunicipio() != null && empresaModel.getIdMunicipio() == -1) {
			result.rejectValue("idMunicipio", "", "requerido");
		}

		if (empresaModel.getLogo() != null && empresaModel.getLogo().getBytes().length > 0) {
			if (!empresaModel.getLogo().getContentType().toLowerCase().equals("image/jpg")
					&& !empresaModel.getLogo().getContentType().toLowerCase().equals("image/jpeg")) {
				result.rejectValue("logo", "", "debe tener el formato JPG");
			} else if (empresaModel.getLogo().getSize() > 500000) {
				result.rejectValue("logo", "", "no puede ser mayor a 500Kb");
			}

		}
	}

	private void validarSucursal(SucursalModel sucursalModel, BindingResult result) {
		if (sucursalModel.getNombre() != null && !sucursalModel.getNombre().isEmpty()
				&& (sucursalModel.getNombre().length() > 100
						|| CARACTERES_ESPECIALES_PATTERN.matcher(sucursalModel.getNombre()).find())) {
			result.rejectValue("nombre", "",
					"debe tener una longitud máxima de 100 sin caracteres especiales como $&+,:;=?@#|/{}\\");
		}
		if (sucursalModel.getIdMunicipio() != null && sucursalModel.getIdMunicipio() == -1) {
			result.rejectValue("idMunicipio", "", "requerido");
		}
		if (sucursalModel.getLatitud() != null && !sucursalModel.getLatitud().isEmpty()
				&& (Double.parseDouble(sucursalModel.getLatitud()) > 90
						|| Double.parseDouble(sucursalModel.getLatitud()) < -90)) {
			result.rejectValue("latitud", "", "invalida");
		}
		if (sucursalModel.getLongitud() != null && !sucursalModel.getLongitud().isEmpty()
				&& (Double.parseDouble(sucursalModel.getLongitud()) > 180
						|| Double.parseDouble(sucursalModel.getLongitud()) < -180)) {
			result.rejectValue("longitud", "", "invalida");
		}
	}
}
