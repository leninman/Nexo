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

function getAgenciaMunicipios(event) {
	var idEstado = event.target.value;

	$.getJSON("municipios/" + idEstado, function(json) {
		$('#idMunicipioAgencia').empty();
		$('#idMunicipioAgencia').append($('<option>').text("Seleccionar").attr('value', -1));
		$.each(json, function(i, obj) {
			$('#idMunicipioAgencia').append($('<option>').text(obj.nombre).attr('value', obj.id));
		});
	});
}

function getRecords(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#agenciaContainer').html(response);
		}
	})
}


function editarAgencia(id) {
	getRecords("agenciaEditar/" + id);
}




function processAgenciaForm(accion, form) {
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: new FormData(form),
		processData: false,
		contentType: false,
		success: function(response) {
			$('#agenciaContainer').html(response);
		}
	});
}

function processAgenciaForm(accion, form) {
	var form = $(form);
	$.ajax({
		type: "POST",
		cache: false,
		url: accion,
		data: form.serialize(),
		success: function(response) {
			$('#agenciaContainer').html(response);
		}
	});
}

function getAgenciaForm(url) {
	$.ajax({
		url: url,
		cache: false,
		async: true,
		success: function(response) {
			$('#agenciaContainer').html(response);
		}
	})
}

function crearAgencia(id) {
	getAgenciaForm("agenciaCrear/" + id);
}

function editarAgencia(id) {
	getAgenciaForm("agenciaEditar/" + id);
}

function agencias(id) {
	$('#agenciaModal').modal('show');
	getAgenciaForm("agenciaHome/" + id);
}

function soloNumeros(input) {
	input.value = input.value.replace(/[^0-9.]/g, '');
}

   
   document.querySelector("#buscar").onkeyup = function(){
        $TableFilter("#tabla", this.value);
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
    }