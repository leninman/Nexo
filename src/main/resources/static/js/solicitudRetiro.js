/*// document.querySelector("#buscarRetiro").onkeyup = function(){
		$TableFilter("#tableListRetiros", this.value);
	}
    
	$TableFilter = function(id, value){
		var rows = document.querySelectorAll(id + ' tbody tr');
	    
		for(var i = 0; i < rows.length; i++){
			var showRow = false;
		    
			var row = rows[i];
			row.style.display = 'none';
		    
			for(var x = 0; x < row.childElementCount; x++){
				if(row.children[x].textContent.toLowerCase().indexOf(value.toLowerCase().trim()) > -1){
					showRow = true;
					break;
				}
			}
		    
			if(showRow){
				row.style.display = null;
			}
		}
	}*/

function getFormulario(url) {
	$('#spinnerModal').modal('show');
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
			$(document).ready(function() {
				$("#autorizado").select2();
			});

		}
	})
}

function processGetForm(accion, form) {
	$.ajax({
		cache: false,
		url: accion,
		data: new FormData(form),
		processData: false,
		contentType: false,
		success: function(response) {
			$('#solicitudesRetiroContainer').html(response);
		}
	});
}

function processForm(accion, form) {
	var montoErrado = false;
	if ($("#fechaEstimada").val() === "") {
		document.getElementById("fechaEstimadaError").style.display = "block";
	} else {
		document.getElementById("fechaEstimadaError").style.display = "none";
	}
	if ($("#monto").val() === "") {
		document.getElementById("montoError").innerText = "requerido";
		document.getElementById("montoError").style.display = "block";
	} else {
		if ($("#moneda").val() == 2) {
			var monto = $("#monto").val();
			if (monto % 5 != 0) {
				montoErrado = true;
				document.getElementById("montoError").innerText = "Para la moneda seleccionada el monto deber ser m\u00FAltiplo de 5";
				document.getElementById("montoError").style.display = "block";
			} else {
				document.getElementById("montoError").style.display = "none";
			}
		} else {
			document.getElementById("montoError").style.display = "none";
		}
	}
	if ($("#autorizado").val() == null) {
		document.getElementById("autorizadoError").style.display = "block";
	} else {
		document.getElementById("autorizadoError").style.display = "none";
	}

	if ($("#fechaEstimada").val() === "" || $("#monto").val() === "" || $("#autorizado").val() == null || montoErrado) {
		return;
	}

	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		async: false,
		data: form.serialize(),
		success: function(response) {
			$('#duplicadaTitulo').text(response);
			$('#duplicadaModal').modal('show');
			if (response == '0')
				$('#divError').show();
			if (accion != 'actualizarSolicitudRetiro')
				pintarTabla();
			else
				$('#solicitudesRetiroContainer').html(response);
		},
		error: function(response) {
			$('#duplicadaTitulo').text(response);
			$('#duplicadaModal').modal('show');
		}

	});
}

function editarSolicitud(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "editarSolicitudRetiro",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
			$(document).ready(function() {
				$("#autorizado").select2();
			});
		}
	});
}

function verSolicitud(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "getSolicitudRetiroAprobar",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
		}
	});
}

function verSolicitudValidar(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "getSolicitudRetiroValidar",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
		}
	});
}

function verSolicitudProcesar(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "getSolicitudRetiroProcesar",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
		}
	});
}

function verSolicitudEntregar(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "getSolicitudRetiroEntregar",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
		}
	});
}

function eliminarSolicitudRetiro(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "POST",
		cache: false,
		url: "anularSolicitudRetiro",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
			document.querySelectorAll(".modal-backdrop")[0].remove();
		},
	});
}

function onlyNumbers(event) {
	const valid = event.charCode >= 48 && event.charCode <= 57;
	return valid;
}

function checkSolicitud() {
	const checkboxes = document.querySelectorAll('input[name="solicitudSeleccionada"]:checked');
	let values = "";
	checkboxes.forEach((checkbox) => {
		values = values.concat("[" + checkbox.value + "]");
	});

	document.getElementById("idsSolicitudAprobar").value = values;
	document.getElementById("idsSolicitudAnular").value = values;

	if (values === "") {
		document.getElementById("aprobarSeleccionadas").disabled = true;
		document.getElementById("anularSeleccionadas").disabled = true;
	} else {
		document.getElementById("aprobarSeleccionadas").disabled = false;
		document.getElementById("anularSeleccionadas").disabled = false;
	}
}

function limpiar() {
	$("#tipoBillete option:selected").removeAttr("selected");
	$("#autorizado option:selected").removeAttr("selected");
	$("#moneda option:selected").removeAttr("selected");
	$("#agencia option:selected").removeAttr("selected");

	$('#fechaEstimada').val('');
	$('#monto').val('');
	$('#crearSolicitud').val('true');
	$('#botonAgregar').val('Agregar');
	$("#fechaEstimadaError").hide();
	$("#montoError").hide();
}

function editarSolicitudRet(id) {
	limpiar();
	$.ajax({
		type: "GET",
		cache: false,
		url: "editSolicitudRetiro",
		data: {
			"idSolicitud": id,
			"crearSolicitud": false
		},
		success: function(response) {
			$('#tipoBillete').val(response.tipoBillete);
			$('#fechaEstimada').val(response.fechaEstimada);
			$('#autorizado').val(response.idAutorizado);
			$('#monto').val(response.monto);
			$('#moneda').val(response.idMoneda);
			$('#agencia').val(response.idAgencia);
			$('#idSolicitud').val(response.idSolicitud);
		},
	});
	$("#monto").focus();
	$('#crearSolicitud').val('false');
	$('#botonAgregar').val('Actualizar');

}

