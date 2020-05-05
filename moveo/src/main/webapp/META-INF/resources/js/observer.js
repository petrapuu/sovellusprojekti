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

/**
 * @fileOverview JavaScript logic for observation view.
 * @module observer
 * @author Ilari Paananen <ilari.k.paananen at student.jyu.fi>
 */

//
// TODO:
// - Remove/comment out console.log calls in release?
//

// NOTE: Functions in observer/index.xhtml.
var CategoryType = getCategoryTypes();
var msg = getMessages();


/**
 * The observation clock that can be paused and resumed.
 * The individual categories get their time from the observation clock.
 * @constructor
 */
function Clock() {
    this.total_time = 0;
    this.resume_time = 0;
    this.running = false;
    
    /**
     * Resumes the observation clock.
     * @param {number} now The time in milliseconds when the observation clock was resumed.
     */
    this.resume = function(now) {
        if (!this.running) {
            this.resume_time = now;
            this.running = true;
        } else {
            console.log("Clock.resume(): Clock is already running!");
        }
    };
    
    /**
     * Pauses the observation clock.
     * @param {number} now The time in milliseconds when the observation clock was paused.
     */
    this.pause = function(now) {
        if (this.running) {
            var delta_time = now - this.resume_time;
            this.total_time += delta_time;
            this.running = false;
        } else {
            console.log("Clock.pause(): Clock is already paused!");
        }
    };
    
    /**
     * Returns the total time the observation clock has been running in milliseconds.
     * @param {number} now The time in milliseconds when the elapsed time was wanted.
     */
    this.getElapsedTime = function(now) {
        if (this.running) {
            return this.total_time + (now - this.resume_time);
        } else {
            return this.total_time;
        }
    };
    
    /**
     * Returns true if the observation clock is paused, otherwise false.
     */
    this.isPaused = function() {
        return !this.running;
    };
}


/**
 * Converts milliseconds to a string representing time.
 * The time format is hh:mm:ss if the given time is at
 * least one hour and mm:ss otherwise.
 * @param {number} ms The time in milliseconds.
 */
function timeToString(ms) {
    var t = Math.floor(ms / 1000);
    var s = t % 60;
    var m = Math.floor(t / 60) % 60;
    var h = Math.floor(t / 60 / 60) % 60;
    var str = (m < 10 ? "0" + m : m) + ":" + (s < 10 ? "0" + s : s);
    if (h > 0) {
        str = (h < 10 ? "0" + h : h) + ":" + str;
    }
    return str;
}


/**
 * Returns the given count as a string with abbreviation, e.g. "13 ct.".
 * @param {number} count The count to make the string from.
 */
function countToString(count) {
    return count + " " + msg.countAbbreviation;
}


/**
 * The class acts as a category button. It creates the HTML
 * elements it needs and responds to the click events it gets.
 * @constructor
 * @param {String} name The name to be displayed on the button.
 * @param {number} type The type of the category (TIMED or COUNTED).
 * @param {number} index The index of the category button.
 * @returns {CategoryItem}
 */
function CategoryItem(name, type, id, index) {
    this.li = $(document.createElement("li"));
    this.li.addClass("category-item");
    this.li.attr("id", "category-item_" + index);
    this.value_div = $(document.createElement("div"));
    this.value_div.addClass("category-value");
    this.name_div = $(document.createElement("div"));
    this.name_div.addClass("category-name");
    this.name_div.append(document.createTextNode(name));
    this.li.append(this.value_div);
    this.li.append(this.name_div);
    
    this.type = type;
    this.id = id;
    
    // Used if type is COUNTED.
    this.count = 0;
    
    // Used if type is TIMED.
    this.time = 0;
    this.start_time = 0;
    this.down = false;
    
    
    if (this.type === CategoryType.TIMED)
        initTimedCategory(this);
    else
        initCountedCategory(this);
    
    
    /**
     * The private method replaces the contents of the HTML element
     * that displays the value of the category button.
     * @param {CategoryItem} this_ The category button.
     * @param {String} text The text to replace the contents of the element with.
     */
    function updateValueDiv(this_, text) {
        this_.value_div.empty();
        this_.value_div.append(document.createTextNode(text));
    }
    
    /**
     * The private method initializes the category button to behave
     * as a time interval category.
     * @param {CategoryItem} this_ The category button.
     */
    function initTimedCategory(this_) {
        updateValueDiv(this_, timeToString(0));
        
	/*
         * Click handler for timed category item.
         */
        this_.click = function(master_time) {
            var record;

            if (this.down) {
                this.li.removeClass("down");
                if (master_time > this.start_time) {
                    this.time += master_time - this.start_time;
                    record = {id: this.id, startTime: this.start_time, endTime: master_time};
                }
                this.down = false;
            } else {
                this.li.addClass("down");
                this.start_time = master_time;
                this.down = true;
            }

            return record;
        };
    	
	/*
         * Updates category item's timer div if the category type is timed.
         */
        this_.updateTimer = function(master_time) {
            var time = this.time;
            if (this.down) {
                time += master_time - this.start_time;
            }
            updateValueDiv(this, timeToString(time));
        };
    }
    
    /**
     * The private method initializes the category button to behave
     * as a category that counts the click events it gets.
     * @param {CategoryItem} this_ The category button.
     */
    function initCountedCategory(this_) {
        updateValueDiv(this_, countToString(0));

        /*
         * Click handler for counted category item.
         */
        this_.click = function(master_time, paused) {
            if (paused) return;
            
            this.count += 1;
            updateValueDiv(this, countToString(this.count));
            
            this.li.addClass("down");
            var item = this.li;
            setTimeout(function() { item.removeClass("down"); }, 50);
            
            return {id: this.id, startTime: master_time, endTime: master_time};
        };

        /*
         * Does nothing because the category type is counted.
         */
        this_.updateTimer = function() { };
    }
}


