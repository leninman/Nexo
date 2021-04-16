
package com.beca.misdivisas.controller;

import java.io.ByteArrayOutputStream;
import java.sql.Timestamp;
import java.text.MessageFormat;
import java.util.Base64;
import java.util.Date;
import java.util.List;
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

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IEstadoRepo;
import com.beca.misdivisas.interfaces.IMunicipioRepo;
import com.beca.misdivisas.interfaces.IPerfilRepo;
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
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;
import com.beca.misdivisas.util.ValidationUtils;

@Controller
public class EmpresaController {

	@Autowired 
	private ObjectFactory<HttpSession> factory;   
	//private static final Pattern CARACTERES_ESPECIALES_PATTERN = Pattern.compile("[\\^\\#\\$\\%\\*\\+\\/\\:\\;\\<\\=\\>\\¿\\?\\@\\[\\]\\_\\{\\|\\}\\\\]");
	
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
	private IPerfilRepo perfileRepo;

	@GetMapping("/empresaHome")
	public String getEmpresas(Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		final List<Empresa> empresas = empresaRepository.findAllOrderByName();
		List<EmpresaModel> emoresasModel = empresas.stream().map((emp) -> convertirEmpresaListToEmpresaModel(emp))
				.collect(Collectors.toList());
		if (emoresasModel.isEmpty())
			emoresasModel = null;
		model.addAttribute(Constantes.EMPRESAS, emoresasModel);
		logServ.registrarLog(Constantes.LISTAR_EMPRESA, Constantes.TEXTO_ADMINISTRAR_EMPRESAS,
				Constantes.EMPRESAS, true, Util.getRemoteIp(request), usuario);

		return "mainEmpresas";
	}

	@GetMapping("/crearEmpresa")
	public String crearEmpresaForm(Model model) {
		EmpresaModel empresaModel = new EmpresaModel();
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute(Constantes.ESTADOS, estados);
		model.addAttribute(Constantes.CREAR, true);
		model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
		return "empresa/empresas";
	}

