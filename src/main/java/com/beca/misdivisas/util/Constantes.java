package com.beca.misdivisas.util;

public final class Constantes {
	public static final int USD = 1;
	public static final int EUR = 2;
	
	public static final int ESTATUS_PENDIENTE = 1;
	public static final int ESTATUS_PROCESADA = 2;
	public static final int ESTATUS_PENDIENTE_ENTREGA = 3;
	public static final int ESTATUS_ENTREGADA = 4;
	public static final int ESTATUS_CANCELADA = 5;
	
	public static final int CREDITO = 1;
	public static final int DEBITO = 2;
	
	public static final int RETIRO_EFECTIVO = 2;
	public static final int DIFERENCIA_FALTANTE = 3;
	public static final int TRASPASO = 4;
	public static final int COBRO_CUANTIA = 5;
	public static final int DEBITO_POR_REINTEGRO_BNA = 6;
	public static final int RETIRO_BNA = 7;
	public static final int TRASPASO_CLIENTES = 8;
	public static final int RETIRO_TAQUILLA = 10;
	public static final int REINTEGRO_CUANTIA = 11;
	public static final int DIFERENCIA_SOBRANTE = 14;
	public static final int RECEPCION_EFECTIVO = 17;
	//public static final int BILLETE_NO_APTO = 18;
	//public static final int REMESA_FALTANTE = 19;
	public static final int EMPRESA_ACTIVA = 1;
	public static final int MENU_INICIO = 1;

	public static final String  SIMBOLO_DOLAR = "$";
	public static final String  SIMBOLO_EURO = "€";
	
	public static final String USD_STRING = "USD";
	public static final String EUR_STRING = "EUR";
	public static final String IMAGES="images";
	
	public static final String MAIN = "main";
	public static final String STRING_BNA = "Billetes No Aptos";
	public static final String STRING_CANCELADA = "Remesa Cancelada";
	public static final String CANCELADA = "Cancelada";
	
	public static final String POSICION_CONSOLIDADA = "Posicion Consolidada";
	public static final String POSICION_BNA = "Posición de Billetes no Aptos";
	public static final String TRACKING_REMESAS = "Tracking de Remesas";
	public static final String TEXTO_REPORTE_MAPA = "Reporte del Mapa";
	public static final String TEXTO_REPORTE_GRAFICO = "Reporte Grafico";
	public static final String TEXTO_ADMINISTRAR_USUARIO = "Administracion Usuario";
	public static final String TEXTO_REMESAS_PENDIENTES = "Remesas Pendientes por Entrega";
	public static final String TEXTO_REPORTE_SUCURSAL= "Reporte por sucursal";
	public static final String CAMBIO_EMPRESA= "Cambio de Empresa";
	
	
	public static final String CONSULTA_POR_PARAMETROS = "Consulta: fecha inicio({0}); fecha fin({1}); moneda({2})";
	public static final String CONSULTA_POR_CARTAPORTE ="Consulta de Remesas por Cart Porte: fecha inicio({0}); fecha fin({1}); carta porte({2})";
	public static final String CONSULTA_IRREGULARIDADES = "Consulta de Irregularidades: Carta Porte ({0})";
	public static final String ACCION_USUARIO = "{0} Usuario : NombreUsuario({1});  idUsuario({2});";
	public static final String CONSULTA_POR_SUCURSAL = "Consulta: idSucursal ({0});  moneda({1});";
	public static final String CONSULTA_PENDIENTE_ENTREGA = "Consulta de Remesas Pendiente por Entregar: IdEmpresa({0})";
	public static final String CONSULTA_DETALLE_POR_SUCURSAL = "Consulta: fecha inicio({0}); fecha fin({1});sucursal({2}); moneda({3})";
	public static final String TEXTO_CAMBIO_EMPRESA = "Cambio a Empresa: {0}";
	
	public static final String OPERACION_CONSULTA = "Consulta";
	public static final String OPERACION_EDICION = "Edicion";
	public static final String OPERACION_CREAR = "Creacion";
	public static final String OPERACION_BORRAR = "Borrado";
	public static final String OPERACION_DESCARGA = "Descarga";
	public static final String CAMBIO_CLAVE = "Cambio de clave";

