/**
 * @fileOverview Javascript methods for the front page
 * @module frontpage
 * @author Visa Nykänen
 * @author Juha Moisio
 */
$(document).ready(function() {
	sendTimezone();
});

/**
 * Sends the timezone information to the server that stores it in a session.
 */
function sendTimezone(){
	$.ajax({
		url : "../../webapi/records/settimezone",
		type : "POST",
		dataType : "text",
		contentType : "application/json",
		cache : false,
		data : JSON.stringify({
			timeZoneOffsetInMs : getTimeZoneOffset(),
			daylightSavingInMs : getDaylightSaving(),
		}),
		success : function(data) {
		},
		error : function(xhr, status, error) {
		}
	});
}


/**
 * Gets the offset of the time zone in milliseconds (in JAVA format).
 */
function getTimeZoneOffset(){
    return -1 * 60 * 1000 * new Date().getTimezoneOffset();
}

/**
 * Gets the daylight saving time offset in milliseconds.
 */
function getDaylightSaving() {
    var now = new Date();
    var jan = new Date(now.getFullYear(), 0, 1);
    return (jan.getTimezoneOffset() - now.getTimezoneOffset()) * 60 * 1000;
}
