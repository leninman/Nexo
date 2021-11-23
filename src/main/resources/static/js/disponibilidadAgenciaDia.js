
function verAgencias(){
	var muestra=0;
	var mensaje ='';
	
	if($( "#fechaI" ).val() == ""){
		$("#fechaI").css("border", "1px solid red");
		mensaje += 'Debe seleccionar una Fecha. <br/>';
		muestra = 1;
	}else{
		$("#fechaI").css("border", "");
	}
	
	var fi = $("#fechaI").datepicker('getDate');
	
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$("#exampleModalCenter").modal('show');
		
	}else{
		var fechaI =  $( "#fechaI" ).val();	
	}
	
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$("#exampleModalCenter").modal('show');
		$("#disponibilidadAgenciaDiaContainer").html().destroy();
		$("#disponibilidadAgenciaDiaContainer").hide();
	}else{
		var fechaInicio =  $( "#fechaInicio" ).val();			
		$("#disponibilidadAgenciaDiaContainer").show();
	}
	
	var formData = new FormData ();
	const fechaIni = document.getElementsByName("fechaI")[0];
	formData.append('fechaS', fechaIni.value);
	
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "POST",
		cache: false,
		url: "mapaAgenciaDia",
		data: formData,
		processData: false,
		contentType: false,
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#disponibilidadAgenciaDiaContainer').html(response);
			
		}
	});

}