window.onload = function() {
	initSession();
}


var timeOfLastActivity;
var timeForCloseSession;
var timeBeforeWarning = 1000;   // 5 seconds
var timeBeforeClose = 180000;    // 10 seconds
var timeBeforeValidation = 20000;

function initSession() {
	timeOfLastActivity = new Date().getTime();
	timeForCloseSession = timeOfLastActivity + timeBeforeClose;
    checkSession();
}

function checkSession() {
	//timeOfLastActivity = new Date().getTime();
	//timeForCloseSession = timeOfLastActivity + timeBeforeClose;
 
    var currentTime = new Date().getTime();
    if (currentTime < timeForCloseSession) {
    	
    	if (currentTime > (timeForCloseSession - timeBeforeValidation)) {
       		$('#spinnerModal').modal('hide');
			$('#sessionModal').modal('show');
		}
    } else {
        location.href = 'logout';
    }
    setTimeout('checkSession()', timeBeforeWarning);
    
}

function extendSession() {
    jQuery.ajax({ 
		url: 'activateSession',
		type: 'POST',
		success: function() {
    	
    	var currentTime = new Date().getTime();
    	if (currentTime > timeForCloseSession) {
			window.location.href = 'logout';
			return;
		} else 
			initSession();
		}				
	});
}


/*
window.onload = function() {
	checkSession();
}

var sessionExpiry;
var timeElapsed = false;

function checkSession() {
    sessionExpiry = Math.abs(getCookie('sessionExpiry'));
    var localTime = (new Date()).getTime();
    if (localTime > (sessionExpiry - 20000) && localTime < sessionExpiry) {
		$('#spinnerModal').modal('hide');
		$('#sessionModal').modal('show');
    } 
	if (localTime > sessionExpiry) {
		location.href = 'logout';
	}
	setTimeout('checkSession()', 1000);    
}

function extendSession() {
	jQuery.ajax({ 
		url: 'activateSession',
		type: 'POST',
		success: function() {
    	var localTime = (new Date()).getTime();
		if (localTime > sessionExpiry){
			window.location.href = 'logout';
			return;
		} else 
			checkSession();
		}				
	});
}

function getCookie(name) {
	const nameEQ = `${name}=`;
	const cookies = document.cookie.split(';');
	let value = null;
	cookies.some((cookie) => {
		cookie = cookie.trim();
		const matchesCookie = cookie.indexOf(nameEQ) === 0;
		if (matchesCookie) {
			value = cookie.substring(nameEQ.length, cookie.length);
		}
		return matchesCookie;
	});
	return value;
}
*/    