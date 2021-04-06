
 function strDes(a, b) {
   if (a.value>b.value) return 1;
   else if (a.value<b.value) return -1;
   else return 0;
 }

function pasarLista1_Lista2(idListBox1, idListBox2){
	var idLB1 = idListBox1 + ' option:selected';
    var selectedOpts = $('#'+idLB1);

    $('#'+idListBox2).append($(selectedOpts).clone());

    $(selectedOpts).remove();	
}

function muestra_lista(idListBox){
	validar();
	var list = $('#' + idListBox + ' option')[0];
	//alert(list);
/*	$('#' + idListBox + ' option').each(function(){
			// this should loop through all the selected elements
			alert($(this).val())
			});*/ 
}

function validar(){
	var muestra=0;
	var mensaje ="";
	
	if($("#nombrePerfil").val() == ""){
		$("#nombrePerfil").css("border", "1px solid red");
		mensaje += "Debe ingresar el nombre del Perfil. <br/>";
		muestra = 1;		
	}else{
		$("#nombrePerfil").css("border", "");
	}
	
	if($('#lstBox2')){
		$("#DivlstBox2").css("border", "1px solid red");
		mensaje += "Debe seleccionar al menos una opcion del menú. <br/>";
		muestra = 1;
	}else{
		$("#subject-info-box-2").css("border", "");
	}
	
	if($('#lstBox4') ){
		$("#DivlstBox4").css("border", "1px solid red");
		mensaje += "Debe seleccionar al menos un Usuario. <br/>";
		muestra = 1;
	}else{
		$("#subject-info-box-2").css("border", "");
	}
	
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$('#exampleModalCenter').modal('show');		
	}else{
		seleccionarTodos();
	}
}

function seleccionarTodos(){	
    $('#lstBox2 option').each(function() {
        $(this).attr('selected',true);
		$(this).prop("selected", true);
      });
    
    $('#lstBox4 option').each(function() {
        $(this).attr('selected',true);
		$(this).prop("selected", true);
      });
}

function limpiarTodos(){	
    $('#lstBox2 option').each(function() {
        $(this).removeAttr('selected');
      });
    
    $('#lstBox4 option').each(function() {
        $(this).removeAttr('selected');
      });
}
