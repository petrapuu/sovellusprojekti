/*
 * Copyright (c) 2016, Jarmo Juujärvi, Sami Kallio, Kai Korhonen, Juha Moisio, Ilari Paananen
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 *     1. Redistributions of source code must retain the above copyright
 *       notice, this list of conditions and the following disclaimer.
 *
 *     2. Redistributions in binary form must reproduce the above copyright
 *       notice, this list of conditions and the following disclaimer in the
 *       documentation and/or other materials provided with the distribution.
 *
 *     3. Neither the name of the copyright holder nor the names of its
 *       contributors may be used to endorse or promote products derived
 *       from this software without specific prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS" AND
 * ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE IMPLIED
 * WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE ARE
 * DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE LIABLE FOR
 * ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR CONSEQUENTIAL DAMAGES
 * (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF SUBSTITUTE GOODS OR SERVICES;
 * LOSS OF USE, DATA, OR PROFITS; OR BUSINESS INTERRUPTION) HOWEVER CAUSED AND
 * ON ANY THEORY OF LIABILITY, WHETHER IN CONTRACT, STRICT LIABILITY, OR TORT
 * (INCLUDING NEGLIGENCE OR OTHERWISE) ARISING IN ANY WAY OUT OF THE USE OF THIS
 * SOFTWARE, EVEN IF ADVISED OF THE POSSIBILITY OF SUCH DAMAGE.
 */

/* global PF, links, SummaryIndex */

/**
 * @fileOverview Javascript methods for the summary page.
 * @module summary
 * @author Juha Moisio <juha.pa.moisio at student.jyu.fi>
 */
var TIMELINE_BEGIN = getLocalZeroDate();
var OBSERVATION_DURATION = SummaryIndex.getObservationDuration(); // function
// in
// summary/index.xhtml
var msg = SummaryIndex.getMessages(); // function in summary/index.xhtml
var ESCAPE_KEY = 27;
var URI;

/**
 * On document ready: - Calculate recordings summary details. - Update the
 * details on time frame change. - Add zoom button click events for timeline
 * zooming. - Show growl message on timeline event selection.
 */
$(function() {
	var timeline = PF("timelineWdgt").getInstance();
	var growl = PF("growlWdgt");
	var startTimeWdgt = PF("startTimeWdgt");
	var endTimeWdgt = PF("endTimeWdgt");
	var timeframe = timeline.getVisibleChartRange();
	var startTimePicker = $("#startTime_input");
	var endTimePicker = $("#endTime_input");
	
	timeline.options.showCurrentTime = false; // NOTE: setting this did not
	// work from Summary Bean.

	updateRecordsTable(timeline, timeframe);

	// Set time select listeners and restore original dates that get reseted on
	// event bind.
	var startDate = startTimeWdgt.getDate();
	var endDate = endTimeWdgt.getDate();
	startTimePicker.timepicker("option", "onSelect", function(startTime) {
		var error = updateTimelineTimeframe(timeline, startTime, endTimePicker
				.val());
		startTimePicker.toggleClass("ui-state-error", error);
		if (error && convertStrToMs(startTime) > OBSERVATION_DURATION) {
			startTimeWdgt.setDate(endDate);
		}
	});
	startTimePicker
			.keyup(function() {
				var error = updateTimelineTimeframe(timeline, startTimePicker
						.val(), endTimePicker.val());
				$(this).toggleClass("ui-state-error", error);
				if (error
						&& convertStrToMs(startTimePicker.val()) > OBSERVATION_DURATION) {
					startTimeWdgt.setDate(endDate);
				}
			});
	endTimePicker.timepicker("option", "onSelect", function(endTime) {
		var error = updateTimelineTimeframe(timeline, startTimePicker.val(),
				endTime);
		endTimePicker.toggleClass("ui-state-error", error);
		if (error && convertStrToMs(endTime) > OBSERVATION_DURATION) {
			endTimeWdgt.setDate(endDate);
		}
	});
	endTimePicker
			.keyup(function() {
				var error = updateTimelineTimeframe(timeline, startTimePicker
						.val(), endTimePicker.val());
				$(this).toggleClass("ui-state-error", error);
				if (error
						&& convertStrToMs(endTimePicker.val()) > OBSERVATION_DURATION) {
					endTimeWdgt.setDate(endDate);
				}
			});
	startTimeWdgt.setDate(startDate);
	endTimeWdgt.setDate(endDate);

	links.events.addListener(timeline, "select", function() {
		showRecordDetails(timeline, growl);
	});

	$(document).click(function(e) {
		if (!$(e.target).hasClass("timeline-event-content")) {
			hideMessages(timeline, growl);
		}
	});

	$(document).keyup(function(e) {
		if (e.keyCode === ESCAPE_KEY) {
			hideMessages(timeline, growl);
		}
	});
	
	saveImage();

	});

