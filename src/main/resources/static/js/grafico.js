// Bar Chart Example



function generaGrafico(canvasGrafico, color, data, label){
var bgColor;

if(color == 1)
	bgColor = "rgba(101,139,65)";
else
	bgColor = "rgba(2,117,216,1)";

if(data != null){
	var monto = 0;
	for (i = 0; i < data.length; i++) {		
		if(data[i] != 0){
			if(monto < parseInt(data[i]))
				monto = parseInt(data[i]);
		}
	}
	monto += parseInt(10000);
	if(monto == 0)
		monto = 2000;
	
}

new Chart(canvasGrafico, {
		  type: 'bar',
		  data: {
		    labels: label,
		    datasets: [{
		      label: "Acumulado",
		      backgroundColor: bgColor,
		      borderColor: "rgba(2,117,216,1)",
		      data: data,
		    }],
		  },
		  options: {
		    scales: {
		      xAxes: [{
		        time: {
		          unit: 'month'
		        },
		        gridLines: {
		          display: false
		        },
		        ticks: {
		          maxTicksLimit: 12
		        }
		      }],
		      yAxes: [{
		        ticks: {
		          min: 0,
		          max: monto,
		          maxTicksLimit: 10
		        },
		        gridLines: {
		          display: true
		        }
		      }],
		    },
		    legend: {
		      display: false
		    },
		    animation: {
		    	  onComplete: function () {
		    	    var chartInstance = this.chart;
		    	    var ctx = chartInstance.ctx;
		    	    console.log(chartInstance);
		    	    var height = chartInstance.controller.boxes[0].bottom;
		    	    ctx.textAlign = "center";
		    	    Chart.helpers.each(this.data.datasets.forEach(function (dataset, i) {
		    	      var meta = chartInstance.controller.getDatasetMeta(i);
		    	      Chart.helpers.each(meta.data.forEach(function (bar, index) {
		    	        ctx.fillText(dataset.data[index], bar._model.x, height - ((height - bar._model.y) / 2));
		    	      }),this)
		    	    }),this);
		    	  }
		    	}
		  }
		});

}
