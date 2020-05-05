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
package com.moveatis.managedbeans;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.event.EventEntity;
import com.moveatis.interfaces.Observation;
import com.moveatis.interfaces.Session;
import com.moveatis.observation.ObservationCategory;
import com.moveatis.observation.ObservationCategorySet;
import com.moveatis.observation.ObservationEntity;
import com.moveatis.records.RecordEntity;

/**
 * The bean is used to manage observations in the appropriate views.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Named(value = "observationBean")
@SessionScoped
public class ObservationManagedBean implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagedBean.class);

	private static final long serialVersionUID = 1L;

	private ObservationEntity observationEntity;
	private List<ObservationCategorySet> categorySetsInUse;
	private EventEntity eventEntity;

	@EJB
	private Observation observationEJB;
	@Inject
	private Session sessionBean;

	// Tag is used to identify the observationcategories within a observation
	private Long nextTag;

	private byte[] image;

	public ObservationManagedBean() {

	}

	@PostConstruct
	public void init() {
		nextTag = 0L;
	}

	/**
	 * Removes the observations the user doesn't want to save to database when the
	 * session timeout happens and the bean is destroyed.
	 */
	@PreDestroy
	public void destroy() {
		if (observationEntity != null) {
			if (!observationEntity.getUserWantsToSaveToDatabase()) {
				observationEJB.removeUnsavedObservation(observationEntity);
			}
		}
	}

	public void resetCategorySetsInUse() {
		this.categorySetsInUse = null;
	}

	public void setEventEntity(EventEntity eventEntity) {
		this.eventEntity = eventEntity;
	}

	public EventEntity getEventEntity() {
		return this.eventEntity;
	}

	/**
	 * Creates a new observation entity and initializes it to be used in a new
	 * observation.
	 */
	public void startObservation() {
		this.observationEntity = new ObservationEntity();
		// Can we use created time for observation start time?
		this.observationEntity.setCreated();
		this.observationEntity.setEvent(eventEntity);
		// Summary view doesn't break if no records are added.
		// TODO: Should observer not let user continue, if there are no records?
		if (observationEntity.getRecords() == null) {
			observationEntity.setRecords(new ArrayList<RecordEntity>());
		}
	}

	/**
	 * Returns the current observation entity.
	 */
	public ObservationEntity getObservationEntity() {
		return observationEntity;
	}

	/**
	 * Sets the current observation entity.
	 */
	public void setObservationEntity(ObservationEntity observationEntity) {
		this.observationEntity = observationEntity;
	}

	/**
	 * Gets the observation categories to be used in the observation.
	 */
	public List<ObservationCategorySet> getCategorySetsInUse() {
		return categorySetsInUse;
	}

	/**
	 * Sets the observation categories to be used in the observation.
	 */
	public void setCategorySetsInUse(List<ObservationCategorySet> categorySetsInUse) {

		for (ObservationCategorySet observationCategorySet : categorySetsInUse) {
			for (ObservationCategory observationCategory : observationCategorySet.getCategories()) {
				if (observationCategory.getTag().equals(-1L)) {
					observationCategory.setTag(getNextTag());
				}
			}
		}
		this.categorySetsInUse = categorySetsInUse;
	}

	public Long getNextTag() {
		return nextTag++;
	}

	public void setObservationName(String name) {
		this.observationEntity.setName(name);
	}

	public void setObservationDuration(long duration) {
		this.observationEntity.setDuration(duration);
	}

	/**
	 * Adds a record to the observation.
	 * 
	 * @param record
	 *            The record to be added to the observation.
	 */
	public void addRecord(RecordEntity record) {
		List<RecordEntity> records = observationEntity.getRecords();
		record.setObservation(observationEntity);
		record.setVoiceComment(null);

		if (records == null) {
			records = new ArrayList<>();
		}

		records.add(record);
		observationEntity.setRecords(records);
	}

	/**
	 * The method is called from REST API to save the records to the observation.
	 */
	public void saveObservation() {
		if (sessionBean.isIdentifiedUser()) {
			observationEntity.setObserver(sessionBean.getLoggedIdentifiedUser());
		}
		/*
		 * NOTE: GroupKey couldn't be removed if there were observations whose observer
		 * was the TagUser corresponding to the GroupKey. Solution: don't set observer
		 * if not identified user.
		 * 
		 * else { TODO: Can we leave observer unset? Should we ensure it is null or...?
		 * observationEntity.setObserver(sessionBean.getLoggedInUser()); }
		 */

		/*
		 * Client wanted that user has the ability to persist observation into database
		 * when he/she wants to. Since it was easier to build business logic by creating
		 * the observationEntity when observation is done, the boolean flag
		 * "userWantsToSaveToDatabase was added. This flag is true if user wants to save
		 * the observation to database. If its false, we need remove this observation
		 * later.
		 */
		observationEntity.setUserWantsToSaveToDatabase(false);
		observationEJB.create(observationEntity);
	}

	/**
	 * The method saves the observation to the database.
	 */
	public void saveObservationToDatabase() {
		observationEntity.setUserWantsToSaveToDatabase(true);
		observationEntity.setObservationCategorySets(new HashSet<>(getCategorySetsInUse()));
		observationEJB.edit(observationEntity);
	}

	public void setImage(byte[] img) {
		this.image = img;
	}

	public byte[] getImage() {
		return image;
	}

}
