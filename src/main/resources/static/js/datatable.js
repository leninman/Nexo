
 
function tabla(fechaInicio, fechaFin, moneda) {
	$('#tablaRemesas').DataTable().destroy();
	var accion = "Descargar Posicion Consolidada";
	var detalle = "Descarga el reporte:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle + ': fecha inicio('+fechaInicio+'); fecha fin('+fechaFin+'); moneda('+moneda+')';	
	
	 var table = $('#tablaRemesas').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',					        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "No hay movimientos en el rango de fechas y moneda seleccionada",
		        emptyTable:     "No hay movimientos en el rango de fechas y moneda seleccionada",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        }
		    },
			sAjaxSource: "remesa/"+fechaInicio+"/"+fechaFin+"/"+moneda,
			sAjaxDataProp: "",
			columns: [
			      { data: "fecha"},
				  { data: "referencia" },
				  { data: "concepto" },
				  { data: "debito" },
				  { data: "credito" },
				  { data: "saldo" }
				  
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    bSort : false,
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
		            $('row:eq(0) c', sheet).attr( 's', '2' );
		            $('row c[r^="D"]', sheet).attr( 's', '62' );
		          },
		      },
		      {extend: 'csvHtml5'}, 
		      {extend: 'pdf'}
		      
		      ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');
	        },
            columnDefs: [
            	{ className: "text-right", "targets": [1,3,4,5]},
            	{ type: "string", "targets": [3,4,5]},
            	{ type:"date-eu", targets :[0]}
            ],
            pageLength:15,
            
	 })
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}

function cargarFecha(){	
	$('#fechaInicio').val($.datepicker.formatDate('dd-m-yy', new Date()));
	$('#fechaFin').val($.datepicker.formatDate('dd-m-yy', new Date()));
}

function registrar(accion, detalle, opcion){	
	$.ajax({
	    url: 'RegistrarLog',
	    dataType: 'json',
	    type: 'post',
	    contentType: 'application/json',
	    data: JSON.stringify( { "accion": accion, "detalle": detalle , "opcion":opcion} ),
	    processData: false
	});
}

function tablaNoAptos(fechaInicio, fechaFin, moneda) {
	$('#tablaNoAptos').DataTable().destroy();
	var accion = "Descargar Posicion BNA";
	var detalle = "Descarga el reporte BNA:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle +fechaInicio+'); fecha fin('+fechaFin+'); moneda('+moneda+')';
	 var table = $('#tablaNoAptos').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',		        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Usted no tiene registro de Billetes No Aptos",
		        emptyTable:     "Usted no tiene registro de Billetes No Aptos",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        },
		        aria: {
		            sortAscending:  ": ordenar ascendente",
		            sortDescending: ": ordenar descendente"
		        }
		    },
			"sAjaxSource": "./remesaNoApta/"+fechaInicio+"/"+fechaFin+"/"+moneda,
			"sAjaxDataProp": "",
			"aoColumns": [
			      { "mData": "fecha"},
				  { "mData": "referencia" },
				  { "mData": "concepto" },
				  { "mData": "saldo" },
                  { "mData": function (data, type, row, meta) {
                             	return '<a href="#" data-toggle="modal" data-target="#detalleRemesa" onclick= "verDetalle(\''+ data.referencia + '\');">ver</a>';}
                  }				  
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
		            $('row:eq(0) c', sheet).attr( 's', '2' );
		          }
		      },
		      {extend: 'csvHtml5'}, 
		      {extend: 'pdf'}
		      
		      ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');
	        },
            columnDefs: [{ className: "text-right", "targets": [3]},
            			 { type:"date-eu", targets :[0]}]
	 })
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}



function tablaIrregularidades(cartaporte) {
	$('#tablaIrregularidades').DataTable().destroy();
	var accion = "Descargar Posicion BNA";
	var detalle = "Descarga el Detalle BNA:  Carta Porte(";
	var opcion = "Descarga";
	detalle = detalle +cartaporte+')';
	 var table = $('#tablaIrregularidades').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',	        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Usted no tiene registro de Billetes No Aptos",
		        emptyTable:     "Usted no tiene registro de Billetes No Aptos",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        },
		        aria: {
		            sortAscending:  ": ordenar ascendente",
		            sortDescending: ": ordenar descendente"
		        }
		    },
			"sAjaxSource": "./irregularidades/"+cartaporte,
			"sAjaxDataProp": "",
			"aoColumns": [
			      { "mData": "fecha"},
				  { "mData": "referencia" },
				  { "mData": "concepto" },
				  { "mData": "denominacion" },
				  { "mData": "cantidad" },
				  //{ "mData": "clasificacion" },
				  { "mData": "moneda" },
				  { "mData": "centro" }
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    buttons: [
		    	'excel',
                'csv',
                'pdf'
            ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');
	        }
	 })
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}

