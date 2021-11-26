package com.beca.misdivisas.util;

import java.util.regex.Pattern;

public final class Constantes {
	
	//Caracteres especiales permitidos: espacio # $ / : ; _ \ & , . s - ( ) ! ¡ * @ + %
	public static final Pattern CARACTERES_PERMITIDOS_PATTERN = Pattern.compile("[a-zA-Z0-9\\ \\Á\\á\\É\\é\\Í\\í\\Ó\\ó\\Ú\\ú\\Ñ\\ñ\\,\\.\\(\\)\\#\\$\\/\\:\\;\\-\\_\\/\\&\\@\\*\\!\\¡\\+\\%\\\\]*");
	
	public static final Pattern CARACTERES_PERMITIDOS_PERFIL_PATTERN = Pattern.compile("[a-zA-Z0-9\\ \\.\\(\\)\\-\\_]*");
	
	
	
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
	public static final int ID_ROL_ADMIN = 1;
	public static final String MENU_INICIO = "1";

	public static final String  SIMBOLO_DOLAR = "$";
	public static final String  SIMBOLO_EURO = "€";
	
	public static final String USD_STRING = "USD";
	public static final String EUR_STRING = "EUR";
	public static final String IMAGES="images";
	
	public static final String MAIN = "main";
	public static final String STRING_BNA = "Billetes No Aptos";
	public static final String STRING_BNA_AV = "BNA";
	public static final String STRING_CANCELADA = "Remesa Cancelada";
	public static final String CANCELADA = "Cancelada";
	
	public static final String POSICION_CONSOLIDADA = "Posicion Consolidada";
	public static final String POSICION_BNA = "Posici\u00F3n de Billetes no Aptos";
	public static final String TRACKING_REMESAS = "Tracking de Remesas";
	public static final String TEXTO_REPORTE_MAPA = "Reporte del Mapa";
	public static final String TEXTO_REPORTE_GRAFICO = "Reporte Grafico";
	public static final String TEXTO_ADMINISTRAR_USUARIO = "Administracion de Usuario";
	public static final String TEXTO_REMESAS_PENDIENTES = "Remesas Pendientes por Entrega";
	public static final String TEXTO_REPORTE_SUCURSAL= "Reporte por sucursal";
	public static final String CAMBIO_EMPRESA= "Cambio de Empresa";
	public static final String TEXTO_ADMINISTRAR_EMPRESAS = "Administracion Empresas";
	public static final String TEXTO_ADMINISTRAR_SUCURSALES = "Administracion Sucursales";
	public static final String TEXTO_ADMINISTRAR_AGENCIAS = "Administracion Agencias";
	
	public static final String CONSULTA_POR_PARAMETROS = "Consulta: fecha inicio({0}); fecha fin({1}); moneda({2})";
	public static final String CONSULTA_POR_CARTAPORTE ="Consulta de Remesas por Cart Porte: fecha inicio({0}); fecha fin({1}); carta porte({2})";
	public static final String CONSULTA_IRREGULARIDADES = "Consulta de Irregularidades: Carta Porte ({0})";
	public static final String ACCION_USUARIO = "Accion {0} sobre el Usuario con idUsuario{1}); NombreUsuario({2}); Empresa({3}); Perfil({4}); ";
	public static final String CONSULTA_POR_SUCURSAL = "Consulta: idSucursal ({0});  moneda({1});";
	public static final String CONSULTA_PENDIENTE_ENTREGA = "Consulta de Remesas Pendiente por Entregar: IdEmpresa({0})";
	public static final String CONSULTA_DETALLE_POR_SUCURSAL = "Consulta: fecha inicio({0}); fecha fin({1});sucursal({2}); moneda({3})";
	public static final String TEXTO_CAMBIO_EMPRESA = "Cambio a Empresa: {0}";
	public static final String SUCURSALES_EMPRESA = "Consulta: Obtiene la lista de las sucursales de la empresa";
	public static final String ACCION_EMPRESA = "{0} Empresa : NombreEmpresa({1});  idEmpresa({2}); idUsuario({3}); nombreUsuario({4});";
	public static final String ACCION_SUCURSAL = "{0} Sucursal : NombreSucursal({1}); idSucursal({2}); idUsuario({3}); nombreUsuario({4});";
	public static final String ACCION_CREAR_AUTORIZADO = "{0} Autorizado : TipoAutorizado({1});  idUsuario({2}); nombreUsuario({3});";
	public static final String ACCION_EDITAR_AUTORIZADO = "{0} Autorizado : IdAutorizado({1}); TipoAutorizado({2});  idUsuario({3}); nombreUsuario({4});";
	public static final String ACCION_PERFIL = "Accion {0} sobre el Perfil con idPerfil({1}); nombrePerfil({2}) tipoPerfil({3}) tipoVista({4}) empresa({5})";
	public static final String ACCION_LISTAR_PERFILES = "Se listaron los perfiles de la empresa {0}";
	public static final String ACCION_SOLICITUD_RETIRO_EFECTIVO = "{0} Solicitud Retiro Efectivo : idSolicitud({1}); idUsuario({2}); nombreUsuario({3});";
	public static final String ACCION_LOGIN="Se intenta ingresar con el nombre de usuario ({0}) desde la ip ({1})";
	public static final String ACCION_AGENCIA_DIA="Consulta mapa disponibilidad agencia";
	