	public static final String OPCION_POSICION = "Posicion Consolidada";
	public static final String OPCION_BNA = "Billetes No Aptos";
	public static final String OPCION_TRACKING = "Tracking de Remesas";
	public static final String OPCION_SEGURIDAD = "Administracion Usuarios";
	public static final String OPCION_MAPA = "Vista del Mapa";
	public static final String OPCION_GRAFICO = "Vista del Grafico";
	public static final String OPCION_LOGIN = "Acceso al sistema";
	

	public static final String ROLES = "roles";
	public static final String ROLES_SELECT = "rolesSelect";

	public static final String ROL_ADMIN_BECA = "ADMIN_BECA";
	public static final String ROL_CONSULTOR = "CONSULTOR";
	public static final String ROL_ADMINISTRADOR = "Administrador";
	public static final String ROL_ADMIN ="ADMIN";
	public static final String INACTIVO = "I";
	public static final String ACTIVO = "A";
	public static final String EDIT = "edit";
	
	public static final String INDEX = "index";
	public static final String GRAFICO ="grafico";
	public static final String SUCURSALES = "sucursales";	
	public static final String MAPA = "mapa";
	
	public static final String LOGIN = "login";
	public static final String LOGIN_BECA = "loginBECA";
	public static final String CLAVE_LOCAL = "local";
	
	public static final String MENUES = "menues";
	public static final String CLIENTE = "cliente";
	public static final String TOTAL_DOLARES = "totalDolares";
	public static final String PENDIENTE_DOLARES = "pendienteDolares";
	public static final String TOTAL_EUROS = "totalEuros";
	public static final String PENDIENTE_EUROS = "pendienteEuros";
	public static final String FECHA_CORTE = "fechaCorte";
	public static final String REPORTE = "reporte";	
	public static final String REPORTE_NA = "reporteNoAptos";
	public static final String REPORTE_TRACK = "trackingRemesas";
	public static final String REPORTE_GRAFICO = "reporteGrafico";
	public static final String REPORTE_REMESAS_PENDIENTES = "remesasPendientes";
	public static final String REPORTE_SUCURSAL = "reporteSucursal";
	
	
	//public static final String 
	
	public static final String FORMATO_FECHA_DDMMYYYYHHMMSS = "dd-MM-yyyy hh:mm:ss";
	public static final String FORMATO_FECHA_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm";
	public static final String FORMATO_FECHA_DDMMYYYY = "dd/MM/yyyy";	
	public static final String FORMATO_HORA_0 = " 00:00:00";
	public static final String FORMATO_HORA_235959 = " 23:59:59";
	
	public static final String MENSAJE_VAL_CONTRASENA_1 = "debe contener entre 8 y 20 caracteres";
	public static final String MENSAJE_VAL_CONTRASENA_2 = "debe contener al menos una mayúscula, una minúscula, un número y un caracter especial ! @ # $ * . _";
	public static final String MENSAJE_VAL_CONTRASENA_3 = "debe coincidir";
	
	public static final String USUARIO_INTERNO = "Interno";
	
	public static final String CHANGE_PASSWORD = "changePassword";
	public static final String USER_NAME = "username";
	public static final String CONTRASENA = "usuario.contrasena";
	public static final String REPITA_CONTRASENA ="usuario.repitaContrasena";
	public static final String NUEVA_CONTRASENA = "nuevaContrasena";
	public static final String INTENTOS = "intentos";
	public static final String NOMBRE_USUARIO = "nombreUsuario";
	public static final String USUARIO = "Usuario";
	public static final String USUARIOS = "Usuarios";
	public static final String ID_EMPRESA ="idEmpresa";
	public static final String ID_SUCURSAL = "idSucursal";
	public static final String U_SUARIO = "usuario";
	public static final String CAMBIO_C = "cambioC";

	
	//public static final String 

	
}