/**
 * Updates the records table information according to the given time frame.
 * 
 * @param {object}
 *            timeline - The timeline component.
 * @param {object}
 *            timeframe - The selected start and end time.
 */
function updateRecordsTable(timeline, timeframe) {
	var recordsTable = $("#records");
	var categories = timeline.getItemsByGroup(timeline.items);
	var timeframeDuration = getTimeframeDuration(timeframe);
	var recordsTotalCount = getRecordsInTimeframe(timeline.items, timeframe).length;
	recordsTable.empty();

	var oldCategorySet;
	$.each(categories, function(category, categoryRecords) {
		var records = getRecordsInTimeframe(categoryRecords, timeframe);
		var duration = getDurationOfRecords(records, timeframe);
		var newCategorySet = category
				.match("<span class=categorySet>(.*)</span>")[1];
		var recordRow = createRecordRow({
			name : category,
			count : records.length,
			duration : duration,
			addGap : oldCategorySet !== newCategorySet,
			countPercent : spanPercentOf(records.length, recordsTotalCount),
			durationPercent : spanPercentOf(duration, timeframeDuration)
		});
		recordsTable.append(recordRow);
		oldCategorySet = newCategorySet;
	});
	var summaryRow = createRecordRow({
		name : msg.sum_total,
		count : recordsTotalCount,
		duration : timeframeDuration,
		countPercent : "           ",
		durationPercent : "           "
	});
	summaryRow.addClass("summary-row");
	recordsTable.append(summaryRow);
}

/**
 * Creates a HTML element containing the data of a record.
 * 
 * @param {object}
 *            record - The object contains record data in the form: {name,
 *            count, countPercentage, duration, durationPercentage}
 * @returns {object} - The jquery object containing the record row element.
 */
function createRecordRow(record, colcount) {
	// TODO: escape XSS; Is it required? Values are from backing bean and are
	// already escaped and user cannot change them later.
	var row = $('<div class="ui-grid-row">');
	var count = $('<div class="ui-grid-col-3">');
	var duration = $('<div class="ui-grid-col-3">');
	count.append('<span>' + record.count + "</span>");
	count.append('<span>' + record.countPercent + "</span>");
	duration.append('<span>' + convertMsToUnits(record.duration) + "</span>");
	duration.append('<span>' + record.durationPercent + "</span>");
	row.append('<div class="ui-grid-col-5">' + record.name + "</div>");
	row.append(count);
	row.append(duration);
	if (record.addGap) {
		row.addClass("gapBefore");
	}
	return row;
}

/**
 * Updates the time frame of the timeline to the given start and end times.
 * 
 * @param {object}
 *            timeline - The timeline component.
 * @param {string}
 *            strStart - The time frame starting time in hh:mm:ss format.
 * @param {string}
 *            strEnd - The time frame ending time in hh:mm:ss format.
 * @returns {boolean} - returns true on errors, false if updated successfully.
 */
function updateTimelineTimeframe(timeline, strStart, strEnd) {
	var msStart = convertStrToMs(strStart);
	var msEnd = convertStrToMs(strEnd);

	// check the validity of time frame
	if (msStart >= msEnd || msStart > OBSERVATION_DURATION
			|| msEnd > OBSERVATION_DURATION) {
		return true;
	}

	if (msStart) {
		timeline.options.min = new Date(TIMELINE_BEGIN.getTime() + msStart);
	} else {
		timeline.options.min = TIMELINE_BEGIN;
	}

	if (msEnd) {
		timeline.options.max = new Date(TIMELINE_BEGIN.getTime() + msEnd);
	} else {
		timeline.options.max = new Date(TIMELINE_BEGIN.getTime()
				+ OBSERVATION_DURATION * 1.1);
	}

	timeline.setVisibleChartRangeAuto();
	updateRecordsTable(timeline, timeline.getVisibleChartRange());
	return false;
}

/**
 * Shows a PrimeFaces growl message with details of the selected record.
 * 
 * @param {object}
 *            timeline - The timeline component.
 * @param {object}
 *            growl - The growl component.
 */
function showRecordDetails(timeline, growl) {
	var selection = timeline.getSelection();
	if (selection.length) {
		if (selection[0].row !== undefined) {
			var record = timeline.getItem(selection[0].row);
			growl.removeAll();
			growl.renderMessage({
				summary : record.group,
				detail : getRecordDetails(record),
				severity : "info"
			});
		}
	}
}

/**
 * Hides all growl messages and removes timeline selection.
 * 
 * @param {object}
 *            timeline - The timeline component.
 * @param {object}
 *            growl - The growl component.
 */
function hideMessages(timeline, growl) {
	growl.removeAll();
	timeline.setSelection(null);
}

