function showLogoutModal() {
  document.getElementById('openModal').style.display = 'block';
}

function getData(){
	var tipoVista = $( "#vista" ).val();
	$("#lstBox1").empty();
	$("#lstBox2").empty();
  $.ajax({
    type: "GET",
    url: './perfiles/'+tipoVista,
    dataType: "json",
    success: function(data){
      $.each(data,function(key, registro) {
        $("#lstBox1").append('<option value='+registro.idMenu+'>'+registro.nombreOpcion+'</option>');		
      });       
    },
    error: function(data) {
      //alert('error');
	location.replace("./errorPage");
    }
  });
}