package com.beca.misdivisas.controller;

import java.math.BigDecimal;
import java.sql.Timestamp;
import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.Iterator;
import java.util.List;
import java.util.Optional;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.ObjectFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.ResponseBody;

import com.beca.misdivisas.interfaces.IEmpresaRepo;
import com.beca.misdivisas.interfaces.IEstatusRemesaRepo;
import com.beca.misdivisas.interfaces.ILogRepo;
import com.beca.misdivisas.interfaces.IRemesaRepo;
import com.beca.misdivisas.interfaces.ISucursalRepo;
import com.beca.misdivisas.jpa.Empresa;
import com.beca.misdivisas.jpa.Log;
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
import com.beca.misdivisas.util.Constantes;
import com.beca.misdivisas.util.Util;

@Controller
public class ReporteController {
	
   @Value("${bancarios}")
    private String bancarios;
	   
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
	private ILogRepo logRepo;

	@Autowired
	private HttpServletRequest request;

	@Autowired
	private static final Logger logger = LoggerFactory.getLogger(ReporteController.class);

	@GetMapping(value = "/reporte")
	public String reporte(Model modelo) {

		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
			List<Remesa> remesasDolar = remesaRepo.getLasRemesaByMoneda(id, Constantes.USD);
			List<Remesa> remesasEuro = remesaRepo.getLasRemesaByMoneda(id, Constantes.EUR);

			BigDecimal montoPendienteDolar = remesaRepo.getLastRemesaByStatus(id, Constantes.USD,
					Constantes.ESTATUS_PENDIENTE);
			BigDecimal montoPendienteEuro = remesaRepo.getLastRemesaByStatus(id, Constantes.EUR,
					Constantes.ESTATUS_PENDIENTE);
			BigDecimal montoPendienteEntregaDolar = remesaRepo.getLastRemesaByStatus(id, Constantes.USD,
					Constantes.ESTATUS_PENDIENTE_ENTREGA);
			BigDecimal montoPendienteEntregaEuro = remesaRepo.getLastRemesaByStatus(id, Constantes.EUR,
					Constantes.ESTATUS_PENDIENTE_ENTREGA);
			Empresa empresa = empresaRepo.findById(id);
			modelo.addAttribute("cliente", empresa.getCaracterRif() + empresa.getRif() + " " + empresa.getEmpresa());

			BigDecimal saldoD = new BigDecimal(0), saldoE = new BigDecimal(0), retiroD = new BigDecimal(0),
					retiroE = new BigDecimal(0);
			BigDecimal saldoTOT = new BigDecimal(0);
			BigDecimal saldoNAD = new BigDecimal(0);
			BigDecimal saldoNAE = new BigDecimal(0);
			BigDecimal tmp = new BigDecimal(0);

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
			Util u = new Util();
			List<Date> fechas = u.obtenerFeriados(this.bancarios);
			modelo.addAttribute("fechaCorte", Util.diaHabilPrevio(fechas));
			
			if (remesasDolar.size() > 0) {

				for (int i = 0; i < remesasDolar.size(); i++) {
					if (!remesasDolar.get(i).getRemesaDetalles().isEmpty() && remesasDolar.get(i).getRemesaDetalles()
							.get(0).getIdEstatusRemesa() != Constantes.ESTATUS_CANCELADA) {

						if (remesasDolar.get(i).getOperacion().getIdTipoOperacion() == Constantes.CREDITO) {
							if (remesasDolar.get(i).getIdOperacion() != Constantes.RETIRO_BNA) {

								saldoD = saldoD.add(remesasDolar.get(i).getRemesaDetalles().get(0).getMonto());
								if (remesasDolar.get(i).getPiezas() != null)
									saldoNAD = saldoNAD.add(getMontoFromPiezas(remesasDolar.get(i).getPiezas()));
							}

						} else {
							if (remesasDolar.get(i).getIdOperacion() != Constantes.RETIRO_BNA)
								retiroD = retiroD.add(remesasDolar.get(i).getRemesaDetalles().get(0).getMonto());
						}
					}
				}

				saldoTOT = saldoD.subtract(retiroD).subtract(saldoNAD);
				tmp = saldoTOT.subtract(montoPendienteEntregaDolar);

			}
			modelo.addAttribute("totalDolares", Util.formatMonto(String.valueOf(saldoTOT)));
			modelo.addAttribute("pendienteDolares", Util.formatMonto(montoPendienteDolar.toString()));
			modelo.addAttribute("pendienteEntregaDolares", Util.formatMonto(montoPendienteEntregaDolar.toString()));
			modelo.addAttribute("disponibleDolares", Util.formatMonto(String.valueOf(tmp)));
			tmp = new BigDecimal(0);
			saldoTOT = new BigDecimal(0);

			if (remesasEuro.size() > 0) {
				saldoTOT = new BigDecimal(0);
				for (int i = 0; i < remesasEuro.size(); i++) {

					if (!remesasEuro.get(i).getRemesaDetalles().isEmpty() && remesasEuro.get(i).getRemesaDetalles()
							.get(0).getIdEstatusRemesa() != Constantes.ESTATUS_CANCELADA) {
						if (remesasEuro.get(i).getOperacion().getIdTipoOperacion() == Constantes.CREDITO
								&& remesasEuro.get(i).getIdOperacion() != Constantes.RETIRO_BNA) {

							saldoE = saldoE.add(remesasEuro.get(i).getRemesaDetalles().get(0).getMonto());
							if (remesasEuro.get(i).getPiezas() != null)
								saldoNAE = saldoNAE.add(getMontoFromPiezas(remesasEuro.get(i).getPiezas()));

						} else {
							if (remesasEuro.get(i).getIdOperacion() != Constantes.RETIRO_BNA)
								retiroE = retiroE.add(remesasEuro.get(i).getRemesaDetalles().get(0).getMonto());
						}
					}
				}

				saldoTOT = saldoE.subtract(retiroE).subtract(saldoNAE);
				tmp = saldoTOT.subtract(montoPendienteEntregaEuro);
			}

			modelo.addAttribute("totalEuros", Util.formatMonto(String.valueOf(saldoTOT)));
			modelo.addAttribute("pendienteEuros", Util.formatMonto(montoPendienteEuro.toString()));
			modelo.addAttribute("pendienteEntregaEuros", Util.formatMonto(montoPendienteEntregaEuro.toString()));
			modelo.addAttribute("disponibleEuros", Util.formatMonto(String.valueOf(tmp)));

			registrarLog(Constantes.POSICION_CONSOLIDADA, Constantes.POSICION_CONSOLIDADA, Constantes.OPCION_POSICION,
					true);

			return "reporte";

		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}

	}

	@RequestMapping(path = "/remesa/{fechaI}/{fechaF}/{moneda}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ReporteSucursal> getAllRemesas(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String moneda) {
		DateFormat formato1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formato2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

		List<ReporteSucursal> reportes = new ArrayList<ReporteSucursal>();
		ReporteSucursal rs = null;
		Remesa remesa = null;
		BigDecimal saldo = new BigDecimal(0);
		try {
			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
			List<Remesa> remesas = remesaRepo.findRemesaByEmpresaId(id, Integer.parseInt(moneda),
					formato2.parse(fechaI + " 00:00:00"), formato2.parse(fechaF + " 23:59:59"));

			for (int i = 0; i < remesas.size(); i++) {
				remesa = remesas.get(i);

				rs = new ReporteSucursal();

				if (remesa.getIdOperacion() != Constantes.RETIRO_BNA) {
					if (remesa.getRemesaDetalles().size() > 0) {
						rs.setFecha(formato1.format(
								remesa.getRemesaDetalles().get(remesa.getRemesaDetalles().size() - 1).getFecha()));
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
						saldo = saldo.add(remesa.getRemesaDetalles().get(0).getMonto());
					} else {

						rs.setDebito(
								Util.formatMonto(remesa.getRemesaDetalles().get(0).getMonto().negate().toString()));
						desc = "";
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

						saldo = saldo.subtract(remesa.getRemesaDetalles().get(0).getMonto());
					}

					rs.setSaldo(Util.formatMonto(saldo.toString()));
					reportes.add(rs);

					if (remesa.getOperacion().getIdTipoOperacion() == Constantes.CREDITO
							&& remesa.getRemesaDetalles().get(0).getIdEstatusRemesa() == Constantes.ESTATUS_CANCELADA) {
						rs = new ReporteSucursal();
						rs.setFecha(formato1.format(remesa.getRemesaDetalles().get(0).getFecha()));
						rs.setReferencia(remesa.getCartaPorte() + "- Cancelada");
						rs.setDebito(
								Util.formatMonto(remesa.getRemesaDetalles().get(0).getMonto().negate().toString()));
						rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + Constantes.STRING_CANCELADA);

						saldo = saldo.subtract(remesa.getRemesaDetalles().get(0).getMonto());
						rs.setSaldo(Util.formatMonto(saldo.toString()));
						reportes.add(rs);
					}

					BigDecimal tmp = getMontoFromPiezas(remesa.getPiezas());
					if (tmp.compareTo(new BigDecimal(0)) == 1
							&& remesa.getIdOperacion() != Constantes.DIFERENCIA_FALTANTE) {
						rs = new ReporteSucursal();
						rs.setFecha(formato1.format(remesa.getRemesaDetalles().get(0).getFecha()));
						rs.setReferencia(remesa.getCartaPorte() + "-BNA");
						rs.setDebito(Util.formatMonto(tmp.negate().toString()));
						rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + Constantes.STRING_BNA);
						saldo = saldo.subtract(tmp);
						rs.setSaldo(Util.formatMonto(saldo.toString()));
						reportes.add(rs);
					}
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}
		String detalle = "Consulta: fecha inicio(" + fechaI + "); fecha fin(" + fechaF + "); moneda(" + moneda + ")";
		registrarLog(Constantes.POSICION_CONSOLIDADA, detalle, Constantes.OPCION_POSICION, true);
		return reportes;
	}

	@GetMapping(value = "/reporteNoAptos")
	public String reporteNoAptos(Model modelo) {

		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");
			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
			List<Remesa> remesasDolar = remesaRepo.getLasRemesaByMoneda(id, Constantes.USD);
			List<Remesa> remesasEuro = remesaRepo.getLasRemesaByMoneda(id, Constantes.EUR);
			BigDecimal noAptoD = new BigDecimal(0), noAptoE = new BigDecimal(0), retiroD = new BigDecimal(0),
					retiroE = new BigDecimal(0);
			for (int i = 0; i < remesasDolar.size(); i++) {
				if (!remesasDolar.get(i).getPiezas().isEmpty()
						&& remesasDolar.get(i).getIdOperacion() != Constantes.RETIRO_BNA) {
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
			for (int i = 0; i < remesasEuro.size(); i++) {
				if (!remesasEuro.get(i).getPiezas().isEmpty()
						&& remesasEuro.get(i).getIdOperacion() != Constantes.RETIRO_BNA) {
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

			Empresa empresa = empresaRepo.findById(id);
			modelo.addAttribute("cliente", empresa.getCaracterRif() + empresa.getRif() + " " + empresa.getEmpresa());

			if (!remesasDolar.isEmpty())
				modelo.addAttribute("fechaCorte",
						formato.format(remesasDolar.get(0).getRemesaDetalles().get(0).getFecha()));

			if (remesasDolar.size() > 0) {
				noAptoD = noAptoD.subtract(retiroD);
				modelo.addAttribute("totalDolares", Util.formatMonto(noAptoD.toString()));
			} else {
				modelo.addAttribute("totalDolares", "0,00");
			}

			if (remesasEuro.size() > 0) {
				noAptoE = noAptoE.subtract(retiroE);
				modelo.addAttribute("totalEuros", Util.formatMonto(noAptoE.toString()));
			} else {
				modelo.addAttribute("totalEuros", "0,00");
			}
			registrarLog(Constantes.POSICION_BNA, Constantes.POSICION_BNA, Constantes.OPCION_BNA, true);
			return "reporteNoAptos";

		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}

	}

	@RequestMapping(path = "/remesaNoApta/{fechaI}/{fechaF}/{moneda}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ReporteSucursal> getAllRemesasNoAptas(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String moneda) {
		DateFormat formato1 = new SimpleDateFormat("dd/MM/yyyy");
		DateFormat formato2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");

		BigDecimal monto = new BigDecimal(0);
		BigDecimal montoD = new BigDecimal(0);
		List<ReporteSucursal> reportes = new ArrayList<ReporteSucursal>();
		ReporteSucursal rs = null;
		Remesa remesa = null;
		try {
			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
			List<Remesa> remesas = remesaRepo.findRemesaByEmpresaId(id, Integer.parseInt(moneda),
					formato2.parse(fechaI + " 00:00:00"), formato2.parse(fechaF + " 23:59:59"));

			for (int i = 0; i < remesas.size(); i++) {
				remesa = remesas.get(i);
				if (remesa.getIdOperacion() != Constantes.DIFERENCIA_FALTANTE) {
					rs = new ReporteSucursal();
					List<Pieza> piezas = remesa.getPiezas();
					for (Iterator iterator = piezas.iterator(); iterator.hasNext();) {
						Pieza pieza = (Pieza) iterator.next();
						if (pieza.getCantidadNoApta() > 0) {
							rs.setFecha(formato1.format(remesa.getRemesaDetalles().get(0).getFecha()));
							rs.setReferencia(remesa.getCartaPorte());

							for (Pieza piezae : piezas) {
								int parcial = piezae.getCantidadNoApta() * piezae.getDenominacion().getDenominacion();
								monto = monto.add(new BigDecimal(parcial));
							}
							if (remesa.getIdOperacion().equals(Constantes.RETIRO_BNA))
								monto = monto.negate();

							rs.setConcepto(remesa.getOperacion().getOperacion() + " - " + Constantes.STRING_BNA);
							rs.setSaldo(Util.formatMonto(monto.toString()));
							reportes.add(rs);
							monto = new BigDecimal(0);
							montoD = new BigDecimal(0);
							break;
						}
						monto = new BigDecimal(0);
						montoD = new BigDecimal(0);
					}
				}
			}

		} catch (ParseException e) {
			e.printStackTrace();
		}

		String detalle = "Consulta: fecha inicio(" + fechaI + "); fecha fin(" + fechaF + "); moneda(" + moneda + ")";
		registrarLog(Constantes.POSICION_BNA, detalle, Constantes.OPCION_BNA, true);
		return reportes;
	}

	@RequestMapping(path = "/irregularidades/{cartaPorte}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ReporteIrregularidades> getIrregularidades(@PathVariable String cartaPorte) {
		DateFormat formato1 = new SimpleDateFormat("dd/MM/yyyy");

		List<ReporteIrregularidades> irregularidades = new ArrayList<ReporteIrregularidades>();
		ReporteIrregularidades ri = null;
		int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
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
			e.printStackTrace();
		}

		return irregularidades;
	}

	@GetMapping(value = "/trackingRemesas")
	public String trackingRemesas(Model modelo) {

		if (((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1() != null
				&& !(((Usuario) factory.getObject().getAttribute("Usuario")).getContrasena1().trim().equals(""))) {

			int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();

			Empresa empresa = empresaRepo.findById(id);
			modelo.addAttribute("cliente", empresa.getCaracterRif() + empresa.getRif() + " " + empresa.getEmpresa());

			return "trackingRemesas";

		} else {
			Usuario usuario = ((Usuario) factory.getObject().getAttribute("Usuario"));
			modelo.addAttribute("usuario", usuario);
			return "changePassword";
		}
	}

	@RequestMapping(path = "/remesabycartaporte/{fechaI}/{fechaF}/{cartaPorte}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ReporteRemesa> getRemesaPorCataporte(@PathVariable String fechaI, @PathVariable String fechaF,
			@PathVariable String cartaPorte) {
		DateFormat formato = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		DateFormat formato2 = new SimpleDateFormat("dd-MM-yyyy hh:mm:ss");
		int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
		List<Remesa> remesas = null;

		Date d1, d2;

		List<ReporteRemesa> rem = new ArrayList<ReporteRemesa>();

		ReporteRemesa reprem = null;

		try {
			d1 = formato2.parse(fechaI + " 00:00:00");
			d2 = formato2.parse(fechaF + " 23:59:59");
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

		} catch (NumberFormatException e) {
			e.printStackTrace();
		} catch (ParseException e) {
			e.printStackTrace();
		}
		String Detalle = "Consulta de Remesas por Cart Porte: fecha inicio(" + fechaI + "); fecha fin(" + fechaF
				+ "); carta porte(" + cartaPorte + ")";
		registrarLog(Constantes.TRACKING_REMESAS, Detalle, Constantes.OPCION_TRACKING, true);
		return rem;
	}

	@GetMapping(value = "/reporte/{idSucursal}")
	public String getAcumuladoSucursal(@PathVariable int idSucursal, Model modelo) {
		modelo.addAttribute("idSucursal", idSucursal);
		factory.getObject().removeAttribute("idSucursal");
		factory.getObject().setAttribute("idSucursal", idSucursal);
		int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();

		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute("cliente", empresa.getCaracterRif() + empresa.getRif() + " " + empresa.getEmpresa());

		Optional<Sucursal> sucursal = sucursalRepo.findById(idSucursal);
		modelo.addAttribute("nombreSucursal", sucursal.get().getSucursal());

		BigDecimal montoDolar = getMontoTotalBySucursal(idSucursal, Constantes.USD);
		BigDecimal montoEuro = getMontoTotalBySucursal(idSucursal, Constantes.EUR);

		if (montoDolar == null)
			montoDolar = new BigDecimal(0);

		if (montoEuro == null)
			montoEuro = new BigDecimal(0);

		modelo.addAttribute("totalDolares", Util.formatMonto(montoDolar.toString()));
		modelo.addAttribute("totalEuros", Util.formatMonto(montoEuro.toString()));

		String Detalle = "Consulta: idSucursal (" + idSucursal + "); ";
		registrarLog(Constantes.REPORTE_MAPA, Detalle, Constantes.OPCION_MAPA, true);

		return "reporteGrafico";
	}

	@RequestMapping(path = "/reporte/sucursalesG/{moneda}", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public DatosReporteMapa getMontosPorMes(@PathVariable int moneda) {
		DateFormat formato = new SimpleDateFormat("dd-MM-yyyy 00:00:00");
		String[] nombreMes = { "Enero", "Febrero", "Marzo", "Abril", "Mayo", "Junio", "Julio", "Agosto", "Septiembre",
				"Octubre", "Noviembre", "Diciembre" };
		BigDecimal[] montos = new BigDecimal[12];
		String[] meses = new String[12];
		ReporteSucursalMapa reporte = null;
		DatosReporteMapa rm = new DatosReporteMapa();
		int idSucursal = (int) factory.getObject().getAttribute("idSucursal");
		LocalDate dd = LocalDate.now().minusMonths(12).withDayOfMonth(1);

		List<ReporteSucursalMapa> reportes, reportesS, reportesF;
		try {
			reportes = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
					Constantes.RECEPCION_EFECTIVO, formato.parse(dd.toString() + " 00:00:00"));
			reportesS = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_PROCESADA,
					Constantes.DIFERENCIA_SOBRANTE, formato.parse(dd.toString() + " 00:00:00"));

			reportes = calculaMontos(reportes, reportesS, Constantes.CREDITO);

			reportesF = remesaRepo.getTotalMensualBySucursal(idSucursal, moneda, Constantes.ESTATUS_ENTREGADA,
					Constantes.DIFERENCIA_FALTANTE, formato.parse(dd.toString() + " 00:00:00"));

			reportes = calculaMontos(reportes, reportesF, Constantes.DEBITO);

			for (int i = 0; i < reportes.size(); i++) {
				reporte = reportes.get(i);
				meses[reporte.getMes() - 1] = nombreMes[reporte.getMes() - 1] + '\'' + reporte.getAno() % 100;
				;
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
			e.printStackTrace();
		}
		String detalle = "Consulta: idSucursal (" + idSucursal + ");  moneda(" + moneda + ");";
		registrarLog(Constantes.REPORTE_GRAFICO, detalle, Constantes.OPCION_GRAFICO, true);

		return rm;
	}

	private BigDecimal getMontoFromPiezas(List<Pieza> piezas) {
		BigDecimal monto = new BigDecimal(0);
		BigDecimal parcial = new BigDecimal(0);
		if (piezas != null) {
			for (Iterator iterator = piezas.iterator(); iterator.hasNext();) {
				Pieza pieza = (Pieza) iterator.next();
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

	public void registrarLog(String accion, String detalle, String opcion, boolean resultado) {
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
		logger.info("Ip origen: " + ip + " Accion:" + accion + " Detalle:" + detalle + " Opcion:" + opcion);
	}

	@GetMapping(value = "/remesasPendientes")
	public String remesasPendientes(Model modelo) {
		int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
		Empresa empresa = empresaRepo.findById(id);
		modelo.addAttribute("cliente", empresa.getCaracterRif() + empresa.getRif() + " " + empresa.getEmpresa());
		Util u = new Util();
		List<Date> fechas = u.obtenerFeriados(this.bancarios);
		modelo.addAttribute("fechaCorte", Util.diaHabilPrevio(fechas));		
		
		return "remesasPendientes";
	}

	@RequestMapping(path = "/remesaEntregaPendiente", method = RequestMethod.GET, produces = "application/json")
	@ResponseBody
	public List<ReporteRemesa> getRemesasPendientes() {
		DateFormat formato = new SimpleDateFormat("dd/MM/yyyy");

		int id = ((Usuario) factory.getObject().getAttribute("Usuario")).getIdEmpresa();
		List<Remesa> remesas = null;

		List<ReporteRemesa> rem = new ArrayList<ReporteRemesa>();

		ReporteRemesa reprem = null;

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

						int dias = Util.getDiasHabiles(cal, cal2);

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
		
		String Detalle = "Consulta de Remesas Pendiente por Entregar: IdEmpresa(" + id + "))";
		registrarLog(Constantes.REMESAS_PENDIENTES, Detalle, Constantes.REMESAS_PENDIENTES, true);
		return rem;
	}
}
