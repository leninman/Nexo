function getRecords(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#agenciaDiaContainer').html(response);
		}
	})
}

function processAgenciaDiaForm(accion, form) {
	var formData = new FormData();
	const checkBoxes = document.getElementsByName("habilitadas");
	let selectedChecks = "";
	for(let i = 0; i < checkBoxes.length; i++){
		if (checkBoxes[i].checked){
			selectedChecks = selectedChecks + "_" +checkBoxes[i].value;
			formData.append('fechasHabilitadas', checkBoxes[i].value);
		}
	
	}
	//alert("selectedChecks" + selectedChecks);

	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: formData,
		processData: false,
		contentType: false,
		success: function(response) {
			$('#agenciaDiaContainer').html(response);
		}
	});
}

function agenciaDiaAgregar(id) {
	$.ajax({
		type: "POST",
		cache: false,
		url: "agenciaDiaAgregar",
		data: { "id_agenciaDia": id },
		success: function(response) {
			$('#agenciaDiaContainer').html(response);
		},
	});
}

function getAgenciaDiaForm(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#agenciaDiaContainer').html(response);
		}
	})
}

function crearAgenciaDia(id) {
	getAgenciaDiaForm("agenciaDiaCrear/" + id);
}




