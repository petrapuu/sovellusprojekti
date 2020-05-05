/**
 * @fileOverview JavaScript methods for feedbackanalysis summary view.
 * @module feedbackanalysissummary
 * @author Tuomas Moisio
 * @author Visa Nykänen
 */
var URI;
var arr = [];
function save() {
	let checkBoxImage = document.getElementById('saveForm:basic:1');
	let checkBoxImage2 = document
			.getElementById('saveForm:anonymityUserBoxes:1');

	let filename = document.getElementById('saveForm:input-name').value;
	if (filename === "") {
		return;
	}
	if (checkBoxImage != null) {
		if (checkBoxImage.checked) {
			for (let i = 0; i < arr.length; i++) {
				saveAsImage(arr[i]);
			}
		}
	}
	if (checkBoxImage2 != null) {
		if (checkBoxImage2.checked) {
			for (let j = 0; j < arr.length; j++) {
				saveAsImage(arr[j]);
			}
		}
	}
}
function createImage() {
	exportChart2();
	html2canvas(document.getElementById('tableImage')).then(function(canvas) {
		let array = [];
		arr = array;
		arr.push(canvas.toDataURL());
	});

	if (document.getElementById('charts:barChart_input').checked) {
		html2canvas(document.getElementById('barimages')).then(
				function(canvas) {
					arr.push(canvas.toDataURL());
				});
	}
	if (document.getElementById('charts:pieChart_input').checked) {
		html2canvas(document.getElementById('pieimages')).then(
				function(canvas) {
					arr.push(canvas.toDataURL());
				});
	}
}

/**
 * Sends an image through ajax to the servlet that handles images.
 * 
 * @param URI
 *            the base64-encoded image to be sent
 */
function sendImage(URI) {
	$.ajax({
		url : "../../webapi/summary/image",
		type : "POST",
		dataType : "text",
		contentType : "text/plain",
		cache : false,
		data : URI,
		success : function(data) {

		},
		error : function(xhr, status, error) {
			showError(msg.obs_errorCouldntSendData + ": " + error);
			this_.waiting = false;
		}
	});
}

/**
 * Sends all the images created on the summary-page to the servlet that handles
 * them
 */
function sendImages() {
	html2canvas(document.getElementById('tableImage')).then(function(canvas) {
		let URI = "analtable," + canvas.toDataURL();
		sendImage(URI)
	});
	try {
		html2canvas(document.getElementById('piechartimage')).then(
				function(canvas) {
					let URI = "analpie," + canvas.toDataURL();
					sendImage(URI)
				});
	} catch (err) {
	}

	try {
		html2canvas(document.getElementById('barchartimage')).then(
				function(canvas) {
					let URI = "analbar," + canvas.toDataURL();
					sendImage(URI)
				});
	} catch (err) {
	}
}

$(document).ready(function() {
	sendImages();
})

$(window).load(function() {
	$('td.jqplot-table-legend-label').each(function(index) {
		$(this).attr('title', $(this).text());
	});
	$(document).tooltip();
});

function saveAsImage(dataURL) {
	var filename;
	var filenameRaw;
	try {
		filenameRaw = document.getElementById('saveForm:input-name').value;
		if (filenameRaw === "") {
			return;
		}
		filename = filenameRaw.replace(/\./g, '-');
	} catch (err) {
		filename = 'summary.png';
	}
	var link = document.createElement('a');
	if (typeof link.download === 'string') {
		link.href = dataURL;
		link.download = filename;

		// Firefox requires the link to be in the body
		document.body.appendChild(link);

		// simulate click
		link.click();

		// remove the link when done
		document.body.removeChild(link);
	} else {
		window.open(URI);
	}
}

function exportChart2() {
	let count = document.getElementById('chartCount').innerHTML;
	for (let index = 0; index < count; index++) {
		let b = 'piechart' + index;
		let a = 'barchart' + index;
		let linebreak = document.createElement('br');
		if (document.getElementById('charts:pieChart_input').checked) {
			document.getElementById('pieimages').append(PF(b).exportAsImage());
		}

		if (document.getElementById('charts:barChart_input').checked) {
			document.getElementById('barimages').append(PF(a).exportAsImage());
		}
	}
}