
 // Return with commas in between
  var numberWithCommas = function(x) {
    return x.toString().replace(/\B(?=(\d{3})+(?!\d))/g, ",");
  };

var dataPack1 = [40, 47, 44, 38, 27];
var dataPack2 = [10, 12, 7, 5, 4];
var dataPack3 = [17, 11, 22, 18, 12];
var dates = ["Alta contacto", "Impuestos", "Bancos", "SAP", "Reclamaciones"];

var bar_ctx = document.getElementById('bar-chart');

var bar_chart = new Chart(bar_ctx, {
    type: 'bar',
    data: {
        labels: dates,
        datasets: [
        {
            label: 'Completados',
            data: dataPack1,
						backgroundColor: "#512DA8",
						hoverBackgroundColor: "#7E57C2",
						hoverBorderWidth: 0
        },
        {
            label: 'Excepciones',
            data: dataPack2,
						backgroundColor: "#FFA000",
						hoverBackgroundColor: "#FFCA28",
						hoverBorderWidth: 0
        },
        {
            label: 'Pendientes',
            data: dataPack3,
						backgroundColor: "#D32F2F",
						hoverBackgroundColor: "#EF5350",
						hoverBorderWidth: 0
        },
        ]
    },
    options: {
     		animation: {
        	duration: 10,
        },
        tooltips: {
					mode: 'label',
          callbacks: {
          label: function(tooltipItem, data) { 
          	return data.datasets[tooltipItem.datasetIndex].label + ": " + numberWithCommas(tooltipItem.yLabel);
          }
          }
         },
        scales: {
          xAxes: [{ 
          	stacked: true, 
            gridLines: { display: true },
            }],
          yAxes: [{ 
          	stacked: true, 
            ticks: {
        			callback: function(value) { return numberWithCommas(value); },
     				}, 
            }],
        },
        legend: {display: true}
    },
    plugins: [{
    beforeInit: function (chart) {
      chart.data.labels.forEach(function (value, index, array) {
        var a = [];
        a.push(value.slice(0, 5));
        var i = 1;
        while(value.length > (i * 5)){
        	a.push(value.slice(i * 5, (i + 1) * 5));
            i++;
        }
        array[index] = a;
      })
    }
  }]
   }
);