/**
 * The class does the actual observation. It keeps the records
 * made during the observation and sends them to the backend after
 * the observation is stopped.
 * @constructor
 * @param category_sets The array of the category sets to be used in the observation.
 * @returns {Observer} Constructed observer.
 */
function Observer(category_sets) {
    this.master_clock = new Clock();
    this.categories = [];
    this.records = [];
    this.started = false;
    this.waiting = false;
    
    initialize(this);
    
    /**
     * The private method initializes the observer. It creates
     * the category buttons and adds them to the HTML element tree.
     * @param {Observer} this_ The observer to be initialized.
     */
    function initialize(this_) {
        $("#continue").hide();
        $("#pause").hide();
        $("#stop").hide();
        $("#total-time").append(document.createTextNode(timeToString(0)));
        
        var category_list = $("#category-list");
        
        var index = 0;
        
        for (var i = 0; i < category_sets.length; i++) {
            
            var set = category_sets[i];
            
            if (set.categories.length > 0) {
                var category_set = $(document.createElement("ul"));
                category_set.attr("id", set.name);
                category_set.addClass("category-set");
                
                for (var j = 0; j < set.categories.length; j++) {
                    var cat = set.categories[j];
                    var category = new CategoryItem(cat.name, cat.type, cat.id, index);
                    this_.categories.push(category);
                    category_set.append(category.li);
                    
                    index += 1;
                }
                
                category_list.append(category_set);
            }
        }
        
        $(".category-item").addClass("disabled");
    }
    
    /**
     * The private method adds the record to the records list if it's not undefined.
     * The method is used by categoryClick() and stopClick().
     * @param record The record or undefined if there is nothing to be added.
     */
    function addRecord(this_, record) {
        if (record !== undefined) {
            this_.records.push(record);
        }
    }
    
    /**
     * The event handler starts the observation. It sends an AJAX notification
     * to the backend when the observation is started.
     */
    this.startClick = function() {
        if (this.waiting) return;
        this.waiting = true;

        var this_ = this;

        $.ajax({
            url: "../../webapi/records/startobservation",
            type: "POST",
            dataType: "text",
            contentType: "text/plain",
            cache: false,
            data: "start observation",
            success: function(data) {
                this_.master_clock.resume(Date.now());
                this_.started = true;
                this_.waiting = false;
                var start_button = $("#start");
                start_button.off("click");
                start_button.hide();
                $("#pause").show();
                $("#stop-disabled").hide();
                $("#stop").show();
                $(".category-item").removeClass("disabled");
            },
            error: function(xhr, status, error) {
                showError(msg.obs_errorCouldntSendStart + " " + error);
                this_.waiting = false;
            }
        });
    };
    
    /**
     * The event handler continues the observation.
     */
    this.continueClick = function () {
        if (this.master_clock.isPaused()) {
            this.master_clock.resume(Date.now());
            $("#continue").hide();
            $("#pause").show();
        }
    };
    
    /**
     * The event handler pauses the observation.
     */
    this.pauseClick = function() {
        if (!this.master_clock.isPaused()) {
            this.master_clock.pause(Date.now());
            $("#pause").hide();
            $("#continue").show();
        }
    };
    
    /**
     * The event handler stops the observation.
     * It disables the continue, pause and category buttons.
     * If some categories were still on, it stops them
     * and creates records accordingly. It sends the recorded
     * information to the backend with AJAX and
     * redirects the user to the summary page (on success).
     */
    this.stopClick = function() {
        if (!this.started || this.waiting) return;
        this.waiting = true;
        
        var now = Date.now();
        
        if (!this.master_clock.isPaused()) {
            this.master_clock.pause(now);
        }
        
        var continue_button = $("#continue");
        var pause_button = $("#pause");
        continue_button.off("click");
        continue_button.addClass("disabled");
        pause_button.off("click");
        pause_button.addClass("disabled");
        
        var time = this.master_clock.getElapsedTime(now);
        
        for (var i = 0; i < this.categories.length; i++) {
            var category = this.categories[i];
            category.li.off("click");
            category.li.addClass("disabled");
            if (category.down) {
                addRecord(this, category.click(time));
            }
        }
        
        var this_ = this;
        
        $.ajax({
            url: "../../webapi/records/addobservationdata",
            type: "POST",
            dataType: "text",
            contentType: "application/json",
            cache: false,
            data: JSON.stringify({
                duration: time,
                timeZoneOffsetInMs: getTimeZoneOffset(),
                daylightSavingInMs: getDaylightSaving(),
                data: this.records
            }),
            success: function(data) {
                this_.waiting = false;
                // TODO: Redirect properly.
                window.location = "../summary/";
            },
            error: function(xhr, status, error) {
                showError(msg.obs_errorCouldntSendData + ": " + error);
                this_.waiting = false;
            }
        });
    };
    
    /**
     * Delegates the click of a category button to the correct category.
     * It adds the (possible) record returned by the category to the
     * list of all the records made during the observation.
     * @param {number} index The index of the category button that was clicked.
     */
    this.categoryClick = function(index) {
        var category = this.categories[index];
        var time = this.master_clock.getElapsedTime(Date.now());
        addRecord(this, category.click(time, this.master_clock.isPaused()));
    };
    
    /**
     * Updates the observation clock and all the categories based on it.
     */
    this.tick = function() {
        var time = this.master_clock.getElapsedTime(Date.now());
        
        var time_str = timeToString(time);
        var total_time = $("#total-time");
        total_time.empty();
        total_time.append(document.createTextNode(time_str));
        
        for (var i = 0; i < this.categories.length; i++) {
            this.categories[i].updateTimer(time);
        }
    };
}