	public static final String OP_CONSULTA = "Consulta";
	public static final String OP_EDICION = "Edicion";
	public static final String OP_CREAR = "Creacion";
	public static final String OP_ELIMINAR = "Eliminar";
	public static final String OP_ANULAR = "Anular";
	public static final String OP_APROBAR = "Aprobar";
	public static final String OP_VALIDAR = "Validar";
	public static final String OP_RECHAZAR = "Rechazar";
	public static final String OP_PROCESAR = "Procesar";
	public static final String OP_ENTREGAR = "Entregar";
	public static final String OP_CANCELAR = "Cancelar";


	public static final String OP_DESCARGA = "Descarga";
	public static final String OP_SOLICITUD = "Solicitud";
	public static final String OP_CREAR_PERFIL = "crearPerfil";
	public static final String OP_EDITAR_PERFIL = "editarPerfil";
	public static final String OP_ELIMINAR_PERFIL = "eliminarPerfil";
	public static final String OP_ENV_EFECTIVO = "solicitud/envioEfectivo";
	public static final String OP_TRAS_EFECTIVO = "solicitud/traspasoEfectivo";
	public static final String OP_RET_EFECTIVO = "solicitud/retiroEfectivo";
	public static final String OP_AUTORIZADOS = "solicitudes/autorizacion/main";
	public static final String OP_AUTORIZADOS_LISTA = "solicitudes/autorizacion/lista";
	public static final String OP_AUTORIZADOS_CREAR = "solicitudes/autorizacion/tipoAutorizados";
	public static final String OP_AUTORIZADOS_BENEF_TRASPASO = "solicitudes/autorizacion/beneficiarioTraspaso";
	public static final String OP_AUTORIZADOS_EMP_TRANSPORTE = "solicitudes/autorizacion/empresaTransporte";
	public static final String OP_AUTORIZADOS_PER_NATURAL = "solicitudes/autorizacion/personaNatural";
	public static final String OP_AUTORIZADOS_PER_JURIDICA = "solicitudes/autorizacion/personaJuridica";
	public static final String OP_SOLICITUDES_RETIRO = "solicitudes/retiroEfectivo/main";
	public static final String OP_SOLICITUDES_RETIRO_LISTA = "solicitudes/retiroEfectivo/lista";
	//public static final String OP_SOLICITUDES_RETIRO_LISTA_ENTREGAR = "solicitudes/retiroEfectivo/listaSolicitudesAprobadasValidadasEntregar";
	public static final String OP_SOLICITUDES_RETIRO_ENTREGAR = "solicitudes/retiroEfectivo/Entregar";
	public static final String OP_SOLICITUD_RETIRO_FORM = "solicitudes/retiroEfectivo/retiroEfectivo";
	