function pintarTabla() {
	$('#divtabla').show();
	tablaCargaMasiva();
	limpiar();
	habilitarGuardarBtn();
}

function agregarSolicitud(accion, form) {
	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: form.serialize(),
		success: function(response) {
			pintarTabla();
		}
	});
}

async function cargaMasiva() {
	$('#errorFile').hide();
	let formData = new FormData();
	formData.append("file", archivo.files[0]);
	let response = await fetch('./cargaMasiva', {
		method: "POST",
		body: formData
	});
	if (response.status == 200) {
		pintarTabla();
		$('#fileContainer').hide();
		verSolicitudes();
		verErrores();
		$('#confirmacionModal').modal('show');
	} else {
		var texto = $('.file-message').text();
		if (texto == '')
			$('#errorFile').show();
	}
}

function versolicitudesMasivas() {
	$.ajax({
		type: "GET",
		cache: false,
		url: "./solicitudesMasivas",
		data: {},
		success: function(response) {

		}
	});
}

function eliminarSolicitudPost(id) {
	$.ajax({
		type: "POST",
		cache: false,
		url: "eliminarSolicitudRetiro",
		data: { "id": id },
		success: function(response) {
			$('#solicitudDeleteModal').modal('hide');
			tablaCargaMasiva();
			habilitarGuardarBtn();
		}
	});
}

function guardarCargaMasiva() {
	$('#spinnerModal').modal('show');
	var request = $.ajax({
		method: "POST",
		cache: false,
		url: "crearSolicitudesDeRetiro",
		data: {}
	});

	request.done(function(msg) {
		$('#spinnerModal').modal('hide');
		$(location).attr('href', './solicitudesRetiro?success');
	});

	request.fail(function(jqXHR, textStatus) {
		$(location).attr('href', './solicitudesRetiro?error');
	});
}


function eliminarSolicitud(id) {
	$('#submitBtn').attr('onClick', 'eliminarSolicitudPost(\'' + id + '\')');
	$('#solicitudDeleteModal').modal('show');
}

function verSolicitudes() {
	$.ajax({
		type: "GET",
		cache: false,
		url: "./nroSolicitudesMasivas",
		data: {},
		success: function(response) {
			if (response > 0) {
				$('#exitoDiv').show();
				$('#nroSolicitudes').text(response);
			}
		},
	});
}

function verErrores() {
	var arr = '';
	$.ajax({
		type: "GET",
		cache: false,
		url: "./erroresSolicitudesMasivas",
		data: {},
		success: function(response) {
			$('#listErrorDiv').html('');
			if (response.length > 0) {
				$('#errorDiv').show();
				$('#nroErrores').text(response.length);
				$.each(response, function(index, value) {
					var res = value.toString().split(',');
					arr += res[0] + '<br/> ' + res[1] + '<br/>';
				});
				$('#listErrorDiv').html(arr);
			}
		},
	});
}

function habilitarGuardarBtn() {
	$.ajax({
		type: "GET",
		cache: false,
		url: "./solicitudesMasivas",
		data: {},
		success: function(response) {
			if (response.length > 0) {
				$('#btnCargaMasiva').attr("disabled", false);
			} else {
				$('#btnCargaMasiva').attr("disabled", true);
			}
		}
	});
}

function getEntregaForm(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#solicitudesRetiroContainer').html(response);
		}
	})
}

function SolicitudEntregarForm(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "GET",
		cache: false,
		url: "generarOtpEntregaSolicitudRetiro",
		data: { "idSolicitud": id },
		success: function(response) {
			$('#entregarModal').modal('show');
			$('#spinnerModal').modal('hide');
			$('#validarOtpContainer').html(response);

		}
	});
}

function entrega(id) {
	SolicitudEntregarForm(id);
}

function processvalidarOtpForm(accion, form) {
	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: form.serialize(),
		success: function(response) {
			if ($(response).find('.text-danger').length) {
				$('#validarOtpContainer').html(response);
			} else {
				$('#entregarModal').modal('hide');
				$('#spinnerModal').modal('hide');
				$('#solicitudesRetiroContainer').html(response);
			}
		}
	});
}

var t = new Date();
t.setMinutes(t.getMinutes() + 3);

countDownDate = t.getTime();
// Update the count down every 1 second
var x = setInterval(function() {

	// Get today's date and time
	var now = new Date().getTime();

	// Find the distance between now and the count down date
	var distance = countDownDate - now;
	// Time calculations for days, hours, minutes and seconds
	var minutes = Math.floor((distance % (1000 * 60 * 60)) / (1000 * 60));
	var seconds = Math.floor((distance % (1000 * 60)) / 1000);

	if (seconds < 10) {
		seconds = '0' + seconds;
	}

	$('#timer').html('0' + minutes + ':' + seconds);

	// If the count down is finished, write some text
	if (distance < 0) {
		clearInterval(x);
		$('#timer').html('00:00');
		var url = $('#back').attr('href');
		location.replace(url);
	}
}, 1000);