	@Transactional
	@PostMapping("empresaAgregar")
	public String addEmpresa(@Valid EmpresaModel empresaModel, BindingResult result, Model model) throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarEmpresa(empresaModel, result);
		if (result.hasErrors()) {
			model.addAttribute(Constantes.U_SUARIO, usuario);
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute(Constantes.ESTADOS, estados);
			if (empresaModel.getIdEstado() != null && empresaModel.getIdEstado().intValue() != -1) {
				final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
				model.addAttribute("municipios", municipios);
			}
			model.addAttribute(Constantes.CREAR, true);
			if (empresaModel.getLogoEmpresa() != null && !empresaModel.getLogoEmpresa().isEmpty()) {
				empresaModel.setImagenLogo(empresaModel.getLogoEmpresa());
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
		Empresa empresaNueva = empresaRepository.save(empresa);
		

		// Creo sucursal principal
		Sucursal sucursal = new Sucursal();
		sucursal.setAcopio(true);
		sucursal.setIdEmpresa(empresaNueva.getIdEmpresa());
		sucursal.setFechaCreacion(ts);
		sucursal.setFechaEstatus(ts);
		sucursal.setSucursal("Sucursal Principal");
		sucursal.setIdEstatusSucursal(empresaNueva.getIdEstatusEmpresa());
		sucursal.setLatitud("10.5060509");
		sucursal.setLongitud("-66.9032145");
		sucursal.setIdMunicipio(empresaNueva.getIdMunicipio());

		sucursalRepository.save(sucursal);
		
		//Creo los perfiles por defecto para la nueva empresa
		
		perfileRepo.crearPerfilesNuevaEmpresa(empresaNueva.getIdEmpresa());

		String detalle = MessageFormat.format(Constantes.ACCION_EMPRESA, Constantes.OP_CREAR, empresa.getEmpresa(),
				empresaNueva.getIdEmpresa(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.CREAR_EMPRESA, detalle, Constantes.EMPRESAS,
				true, Util.getRemoteIp(request), usuario);

		return "redirect:empresaListar?success";
	}

	@GetMapping("/empresaListar")
	public String empresaListar(Model model) {
		final List<Empresa> empresas = empresaRepository.findAllOrderByName();
		List<EmpresaModel> empresasModel = empresas.stream().map((emp) -> convertirEmpresaListToEmpresaModel(emp))
				.collect(Collectors.toList());
		if (empresasModel.isEmpty())
			empresasModel = null;
		model.addAttribute(Constantes.EMPRESAS, empresasModel);
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
			model.addAttribute(Constantes.ESTADOS, estados);
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
			model.addAttribute(Constantes.MUNICIPIOS, municipios);
			model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
			model.addAttribute(Constantes.EDITAR, true);
			return "empresa/empresas";
		}
	}

	@PostMapping("empresaActualizar")
	public String actualizarEmpresa(@Valid EmpresaModel empresaModel, BindingResult result, Model model)
			throws Exception {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarEmpresa(empresaModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(empresaModel.getIdEstado());
			model.addAttribute(Constantes.MUNICIPIOS, municipios);
			model.addAttribute(Constantes.ESTADOS, estados);
			model.addAttribute(Constantes.EDITAR, true);
			if (empresaModel.getLogoEmpresa() != null && !empresaModel.getLogoEmpresa().isEmpty()) {
				empresaModel.setImagenLogo(empresaModel.getLogoEmpresa());
	            Tika defaultTika = new Tika();
	            String deteccion = defaultTika.detect(empresaModel.getLogoEmpresa().getBytes());
	            String tipo = deteccion.split("/")[0];
	            String extension = deteccion.split("/")[1];
	           
	            if(tipo.equalsIgnoreCase("image")) {
	                ByteArrayOutputStream baos = new ByteArrayOutputStream();
	                ImageIO.write(Util.convertFile(empresaModel.getLogoEmpresa().getBytes(), 'l'), extension, baos);
	                empresaModel.setLogo(new MockMultipartFile("logo",Base64.getDecoder().decode(baos.toByteArray())));   
	            }

			}
			model.addAttribute(Constantes.EMPRESA_MODEL, empresaModel);
			return "empresa/empresas";
		}

		final Empresa empresaToUpdate = empresaRepository.findById(empresaModel.getIdEmpresa()).get();
		Empresa empresa = convertirEmpresaModelToEmpresa(empresaModel);

		if (!empresa.getIdEstatusEmpresa().equals(empresaToUpdate.getIdEstatusEmpresa())) {
			Date date = new Date();
			long time = date.getTime();
			Timestamp ts = new Timestamp(time);
			empresa.setFechaEstatus(ts);
		} else {
			empresa.setFechaEstatus(empresaToUpdate.getFechaEstatus());
		}

		empresa.setFechaCreacion(empresaToUpdate.getFechaCreacion());

		empresaRepository.save(empresa);

		String detalle = MessageFormat.format(Constantes.ACCION_EMPRESA, Constantes.OP_EDICION, empresa.getEmpresa(),
				empresa.getIdEmpresa(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.EDICION_EMPRESA, detalle, Constantes.EMPRESAS,
				true, Util.getRemoteIp(request), usuario);
		return "redirect:empresaListar?success";
	}

	//////// SUCURSAL API
	//////// /////////////////////////////////////////////////////////////////////////////////
	@GetMapping(path = "/sucursalHome/{idEmpresa}")
	public String getSucursales(@PathVariable("idEmpresa") int idEmpresa, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		model.addAttribute(Constantes.U_SUARIO, usuario);
		List<Sucursal> sucursales = sucursalRepository.findSucursalByEmpId(idEmpresa);
		Empresa empresa = empresaRepository.findById(idEmpresa);
		model.addAttribute("sigla", empresa.getSigla());
		model.addAttribute("idEmpresa", idEmpresa);
		if (sucursales.isEmpty())
			sucursales = null;
		model.addAttribute(Constantes.SUCURSALES, sucursales);
		model.addAttribute("listar", true);

		logServ.registrarLog(Constantes.LISTAR_SUCURSAL, Constantes.TEXTO_ADMINISTRAR_SUCURSALES,
				Constantes.EMPRESAS, true, Util.getRemoteIp(request), usuario);

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
		model.addAttribute(Constantes.ESTADOS, estados);
		model.addAttribute(Constantes.CREAR, true);
		model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);

		return "empresa/sucursales";
	}

	@PostMapping("sucursalAgregar")
	public String addSucursal(@Valid SucursalModel sucursalModel, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarSucursal(sucursalModel, result);
		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			model.addAttribute(Constantes.ESTADOS, estados);
			if (sucursalModel.getIdEstado() != null && sucursalModel.getIdEstado().intValue() != -1) {
				final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
				model.addAttribute(Constantes.MUNICIPIOS, municipios);
			}
			model.addAttribute(Constantes.CREAR, true);
			final List<Sucursal> sucursalesConAcopio = sucursalRepository
					.findSucursalByEmpIdAndAcopio(sucursalModel.getIdEmpresa(), true);
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
		Sucursal nuevaSucursal = sucursalRepository.save(sucursal);

		String detalle = MessageFormat.format(Constantes.ACCION_SUCURSAL, Constantes.OP_CREAR, sucursal.getSucursal(),
				nuevaSucursal.getIdSucursal(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.CREAR_SUCURSAL, detalle, Constantes.EMPRESAS,
				true, Util.getRemoteIp(request), usuario);

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
		Sucursal sucursal = sucursalRepository.findById(idSucursal).orElse(new Sucursal());
		SucursalModel sucursalModel = convertirSucursalToSucursalModel(sucursal);
		final List<Sucursal> sucursalesConAcopio = sucursalRepository
				.findSucursalByEmpIdAndAcopio(sucursal.getIdEmpresa(), true);
		boolean permitirAcopio = sucursalesConAcopio.isEmpty();
		model.addAttribute("permitirAcopio", permitirAcopio);
		final List<Estado> estados = estadoRepository.findAll();
		model.addAttribute(Constantes.ESTADOS, estados);
		final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
		model.addAttribute(Constantes.MUNICIPIOS, municipios);
		model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);
		model.addAttribute(Constantes.EDITAR, true);
		return "empresa/sucursales";
	}

	@PostMapping("sucursalActualizar")
	public String actualizarSucursal(@Valid SucursalModel sucursalModel, BindingResult result, Model model) {
		Usuario usuario = ((Usuario) factory.getObject().getAttribute(Constantes.USUARIO));
		model.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		validarSucursal(sucursalModel, result);
		Sucursal sucursalToUpdate = sucursalRepository.findById(sucursalModel.getIdSucursal()).get();

		if (result.hasErrors()) {
			final List<Estado> estados = estadoRepository.findAll();
			final List<Municipio> municipios = municipioRepository.findAllByIdEstado(sucursalModel.getIdEstado());
			model.addAttribute(Constantes.MUNICIPIOS, municipios);
			model.addAttribute(Constantes.ESTADOS, estados);
			final List<Sucursal> sucursalesConAcopio = sucursalRepository
					.findSucursalByEmpIdAndAcopio(sucursalToUpdate.getIdEmpresa(), true);
			boolean permitirAcopio = sucursalesConAcopio.isEmpty();
			model.addAttribute("permitirAcopio", permitirAcopio);
			model.addAttribute(Constantes.EDITAR, true);
			model.addAttribute(Constantes.SUCURSAL_MODEL, sucursalModel);
			return "empresa/sucursales";
		}

		Sucursal sucursal = convertirSucursalModelToSucursal(sucursalModel);

		if (!sucursal.getIdEstatusSucursal().equals(sucursalToUpdate.getIdEstatusSucursal())) {
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

		String detalle = MessageFormat.format(Constantes.ACCION_SUCURSAL, Constantes.OP_EDICION, sucursal.getSucursal(),
				sucursal.getIdSucursal(), usuario.getIdUsuario(), usuario.getNombreUsuario());
		logServ.registrarLog(Constantes.EDICION_SUCURSAL, detalle, Constantes.EMPRESAS,
				true, Util.getRemoteIp(request), usuario);

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
	
	/*@RequestMapping(value = "/validarRif", method = RequestMethod.POST,
            produces = MediaType.APPLICATION_JSON_VALUE)
	public String validarRif(String rif) {	
	Integer rifInt=Integer.valueOf(rif);
	String responseRif = null;
		final List<Empresa> empresasByRifSearch = empresaRepository.findByRif(Integer.valueOf(rifInt));		
		 
		if(empresasByRifSearch.stream().anyMatch(emp -> emp.getRif().equals(rifInt)
						&& !emp.getIdEmpresa().equals(rifInt))) 
		{
			responseRif = "V";
		}else 
		{
			responseRif = "F";
		}					
				
		return responseRif;
	}*/

	private EmpresaModel convertirEmpresaToEmpresaModel(Empresa empresa) {
		String rif = StringUtils.leftPad(String.valueOf(empresa.getRif()), 9, "0");
		EmpresaModel empresaModel = new EmpresaModel(empresa.getIdEmpresa(), empresa.getCaracterRif(),
				empresa.getDireccion(), empresa.getEmpresa(), empresa.getFechaCreacion(), empresa.getFechaEstatus(),
				empresa.getIdEstatusEmpresa(), empresa.getIdMunicipio(), (empresa.getIdEmpresaCoe()!=null ? empresa.getIdEmpresaCoe().toString() : null), rif,
				empresa.getSigla(), empresa.getLogo());

		int idEstado = municipioRepository.findById(empresa.getIdMunicipio()).get().getIdEstado();
		empresaModel.setIdEstado(idEstado);

		return empresaModel;
	}

	private EmpresaModel convertirEmpresaListToEmpresaModel(Empresa empresa) {
		String rif = StringUtils.leftPad(String.valueOf(empresa.getRif()), 9, "0");
		EmpresaModel empresaModel = new EmpresaModel(empresa.getIdEmpresa(), empresa.getCaracterRif(),
				empresa.getDireccion(), empresa.getEmpresa(), empresa.getFechaCreacion(), empresa.getFechaEstatus(),
				empresa.getIdEstatusEmpresa(), empresa.getIdMunicipio(), (empresa.getIdEmpresaCoe()!=null ? empresa.getIdEmpresaCoe().toString() : null), rif,
				empresa.getSigla(), empresa.getLogo());

		empresaModel.setEstatus(empresa.getEstatusEmpresa().getEstatusEmpresa());
		return empresaModel;
	}

	private Empresa convertirEmpresaModelToEmpresa(EmpresaModel empresaModel) throws Exception {
		Empresa empresa = new Empresa();
		if (empresa.getIdEmpresa() != null)
			empresa.setIdEmpresa(empresaModel.getIdEmpresa());

		empresa.setIdEmpresa(empresaModel.getIdEmpresa());
		empresa.setCaracterRif(empresaModel.getCaracterRif());
		empresa.setDireccion(empresaModel.getDireccion());
		empresa.setEmpresa(empresaModel.getNombre());
		empresa.setIdEstatusEmpresa(empresaModel.getIdEstatusEmpresa());
		empresa.setIdMunicipio(empresaModel.getIdMunicipio());
		empresa.setIdEmpresaCoe(Integer.valueOf(empresaModel.getIdEmpresaCoe()));
		empresa.setRif(Integer.valueOf(empresaModel.getRif()));
		empresa.setSigla(empresaModel.getSigla().toUpperCase());
		
		if (empresaModel.getLogo().getOriginalFilename() != null && !empresaModel.getLogo().getOriginalFilename().isEmpty()) {
            Tika defaultTika = new Tika();
            String deteccion = defaultTika.detect(empresaModel.getLogo().getBytes());
            String tipo = deteccion.split("/")[0];
            String extension = deteccion.split("/")[1];
           
            if(tipo.equalsIgnoreCase("image")) {
                ByteArrayOutputStream baos = new ByteArrayOutputStream();
                ImageIO.write(Util.convertFile(empresaModel.getLogoEmpresa().getBytes(), 'l'), extension, baos);
                empresa.setLogo(baos.toByteArray()); 
            }
		}else if (empresaModel.getIdEmpresa()!= null) {
			empresa.setLogo(empresaRepository.getLogoByIdEmpresa(empresaModel.getIdEmpresa()));
		}
		else {
			empresa.setLogo(null);
		}

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
		
		if (empresaModel.getRif() != null && !empresaModel.getRif().trim().isEmpty()) {
			
			empresaModel.setRif(empresaModel.getRif().trim());
			
			if (!empresaModel.getRif().matches("[0-9]{9}")) {
				result.rejectValue("rif", "", "debe tener 9 d\u00EDgitos");
			} 
			else if (!validaRif(empresaModel.getCaracterRif() + empresaModel.getRif().trim())) {
				result.rejectValue("rif", "", "inv\u00E1lido");
			} 
			else {
				final List<Empresa> empresasByRif = empresaRepository.findByCaracterRifAndRif(empresaModel.getCaracterRif(),Integer.valueOf(empresaModel.getRif().trim()));
				if (empresasByRif != null && empresasByRif.size() > 0) { 
					if (empresaModel.getIdEmpresa() == null) { // creando
						result.rejectValue("rif", "", "ya existe una empresa con este RIF");
					}
					else if (!empresasByRif.get(0).getIdEmpresa().equals(empresaModel.getIdEmpresa())){ // actualizando
						result.rejectValue("rif", "", "ya existe una empresa con este RIF");
					}
				}
			}
		}
		
		if (empresaModel.getNombre() != null && !empresaModel.getNombre().trim().isEmpty()) {
			
			empresaModel.setNombre(empresaModel.getNombre().trim());
			
			if (!empresaModel.getNombre().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("nombre", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (empresaModel.getNombre().length() > 100) {
				result.rejectValue("nombre", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}		
		
		if (empresaModel.getDireccion() != null && !empresaModel.getDireccion().trim().isEmpty()) {
			
			empresaModel.setDireccion(empresaModel.getDireccion().trim());
			
			if (!empresaModel.getDireccion().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("direccion", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (empresaModel.getDireccion().length() > 100) {
				result.rejectValue("direccion", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}
		
		if (empresaModel.getSigla() != null && !empresaModel.getSigla().trim().isEmpty()){
			
			empresaModel.setSigla(empresaModel.getSigla().trim().toUpperCase());
			
			if (!empresaModel.getSigla().matches("[a-zA-Z]{3}")) {
				result.rejectValue("sigla", "", "debe tener 3 caracteres alfab\u00E9ticos");
			}
			else {
				final List<Empresa> empresasBySigla = empresaRepository.findBySigla(empresaModel.getSigla());
				if (empresasBySigla != null	&&  empresasBySigla.size() > 0) {
					if(empresaModel.getIdEmpresa() == null) { // creando
						result.rejectValue("sigla", "", "ya existe esta sigla para otra empresa");	
					}
					else if (!empresasBySigla.get(0).getIdEmpresa().equals(empresaModel.getIdEmpresa())){ //actualizando
						result.rejectValue("sigla", "", "ya existe esta sigla para otra empresa");	
					}
				}
			}
		}
		
		if (empresaModel.getIdEmpresaCoe() != null && !empresaModel.getIdEmpresaCoe().trim().isEmpty()) {
			
			empresaModel.setIdEmpresaCoe(empresaModel.getIdEmpresaCoe().trim());
			
			if (!empresaModel.getIdEmpresaCoe().matches("[0-9]*")) {
				result.rejectValue("idEmpresaCoe", "", "debe tener solo d\u00EDgitos");
			} else {
				final List<Empresa> empresasByCoe = empresaRepository.findByIdEmpresaCoe(Integer.valueOf(empresaModel.getIdEmpresaCoe()));
				
				if (empresasByCoe != null && empresasByCoe.size() > 0) {
					if(empresaModel.getIdEmpresa() == null) { // creando
						result.rejectValue("idEmpresaCoe", "", "ya existe este ID de COE para otra empresa");	
					}
					else if (!empresasByCoe.get(0).getIdEmpresa().equals(empresaModel.getIdEmpresa())){ //actualizando
						result.rejectValue("idEmpresaCoe", "", "ya existe este ID de COE para otra empresa");
					}
				}
			}
		}

		if (empresaModel.getIdMunicipio() != null && empresaModel.getIdMunicipio().intValue() == -1) {
			result.rejectValue("idMunicipio", "", "requerido");
		}
		
		if (empresaModel.getLogo() != null && !empresaModel.getLogo().isEmpty() && empresaModel.getLogo().getBytes().length > 0) {
			if (!ValidationUtils.isValidImageType(empresaModel.getLogo())) {
				result.rejectValue("logo", "", "debe tener el formato JPG, GIF, BMP o PNG");
			} else if (!ValidationUtils.isValidImageSize(empresaModel.getLogo())) {
				result.rejectValue("logo", "", "no puede ser mayor a 1 MB");
			}
		}
	
	}


	private void validarSucursal(SucursalModel sucursalModel, BindingResult result) {
		
		
		if (sucursalModel.getNombre() != null && !sucursalModel.getNombre().trim().isEmpty()) {
			
			sucursalModel.setNombre(sucursalModel.getNombre().trim());
			
			if (!sucursalModel.getNombre().matches(Constantes.CARACTERES_PERMITIDOS_PATTERN.pattern())) {
				result.rejectValue("nombre", "", "contiene caracteres especiales no v\u00E1lidos");	
			}
			else if (sucursalModel.getNombre().length() > 100) {
				result.rejectValue("nombre", "", "debe tener una longitud m\u00E1xima de 100 caracteres");
			}
		}	
		
		
		if (sucursalModel.getIdMunicipio() != null && sucursalModel.getIdMunicipio().intValue() == -1) {
			result.rejectValue("idMunicipio", "", "requerido");
		}
		
		if (sucursalModel.getLatitud() != null && !sucursalModel.getLatitud().trim().isEmpty()) {
			
			sucursalModel.setLatitud(sucursalModel.getLatitud().trim());
			
			if (!sucursalModel.getLatitud().matches("^[-]?\\d+[\\.]?\\d*$") || Double.parseDouble(sucursalModel.getLatitud()) > 90 || Double.parseDouble(sucursalModel.getLatitud()) < -90)
				result.rejectValue("latitud", "", "invalida");
		}
		
		if (sucursalModel.getLongitud() != null && !sucursalModel.getLongitud().trim().isEmpty()) {
			
			sucursalModel.setLongitud(sucursalModel.getLongitud().trim());
			
			if (!sucursalModel.getLongitud().matches("^[-]?\\d+[\\.]?\\d*$") || Double.parseDouble(sucursalModel.getLongitud()) > 180 || Double.parseDouble(sucursalModel.getLongitud()) < -180)
				result.rejectValue("longitud", "", "invalida");
		}

	}

	private static boolean validaRif(String rif) {
		boolean result = false;
		int digito = -1;
		int per = 0;
		rif = rif.toUpperCase();
		// Valido que la letra inicial sea correcta
		try {
			switch (rif.charAt(0)) {
			case 'V':
				per = 1;
				break;
			case 'E':
				per = 2;
				break;
			case 'J':
				per = 3;
				break;
			case 'C':
				per = 4;
				break;
			case 'G':
				per = 5;
				break;
			default:
				return false;
			}

			if (per > 0) {
				int suma = (per * 4) + (Integer.parseInt(rif.substring(1, 2)) * 3)
						+ (Integer.parseInt(rif.substring(2, 3)) * 2) + (Integer.parseInt(rif.substring(3, 4)) * 7)
						+ (Integer.parseInt(rif.substring(4, 5)) * 6) + (Integer.parseInt(rif.substring(5, 6)) * 5)
						+ (Integer.parseInt(rif.substring(6, 7)) * 4) + (Integer.parseInt(rif.substring(7, 8)) * 3)
						+ (Integer.parseInt(rif.substring(8, 9)) * 2);

				int resto = suma % 11;

				if (resto > 1) {
					digito = 11 - resto;
				} else {
					digito = 0;
				}
			}

		} catch (Throwable ignored) {
		}
		result = (digito == Integer.parseInt(rif.substring(9)));
		return result;
	}
}