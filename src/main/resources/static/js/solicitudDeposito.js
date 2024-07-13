var idCliente;
var ipOrigen;


$(document).ready(function() {
	$("#ejecReverso").hide();
});


$("#pass").keyup(function() {
	var usuario, contraseña;
	
		usuario = $.trim($("#usuario").val());
		contraseña = $.trim($("#pass").val());
		if (usuario.length > 0 && contraseña.length > 0) {
			$("#aceptarReverso").prop('disabled',false);
		} 
});
/*$("#pass").keyup(function() {
	var usuario, contraseña;
	usuario = $.trim($("#usuario").val());
	contraseña = $.trim($("#pass").val());
	if (usuario.length > 0 && contraseña.length > 0) {
		$("button[aceptarReverso='submit']").removeAttr('disabled');
	} else {
		$("button[aceptarReverso='submit']").attr('disabled', 'disabled');
	}
});*/
$("#aceptarReverso").click(function() {
	$("#ejecReverso").show();
	$("#solicReverso").hide();
});


function getFormulario(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#depositosContainer').html(response);
		}
	})
}



function processPostForm(accion, form) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: new FormData(form),
		processData: false,
		contentType: false,
		success: function(response) {
			$('#spinnerModal').modal('hide');
			if (response == 'errores')
				$(location).attr('href', './error');
			else
				$('#depositosContainer').html(response);
		},
		error: function(xhr) {
			$('#spinnerModal').modal('hide');
			$(location).attr('href', './error');
		}
	});
}

function printVoucher() {

	document.getElementById('print').style.display = 'none';
	window.print();
	document.getElementById('print').style.display = 'inline'
}

/*$("#buscarCuenta").click(function(){
	idCliente=$("#tipoDocumento").val()+$("#numeroDocumento").val();
	ipOrigen=$("#ipOrigen").val();

	$.ajax({
		data:{
			idCliente:idCliente,
			ipOrigen:ipOrigen,
		},
		url:
	})
});*/


