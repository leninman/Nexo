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


function validarImagen() {
	const fi = document.getElementById('logo');
	var filePath = fi.value;

	// Allowing file type
	var allowedExtensions = /(\.jpg|\.jpeg|\.bmp|\.png|\.gif)$/i;

	if (!allowedExtensions.exec(filePath)) {
		if (document.getElementById('logoErrorServer'))
			document.getElementById('logoErrorServer').innerHTML = "";
		document.getElementById("logoErrorClient").style.display = "block";
		document.getElementById('logoErrorClient').innerHTML = "debe tener el formato JPG, GIF, BMP o PNG";
		cleanLogo();
		return false;
	}
	else if (fi.files.length > 0) { // Check size file
		for (i = 0; i <= fi.files.length - 1; i++) {
			const fsize = fi.files.item(i).size;
			const file = Math.round((fsize / 1024));
			// The size of the file.
			if (file >= 1024) {
				if (document.getElementById('logoErrorServer'))
					document.getElementById('logoErrorServer').innerHTML = "";
				document.getElementById("logoErrorClient").style.display = "block";
				document.getElementById('logoErrorClient').innerHTML = "no puede ser mayor a 1MB";
				cleanLogo();
				return false;
			}
		}
	}
	if (document.getElementById('logoErrorServer'))
		document.getElementById('logoErrorServer').innerHTML = "";
	if (document.getElementById('logoErrorClient'))
		document.getElementById('logoErrorClient').innerHTML = "";
	return true;
}

function cleanLogo() {
	document.getElementById('logo').value = '';
	document.getElementById('logoEmpresa').value = '';
	if (document.getElementById('imagenLogo'))
		document.getElementById("imagenLogo").remove();
}

function loadLogo(event) {
	// define reader as a new instance of FileReader
	if (validarImagen()) {
		var reader = new FileReader();
		reader.readAsDataURL(event.target.files[0]);
		// Handle progress, success, and errors
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
	}
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

/*function loadLogo(event) {
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

};*/