	public static final String OP_GENERACION = "RetiroEfectivo/Generacion";
	public static final String OP_SOLICITUDES_RETIRO_APROBAR = "solicitudes/retiroEfectivo/aprobarMain";
	public static final String OP_APROBACION = "RetiroEfectivo/Aprobacion";
	public static final String OP_ENTREGA = "RetiroEfectivo/Entrega";
	public static final String OP_SOLICITUD_RETIRO_VIEW = "solicitudes/retiroEfectivo/solicitudRetiroView";
	public static final String OP_VALIDACION = "RetiroEfectivo/Validacion";
	public static final String OP_SOLICITUDES_RETIRO_VALIDAR_PROCESAR = "solicitudes/retiroEfectivo/validarProcesarMain";
	public static final String OP_SOLICITUDES_RETIRO_PROCESAR_ENTREGAR = "solicitudes/retiroEfectivo/procesarEntregarMain";
	public static final String OP_SOLICITUD_RETIRO_APROBAR_VIEW = "solicitudes/retiroEfectivo/solicitudRetiroAprobadaView";
	public static final String OP_PROCESAMIENTO = "RetiroEfectivo/Procesamiento";
	public static final String OP_SOLICITUDES_RETIRO_PROCESAR = "solicitudes/retiroEfectivo/procesarMain";
	public static final String OP_SOLICITUD_RETIRO_VALIDADAS_VIEW = "solicitudes/retiroEfectivo/solicitudRetiroValidadaView";
	public static final String OP_SOLICITUD_RETIRO_PROCESADAS_VIEW = "solicitudes/retiroEfectivo/solicitudRetiroProcesadaView";
	public static final String OP_SOLICITUD_RETIRO_ENTREGADAS_VIEW = "solicitudes/retiroEfectivo/solicitudRetiroEntregadaView";
	public static final String OP_DEPOSITOS = "depositos/main";
	public static final String OP_RETIROS = "retiros/main";
	public static final String OP_DEPOSITOS_FORM = "depositos/deposito";
	public static final String OP_RETIROS_FORM = "retiros/retiro";
	public static final String OP_DEPOSITOS_DETALLE_FORM = "depositos/detalle";
	public static final String OP_RETIROS_DETALLE_FORM = "retiros/detalle";
	public static final String OP_AGENCIA_DIAS_VIEW = "agencia/agenciaDias";
	public static final String OP_DISPONIBILIDAD_AGENCIA_DIAS_VIEW = "agencia/disponibilidadAgenciaDias";
	public static final String VOUCHER_DEPOSITO = "depositos/voucherDeposito";
	public static final String VOUCHER_RETIRO = "retiros/voucherRetiro";
	
	
	

	public static final String CAMBIO_CLAVE = "Cambio de clave";
	public static final String EDICION_EMPRESA = "Editar empresa";
	public static final String CREAR_EMPRESA = "Crear empresa";
	public static final String LISTAR_EMPRESA = "Listar empresas";
	public static final String EDICION_SUCURSAL = "Editar sucursal";
	public static final String CREAR_SUCURSAL = "Crear sucursal";
	public static final String LISTAR_SUCURSAL = "Listar sucursales";
	public static final String CREAR_AUTORIZADO = "Crear autorizado";
	public static final String EDITAR_AUTORIZADO = "Editar autorizado";
	public static final String ELIMINAR_AUTORIZADO = "Eliminar autorizado";
	public static final String CREAR_PERFIL = "Crear Perfil";
	public static final String EDITAR_PERFIL = "Editar Perfil";
	public static final String ELIMINAR_PERFIL = "Eliminar Perfil";
	public static final String LISTAR_SOLICITUD_RETIRO_EFECTIVO = "Listar solicitud ret efectivo";
	public static final String EDICION_SOLICITUD_RETIRO_EFECTIVO = "Editar solicitud ret efectivo";
	public static final String CREAR_SOLICITUD_RETIRO_EFECTIVO = "Crear solicitud ret efectivo";
	public static final String ANULAR_SOLICITUD_RETIRO_EFECTIVO = "Anular solicitud ret efectivo";
	public static final String APROBAR_SOLICITUD_RETIRO_EFECTIVO = "Aprobar solic ret efectivo";
	public static final String VALIDAR_SOLICITUD_RETIRO_EFECTIVO = "Validar solic ret efectivo";
	public static final String RECHAZAR_SOLICITUD_RETIRO_EFECTIVO = "Rechazar solic ret efectivo";
	public static final String CANCELAR_SOLICITUD_RETIRO_EFECTIVO = "Cancelar solic ret efectivo";
	public static final String PROCESAR_SOLICITUD_RETIRO_EFECTIVO = "Procesar solic ret efectivo";