/**
 * Gets all the records that are fully or partially in the given time frame.
 * 
 * @param {object}
 *            records - The object containing the records.
 * @param {object}
 *            timeframe - The selected start and end time.
 * @returns {object} - returns a list of matched records.
 */
function getRecordsInTimeframe(records, timeframe) {
	var recordsIn = [];
	$.each(records,
			function(i, record) {
				if (record.className === "dummyRecord") {
					return true;
				} else if (record.start >= timeframe.start
						&& record.start < timeframe.end) {
					recordsIn.push(record);
				} else if (record.end <= timeframe.end
						&& record.end > timeframe.start) {
					recordsIn.push(record);
				} else if (record.start < timeframe.start
						&& record.end > timeframe.end) {
					recordsIn.push(record);
				}
			});
	return recordsIn;
}

/**
 * Gets the record details as a string.
 * 
 * @param {object}
 *            record - The record object from the timeline component.
 * @returns {string} - The details as a string value.
 */
function getRecordDetails(record) {
	var details = "";
	var start = toTimelineTime(record.start);
	var end = toTimelineTime(record.end);
	details += msg.sum_begin + ": " + convertMsToStr(start);
	details += "<br/>";
	details += msg.sum_end + ": " + convertMsToStr(end);
	details += "<br/>";
	details += msg.sum_duration + ": " + convertMsToUnits(end - start);
	return details;
}

/**
 * Get total duration of records of all categories in given time frame.
 * 
 * @param {object}
 *            records - object containing the records.
 * @param {object}
 *            timeframe - The selected start and end time.
 * @returns {number} - duration of the records.
 */
function getDurationOfCategories(categories, timeframe) {
	var duration = 0;
	$.each(categories, function(category, records) {
		duration += getDurationOfRecords(records, timeframe);
	});
	return duration;
}

/**
 * Gets the duration of the given time frame.
 * 
 * @param {object}
 *            timeframe - The selected start and end time.
 * @returns {number} - duration of the observation's time frame.
 */
function getTimeframeDuration(timeframe) {
	var rStartMs = toTimelineTime(timeframe.start);
	var rEndMs = toTimelineTime(timeframe.end);
	var start = (rStartMs > 0) ? rStartMs : 0;
	var end = (rEndMs < OBSERVATION_DURATION) ? rEndMs : OBSERVATION_DURATION;
	return end - start;
}

/**
 * Gets the total duration of the records in the given time frame.
 * 
 * @param {object}
 *            records - The object containing the records.
 * @returns {number} - The duration of the records.
 */
function getDurationOfRecords(records, timeframe) {
	var duration = 0;
	$.each(records, function() {
		var start = this.start;
		var end = this.end;
		if (this.className === "dummyRecord") {
			return true;
		}
		if (start < timeframe.start) {
			start = timeframe.start;
		}
		if (end > timeframe.end) {
			end = timeframe.end;
		}
		if (end > start) {
			duration += end - start;
		}
	});
	return duration;
}

/**
 * Converts the time in milliseconds to a string hh:mm:ss.
 * 
 * @param {number}
 *            ms - The time in milliseconds.
 * @returns {string} - The time in string as hh:mm:ss.
 */
function convertMsToStr(ms) {
	var d = ms;
	d = Math.floor(d / 1000);
	var s = d % 60;
	d = Math.floor(d / 60);
	var m = d % 60;
	d = Math.floor(d / 60);
	var h = d % 60;
	return [ h, m, s ].map(leadingZero).join(':');
}

/**
 * Converts the time string in the form hh:mm:ss to milliseconds.
 * 
 * @param {string}
 *            str - The time in a string as hh:mm:ss.
 * @returns {number} - The time in milliseconds or NaN for unparseable time
 *          string.
 */
function convertStrToMs(str) {
	var time = str.split(/:/);
	// insert missing values
	for (var i = 3 - time.length; i > 0; i--) {
		time.unshift("0");
	}
	var seconds = 0;
	for (var i = 0; i < time.length; i++) {
		seconds += parseInt(time[i], 10) * Math.pow(60, 2 - i);
	}
	return seconds * 1000;
}

/**
 * Converts the time in milliseconds to a string with the time units e.g. 1h 2m
 * 0s.
 * 
 * @param {number}
 *            ms - The time in milliseconds.
 * @returns {string} - The time in string with units e.g. 1h 2m 0s.
 */
function convertMsToUnits(ms) {
	var time = convertMsToStr(ms).split(":");
	var units = "";
	var getTimeUnit = function(i, unit) {
		var n = parseInt(time[i], 10);
		if (n > 0) {
			units += n + unit;
		}
	};

	if (ms <= 0) {
		return "0 s";
	}
	if (ms < 1000) {
		return "~1 s";
	}
	if (time.length === 3) {
		getTimeUnit(0, " h");
		getTimeUnit(1, " m");
		getTimeUnit(2, " s");
	} else {
		return "0 s";
	}
	return units.replace(/([hms])(\d)/g, "$1 $2");
}

