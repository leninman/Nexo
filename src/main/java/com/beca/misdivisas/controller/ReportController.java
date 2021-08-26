package com.beca.misdivisas.controller;

import java.math.BigDecimal;
import java.text.DateFormat;
import java.text.MessageFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Collections;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IEstatusRemesaRepo;
import com.beca.misdivisas.interfaces.IRemesaRepo;
import com.beca.misdivisas.interfaces.ISucursalRepo;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Pieza;
import com.beca.misdivisas.jpa.Remesa;
import com.beca.misdivisas.jpa.RemesaDetalle;
import com.beca.misdivisas.jpa.Sucursal;
import com.beca.misdivisas.jpa.Usuario;
import com.beca.misdivisas.model.DatosReporteMapa;
import com.beca.misdivisas.model.ReporteIrregularidades;
import com.beca.misdivisas.model.ReporteRemesa;
import com.beca.misdivisas.model.ReporteSucursal;
import com.beca.misdivisas.model.ReporteSucursalMapa;
import com.beca.misdivisas.services.FeriadosService;
import com.beca.misdivisas.services.LogService;
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class ReportController {

	@Autowired
	private FeriadosService feriadoServ;

	@Autowired
	private IRemesaRepo remesaRepo;

	@Autowired
	private IEmpresaRepo empresaRepo;

	@Autowired
	private ObjectFactory<HttpSession> factory;

	@Autowired
	private IEstatusRemesaRepo estatusRemesaRepo;

	@Autowired
	private ISucursalRepo sucursalRepo;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private HttpServletResponse response;

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(ReportController.class);

	@Autowired
	private LogService logServ;

	@GetMapping("/reporteG")
	public String reporte() {
		return Constantes.REPORTE_GRAFICO;
	}

	@GetMapping(value = "/reporte")
	public String reporte(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		int id = usuario.getIdEmpresa();
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
		BigDecimal montoPendienteDolar = remesaRepo.getLastRemesaByStatus(id, Constantes.USD,
				Constantes.ESTATUS_PENDIENTE);
		BigDecimal montoPendienteEuro = remesaRepo.getLastRemesaByStatus(id, Constantes.EUR,
				Constantes.ESTATUS_PENDIENTE);
		BigDecimal montoPendienteEntregaDolar = remesaRepo.getLastRemesaByStatus(id, Constantes.USD,
				Constantes.ESTATUS_PENDIENTE_ENTREGA);
		BigDecimal montoPendienteEntregaEuro = remesaRepo.getLastRemesaByStatus(id, Constantes.EUR,
				Constantes.ESTATUS_PENDIENTE_ENTREGA);
		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());

		BigDecimal saldoTOT;
		BigDecimal tmp;

		saldoTOT = remesaRepo.getTotalByEmpresaAndDate(id, Constantes.USD, formato2.format(new Date()));

		if (montoPendienteDolar == null)
			montoPendienteDolar = BigDecimal.valueOf(0.00);
		if (montoPendienteEuro == null)
			montoPendienteEuro = BigDecimal.valueOf(0.00);
		if (montoPendienteEntregaDolar == null) {
			montoPendienteEntregaDolar = BigDecimal.valueOf(0.00);
		}
		if (montoPendienteEntregaEuro == null) {
			montoPendienteEntregaEuro = BigDecimal.valueOf(0.00);
		}
		List<Date> fechas = feriadoServ.obtenerFeriados();
		modelo.addAttribute(Constantes.FECHA_CORTE, Util.diaHabilPrevio(fechas));

		tmp = saldoTOT.subtract(montoPendienteEntregaDolar);

		modelo.addAttribute(Constantes.TOTAL_DOLARES, Util.formatMonto(String.valueOf(saldoTOT)));
		modelo.addAttribute(Constantes.PENDIENTE_DOLARES, Util.formatMonto(montoPendienteDolar.toString()));
		modelo.addAttribute(Constantes.PENDIENTE_ENTREGA_DOLARES, Util.formatMonto(montoPendienteEntregaDolar.toString()));
		modelo.addAttribute(Constantes.DISPONIBLE_DOLARES, Util.formatMonto(String.valueOf(tmp)));
		saldoTOT = remesaRepo.getTotalByEmpresaAndDate(id, Constantes.EUR, formato2.format(new Date()));

		tmp = saldoTOT.subtract(montoPendienteEntregaEuro);

		modelo.addAttribute(Constantes.TOTAL_EUROS, Util.formatMonto(String.valueOf(saldoTOT)));
		modelo.addAttribute(Constantes.PENDIENTE_EUROS, Util.formatMonto(montoPendienteEuro.toString()));
		modelo.addAttribute(Constantes.PENDIENTE_ENTREGA_EUROS, Util.formatMonto(montoPendienteEntregaEuro.toString()));
		modelo.addAttribute(Constantes.DISPONIBLE_EUROS, Util.formatMonto(String.valueOf(tmp)));

		logServ.registrarLog(Constantes.POSICION_CONSOLIDADA, Constantes.POSICION_CONSOLIDADA,
				Constantes.POSICION_CONSOLIDADA, true, Util.getRemoteIp(request), usuario);

		return Constantes.REPORTE;
	}

	@GetMapping(path = "/remesa/{fechaI}/{fechaF}/{moneda}", produces = "application/json")
	@ResponseBody
	public List<ReporteSucursal> getAllRemesas(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String moneda) {
		DateFormat formato1 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);

		List<ReporteSucursal> reportes = new ArrayList<>();
		ReporteSucursal rs = null;
		RemesaDetalle remesaDetalle = null;
		BigDecimal saldo = new BigDecimal(0);

		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		try {
			int id = usuario.getIdEmpresa();

			saldo = remesaRepo.getTotalByEmpresaAndDate(id, Integer.parseInt(moneda),
					fechaI + Constantes.FORMATO_HORA_0);

			List<RemesaDetalle> remesaDetalles = remesaRepo.findRemeDetalle(id, Integer.parseInt(moneda),
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959));

			for (int i = 0; i < remesaDetalles.size(); i++) {
				remesaDetalle = remesaDetalles.get(i);
				Collections.sort(remesaDetalle.getRemesa().getRemesaDetalles(), Collections.reverseOrder());
				rs = new ReporteSucursal();

				if (!remesaDetalle.getRemesa().getRemesaDetalles().isEmpty()) {
					rs.setFecha(formato1.format(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getFecha()));
				}

				rs.setReferencia(remesaDetalle.getRemesa().getCartaPorte());
				String desc = "";
				if (remesaDetalle.getRemesa().getOperacion().getIdTipoOperacion() == Constantes.CREDITO) {
					if (remesaDetalle.getRemesa().getDescripcion() != null
							&& !remesaDetalle.getRemesa().getDescripcion().isEmpty()) {
						String[] s = remesaDetalle.getRemesa().getDescripcion().split(" ");

						if (s.length > 2) {
							for (int j = 2; j < s.length; j++) {
								desc += " " + s[j];
							}
							rs.setConcepto(remesaDetalle.getRemesa().getOperacion().getOperacion() + " - " + desc);
						} else {
							rs.setConcepto(remesaDetalle.getRemesa().getOperacion().getOperacion() + " - "
									+ remesaDetalle.getRemesa().getSucursal().getSucursal());
						}
					}

					rs.setCredito(Util
							.formatMonto(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto().toString()));
					saldo = saldo.add(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto());
				} else {

					rs.setDebito(Util.formatMonto(
							remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto().negate().toString()));
					desc = "";
					if (remesaDetalle.getRemesa().getDescripcion() != null
							&& !remesaDetalle.getRemesa().getDescripcion().isEmpty()) {
						String[] s = remesaDetalle.getRemesa().getDescripcion().split(" ");
						if (s.length > 2) {
							for (int j = 2; j < s.length; j++) {
								desc += " " + s[j];
							}
							rs.setConcepto(remesaDetalle.getRemesa().getOperacion().getOperacion() + " - " + desc);
						} else {
							rs.setConcepto(remesaDetalle.getRemesa().getOperacion().getOperacion() + " - "
									+ remesaDetalle.getRemesa().getSucursal().getSucursal());
						}
					}

					saldo = saldo.subtract(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto());
				}

				rs.setSaldo(Util.formatMonto(saldo.toString()));
				reportes.add(rs);

				if (remesaDetalle.getRemesa().getOperacion().getIdTipoOperacion() == Constantes.CREDITO && remesaDetalle
						.getRemesa().getRemesaDetalles().get(0).getIdEstatusRemesa() == Constantes.ESTATUS_CANCELADA) {
					rs = new ReporteSucursal();
					rs.setFecha(formato1.format(remesaDetalle.getRemesa().getRemesaDetalles()
							.get(remesaDetalle.getRemesa().getRemesaDetalles().size() - 1).getFecha()));
					rs.setReferencia(remesaDetalle.getRemesa().getCartaPorte() + " - " + Constantes.CANCELADA);
					rs.setDebito(Util.formatMonto(
							remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto().negate().toString()));
					rs.setConcepto(remesaDetalle.getRemesa().getOperacion().getOperacion() + " - "
							+ Constantes.STRING_CANCELADA);

					saldo = saldo.subtract(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getMonto());
					rs.setSaldo(Util.formatMonto(saldo.toString()));
					reportes.add(rs);
				}

				BigDecimal tmp = getMontoFromPiezas(remesaDetalle.getRemesa().getPiezas());
				if (tmp.compareTo(new BigDecimal(0)) == 1
						&& remesaDetalle.getRemesa().getIdOperacion() != Constantes.DIFERENCIA_FALTANTE) {
					rs = new ReporteSucursal();
					rs.setFecha(formato1.format(remesaDetalle.getRemesa().getRemesaDetalles().get(0).getFecha()));
					rs.setReferencia(remesaDetalle.getRemesa().getCartaPorte() + "-" + Constantes.STRING_BNA_AV);
					rs.setDebito(Util.formatMonto(tmp.negate().toString()));
					rs.setConcepto(
							remesaDetalle.getRemesa().getOperacion().getOperacion() + " - " + Constantes.STRING_BNA);
					saldo = saldo.subtract(tmp);
					rs.setSaldo(Util.formatMonto(saldo.toString()));
					reportes.add(rs);
				}
			}

		} catch (ParseException e) {
			logServ.registrarLog(Constantes.POSICION_CONSOLIDADA, e.getLocalizedMessage(), Constantes.POSICION_CONSOLIDADA,
					false, Util.getRemoteIp(request), usuario);
		}
		String detalle = MessageFormat.format(Constantes.CONSULTA_POR_PARAMETROS, fechaI, fechaF, moneda);
		logServ.registrarLog(Constantes.POSICION_CONSOLIDADA, detalle, Constantes.POSICION_CONSOLIDADA,
				true, Util.getRemoteIp(request), usuario);

		return reportes;
	}

	@GetMapping(value = "/reporteNoAptos")
	public String reporteNoAptos(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		int id = usuario.getIdEmpresa();
		List<Remesa> remesasDolar = remesaRepo.getLasRemesaByMoneda(id, Constantes.USD);
		List<Remesa> remesasEuro = remesaRepo.getLasRemesaByMoneda(id, Constantes.EUR);
		BigDecimal noAptoD = new BigDecimal(0), noAptoE = new BigDecimal(0), retiroD = new BigDecimal(0),
				retiroE = new BigDecimal(0);
		for (int i = 0; i < remesasDolar.size(); i++) {
			if (!remesasDolar.get(i).getPiezas().isEmpty()) {
				if (remesasDolar.get(i).getIdOperacion() != Constantes.RETIRO_BNA
						&& remesasDolar.get(i).getIdOperacion() != Constantes.DEBITO_POR_REINTEGRO_BNA) {
					if (remesasDolar.get(i).getIdOperacion() != Constantes.DIFERENCIA_FALTANTE)
						for (int j = 0; j < remesasDolar.get(i).getPiezas().size(); j++) {
							noAptoD = noAptoD.add(new BigDecimal(
									remesasDolar.get(i).getPiezas().get(j).getCantidadNoApta() * remesasDolar.get(i)
											.getPiezas().get(j).getDenominacion().getDenominacion()));
						}
				} else {
					for (int j = 0; j < remesasDolar.get(i).getPiezas().size(); j++) {
						retiroD = retiroD.add(new BigDecimal(remesasDolar.get(i).getPiezas().get(j).getCantidadNoApta()
								* remesasDolar.get(i).getPiezas().get(j).getDenominacion().getDenominacion()));
					}
				}
			}
		}
		for (int i = 0; i < remesasEuro.size(); i++) {
			if (!remesasEuro.get(i).getPiezas().isEmpty()) {
				if (remesasEuro.get(i).getIdOperacion() != Constantes.RETIRO_BNA
						&& remesasEuro.get(i).getIdOperacion() != Constantes.DEBITO_POR_REINTEGRO_BNA) {
					for (int j = 0; j < remesasEuro.get(i).getPiezas().size(); j++) {
						noAptoE = noAptoE.add(new BigDecimal(remesasEuro.get(i).getPiezas().get(j).getCantidadNoApta()
								* remesasEuro.get(i).getPiezas().get(j).getDenominacion().getDenominacion()));
					}
				} else {
					for (int j = 0; j < remesasEuro.get(i).getPiezas().size(); j++) {
						retiroE = retiroE.add(new BigDecimal(remesasEuro.get(i).getPiezas().get(j).getCantidadNoApta()
								* remesasEuro.get(i).getPiezas().get(j).getDenominacion().getDenominacion()));
					}
				}
			}
		}

		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());

		List<Date> fechas = feriadoServ.obtenerFeriados();
		modelo.addAttribute(Constantes.FECHA_CORTE, Util.diaHabilPrevio(fechas));

		if (!remesasDolar.isEmpty()) {
			noAptoD = noAptoD.subtract(retiroD);
			modelo.addAttribute(Constantes.TOTAL_DOLARES, Util.formatMonto(noAptoD.toString()));
		} else {
			modelo.addAttribute(Constantes.TOTAL_DOLARES, "0,00");
		}

		if (!remesasEuro.isEmpty()) {
			noAptoE = noAptoE.subtract(retiroE);
			modelo.addAttribute(Constantes.TOTAL_EUROS, Util.formatMonto(noAptoE.toString()));
		} else {
			modelo.addAttribute(Constantes.TOTAL_EUROS, "0,00");
		}
		logServ.registrarLog(Constantes.POSICION_BNA, Constantes.POSICION_BNA, Constantes.POSICION_BNA,
				true, Util.getRemoteIp(request), usuario);

		return Constantes.REPORTE_NA;
	}

	@GetMapping(path = "/remesaNoApta/{fechaI}/{fechaF}/{moneda}", produces = "application/json")
	@ResponseBody
	public List<ReporteSucursal> getAllRemesasNoAptas(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String moneda) {
		DateFormat formato1 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		List<ReporteSucursal> reportes = new ArrayList<>();

		BigDecimal monto = new BigDecimal(0);
		ReporteSucursal rs = null;
		Remesa remesa = null;
		try {
			int id = usuario.getIdEmpresa();
			List<Remesa> remesas = remesaRepo.findRemesaByEmpresaId(id, Integer.parseInt(moneda),
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959));

			for (int i = 0; i < remesas.size(); i++) {
				remesa = remesas.get(i);
				if (remesa.getIdOperacion() != Constantes.DIFERENCIA_FALTANTE) {
					rs = new ReporteSucursal();
					List<Pieza> piezas = remesa.getPiezas();
					for (Iterator<Pieza> iterator = piezas.iterator(); iterator.hasNext();) {
						Pieza pieza = iterator.next();
						if (pieza.getCantidadNoApta() > 0) {
							rs.setFecha(formato1.format(remesa.getRemesaDetalles().get(0).getFecha()));
							rs.setReferencia(remesa.getCartaPorte());

							for (Pieza piezae : piezas) {
								int parcial = piezae.getCantidadNoApta() * piezae.getDenominacion().getDenominacion();
								monto = monto.add(new BigDecimal(parcial));
							}
							if (remesa.getIdOperacion().equals(Constantes.RETIRO_BNA)
									|| remesa.getIdOperacion().equals(Constantes.DEBITO_POR_REINTEGRO_BNA))
								monto = monto.negate();

							rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + Constantes.STRING_BNA);
							rs.setSaldo(Util.formatMonto(monto.toString()));
							reportes.add(rs);
							monto = new BigDecimal(0);
							break;
						}
						monto = new BigDecimal(0);
					}
				}
			}

		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
		}

		String detalle = MessageFormat.format(Constantes.CONSULTA_POR_PARAMETROS, fechaI, fechaF, moneda);
		logServ.registrarLog(Constantes.POSICION_BNA, detalle, Constantes.POSICION_BNA, true,
				Util.getRemoteIp(request), usuario);
		Collections.sort(reportes);
		return reportes;
	}

	@GetMapping(path = "/irregularidades/{cartaPorte}", produces = "application/json")
	@ResponseBody
	public List<ReporteIrregularidades> getIrregularidades(@PathVariable String cartaPorte) {
		DateFormat formato1 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		List<ReporteIrregularidades> irregularidades = new ArrayList<>();

		ReporteIrregularidades ri = null;
		int id = usuario.getIdEmpresa();
		try {
			List<Remesa> remesas = remesaRepo.findRemesaByCartaporteAndIdEmpresa(id, cartaPorte);

			for (int i = 0; i < remesas.size(); i++) {
				if (!remesas.get(i).getPiezas().isEmpty()
						&& remesas.get(i).getIdOperacion() != Constantes.DIFERENCIA_FALTANTE) {
					for (int j = 0; j < remesas.get(i).getPiezas().size(); j++) {
						if (remesas.get(i).getPiezas().get(j).getCantidadNoApta() > 0) {
							ri = new ReporteIrregularidades();
							ri.setFecha(formato1.format(remesas.get(i).getRemesaDetalles().get(0).getFecha()));
							ri.setReferencia(remesas.get(i).getCartaPorte());
							ri.setConcepto(remesas.get(i).getOperacion().getOperacion() + " - "
									+ remesas.get(i).getSucursal().getSucursal());
							ri.setClasificacion(remesas.get(i).getOperacion().getOperacion());
							ri.setCentro(remesas.get(i).getAgencia().getAgencia());
							ri.setDenominacion(
									remesas.get(i).getPiezas().get(j).getDenominacion().getDenominacion().toString());
							ri.setCantidad(remesas.get(i).getPiezas().get(j).getCantidadNoApta().toString());
							ri.setMoneda(remesas.get(i).getPiezas().get(j).getMoneda().getMoneda());
							irregularidades.add(ri);
						}
					}
				}
			}
		} catch (NumberFormatException e) {
			logServ.registrarLog(Constantes.POSICION_BNA, "ERROR: "+e.getLocalizedMessage(), Constantes.POSICION_BNA, false,
					Util.getRemoteIp(request), usuario);
		}
		String detalle = MessageFormat.format(Constantes.CONSULTA_IRREGULARIDADES, cartaPorte);
		logServ.registrarLog(Constantes.POSICION_BNA, detalle, Constantes.POSICION_BNA, true,
				Util.getRemoteIp(request), usuario);

		return irregularidades;
	}

	@GetMapping(value = "/trackingRemesas")
	public String trackingRemesas(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		int id = usuario.getIdEmpresa();

		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());
		String detalle = MessageFormat.format(Constantes.TRACKING_REMESAS, id);
        logServ.registrarLog(Constantes.TRACKING_REMESAS, detalle, Constantes.TRACKING_REMESAS, true,
        Util.getRemoteIp(request), usuario);
		return Constantes.REPORTE_TRACK;
	}

	@GetMapping(path = "/remesabycartaporte/{fechaI}/{fechaF}/{cartaPorte}", produces = "application/json")
	@ResponseBody
	public List<ReporteRemesa> getRemesaPorCataporte(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String cartaPorte) {
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMM);
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		List<ReporteRemesa> rem = new ArrayList<>();
		int id = usuario.getIdEmpresa();
		List<Remesa> remesas = null;
		Date d1, d2;
		ReporteRemesa reprem = null;
		String detalle = null;

		try {
			d1 = formato2.parse(fechaI + Constantes.FORMATO_HORA_0);
			d2 = formato2.parse(fechaF + Constantes.FORMATO_HORA_235959);
			remesas = remesaRepo.findRemesaByCartaporteAndIdEmpresaByDate(id, cartaPorte, d1, d2);

			if (remesas != null && !remesas.isEmpty()) {
				for (Remesa remesa : remesas) {
					for (RemesaDetalle rd : remesa.getRemesaDetalles()) {

						if (rd.getFecha().after(d1) && rd.getFecha().before(d2)) {
							reprem = new ReporteRemesa();
							if (remesa.getRemesaDetalles().get(0).getIdMoneda() == Constantes.USD)
								reprem.setMoneda(Constantes.USD_STRING);
							else if (remesa.getRemesaDetalles().get(0).getIdMoneda() == Constantes.EUR)
								reprem.setMoneda(Constantes.EUR_STRING);

							reprem.setFecha(formato.format(rd.getFecha()));
							reprem.setReferencia(remesa.getCartaPorte());
							reprem.setEstado(estatusRemesaRepo.findEstatusRemesaById(rd.getIdEstatusRemesa())
									.getEstatusRemesa());
							reprem.setMonto(Util.formatMonto(rd.getMonto().toString()));
							reprem.setCentro(remesa.getAgencia().getAgencia());
							rem.add(reprem);
						}
					}
				}
			}
			detalle = MessageFormat.format(Constantes.CONSULTA_POR_CARTAPORTE, fechaI, fechaF, cartaPorte);

		} catch (Exception e) {
			logServ.registrarLog(Constantes.TRACKING_REMESAS, "ERROR: "+e.getLocalizedMessage(), Constantes.TRACKING_REMESAS,
					false, Util.getRemoteIp(request), usuario);
		}

		logServ.registrarLog(Constantes.TRACKING_REMESAS, detalle, Constantes.TRACKING_REMESAS, true, Util.getRemoteIp(request), usuario);

		return rem;
	}

	@PostMapping(value = "/reporte")
	public String getAcumuladoSucursal(@RequestParam("sucursalId") int idSucursal, Model modelo) {
		modelo.addAttribute(Constantes.ID_SUCURSAL, idSucursal);
		factory.getObject().removeAttribute(Constantes.ID_SUCURSAL);
		factory.getObject().setAttribute(Constantes.ID_SUCURSAL, idSucursal);
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		int id = usuario.getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());

		Optional<Sucursal> sucursal = sucursalRepo.findById(idSucursal);
		if (sucursal.isPresent())
			modelo.addAttribute("nombreSucursal", sucursal.get().getSucursal());

		BigDecimal montoDolar = getMontoTotalBySucursal(idSucursal, Constantes.USD);
		BigDecimal montoEuro = getMontoTotalBySucursal(idSucursal, Constantes.EUR);

		if (montoDolar == null)
			montoDolar = new BigDecimal(0);

		if (montoEuro == null)
			montoEuro = new BigDecimal(0);

		modelo.addAttribute(Constantes.TOTAL_DOLARES, Util.formatMonto(montoDolar.toString()));
		modelo.addAttribute(Constantes.TOTAL_EUROS, Util.formatMonto(montoEuro.toString()));

		String detalle = "Consulta: idSucursal (" + idSucursal + "); ";

		HttpSession session = factory.getObject();
		logServ.registrarLog(Constantes.TEXTO_REPORTE_MAPA, detalle, Constantes.TEXTO_REPORTE_MAPA, true,
				Util.getRemoteIp(request), (Usuario) session.getAttribute(Constantes.USUARIO));

		return Constantes.REPORTE_GRAFICO;

	}

	@GetMapping(path = "/reporte/sucursalesG/{moneda}", produces = "application/json")
	@ResponseBody
	public DatosReporteMapa getMontosPorMes(@PathVariable int moneda) {
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_DDMMYYYY000);
		String[] nombreMes = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		BigDecimal[] montos = new BigDecimal[12];
		String[] meses = new String[12];
		ReporteSucursalMapa reporte = null;
		DatosReporteMapa rm = new DatosReporteMapa();
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		int idSucursal = (int) factory.getObject().getAttribute(Constantes.ID_SUCURSAL);
		LocalDate dd = LocalDate.now().minusMonths(12).withDayOfMonth(1);
		List<ReporteSucursalMapa> reportes, reportesS, reportesF;
		try {
			reportes = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
					Constantes.RECEPCION_EFECTIVO, formato.parse(dd.toString() + Constantes.FORMATO_HORA_0));
			reportesS = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
					Constantes.DIFERENCIA_SOBRANTE, formato.parse(dd.toString() + Constantes.FORMATO_HORA_0));

			reportes = calculaMontos(reportes, reportesS, Constantes.CREDITO);

			reportesF = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_ENTREGADA,
					Constantes.DIFERENCIA_FALTANTE, formato.parse(dd.toString() + Constantes.FORMATO_HORA_0));

			reportes = calculaMontos(reportes, reportesF, Constantes.DEBITO);

			for (int i = 0; i < reportes.size(); i++) {
				reporte = reportes.get(i);
				meses[reporte.getMes() - 1] = nombreMes[reporte.getMes() - 1] + '\'' + reporte.getAno() % 100;
				montos[reporte.getMes() - 1] = reporte.getSuma();
			}

			for (int i = 0; i < meses.length; i++) {
				if (meses[i] == null) {
					meses[i] = nombreMes[i];
				}
			}

			rm.setMeses(meses);
			rm.setMontos(montos);

		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
		}
		String detalle = MessageFormat.format(Constantes.CONSULTA_POR_SUCURSAL, idSucursal, moneda);
		logServ.registrarLog(Constantes.TEXTO_REPORTE_GRAFICO, detalle, Constantes.TEXTO_REPORTE_GRAFICO, true,
				Util.getRemoteIp(request), usuario);

		return rm;
	}

	private BigDecimal getMontoFromPiezas(List<Pieza> piezas) {
		BigDecimal monto = new BigDecimal(0);
		BigDecimal parcial = new BigDecimal(0);
		if (piezas != null) {
			for (Iterator<Pieza> iterator = piezas.iterator(); iterator.hasNext();) {
				Pieza pieza = iterator.next();
				if (pieza.getCantidadNoApta() > 0) {
					parcial = BigDecimal.valueOf(pieza.getCantidadNoApta() * pieza.getDenominacion().getDenominacion());
					monto = monto.add(parcial);
				}
			}
		}
		return monto;
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

	private List<ReporteSucursalMapa> calculaMontos(List<ReporteSucursalMapa> recepcion,
			List<ReporteSucursalMapa> mixto, int operacion) {
		for (int i = 0; i < recepcion.size(); i++) {
			for (int j = 0; j < mixto.size(); j++) {
				if (recepcion.get(i).getAno() == mixto.get(j).getAno()) {
					if ((recepcion.get(i).getMes() == mixto.get(j).getMes())) {
						if (operacion == 1)
							recepcion.get(i).setSuma(recepcion.get(i).getSuma().add(mixto.get(j).getSuma()));
						else
							recepcion.get(i).setSuma(recepcion.get(i).getSuma().subtract(mixto.get(j).getSuma()));
					}
				}
			}
		}
		return recepcion;
	}

	@GetMapping(value = "/remesasPendientes")
	public String remesasPendientes(Model modelo) {

		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));

		int id = usuario.getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());

		List<Date> fechas = feriadoServ.obtenerFeriados();
		modelo.addAttribute(Constantes.FECHA_CORTE, Util.diaHabilPrevio(fechas));
		response.setHeader("Set-Cookie", "key=value; HttpOnly; SameSite=strict");

		return Constantes.REPORTE_REMESAS_PENDIENTES;
	}

	@GetMapping(path = "/remesaEntregaPendiente", produces = "application/json")
	@ResponseBody
	public List<ReporteRemesa> getRemesasPendientes() {
		DateFormat formato = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		List<Remesa> remesas = null;
		List<ReporteRemesa> rem = new ArrayList<>();
		ReporteRemesa reprem = null;
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		int id = usuario.getIdEmpresa();
		try {
			remesas = remesaRepo.findRemesasPendientesByIdEmpresa(id);
			if (remesas != null && !remesas.isEmpty()) {
				for (Remesa remesa : remesas) {

					for (RemesaDetalle rd : remesa.getRemesaDetalles()) {
						reprem = new ReporteRemesa();
						if (remesa.getRemesaDetalles().get(0).getIdMoneda() == Constantes.USD)
							reprem.setMoneda(Constantes.USD_STRING);
						else if (remesa.getRemesaDetalles().get(0).getIdMoneda() == Constantes.EUR)
							reprem.setMoneda(Constantes.EUR_STRING);

						reprem.setFecha(formato.format(rd.getFecha()));

						Calendar cal = new GregorianCalendar();
						Calendar cal2 = new GregorianCalendar();

						cal.setTime(rd.getFecha());
						cal2.setTime(new Date());

						List<Date> fechas = feriadoServ.obtenerFeriados();

						int dias = Util.getDiasHabiles(cal, cal2, fechas);

						reprem.setCentro(String.valueOf(dias));
						reprem.setReferencia(remesa.getCartaPorte());

						if (remesa.getDescripcion() != null && !remesa.getDescripcion().isEmpty()) {
							String desc = "";
							String[] s = remesa.getDescripcion().split(" ");
							if (s.length > 2) {
								for (int j = 2; j < s.length; j++) {
									desc += " " + s[j];
								}
								reprem.setEstado(estatusRemesaRepo.findEstatusRemesaById(rd.getIdEstatusRemesa())
										.getEstatusRemesa() + " - " + desc);
							} else {
								reprem.setEstado(estatusRemesaRepo.findEstatusRemesaById(rd.getIdEstatusRemesa())
										.getEstatusRemesa() + " - " + remesa.getSucursal().getSucursal());
							}
						} else {
							reprem.setEstado(estatusRemesaRepo.findEstatusRemesaById(rd.getIdEstatusRemesa())
									.getEstatusRemesa());
						}
						reprem.setMonto(Util.formatMonto(rd.getMonto().toString()));
						rem.add(reprem);
					}
				}
			}

		} catch (NumberFormatException e) {
			logger.error(e.getLocalizedMessage());
		}

		String detalle = MessageFormat.format(Constantes.CONSULTA_PENDIENTE_ENTREGA, id);
		logServ.registrarLog(Constantes.TEXTO_REMESAS_PENDIENTES, detalle, Constantes.TEXTO_REMESAS_PENDIENTES,
				true, Util.getRemoteIp(request), usuario);

		return rem;
	}

	@GetMapping(value = "/reporteSucursal")
	public String reporteSucursal(Model modelo) {
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);
		com.beca.misdivisas.model.Usuario usuarioModel = new com.beca.misdivisas.model.Usuario();
		usuarioModel.setUsuario(usuario);
		modelo.addAttribute(Constantes.U_SUARIO, usuarioModel);
		modelo.addAttribute(Constantes.MENUES, factory.getObject().getAttribute(Constantes.USUARIO_MENUES));
		int id = usuario.getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);

		modelo.addAttribute(Constantes.CLIENTE,
				Util.formatRif(empresa.getCaracterRif(), empresa.getRif()) + " " + empresa.getEmpresa());

		List<Date> fechas = feriadoServ.obtenerFeriados();
		modelo.addAttribute(Constantes.FECHA_CORTE, Util.diaHabilPrevio(fechas));

		List<Sucursal> sucs = sucursalRepo.findSucursalByEmpId(id);
		modelo.addAttribute(Constantes.SUCURSALES, sucs);
		
		String detalle = MessageFormat.format(Constantes.REPORTE_SUCURSAL, id);
        logServ.registrarLog(Constantes.REPORTE_SUCURSAL, detalle, Constantes.REPORTE_SUCURSAL, true,
                Util.getRemoteIp(request), usuario);
		
		return Constantes.REPORTE_SUCURSAL;
	}

	@GetMapping(path = "/reporteSucursal/{fechaI}/{fechaF}/{sucursal}/{moneda}", produces = "application/json")
	@ResponseBody
	public List<ReporteSucursal> getReporteSucursal(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable int sucursal, @PathVariable int moneda) {
		DateFormat formato1 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYY);
		DateFormat formato2 = new SimpleDateFormat(Constantes.FORMATO_FECHA_DDMMYYYYHHMMSS);

		List<ReporteSucursal> reportes = new ArrayList<ReporteSucursal>();
		ReporteSucursal rs = null;
		Remesa remesa = null;
		Usuario usuario = (Usuario) factory.getObject().getAttribute(Constantes.USUARIO);

		try {
			List<Remesa> remesas = remesaRepo.findBySucursalGivenDates(sucursal, moneda,
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959), Constantes.ESTATUS_PROCESADA,
					Constantes.RECEPCION_EFECTIVO);

			remesas.addAll(remesaRepo.findBySucursalGivenDates(sucursal, moneda,
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959), Constantes.ESTATUS_PROCESADA,
					Constantes.DIFERENCIA_SOBRANTE));

			remesas.addAll(remesaRepo.findBySucursalGivenDates(sucursal, moneda,
					formato2.parse(fechaI + Constantes.FORMATO_HORA_0),
					formato2.parse(fechaF + Constantes.FORMATO_HORA_235959), Constantes.ESTATUS_ENTREGADA,
					Constantes.DIFERENCIA_FALTANTE));

			Collections.sort(remesas);

			for (int i = 0; i < remesas.size(); i++) {
				remesa = remesas.get(i);

				rs = new ReporteSucursal();
				if (moneda == Constantes.USD) {
					rs.setDebito(Constantes.USD_STRING);
				} else {
					rs.setDebito(Constantes.EUR_STRING);
				}
				if (!remesa.getRemesaDetalles().isEmpty()) {
					rs.setFecha(formato1
							.format(remesa.getRemesaDetalles().get(remesa.getRemesaDetalles().size() - 1).getFecha()));
				}

				rs.setReferencia(remesa.getCartaPorte());
				String desc = "";
				if (remesa.getOperacion().getIdTipoOperacion() == Constantes.CREDITO) {
					if (remesa.getIdOperacion() != Constantes.TRASPASO_CLIENTES
							|| remesa.getIdOperacion() != Constantes.TRASPASO) {
						if (remesa.getDescripcion() != null && !remesa.getDescripcion().isEmpty()) {
							String[] s = remesa.getDescripcion().split(" ");
							if (s.length > 2) {
								for (int j = 2; j < s.length; j++) {
									desc += " " + s[j];
								}
								rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + desc);
							} else {
								rs.setConcepto(remesa.getOperacion().getOperacion() + " - "
										+ remesa.getSucursal().getSucursal());
							}
						}
					}

					rs.setCredito(Util.formatMonto(remesa.getRemesaDetalles().get(0).getMonto().toString()));
					reportes.add(rs);
				}

				if (remesa.getOperacion().getIdTipoOperacion() == Constantes.CREDITO
						&& remesa.getRemesaDetalles().get(0).getIdEstatusRemesa() == Constantes.ESTATUS_CANCELADA) {
					rs = new ReporteSucursal();
					rs.setFecha(formato1.format(remesa.getRemesaDetalles().get(0).getFecha()));
					rs.setReferencia(remesa.getCartaPorte() + " - " + Constantes.CANCELADA);
					rs.setDebito(Util.formatMonto(remesa.getRemesaDetalles().get(0).getMonto().negate().toString()));
					rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + Constantes.STRING_CANCELADA);
					reportes.add(rs);
				}
			}

		} catch (ParseException e) {
			logger.error(e.getLocalizedMessage());
		}

		String detalle = MessageFormat.format(Constantes.CONSULTA_DETALLE_POR_SUCURSAL, fechaI, fechaF, sucursal,
				moneda);
		logServ.registrarLog(Constantes.TEXTO_REPORTE_SUCURSAL, detalle, Constantes.TEXTO_REPORTE_SUCURSAL,
				true, Util.getRemoteIp(request), usuario);

		return reportes;
	}

	@GetMapping(path = "/totalPorSucursal/{sucursal}/{moneda}", produces = "application/json")
	@ResponseBody
	public String getTotalPorSucursal(@PathVariable int sucursal, @PathVariable int moneda) {
		BigDecimal saldo;
		saldo = getMontoTotalBySucursal(sucursal, moneda);
		return saldo.toString();

	}

}
