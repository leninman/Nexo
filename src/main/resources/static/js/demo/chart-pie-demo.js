// Set new default font family and font color to mimic Bootstrap's default styling
Chart.defaults.global.defaultFontFamily = 'Nunito', '-apple-system,system-ui,BlinkMacSystemFont,"Segoe UI",Roboto,"Helvetica Neue",Arial,sans-serif';
Chart.defaults.global.defaultFontColor = '#858796';
















// Pie Chart Example
var ctx = document.getElementById("robots");
var myPieChart = new Chart(ctx, {
  type: 'doughnut',
  data: {
    labels: ["Piloto", "Produccion", "Offline"],
    datasets: [{
      data: [55, 30, 15],
      backgroundColor: ['#FF0F0F', '#00AEFF', '#36b9cc'],
      hoverBackgroundColor: ['#FF0F0F', '#00AEFF', '#0FFF6A'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: false,
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
    },
    legend: {
      display: true
    },
    cutoutPercentage: 80,
  },
});


//Pie Chart Example
var ctx = document.getElementById("operaciones");
var myPieChartOper = new Chart(ctx, {
  type: 'doughnut',
  data: {
    labels: ["En ejecución", "OK", "KO"],
    datasets: [{
      data: [55, 30, 15],
      backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc'],
      hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: false,
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
    },
    legend: {
      display: true
    },
    cutoutPercentage: 80,
  },
});





//Pie Chart Example
var ctx = document.getElementById("procesos");
var myPieChartPro = new Chart(ctx, {
	  type: 'doughnut',
	  data: {
	    labels: ["Forzado", "Pago", "Impuesto", "Banco"],
	    datasets: [{
	      data: [55, 30, 15, 33],
	      backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc', '#FF0F0F'],
	      hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf', '#FF0F0F'],
	      hoverBorderColor: "rgba(234, 236, 244, 1)",
	    }],
	  },
	  options: {
	    maintainAspectRatio: false,
	    tooltips: {
	      backgroundColor: "rgb(255,255,255)",
	      bodyFontColor: "#858796",
	      borderColor: '#dddfeb',
	      borderWidth: 1,
	      xPadding: 15,
	      yPadding: 15,
	      displayColors: false,
	      caretPadding: 10,
	    },
	    legend: {
	      display: true
	    },
	    cutoutPercentage: 80,
	  },
	});




//Pie Chart Example
var ctx = document.getElementById("Schedulers");
var myPieChartSch = new Chart(ctx, {
  type: 'doughnut',
  data: {
    labels: ["Ejecución", "OK", "KO"],
    datasets: [{
      data: [55, 30, 15],
      backgroundColor: ['#4e73df', '#1cc88a', '#36b9cc'],
      hoverBackgroundColor: ['#2e59d9', '#17a673', '#2c9faf'],
      hoverBorderColor: "rgba(234, 236, 244, 1)",
    }],
  },
  options: {
    maintainAspectRatio: false,
    tooltips: {
      backgroundColor: "rgb(255,255,255)",
      bodyFontColor: "#858796",
      borderColor: '#dddfeb',
      borderWidth: 1,
      xPadding: 15,
      yPadding: 15,
      displayColors: false,
      caretPadding: 10,
    },
    legend: {
      display: true
    },
    cutoutPercentage: 80,
  },
});