/**
 * Returns the given number as a string and appends a leading zero to it if the
 * number is a single digit number.
 * 
 * @param {number}
 *            n - The given number.
 * @returns {string} - number with possible leading zero.
 */
function leadingZero(n) {
	return (n < 10 ? "0" + n : n.toString());
}

/**
 * Calculates the percentage of two values.
 * 
 * @param {number}
 *            a - The number of share.
 * @param {number}
 *            b - The number of total quantity.
 * @returns {number} - percentage ratio.
 */
function percentOf(a, b) {
	if (a === 0 || b === 0) {
		return 0;
	}
	return Math.round((a / b) * 100);
}

/**
 * Gets the percentage of two values as a span element string.
 * 
 * @param {number}
 *            a - The number of share.
 * @param {number}
 *            b - The number of total quantity.
 * @returns {string} - percent as span element string.
 */
function spanPercentOf(a, b) {
	var percent = percentOf(a, b);
	var str = " (" + percent.toString() + "%)";
	if (percent < 10) {
		str = "  " + str;
	}
	if (percent < 100) {
		str = "  " + str;
	}
	return '<span class="percent">' + str + "</span>";
}

/**
 * Gets the "zero" date with the time zone offset.
 * 
 * @returns {date} - The zero date with the time zone offset.
 */
function getLocalZeroDate() {
	var localDate = new Date(0);
	var zeroDate = new Date(localDate.getTimezoneOffset() * 60 * 1000);
	return zeroDate;
}

/**
 * Converts the date object to the timeline component time.
 * 
 * @param {date}
 *            date - The date object of the time to be converted.
 * @returns {number} - The converted time in milliseconds.
 */
function toTimelineTime(date) {
	return Math.abs(TIMELINE_BEGIN.getTime() - date.getTime());
}

/**
 * Encodes HTML markup characters to HTML entities.
 * 
 * @param {string}
 *            str - The string to be encoded.
 * @returns {str} - The encoded string.
 */
function encodeHTML(str) {
	return str.replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g,
			'&gt;').replace(/"/g, '&quot;');
}

/**
 * Checks if the user has scrolled to the bottom of the page.
 * 
 * @param {number}
 *            padding - An extra padding to be checked.
 * @return {boolean} - true if at bottom otherwise false.
 */
function isBottomOfDocument(padding) {
	return $(window).scrollTop() >= $(document).height() - padding
			- $(window).height();
}

/**
 * Checks what checkboxes user has selected and performs action if specific
 * checkbox is selected
 */
function checkCheckBoxes() {
	var checkBox1 = document.getElementById('saveForm:basic:1');
	var checkBox2 = document.getElementById('saveForm:anonymityUserBoxes:1');
	if (checkBox2 != null) {
		if (checkBox2.checked) {
			saveAsImage();
		}
	}
	if (checkBox1 != null) {
		if (checkBox1.checked) {
			saveAsImage();
		}
	}

}

/**
 * Creates canvas element where the timeline and datatable are inserted
 * sends the created image through ajax to Java 
 */
function saveImage() {
	html2canvas(document.getElementById('recordingsPhoto')).then(
			function(canvas) {
				let URI = canvas.toDataURL();
				sendImage(URI);
			});
}

/**
 * Sends the given string to the servlet that expects a base64-encoded png
 * @param URI The base64-encoded png-file
 */
function sendImage(URI) {
	$.ajax({
		url : "../../webapi/summary/image",
		type : "POST",
		dataType : "text",
		contentType : "text/plain",
		cache : false,
		data : "obsimg,"+URI,
		success : function(data) {

		},
		error : function(xhr, status, error) {
			showError(msg.obs_errorCouldntSendData + ": " + error);
			this_.waiting = false;
		}
	});
}

/**
 * Saves the canvas as png.
 */
function saveAsImage() {
	var filename;
	var filenameRaw;
	try {
		filenameRaw = document.getElementById('saveForm:input-name').value;
		if (filenameRaw == "") {
			return;
		}
		filename = filenameRaw.replace(/\./g, '-');
	} catch (err) {
		filename = 'summary.png';
	}
	var link = document.createElement('a');
	if (typeof link.download === 'string') {
		link.href = URI;
		link.download = filename;

		// Firefox requires the link to be in the body
		document.body.appendChild(link);

		// simulate click
		link.click();
		
		document.body.removeChild(link);
	} else {
		window.open(URI);
	}
	document.body.removeChild(link);
}

