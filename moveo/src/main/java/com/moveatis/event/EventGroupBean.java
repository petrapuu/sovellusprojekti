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

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Predicate;
import javax.persistence.criteria.Root;
import javax.persistence.criteria.SetJoin;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractBean;
import com.moveatis.abstracts.AbstractCategorySetEntity;
import com.moveatis.category.CategorySetEntity;
import com.moveatis.category.CategorySetEntity_;
import com.moveatis.interfaces.AnonUser;
import com.moveatis.interfaces.EventGroup;
import com.moveatis.user.AbstractUser;
import com.moveatis.user.AbstractUser_;

/**
 * The EJB manages the event group entities.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Stateless
public class EventGroupBean extends AbstractBean<EventGroupEntity> implements EventGroup {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventGroupBean.class);

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	@Inject
	private AnonUser anonUserEJB;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public EventGroupBean() {
		super(EventGroupEntity.class);
	}

	/**
	 * Finds and returns a list of the event groups belonging to the given user.
	 * 
	 * @param owner
	 *            The user whose event groups are searched for.
	 * @return A list of the event groups.
	 */
	@Override
	public List<EventGroupEntity> findAllForOwner(AbstractUser owner) {
		TypedQuery<EventGroupEntity> query = em.createNamedQuery("findEventGroupByOwner", EventGroupEntity.class);
		query.setParameter("ownerEntity", owner);
		return query.getResultList();
	}

	/**
	 * Finds and returns a list of event groups, which the given user has access to.
	 * 
	 * @param user
	 *            The user whose event groups are searched for.
	 * @return A list of the event groups.
	 */
	@Override
	public List<EventGroupEntity> findAllForUser(AbstractUser user) {
		return findAllForAbstractUser(user);
	}

	/**
	 * Finds and returns the event groups that are allowed for public use.
	 * 
	 * @return A list of the event groups.
	 */
	@Override
	public List<EventGroupEntity> findAllForPublicUser() {
		return findAllForAbstractUser(anonUserEJB.find());
	}

	/**
	 * The method finds and returns the event groups, which the given user has
	 * access to.
	 * 
	 * @param user
	 *            The user to search event groups for.
	 * @return A list of the event groups.
	 */
	private List<EventGroupEntity> findAllForAbstractUser(AbstractUser user) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventGroupEntity> cq = cb.createQuery(EventGroupEntity.class);

		Root<EventGroupEntity> groupRoot = cq.from(EventGroupEntity.class);

		SetJoin<EventGroupEntity, AbstractUser> userJoin = groupRoot.join(EventGroupEntity_.users);
		Predicate p = cb.equal(userJoin.get(AbstractUser_.id), user.getId());

		cq.select(groupRoot).where(p);

		TypedQuery<EventGroupEntity> query = em.createQuery(cq);

		return query.getResultList();
	}

	/**
	 * The method removes the given category set from all of the event groups that
	 * have the category set.
	 * 
	 * @param categorySetEntity
	 *            The category set to be removed from the event groups.
	 */
	@Override
	public void removeCategorySetEntityFromEventGroups(AbstractCategorySetEntity categorySetEntity) {
		CriteriaBuilder cb = em.getCriteriaBuilder();
		CriteriaQuery<EventGroupEntity> cq = cb.createQuery(EventGroupEntity.class);

		Root<EventGroupEntity> groupRoot = cq.from(EventGroupEntity.class);
		SetJoin<EventGroupEntity, CategorySetEntity> categoryJoin = groupRoot.join(EventGroupEntity_.categorySets);
		Predicate p = cb.equal(categoryJoin.get(CategorySetEntity_.id), categorySetEntity.getId());

		cq.select(groupRoot).where(p);
		TypedQuery<EventGroupEntity> query = em.createQuery(cq);
		List<EventGroupEntity> eventGroups = query.getResultList();
		if (!eventGroups.isEmpty()) {
			for (EventGroupEntity eventGroup : eventGroups) {
				Set<CategorySetEntity> categorySets = eventGroup.getCategorySets();
				categorySets.remove(categorySetEntity);
				eventGroup.setCategorySets(categorySets);
				super.edit(eventGroup);
			}
		}
	}
}
