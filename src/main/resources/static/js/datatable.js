
 
function tabla(fechaInicio, fechaFin, moneda) {
	$('#reportable').DataTable().destroy();
	var accion = "Descargar Posicion Consolidada";
	var detalle = "Descarga el reporte:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle + ': fecha inicio('+fechaInicio+'); fecha fin('+fechaFin+'); moneda('+moneda+')';
	
	
	 var table = $('#reportable').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',					        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Sin registros",
		        emptyTable:     "No hay movimientos en el rango de fechas y moneda seleccionada",
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
			sAjaxSource: "remesa/"+fechaInicio+"/"+fechaFin+"/"+moneda,
			sAjaxDataProp: "",
			order: [[ 0, "desc" ]],
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
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
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
            	{ className: "text-right", "targets": [1,3,4,5]},
            	{ type: "string", "targets": [3,4,5]}
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
	$('#reportable').DataTable().destroy();
	var accion = "Descargar Posicion BNA";
	var detalle = "Descarga el reporte BNA:  fecha inicio(";
	var opcion = "Descarga";
	detalle = detalle +fechaInicio+'); fecha fin('+fechaFin+'); moneda('+moneda+')';
	 var table = $('#reportable').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',		        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Sin registros",
		        emptyTable:     "No hay datos para visualizar",
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
			"order": [[ 0, "asc" ]],
			"aoColumns": [
			      { "mData": "fecha"},
				  { "mData": "referencia" },
				  { "mData": "concepto" },
				  { "mData": "saldo" },
                  { "mData": function (data, type, row, meta) {
                             	return '<a href="#" data-toggle="modal" data-target="#detalleRemesa" onclick= "verDetalle(\''+ data.referencia + '\');">ver</a>';
                              }
                  }				  
			],
			bFilter: false,		    
		    dom: 'Bfrtip',
		    buttons: [{
		        extend: 'excelHtml5',
		        customize: function( xlsx ) {
		            var sheet = xlsx.xl.worksheets['sheet1.xml'];
		            $('row c[r^="A"]', sheet).attr( 's', '0' );
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
            columnDefs: [{ className: "text-right", "targets": [3]
            }]
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
	$('#tablaNoAptos').DataTable().destroy();
	var accion = "Descargar Posicion BNA";
	var detalle = "Descarga el Detalle BNA:  Carta Porte(";
	var opcion = "Descarga";
	detalle = detalle +cartaporte+')';
	 var table = $('#tablaNoAptos').DataTable({
	        processing: true,
	        language: {			 
	        	processing: '<i class="fa fa-spinner fa-spin fa-3x fa-fw"></i><span class="sr-only">Cargando...</span> ',	        
		        lengthMenu:    	"Mostrar _MENU_ elementos",
		        info:           "Mostrando elemento _START_ al _END_ de _TOTAL_ elementos",		     
		        zeroRecords:    "Sin registros",
		        emptyTable:     "No hay datos para visualizar",
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
			"order": [[ 0, "asc" ]],
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

function tablaRemesa(fechaInicio, fechaFin, cartaPorte) {
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
		        zeroRecords:    "Sin registros",
		        emptyTable:     "Número de Carta Porte No Encontrado",
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