	public static final String OPCION_POSICION = "Posicion Consolidada";
	public static final String OPCION_BNA = "Billetes No Aptos";
	public static final String OPCION_TRACKING = "Tracking de Remesas";
	public static final String OPCION_SEGURIDAD = "Administracion Usuarios";
	public static final String OPCION_MAPA = "Vista del Mapa";
	public static final String OPCION_GRAFICO = "Vista del Grafico";
	public static final String OPCION_LOGIN = "Acceso al sistema";
	public static final String OPCION_STR_EFECTIVO = "Solicitud {0} de Efectivo";
	public static final String OPCION_SELECT ="opcionesSelect";
	public static final String OPCION_INICIO ="Inicio";
	public static final String OPCION_AUTORIZACION = "Ver Solicitudes de Autorizacion";
	public static final String DOMINIO_PROD = "bancoexterior.com";
	public static final String DOMINIO = "dominio";
	public static final String ROL = "rol";
	public static final String ROLES = "roles";
	public static final String ROLES_SELECT = "rolesSelect";
	public static final String GESTIONAR_ROLES = "gestionarRoles";
	public static final String PERFIL = "perfil";
	public static final String PERFILES = "Perfiles";
	public static final String PERFILES_SELECT = "perfilesSelect";	
	public static final String GESTIONAR_PERFILES = "gestionarPerfiles";
	public static final String REVISAR_PERFILES = "revisarPerfiles";
	public static final String NOMBRE_ROL = "nombreRol";
	public static final String NOMBRE_PERFIL = "nombrePerfil";
	public static final String EMPRESAS = "Empresas";
	public static final String EMPRESA_MODEL = "empresaModel";
	public static final String SUCURSAL_MODEL = "sucursalModel";
	public static final String BENEFICIARIO_TRASPASO = "Beneficiario Traspaso";
	public static final String EMPRESA_TRANSPORTE = "Empresa Transporte";
	public static final String AUTORIZADO_PERSONA_NATURAL = "Autorizado Persona Natural";
	public static final String AUTORIZADO_PERSONA_JURIDICA = "Autorizado Persona Juridica";
	public static final String SOLICTUD_RETIRO_ACCION_FROM = "accionFrom";
	public static final String AGENCIA_DIA = "Agencia Dia";
	
	public static final String TIPO_PERFIL_I = "I";
	public static final String TIPO_PERFIL_E = "E";
	public static final String ROL_PRE = "ROLE_";
	public static final String ROL_ADMIN_BECA = "ADMIN_BECA";
	public static final String ROL_DEFINIDOR = "Definidor";
	public static final String ROL_VALIDADOR_RETIRO = "Validador_Retiro";
	public static final String ROL_PROCESADOR_RETIRO = "Procesador_Retiro";
	public static final String ROL_GENERADOR_REPORTE = "Generador_Reporte";

	public static final String ROL_CONSULTOR = "CONSULTOR";
	public static final String PERFIL_ADMINISTRADOR = "ADMIN";
	public static final String INACTIVO = "I";
	public static final String ACTIVO = "A";
	public static final String EDITAR = "editar";
	public static final String SUCCESS = "success";
	public static final String CREAR = "crear";
	public static final String CREARDEPOSITO = "creardeposito";
	public static final String CREARRETIRO = "crearRetiro";
	public static final String VERDEPOSITO = "verdeposito";
	public static final String VERVOUCHER = "verVoucher";
	public static final String VERRETIRO = "verretiro";
	
	public static final String INDEX = "index";
	public static final String GRAFICO = "grafico";
	public static final String SUCURSALES = "sucursales";
	public static final String ESTADOS = "estados";
	public static final String MUNICIPIOS = "municipios";
	public static final String MAPA = "mapa";
	public static final String MAPA_AGENCIA_DIA= "mapaAgenciaDia";
	public static final String MAPA_AGENCIA = "agencia/mapaAgencia";
	public static final String TIPO_MAPA = "tipoMapa";
	public static final String VISTA_GENERAL = "Vista general";
	public static final String VISTA_EMPRESA = "Vista por empresa";
	public static final String TIPO_VISTA_EMPRESA = "E";
	public static final String TIPO_VISTA_GENERAL = "G";
	public static final String TIPO_VISTA_TODOS = "T";
	
	public static final String LOGIN = "login";
	public static final String LOGOUT = "logout";
	public static final String ACCEDER = "Acceder";
	public static final String LOGIN_BECA = "loginBECA";
	public static final String CLAVE_LOCAL = "local";
	
