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
package com.moveatis.abstracts;

import java.util.Calendar;
import java.util.List;

import javax.persistence.EntityManager;
import javax.persistence.Query;
import javax.persistence.TypedQuery;
import javax.persistence.criteria.CriteriaBuilder;
import javax.persistence.criteria.CriteriaQuery;
import javax.persistence.criteria.Root;

import com.moveatis.timezone.TimeZoneInformation;

/**
 * The super class to the enterprise beans manages the persistent connection and
 * entities.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @param <T>
 *            The entity the child of this bean uses
 * 
 */
public abstract class AbstractBean<T extends BaseEntity> {

	private Class<T> entityClass;

	public AbstractBean(Class<T> entityClass) {
		this.entityClass = entityClass;
	}

	protected abstract EntityManager getEntityManager();

	/**
	 * Creates a new entity.
	 * 
	 * @param entity
	 *            The entity to be created.
	 */
	public void create(T entity) {
		getEntityManager().persist(entity);
	}

	/**
	 * Edits the entity.
	 * 
	 * @param entity
	 *            The entity to be edited.
	 */
	public void edit(T entity) {
		getEntityManager().merge(entity);
	}

	/**
	 * Removes the entity.
	 * 
	 * @param entity
	 *            The entity to be removed.
	 */
	public void remove(T entity) {
		entity.setRemoved(); // entity is not actually removed, only the removed-date is set
		getEntityManager().merge(entity);
	}

	/**
	 * Finds an entity and returns it if it's not out of date.
	 * 
	 * @param id
	 *            The id of the entity to be found.
	 * @return The entity, if it is found. Otherwise null.
	 */
	public T find(Object id) {
		T entity = (T) getEntityManager().find(entityClass, id);

		if (entity.getRemoved() != null) {
			Calendar calendar = Calendar.getInstance(TimeZoneInformation.getTimeZone());
			Calendar entityCalendar = Calendar.getInstance(TimeZoneInformation.getTimeZone());
			entityCalendar.setTime(entity.getRemoved());

			if (entityCalendar.before(calendar)) {
				return entity;
			} else {
				return null;
			}
		} else {
			return entity;
		}
	}

	/**
	 * Finds all the entities, whose type matches the requested entity.
	 * 
	 * @return A list of all the entities of the requested entity type.
	 */
	public List<T> findAll() {
		CriteriaBuilder cb = getEntityManager().getCriteriaBuilder();
		CriteriaQuery<T> cq = cb.createQuery(entityClass);

		Root<T> rt = cq.from(entityClass);
		CriteriaQuery<T> all = cq.select(rt);

		TypedQuery<T> allQuery = getEntityManager().createQuery(all);
		return allQuery.getResultList();
	}

	/**
	 * Finds and returns the list of entities in the specified range. The range
	 * array has two elements: the minimum and the maximum of the range.
	 * 
	 * @param range
	 *            An array with two elements.
	 * @return A list of the entities in the range.
	 */
	public List<T> findRange(int[] range) {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		cq.select(cq.from(entityClass));
		Query q = getEntityManager().createQuery(cq);
		q.setMaxResults(range[1] - range[0] + 1);
		q.setFirstResult(range[0]);
		return q.getResultList();
	}

	/**
	 * Counts how many entities there are of the requested type.
	 * 
	 * @return The count of the entities.
	 */
	public int count() {
		CriteriaQuery cq = getEntityManager().getCriteriaBuilder().createQuery();
		Root<T> rt = cq.from(entityClass);
		cq.select(getEntityManager().getCriteriaBuilder().count(rt));
		Query q = getEntityManager().createQuery(cq);
		return ((Long) q.getSingleResult()).intValue();
	}

}
