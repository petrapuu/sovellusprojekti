/* 
 * Copyright (c) 2016, Jarmo Juujärvi, Sami Kallio, Kai Korhonen, Juha Moisio, Ilari Paananen 
 * Copyright (c) 2019, Visa Nykänen, Tuomas Moisio, Petra Puumala, Karoliina Lappalainen 
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
package com.moveatis.feedbackanalysis;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateful;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractBean;
import com.moveatis.interfaces.Event;
import com.moveatis.interfaces.FeedbackAnalysisRecord;
import com.moveatis.interfaces.FeedbackAnalysis;
import com.moveatis.observation.ObservationBean;
import com.moveatis.records.FeedbackAnalysisRecordEntity;
import com.moveatis.session.SessionBean;
import com.moveatis.user.AbstractUser;

/**
 * Manages the database connection for the feedback analyses
 * 
 * @author Visa Nykänen
 */
@Stateful
public class FeedbackAnalysisBean extends AbstractBean<FeedbackAnalysisEntity>
		implements FeedbackAnalysis, Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(ObservationBean.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private SessionBean sessionBean;

	@EJB
	private Event eventEJB;

	private FeedbackAnalysisEntity feedbackAnalysisEntity;

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	@Inject
	private FeedbackAnalysisRecord feedbackAnalysisRecordEJB;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public FeedbackAnalysisBean() {
		super(FeedbackAnalysisEntity.class);
	}

	/**
	 * Finds and returns all observations for the specific user.
	 * 
	 * @param observer
	 *            The user, whose analyses are to be searched.
	 * @return A list of the analyses for the user.
	 */
	@Override
	public List<FeedbackAnalysisEntity> findAllByObserver(AbstractUser observer) {
		TypedQuery<FeedbackAnalysisEntity> query = em.createNamedQuery("findFeedbackAnalysesByObserver",
				FeedbackAnalysisEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Finds the analyses for the user, which have no event attached to them.
	 * 
	 * @param observer
	 *            The user, whose analyses are to be searched.
	 * @return A list of the analyses.
	 */
	@Override
	public List<FeedbackAnalysisEntity> findWithoutEvent(AbstractUser observer) {
		TypedQuery<FeedbackAnalysisEntity> query = em.createNamedQuery("findFeedbackAnalysesWithoutEvent",
				FeedbackAnalysisEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Finds the analyzatinos that are made for events that the specified user does
	 * not own.
	 * 
	 * @param observer
	 *            The user, whose analyses are to be searched.
	 * @return A list of the analyses.
	 */
	@Override
	public List<FeedbackAnalysisEntity> findByEventsNotOwned(AbstractUser observer) {
		TypedQuery<FeedbackAnalysisEntity> query = em.createNamedQuery("findFeedbackAnalysesByEventsNotOwned",
				FeedbackAnalysisEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Persists the observations to the database.
	 * 
	 * @param feedbackAnalysis
	 *            The observatio entity to be persisted.
	 */
	@Override
	public void create(FeedbackAnalysisEntity feedbackAnalysis) {
		super.create(feedbackAnalysis);
	}

	@Override
	public void edit(FeedbackAnalysisEntity feedbackAnalysis) {
		super.edit(feedbackAnalysis);
	}

	/**
	 * Finds a list of the records for the observation with the given id.
	 * 
	 * @param id
	 *            The id of the analysis.
	 * @return A list of the records.
	 */
	@Override
	public List<FeedbackAnalysisRecordEntity> findRecords(Object id) {
		feedbackAnalysisEntity = em.find(FeedbackAnalysisEntity.class, id);
		if (feedbackAnalysisEntity != null) {
			return feedbackAnalysisEntity.getRecords();
		}
		return new ArrayList<>(); // return empty list
	}

	/**
	 * Removes the analysis and also removes the analysis from the event it was
	 * associated with.
	 * 
	 * @param feedbackAnalysisEntity
	 *            The analysis to be removed.
	 */
	@Override
	public void remove(FeedbackAnalysisEntity feedbackAnalysisEntity) {
		super.remove(feedbackAnalysisEntity);
		eventEJB.removeFeedbackAnalysis(feedbackAnalysisEntity);
		feedbackAnalysisEntity.setEvent(null);
		super.edit(feedbackAnalysisEntity);
	}

	@Override
	public void removeRecordFromAnalysis(FeedbackAnalysisEntity feedbackAnalysis, FeedbackAnalysisRecordEntity record) {
		List<FeedbackAnalysisRecordEntity> records = feedbackAnalysis.getRecords();
		records.remove(record);
		record.setFeedbackAnalysis(null);
		feedbackAnalysis.setRecords(records);
		feedbackAnalysisRecordEJB.remove(record);
		for (FeedbackAnalysisRecordEntity rec : feedbackAnalysis.getRecords()) {
			if (rec.getId() == null)
				feedbackAnalysisRecordEJB.create(rec);
			else
				feedbackAnalysisRecordEJB.edit(rec);
		}
		super.edit(feedbackAnalysis);
	}

	/**
	 * Permanently removes the analysis, which the user did not set to be saved into
	 * the database.
	 * 
	 * @param feedbackAnalysisEntity
	 *            The analysis to be removed.
	 */
	@Override
	public void removeUnsavedObservation(FeedbackAnalysisEntity feedbackAnalysisEntity) {
		em.remove(em.merge(feedbackAnalysisEntity));
	}
}
