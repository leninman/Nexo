
function validar_remesa(){
	var muestra=0;
	var mensaje ="";
	
	if($( "#cartaporte" ).val() == ""){
		$("#cartaporte").css("border", "1px solid red");
		mensaje += "Debe ingresar un número de \"Carta Porte\" válido. <br/>";
		muestra = 1;		
	}else{
		$("#cartaporte").css("border", "");
	}
	
	if($( "#fechaInicio" ).val() == ""){
		$("#fechaInicio").css("border", "1px solid red");
		mensaje += "Debe seleccionar Fecha de Inicio.<br/>";
		muestra = 1;
	}else{
		$("#fechaInicio").css("border", "");
	}
	
	if($( "#fechaFin" ).val() == ""){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += "Debe seleccionar Fecha de Fin.<br/>";
		muestra = 1;
	}else{
		$("#fechaFin").css("border", "");
	}
	
	var fi = $("#fechaInicio").datepicker('getDate');
	var ff = $("#fechaFin").datepicker('getDate');
	
	if(fi>ff){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += ' Fecha Fin debe ser mayor o igual que Fecha Inicio.<br/>';
		muestra = 1;
	}
	
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$('#exampleModalCenter').modal('show');
		$('#tablaRemesa').DataTable().destroy();
		$("#father").hide();
	}else{
		var fechaInicio =  $( "#fechaInicio" ).val();	
		var fechaFin =  $( "#fechaFin" ).val();
		var cartaporte = $( "#cartaporte" ).val();
		tablaRemesa(fechaInicio, fechaFin, cartaporte);
		$("#father").show();
	}
}


function validar_no_aptos(){
	var muestra=0;
	var mensaje ='';
	
	if($( "#fechaInicio" ).val() == ""){
		$("#fechaInicio").css("border", "1px solid red");
		mensaje += 'Debe seleccionar Fecha de Inicio. <br/>';
		muestra = 1;
	}else{
		$("#fechaInicio").css("border", "");
	}
	
	if($( "#fechaFin" ).val() == ""){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += 'Debe seleccionar Fecha de Fin.<br/>';
		muestra = 1;
	}else{
		$("#fechaFin").css("border", "");
	}
	var fi = $("#fechaInicio").datepicker('getDate');
	var ff = $("#fechaFin").datepicker('getDate');
	
	if(fi>ff){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += 'Fecha de Fin debe ser mayor o igual que Fecha Inicio.<br/>';
		muestra = 1;
	}
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$('#exampleModalCenter').modal('show');
		$('#tablaNoAptos').DataTable().destroy();
		$("#father").hide();
	}else{
		var fechaInicio =  $( "#fechaInicio" ).val();	
		var fechaFin =  $( "#fechaFin" ).val();
		var moneda = $( "#moneda" ).val();

		tablaNoAptos(fechaInicio, fechaFin, moneda);
		
		$("#father").show();
	}
}

function validar_consolidada(){
	var muestra=0;
	var mensaje ='';
	
	if($( "#fechaInicio" ).val() == ""){
		$("#fechaInicio").css("border", "1px solid red");
		mensaje += 'Debe seleccionar Fecha de Inicio. <br/>';
		muestra = 1;
	}else{
		$("#fechaInicio").css("border", "");
	}
	
	if($( "#fechaFin" ).val() == ""){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += 'Debe seleccionar Fecha de Fin.<br/>';
		muestra = 1;
	}else{
		$("#fechaFin").css("border", "");
	}
	
	var fi = $("#fechaInicio").datepicker('getDate');
	var ff = $("#fechaFin").datepicker('getDate');
	
	if(fi>ff){
		$("#fechaFin").css("border", "1px solid red");
		mensaje += 'Fecha Fin debe ser mayor o igual que Fecha Inicio.<br/>';
		muestra = 1;
	}
	
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$("#exampleModalCenter").modal('show');
		$("#tablaRemesas").DataTable().destroy();
		$("#father").hide();
	}else{
		var fechaInicio =  $( "#fechaInicio" ).val();	
		var fechaFin =  $( "#fechaFin" ).val();
		var moneda = $( "#moneda" ).val();

		tabla(fechaInicio, fechaFin, moneda);
		
		$("#father").show();
	}
}