/**
 * Sends an AJAX keep-alive signal to the backend.
 */
function keepAlive() {
    $.ajax({
        url: "../../webapi/records/keepalive",
        type: "POST",
        dataType: "text",
        contentType: "text/plain",
        cache: false,
        data: "keep-alive",
        success: function(data) {
            //console.log("Success: " + data);
        },
        error: function(xhr, status, error) {
            showError(msg.obs_errorKeepAliveFailed + ": " + error);
        }
    });
}


/**
 * Shows an error message in a PrimeFaces growl.
 * @param {String} error_msg The error message to be shown.
 */
function showError(error_msg) {
    var growl = PF("growlWdgt");
    growl.removeAll();
    growl.renderMessage({
        summary: msg.dialogErrorTitle,
        detail: error_msg,
        severity: "error"
    });
}


/**
 * The function will call observer.stop().
 * It is needed if the stopping of the observation has to be confirmed.
 */
var stopObservation = function () {};


/**
 * This function is ran when the document is ready.
 * Creates observer, binds event handlers, and sets two intervals:
 * one that updates the observer and one that sends keep alive to backend.
 */
$(document).ready(function() {
    var category_sets = getCategorySets(); // NOTE: Function in observer/index.xhtml.
    var observer = new Observer(category_sets);
    
    $("#start").click(function() {
        observer.startClick();
        $(".category-item").click(function() {
            var id = $(this).attr("id");
            var index = parseInt(id.split("_")[1]);
            observer.categoryClick(index);
        });
    });
    $("#continue").click(function() { observer.continueClick(); });
    $("#pause").click(function() { observer.pauseClick(); });
    stopObservation = function() { observer.stopClick(); };

    setInterval(function() { observer.tick(); }, 200);
    setInterval(keepAlive, 5*60000); // Send keep-alive every 5 minutes.
});

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
