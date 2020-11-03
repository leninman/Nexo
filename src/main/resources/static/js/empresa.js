function getMunicipios(event) {
	var idEstado = event.target.value;

	$.getJSON("municipios/" + idEstado, function(json) {
		$('#idMunicipio').empty();
		$('#idMunicipio').append($('<option>').text("Seleccionar").attr('value', -1));
		$.each(json, function(i, obj) {
			$('#idMunicipio').append($('<option>').text(obj.nombre).attr('value', obj.id));
		});
	});
}

function getSucursalMunicipios(event) {
	var idEstado = event.target.value;

	$.getJSON("municipios/" + idEstado, function(json) {
		$('#idMunicipioSucursal').empty();
		$('#idMunicipioSucursal').append($('<option>').text("Seleccionar").attr('value', -1));
		$.each(json, function(i, obj) {
			$('#idMunicipioSucursal').append($('<option>').text(obj.nombre).attr('value', obj.id));
		});
	});
}

function getRecords(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#empresaContainer').html(response);
		}
	})
}

function editarEmpresa(id) {
	getRecords("empresaEditar/" + id);
}

function processEmpresaForm(accion, form) {
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: new FormData(form),
		processData: false,
		contentType: false,
		success: function(response) {
			$('#empresaContainer').html(response);
		}
	});
}

function processSucursalForm(accion, form) {
	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: form.serialize(),
		success: function(response) {
			$('#sucursalContainer').html(response);
		}
	});
}

function getSucursalForm(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#sucursalContainer').html(response);
		}
	})
}

function crearSucursal(id) {
	getSucursalForm("sucursalCrear/" + id);
}

function editarSucursal(id) {
	getSucursalForm("sucursalEditar/" + id);
}

function sucursales(id) {
	$('#sucuralModal').modal('show');
	getSucursalForm("sucursalHome/" + id);
}

function loadLogo(event) {
	var reader = new FileReader();

	reader.onload = function() {
		var container = document.getElementById('logoContainer');
		container.innerHTML = '';
		var imageNode = document.createElement('img');
		imageNode.className = 'logo-image';
		imageNode.id = 'imagenLogo';
		imageNode.src = reader.result;
		document.getElementById('logoEmpresa').value = reader.result.substring(reader.result.indexOf(",") + 1);
		container.appendChild(imageNode);
	};	
	reader.readAsDataURL(event.target.files[0]);

};