	public static final String TIPO_MENU_S="S";
	public static final String TIPO_MENU_U="U";
	public static final String MENUES = "menues";
	public static final String CLIENTE = "cliente";
	public static final String TOTAL_DOLARES = "totalDolares";
	public static final String DISPONIBLE_DOLARES = "disponibleDolares";
	public static final String PENDIENTE_DOLARES = "pendienteDolares";
	public static final String PENDIENTE_ENTREGA_DOLARES = "pendienteEntregaDolares";
	public static final String TOTAL_EUROS = "totalEuros";
	public static final String DISPONIBLE_EUROS = "disponibleEuros";
	public static final String PENDIENTE_EUROS = "pendienteEuros";
	public static final String PENDIENTE_ENTREGA_EUROS = "pendienteEntregaEuros";
	public static final String FECHA_CORTE = "fechaCorte";
	public static final String REPORTE = "reporte/reporte";	
	public static final String REPORTE_NA = "reporte/reporteNoAptos";
	public static final String REPORTE_TRACK = "reporte/trackingRemesas";
	public static final String REPORTE_GRAFICO = "reporte/reporteGrafico";
	public static final String REPORTE_REMESAS_PENDIENTES = "reporte/remesasPendientes";
	public static final String REPORTE_SUCURSAL = "reporte/reporteSucursal";
	public static final String REPORTE_SOLICITUD_RETIRO = "reporte/reporteSolicitudRetiro";
	public static final String PERFIL_MAIN = "perfil/perfilMain";
	public static final String PERFIL_HOME = "perfil/perfilHome";
	public static final String USUARIO_MAIN = "mainUsuarios";
	public static final String USUARIO_ADD = "usuario/addUsuario";
	public static final String USUARIO_SELECT = "usuariosSelect";
	public static final String USUARIO_MENUES = "usuarioMenues";
	public static final String VER_RESULTADO = "verResultado";
	public static final String PERFILES_GESTIONAR = "redirect:/gestionarPerfiles?success";
	public static final String ERROR = "error/error";
	public static final String EXISTE_AD = "existe";
	public static final String ERRORES = "errores";
	
	//public static final String 
	
	public static final String FORMATO_FECHA_DDMMYYYYHHMMSS = "dd-MM-yyyy hh:mm:ss";
	public static final String FORMATO_FECHA_DDMMYYYYHHMM = "dd/MM/yyyy HH:mm a";
	public static final String FORMATO_FECHA_DDMMYYYY = "dd/MM/yyyy";	
	public static final String FORMATO_HORA_0 = " 00:00:00";
	public static final String FORMATO_HORA_235959 = " 23:59:59";
	public static final String FORMATO_DDMMYYYY000 = "dd-MM-yyyy 00:00:00";
	public static final String FORMATO_FECHA_DD_MM_YYYY = "dd-MM-yyyy";
	public static final String FORMATO_FECHA_ddMMYYYY = "ddMMyyyy";
	public static final String FORMATO_FECHA_D_M_YYYY = "d-M-yyyy";
	public static final String FORMATO_FECHA_YYYYMMDDHHMMSS = "yyyyMMddhhmmss";

	public static final Pattern CARACTERES_ESPECIALES_PATTERN_I = Pattern.compile("[\\\"%()¡!*$&+.,:;=?¿@#|/{}\\\\]");
	public static final Pattern CARACTERES_ESPECIALES_PATTERN_E = Pattern.compile("[\\\"%¡!*$&+.,:;=?¿@#|/{}\\\\]");
	public static final String MENSAJE_VAL_CONTRASENA_1 = "debe contener entre 8 y 20 caracteres";
	public static final String MENSAJE_VAL_CONTRASENA_2 = "debe contener al menos una may\u00FAscula, una min\u00FAscula, un n\u00FAmero y un caracter especial ! @ # $ * . _";
	public static final String MENSAJE_VAL_CONTRASENA_3 = "debe coincidir";
	public static final String MENSAJE_VAL_CONTRASENA_4 = "no puede ser igual a las \u00FAltimas 5 utilizadas";
	public static final String MENSAJE_VAL_CONTRASENA_5 = "no coincide con la actual";
	
