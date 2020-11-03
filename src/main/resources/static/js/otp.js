var countDownSeconds = 120;
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
  document.getElementById('otp_form').submit()}