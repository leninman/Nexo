var speedCanvas = document.getElementById("speedChart");

Chart.defaults.global.defaultFontFamily = "Lato";
Chart.defaults.global.defaultFontSize = 18;

var trendData = {
  labels: ["08:00am", "09:00am", "10:00am", "11:00am", "12:00m", "01:00pm", "02:00pm"],
  datasets: [{
    label: "Tendencias de uso",
    data: [0, 2, 5, 5, 1, 0, 3],
    lineTension: 0,
    fill: false,
    borderColor: 'orange',
    backgroundColor: 'transparent',
    borderDash: [5, 5],
    pointBorderColor: 'orange',
    pointBackgroundColor: 'rgba(255,150,0,0.5)',
    pointRadius: 5,
    pointHoverRadius: 10,
    pointHitRadius: 30,
    pointBorderWidth: 2,
    pointStyle: 'circle'
  }]
};

var chartOptions = {
  legend: {
    display: true,
    position: 'right',
    labels: {
      boxWidth: 80,
      fontColor: 'black'
    }
  }
};

var lineChart = new Chart(speedCanvas, {
  type: 'line',
  data: trendData,
  options: chartOptions
});


//Pie Chart Example
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