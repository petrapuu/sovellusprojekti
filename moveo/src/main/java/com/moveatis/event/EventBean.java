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
package com.moveatis.event;

import java.util.List;
import java.util.Set;

import javax.ejb.Stateful;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import com.moveatis.abstracts.AbstractBean;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.interfaces.Event;
import com.moveatis.observation.ObservationEntity;
import com.moveatis.observation.ObservationEntity_;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The EJB manages the events of an user.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Stateful
public class EventBean extends AbstractBean<EventEntity> implements Event {

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	private EventEntity eventEntity;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public EventBean() {
		super(EventEntity.class);
	}

	/**
	 * Returns a list of the events belonging to the requested user.
	 * 
	 * @param user
	 *            The user for which the events should be searched for.
	 * @return A list of the user's events.
	 */
	@Override
	public List<EventEntity> findEventsForUser(IdentifiedUserEntity user) {
		TypedQuery<EventEntity> query = em.createNamedQuery("SceneEntity.findByUser", EventEntity.class);
		query.setParameter("owner", user);
		return query.getResultList();
	}

	/**
	 * Gets the event that is currently associated with the instance of the
	 * eventBean.
	 * 
	 * @return the EventEntity associated with the instance.
	 */
	@Override
	public EventEntity getEventEntity() {
		return eventEntity;
	}

	/**
	 * The method removes the observations from the event of the instance of the
	 * eventBean.
	 * 
	 * @param observationEntity
	 *            The observation to be removed from the event.
	 */
	@Override
	public void removeObservation(ObservationEntity observationEntity) {

		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventEntity> cq = cb.createQuery(EventEntity.class);

		Root<EventEntity> groupRoot = cq.from(EventEntity.class);
		SetJoin<EventEntity, ObservationEntity> observationJoin = groupRoot.join(EventEntity_.observations);
		Predicate p = cb.equal(observationJoin.get(ObservationEntity_.id), observationEntity.getId());

		cq.select(groupRoot).where(p);
		TypedQuery<EventEntity> query = em.createQuery(cq);
		try {
			EventEntity event = query.getSingleResult();
			Set<ObservationEntity> observationSets = event.getObservations();
			observationSets.remove(observationEntity);
			event.setObservations(observationSets);
			super.edit(event);
		} catch (NoResultException nre) {
			// NoResultException is not an actual exception IMHO ...
		}
	}

	@Override
	public void removeFeedbackAnalysis(FeedbackAnalysisEntity feedbackAnalysisEntity) {
		// TODO Auto-generated method stub

	}
}
