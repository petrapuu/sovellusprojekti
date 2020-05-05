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
package com.moveatis.restful;

import java.io.Serializable;
import java.io.StringReader;
import java.text.DateFormat;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.TimeZone;

import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.inject.Named;
import javax.json.Json;
import javax.json.JsonArray;
import javax.json.JsonNumber;
import javax.json.JsonObject;
import javax.json.JsonReader;
import javax.servlet.http.HttpServletRequest;
import javax.ws.rs.Consumes;
import javax.ws.rs.POST;
import javax.ws.rs.Path;
import javax.ws.rs.Produces;
import javax.ws.rs.core.Context;
import javax.ws.rs.core.MediaType;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.interfaces.Category;
import com.moveatis.interfaces.Label;
import com.moveatis.interfaces.Observation;
import com.moveatis.interfaces.Record;
import com.moveatis.interfaces.Session;
import com.moveatis.managedbeans.ObservationManagedBean;
import com.moveatis.managedbeans.UserManagedBean;
import com.moveatis.observation.ObservationCategory;
import com.moveatis.observation.ObservationCategorySet;
import com.moveatis.records.RecordEntity;
import com.moveatis.timezone.TimeZoneInformation;

/**
 * The bean manages REST API for adding records to an observation as well as
 * starting and stopping an observation.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Path("/records")
@Named(value = "recordBean")
@Stateful
public class RecordListenerBean implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(RecordListenerBean.class);
	private static final long serialVersionUID = 1L;

	private JsonReader jsonReader;

	@Context
	private HttpServletRequest httpRequest;

	@Inject
	private Session sessionBean;
	@Inject
	private ObservationManagedBean observationManagedBean;
	@Inject
	private UserManagedBean userManagedBean;

	private ResourceBundle messages;

	@Inject
	private Observation observationEJB;
	@Inject
	private Record recordEJB;
	@Inject
	private Category categoryEJB;
	@Inject
	private Label labelEJB;

	public RecordListenerBean() {

	}

	/**
	 * Marks the observation as started.
	 */
	@POST
	@Path("startobservation")
	@Consumes(MediaType.TEXT_PLAIN)
	@Produces(MediaType.TEXT_PLAIN)
	public String startObservation(String data) {
		observationManagedBean.startObservation();
		return "success";
	}

	/**
	 * Keeps the session alive during the observation.
	 */
	/*
	 * TODO: Needs work - what to do when keep-alive request is commenced?
	 */
	// NOTE: Used by observer view.
	// TODO: What about other views? What happens if session expires? Redirect to
	// front page?
	@POST
	@Path("keepalive")
	@Produces(MediaType.TEXT_PLAIN)
	public String keepAlive() {
		return "keep-alive";
	}

	@POST
	@Path("settimezone")
	@Consumes(MediaType.APPLICATION_JSON)
	public void setTimezone(String data) {
		StringReader stringReader = new StringReader(data);

		jsonReader = Json.createReader(stringReader);

		JsonObject jObject = jsonReader.readObject();
		JsonNumber timeZoneOffset = jObject.getJsonNumber("timeZoneOffsetInMs");
		JsonNumber DSTOffset = jObject.getJsonNumber("daylightSavingInMs");

		jsonReader.close();

		TimeZone timeZone = TimeZoneInformation.getTimeZoneFromOffset(timeZoneOffset.intValue(), DSTOffset.intValue());

		sessionBean.setSessionTimeZone(timeZone);

	}

	/**
	 * Adds the observation data coming from the observation view. It could be used
	 * to take data from other clients, but not yet implemented or even planned.
	 * 
	 * @param data
	 *            The JSON data containing the records of the observation.
	 * @return "success" if the action succeeded and "failed" if it failed.
	 */
	@POST
	@Path("addobservationdata")
	@Consumes(MediaType.APPLICATION_JSON)
	@Produces(MediaType.TEXT_PLAIN)
	public String addObservationData(String data) {
		Locale locale = userManagedBean.getLocale();
		messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
		StringReader stringReader = new StringReader(data);

		jsonReader = Json.createReader(stringReader);

		JsonObject jObject = jsonReader.readObject();
		JsonNumber duration = jObject.getJsonNumber("duration");
		JsonNumber timeZoneOffset = jObject.getJsonNumber("timeZoneOffsetInMs");
		JsonNumber DSTOffset = jObject.getJsonNumber("daylightSavingInMs");
		JsonArray array = jObject.getJsonArray("data");

		jsonReader.close();

		Date createdTime = observationManagedBean.getObservationEntity().getCreated();

		TimeZone timeZone = TimeZoneInformation.getTimeZoneFromOffset(timeZoneOffset.intValue(), DSTOffset.intValue());

		sessionBean.setSessionTimeZone(timeZone);

		DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
		dateFormat.setTimeZone(timeZone);

		observationManagedBean
				.setObservationName(messages.getString("obs_title") + " - " + dateFormat.format(createdTime));
		observationManagedBean.setObservationDuration(duration.longValue());

		Map<Long, ObservationCategory> categoriesById = new HashMap<>();

		for (ObservationCategorySet categorySet : observationManagedBean.getCategorySetsInUse()) {
			for (ObservationCategory category : categorySet.getCategories()) {
				categoriesById.put(category.getTag(), category);
			}
		}

		try {
			for (int i = 0; i < array.size(); i++) {
				JsonObject object = array.getJsonObject(i);
				RecordEntity record = new RecordEntity();

				Long id = object.getJsonNumber("id").longValue();
				ObservationCategory category = categoriesById.get(id);

				record.setObservationCategory(category);

				record.setStartTime(object.getJsonNumber("startTime").longValue());
				record.setEndTime(object.getJsonNumber("endTime").longValue());

				observationManagedBean.addRecord(record);
			}
		} catch (Exception e) {
			LOGGER.error("Parsing JSON to records failed", e);
			return "failed";
		}

		observationManagedBean.saveObservation();

		return "success";
	}
}