	public static final String MENSAJE_VAL_PERFIL = "requerido";
	public static final String MENSAJE_VAL_PERFIL_ESPECIALES_E = "caracteres especiales permitidos (._-)";
	public static final String MENSAJE_VAL_PERFIL_ESPECIALES_I = "caracteres especiales permitidos _-";
	public static final String MENSAJE_VAL_PERFIL_1 = "debe seleccionar al menos una opci\u00F3n del men\u00FA";
	public static final String MENSAJE_VAL_PERFIL_2 = "ya existe un perfil con ese nombre";
	
	public static final String MENSAJE_VAL_USUARIO = "ya esta siendo utilizado";
	public static final String MENSAJE_VAL_USUARIO_1 = "caracteres especiales validos @ . _ -";
	public static final String MENSAJE_VAL_USUARIO_AD = "ya existe un admin para esta empresa";
	public static final String MENSAJE_VAL_REQUERIDO = "requerido";
	
	public static final String USUARIO_INTERNO = "Interno";
	public static final String USUARIO_EXTERNO = "Externo";
	
	public static final String CHANGE_PASSWORD = "usuario/cambiarContrasena";
	public static final String USER_NAME = "username";
	public static final String CONTRASENA = "contrasena";
	public static final String REPITA_CONTRASENA ="repitaContrasena";
	public static final String USUARIO_CONTRASENA = "usuario.contrasena";
	public static final String USUARIO_NUEVA_CONTRASENA = "usuario.nuevaContrasena";
	public static final String USUARIO_REPITA_CONTRASENA ="usuario.repitaContrasena";
	public static final String ULTIMO_INGRESO = "ultimoIngreso";
	
	public static final String NUEVA_CONTRASENA = "nuevaContrasena";
	public static final String INTENTOS = "intentos";
	public static final String NOMBRE_USUARIO = "usuario.nombreUsuario";
	public static final String USUARIO = "Usuario";
	public static final String USUARIOS = "Usuarios";
	public static final String ID_EMPRESA ="idEmpresa";
	public static final String ID_SUCURSAL = "idSucursal";
	public static final String U_SUARIO = "usuario";
	public static final String U_SUARIOS = "usuarios";
	public static final String CAMBIO_C = "cambioC";
	public static final String OPCIONES = "opciones";
	public static final String TIPOS_AUTORIZADO = "tiposAutorizado";
	public static final String TIPO_AUTORIZADO_MODEL = "tipoAutorizadoModel";

	public static final String AGENCIAS = "agencias";
	public static final String LISTAR_AGENCIA = "Listar agencias";
	public static final String CREAR_AGENCIA = "Crear agencia";
	public static final String AGENCIA_MODEL = "agenciaModel";
	public static final String ACCION_AGENCIA = "{0} Agencia : NombreAgencia({1});  idAgencia({2}); idUsuario({3}); nombreUsuario({4});";
	public static final String EDICION_AGENCIA = "Editar agencia";

	public static final String ACTIVA = "Activa";
	
	public static final String GENERAR = "generar";
	public static final String CREATE = "create";
	public static final String GET = "get";
	public static final String UPDATE="update";

	public static final String AGENCIA_DIAS = "agenciaDias";
	public static final String AGENCIA_DIAS_MODEL = "agenciaDiaOperaciones";

	public static final String FORMATO_FECHA_EMAIL = "dd/MM/yyyy 'a las' hh:mm:ss a";
	public static final String ASUNTO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO = "Solicitud de Retiro Procesada en Exterior NEXO Divisas";
	public static final String ENCABEZADO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO = "Estimado(a) ";
	public static final String CUERPO_CORREO_SOLICITUD_PROCESADA_AUTORIZADO = "Le informamos que en fecha {0} ha sido procesada la solicitud de retiro identificada con el Carta Porte <b>{1}</b>. <br/><br/> Esta solicitud fue generada para usted, por la empresa {2}, quienes le autorizan a presentarse el día {3} en nuestra agencia {4}. <br/><br/> Recuerde traer sus documentos de identificación y su teléfono celular.";
	public static final String PIE_CORREO_SOLICITUD_PROCESADA_AUTORIZADO = "Este es un mail informativo que no requiere ser respondido.";

	public static final String EXITO = "Validación Exitosa";
	public static final String OTP_INVALIDO = "Otp Invalido";
	public static final String OTP_EXCEDIDO ="Excedio el numero de intentos";
	public static final String COD_0000 = "0000";
	
}
