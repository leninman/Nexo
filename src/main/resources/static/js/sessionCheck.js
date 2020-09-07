window.onload = function() {
	checkSession();
}

var sessionExpiry;

function checkSession() {
    sessionExpiry = Math.abs(getCookie('sessionExpiry'));
    var localTime = (new Date()).getTime();
    if (localTime > (sessionExpiry - 20000)) {
		$('#sessionModal').modal('show');
    } else {
	    setTimeout('checkSession()', 1000);
    }
}

function extendSession() {
	$.ajax({ 
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
    