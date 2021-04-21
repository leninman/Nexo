function getFormulario(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#solicitudesContainer').html(response);
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
			$('#solicitudesContainer').html(response);
		}
	});
}

function processForm(accion, form) {
	$('#spinnerModal').modal('show');
	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: form.serialize(),
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesContainer').html(response);
		},
		error: function(xhr){
			$('#spinnerModal').modal('hide');
			$(location).attr('href', './error');
		}
	});
}

function editarAutorizado(id) {
	$('#spinnerModal').modal('show');
	$.ajax({
		type: "POST",
		cache: false,
		url: "autorizadoEditar",
		data: { "idAutorizado": id },
		success: function(response) {
			$('#spinnerModal').modal('hide');
			if(response=='errores')
				$(location).attr('href', './error');
			else			
				$('#solicitudesContainer').html(response);
			$('#solicitudesContainer').html(response);
		},
		error: function(){
			$('#spinnerModal').modal('hide');
			$(location).attr('href', './error');
		}
	});
}

function eliminarAutorizado(id) {
	$.ajax({
		type: "POST",
		cache: false,
		url: "autorizadoEliminar",
		data: { "idAutorizado": id },
		success: function(response) {
			$('#solicitudesContainer').html(response);
			document.querySelectorAll(".modal-backdrop")[0].remove();
		},
		error: function(){
			$(location).attr('href', './error');
		}
	});
}

function loadImage(event, imageContainer, id, imageId, imgErrorServer, imgErrorClient) {
	if (validarImagen(imageId, event.target.id, id, imgErrorServer, imgErrorClient)) {
		var reader = new FileReader();
		reader.onload = function() {
			var container = document.getElementById(imageContainer);
			container.innerHTML = '';
			var imageNode = document.createElement('img');
			imageNode.className = 'logo-image';
			imageNode.id = id;
			imageNode.src = reader.result;
			document.getElementById(imageId).value = reader.result.substring(reader.result.indexOf(",") + 1);
			container.appendChild(imageNode);
		};
		reader.readAsDataURL(event.target.files[0]);
	}

};

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
		if(response=='errores')
			$(location).attr('href', './error');
		else			
			$('#solicitudesContainer').html(response);
		},
		error: function(xhr){
			$('#spinnerModal').modal('hide');
			$(location).attr('href', './error');
		}
	});
}

function soloNumeros(input) {
	input.value = input.value.replace(/[^0-9.]/g, '');
}


function limpiarImagen(hiddenId, imgContainer) {
	var etqHidden, etqImgContainer;

	if (hiddenId != '') {
		etqHidden = '#' + hiddenId;
		$(etqHidden).val('');
	}

	if (imgContainer != '') {
		etqImgContainer = '#' + imgContainer;
		$(etqImgContainer).empty();
		$(etqImgContainer).val('');
	}
}

function validarImagen(hiddenImageId, imagenId, shownImageid, imgErrorServer, imgErrorClient) {
	const fi = document.getElementById(imagenId);
	var filePath = fi.value;

	// Allowing file type
	var allowedExtensions = /(\.jpg|\.jpeg|\.bmp|\.png|\.gif)$/i;

	if (!allowedExtensions.exec(filePath)) {
		if (document.getElementById(imgErrorServer))
			document.getElementById(imgErrorServer).innerHTML = "";
		document.getElementById(imgErrorClient).style.display = "block";
		document.getElementById(imgErrorClient).innerHTML = "debe tener el formato JPG, GIF, BMP o PNG";
		cleanImg(imagenId, hiddenImageId, shownImageid);
		return false;
	}
	else if (fi.files.length > 0) { // Check size file
		for (i = 0; i <= fi.files.length - 1; i++) {
			const fsize = fi.files.item(i).size;
			const file = Math.round((fsize / 1024));
			// The size of the file.
			if (file >= 1024) {
				if (document.getElementById(imgErrorServer))
					document.getElementById(imgErrorServer).innerHTML = "";
				document.getElementById(imgErrorClient).style.display = "block";
				document.getElementById(imgErrorClient).innerHTML = "no puede ser mayor a 1MB";
				cleanImg(imagenId, hiddenImageId, shownImageid);
				return false;
			}
		}
	}
	if (document.getElementById(imgErrorServer))
		document.getElementById(imgErrorServer).innerHTML = "";
	if (document.getElementById(imgErrorClient))
		document.getElementById(imgErrorClient).innerHTML = "";
	return true;
}

function cleanImg(imagenId, hiddenImageId, shownImageid) {
	document.getElementById(imagenId).value = '';
	document.getElementById(hiddenImageId).value = '';
	if (document.getElementById(shownImageid))
		document.getElementById(shownImageid).remove();
}