function trackingRemesa(fechaInicio, fechaFin, cartaPorte) {
	$('#trackingRemesa').DataTable().destroy();
	var accion = "Descargar Tracking de Remesas";
	var detalle = "Descarga el Tracking:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle +fechaInicio+'); fecha fin('+fechaFin+'); cartaporte('+cartaporte+')';
	 var table = $('#trackingRemesa').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',		        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Disculpe... Número de Carta Porte no encontrado",
		        emptyTable:     "Disculpe... Número de Carta Porte no encontrado",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        },
		        aria: {
		            sortAscending:  ": ordenar ascendente",
		            sortDescending: ": ordenar descendente"
		        }
		    },
			"sAjaxSource": "./remesabycartaporte/"+fechaInicio+"/"+fechaFin+"/"+cartaPorte,
			"sAjaxDataProp": "",
			"order": [[ 0, "d" ]],
			"aoColumns": [
			      { "mData": "fecha"},
				  { "mData": "referencia" },
				  { "mData": "estado" },
				  { "mData": "monto" },
				  { "mData": "moneda" },
				  { "mData": "centro" }
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
		            $('row:eq(0) c', sheet).attr( 's', '2' );
		          }
		      },
		      {extend: 'csvHtml5'}, 
		      {extend: 'pdf'}
		      ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');

	        },
            columnDefs: [{ className: "text-right", "targets": [3]}]
	 })
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}


function remesasPendientes() {
	//$('#tablaRemesasPendientes').DataTable().destroy();
	var accion = "Descargar Remesas Pendientes";
	var detalle = "Descarga las Remesas Pendientes";
	var opcion = "Descarga";
	 var table = $('#tablaRemesasPendientes').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',		        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "No tienes Remesas Pendiente por Entregar",
		        emptyTable:     "No tienes Remesas Pendiente por Entregar",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        },
		        aria: {
		            sortAscending:  ": ordenar ascendente",
		            sortDescending: ": ordenar descendente"
		        }
		    },
			"sAjaxSource": "./remesaEntregaPendiente",
			"sAjaxDataProp": "",
			"order": [[ 0, "d" ]],
			"aoColumns": [
			      { "mData": "fecha"},
				  { "mData": "referencia" },
				  { "mData": "estado" },
				  { "mData": "centro" },
				  { "mData": "moneda" },
				  { "mData": "monto" }
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
		            $('row:eq(0) c', sheet).attr( 's', '2' );
		          }
		      },
		      {extend: 'csvHtml5'}, 
		      {extend: 'pdf'}
		      ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');

	        },
            columnDefs: [{ className: "text-right", "targets": [3,5]},
            			{ className: "text-center", "targets": [0,4]},
            			{ type:"date-eu", targets :[0]}]
	        
	 })
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}

function tablaReporteSucursal(fechaInicio, fechaFin, sucursal, moneda) {
	$('#tablaReporteSucursal').DataTable().destroy();
	var accion = "Descargar Reporte por Sucursal";
	var detalle = "Descarga el reporte:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle + ': fecha inicio('+fechaInicio+'); fecha fin('+fechaFin+'); sucursal('+sucursal+'); moneda('+moneda+')';	
	
	 var table = $('#tablaReporteSucursal').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',					        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "No hay movimientos en el rango de fechas, sucursal y moneda seleccionada",
		        emptyTable:     "No hay movimientos en el rango de fechas, sucursal y moneda seleccionada",
		        paginate: {
		            first:      "Primero",
		            previous:   "Previo",
		            next:       "Siguiente",
		            last:       "Ultimo"
		        }
		    },
			sAjaxSource: "reporteSucursal/"+fechaInicio+"/"+fechaFin+"/"+sucursal+"/"+moneda,
			sAjaxDataProp: "",
			order: [[ 0, "d" ]],
			order: [[ 0, "asc" ]],
			columns: [
			      { data: "fecha"},
				  { data: "referencia" },
				  { data: "concepto" },
				  { data: "debito" },
				  { data: "credito" }
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    bSort : false,
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
		            $('row:eq(0) c', sheet).attr( 's', '2' );
		          }
		      },
		      {extend: 'csvHtml5'}, 
		      {extend: 'pdf'}
		      
		      ],
	        initComplete: function () {
	            var btns = $('.dt-button');
	            btns.addClass('btn btn-info btn-sm');
	            btns.removeClass('dt-button');
	        },
            columnDefs: [
            	{ className: "text-right", "targets": [1,4]},
            	{ className: "text-center", "targets": [0,3]},
            	{ type: "string", "targets": [3,4]},
            	{ type:"date-eu", targets :[0]}
            ],
            pageLength:15,
            
	 })
	 $("#saldoTotal").html(getTotalBySucursal(sucursal,moneda));
	 
	 $(".buttons-excel").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-csv").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
	 $(".buttons-pdf").on('click', function(event){
		 registrar(accion, detalle, opcion);
		});
}

function getTotalBySucursal(sucursal, moneda) {
	var strReturn="";
	  jQuery.ajax({
	    url:"totalPorSucursal/"+sucursal+"/"+moneda,
	    success: function(html) {
	      strReturn = Number(parseFloat(html).toFixed(2)).toLocaleString('de', {minimumFractionDigits: 2}); 
	    },
	    async:false, 
	    error: function() {
	    	alert("error");
	    }
	  });

	  return strReturn;
}
