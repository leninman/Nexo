/*var countDownSeconds = 120;
var intentos = 2;

var counter = setInterval(function() {  
  countDownSeconds = countDownSeconds - 1;
  document.getElementById("counter").innerHTML = countDownSeconds;
  if (countDownSeconds < 0) {
	location.href = 'logout';
  }
}, 1000);

function validateForm() {
  var otp = document.getElementById("otp").value.trim();
  var generatedOtp = document.getElementById("generatedOtp").value;
  if (otp === "") {
	document.getElementById("otpError").innerHTML = "Introduzca el c&oacute;digo enviado a su correo electr&oacute;nico.";
    return false;
  }
  if (otp !== generatedOtp) {
	if (intentos !== 0) {
		document.getElementById("otpError").innerHTML = "El c&oacute;digo no coincide con el que fue enviado a su correo electr&oacute;nico. Le restan " + intentos + " intentos.";
		intentos--;
	} else {
		location.href = 'logout';
	}    
	return false;
  }
  document.getElementById('otp_form').submit()}*/

/*
$("#otp").keyup(function () {
	var otp = $("#otp").val();	
	if (otp.length > 0) {
			$("#idSolicitud").prop("disabled", false );
		} else{
			$("#idSolicitud").prop("disabled", true );
		}	
		});
*/
/*
function validar(id){
	var muestra=0;
	var mensaje ='';
	
	if($( "#otp" ).val() == ""){
		$("#otp").css("border", "1px solid red");
		mensaje += '"Introduzca el c&oacute;digo enviado a su correo electr&oacute;nico.";. <br/>';
		muestra = 1;
	}else{
		$("#otp").css("border", "");
	}
		
	if(muestra==1){
		$("#modalbody").html(mensaje);
		$("#exampleModalCenter").modal('show');
		
	}else{
		var otp =  $( "#otp" ).val();	
	}

	$.ajax({
		type: "POST",
		cache: false,
		url: "validarOtpEntregaSolicitudRetiro",
		data: {"otpIntroducida": otp, "idSolicitud": id }, 
		success: function(response) {
			$('#spinnerModal').modal('hide');
			$('#solicitudesRetiroContainer').html(response);
		},
		error: function(xhr, status, error){
        // var errorMessage = xhr.status + ': ' + xhr.statusText
         alert('Error - ');
     }
	});

}




    var t = new Date();
    t.setMinutes(t.getMinutes() + 5);

    countDownDate = t.getTime();
    // Update the count down every 1 second
    var x = setInterval(function () {

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
*/