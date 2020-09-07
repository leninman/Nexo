

	// Array of Icon features
	var iconFeatures=[];
	for (var i = 0; i < locations.length; i++) {
	  var iconFeature = new ol.Feature({
	  	type: 'click',
		desc: locations[i][0],
		url: locations[i][3],
		image: locations[i][4], 
	    geometry: new ol.geom.Point(ol.proj.transform([locations[i][2], locations[i][1]], 'EPSG:4326', 'EPSG:3857')),
	  });

	  iconFeatures.push(iconFeature);
	}

	var vectorSource = new ol.source.Vector({
		features: iconFeatures
	});

	// Custom image for marker
	var iconStyle = new ol.style.Style({
	    image: new ol.style.Icon({
	      anchor: [0.5, 0.5],
	      anchorXUnits: 'fraction',
	      anchorYUnits: 'fraction',
	      src: './img/map-pin.png',
	      scale: 0.15
		    })
	});
	  
	var vectorLayer = new ol.layer.Vector({
	  source: vectorSource,
	  style: iconStyle,
	  updateWhileAnimating: true,
	  updateWhileInteracting: true,
	});

	// Create our initial map view
	var mapCenter = ol.proj.fromLonLat([  -65.961914, 8.059230 ]);
	var view = new ol.View({
	  center: mapCenter,
	  zoom: 5
	});

	// Now create our map
	var map = new ol.Map({
	  target: 'map-canvas',
	  view: view,
	  layers: [
	    new ol.layer.Tile({
	      source: new ol.source.OSM(),
	    }),
	    vectorLayer,
	  ],
	  loadTilesWhileAnimating: true,
	});

	var popup = new ol.Overlay.Popup();
	map.addOverlay(popup);

	// Add an event handler for when someone clicks on a marker
	map.on('singleclick', function(evt) {

	    // Hide existing popup and reset it's offset
	    popup.hide();
	    popup.setOffset([0, 0]);

	    // Attempt to find a feature in one of the visible vector layers
	    var feature = map.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
	        return feature;
	    });

	    if (feature) {
	        var coord = feature.getGeometry().getCoordinates();
	        var props = feature.getProperties();
	        var info = '<a style="color:black; font-weight:600; font-size:11px" href="http://www.farmatodo.com.ve/' + props.url + '">' + 
		'<img width="500" src="' +  props.image + '"  />' + 
		'<div style="width:220px; margin-top:3px">' + props.desc + '</div></a>';

	        // Offset the popup so it points at the middle of the marker not the tip
	        popup.setOffset([0, -22]);
	        popup.show(coord, info);
	    }
	});

	// Add an event handler for when someone hovers over a marker
	// This changes the cursor to a pointer
	map.on("pointermove", function (evt) {
	    var hit = map.forEachFeatureAtPixel(evt.pixel, function(feature, layer) {
	        return true;
	    }); 
	    if (hit) {
	        this.getTargetElement().style.cursor = 'pointer';
	    } else {
	        this.getTargetElement().style.cursor = '';
	    }
	});
