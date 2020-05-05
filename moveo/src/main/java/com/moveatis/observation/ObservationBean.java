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
package com.moveatis.observation;

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
import com.moveatis.interfaces.Observation;
import com.moveatis.records.RecordEntity;
import com.moveatis.session.SessionBean;
import com.moveatis.user.AbstractUser;

/**
 * The EJB manages observations..
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Stateful
public class ObservationBean extends AbstractBean<ObservationEntity> implements Observation, Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObservationBean.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private SessionBean sessionBean;

	@EJB
	private Event eventEJB;

	private ObservationEntity observationEntity;

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public ObservationBean() {
		super(ObservationEntity.class);
	}

	/**
	 * Finds and returns all observations for the specific user.
	 * 
	 * @param observer
	 *            The user, whose observations are to be searched.
	 * @return A list of the observations for the user.
	 */
	@Override
	public List<ObservationEntity> findAllByObserver(AbstractUser observer) {
		TypedQuery<ObservationEntity> query = em.createNamedQuery("findByObserver", ObservationEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Finds the observations for the user, which have no event attached to them.
	 * 
	 * @param observer
	 *            The user, whose observations are to be searched.
	 * @return A list of the observations.
	 */
	@Override
	public List<ObservationEntity> findWithoutEvent(AbstractUser observer) {
		TypedQuery<ObservationEntity> query = em.createNamedQuery("findWithoutEvent", ObservationEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Finds the observations that are made for events that the specified user does
	 * not own.
	 * 
	 * @param observer
	 *            The user, whose observations are to be searched.
	 * @return A list of the observations.
	 */
	@Override
	public List<ObservationEntity> findByEventsNotOwned(AbstractUser observer) {
		TypedQuery<ObservationEntity> query = em.createNamedQuery("findByEventsNotOwned", ObservationEntity.class);
		query.setParameter("observer", observer);
		return query.getResultList();
	}

	/**
	 * Persists the observations to the database.
	 * 
	 * @param observationEntity
	 *            The observatio entity to be persisted.
	 */
	@Override
	public void create(ObservationEntity observationEntity) {
		super.create(observationEntity);
	}

	/**
	 * Finds a list of the records for the observation with the given id.
	 * 
	 * @param id
	 *            The id of the observation.
	 * @return A list of the records.
	 */
	@Override
	public List<RecordEntity> findRecords(Object id) {
		observationEntity = em.find(ObservationEntity.class, id);
		if (observationEntity != null) {
			return observationEntity.getRecords();
		}
		return new ArrayList<>(); // return empty list
	}

	/**
	 * Removes the observation and also removes the observation from the event it
	 * was associated with.
	 * 
	 * @param observationEntity
	 *            The observation to be removed.
	 */
	@Override
	public void remove(ObservationEntity observationEntity) {
		super.remove(observationEntity);
		eventEJB.removeObservation(observationEntity);
		observationEntity.setEvent(null);
		super.edit(observationEntity);
	}

	/**
	 * Permanently removes the observation, which the user did not set to be saved
	 * into the database.
	 * 
	 * @param observationEntity
	 *            The observation to be removed.
	 */
	@Override
	public void removeUnsavedObservation(ObservationEntity observationEntity) {
		em.remove(em.merge(observationEntity));
	}
}
