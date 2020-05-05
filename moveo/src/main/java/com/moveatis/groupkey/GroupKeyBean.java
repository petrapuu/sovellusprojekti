/*
 * Copyright 2016 Sami Kallio.
 * All rights reserved.
 *
 * Redistribution and use in source and binary forms, with or without
 * modification, are permitted provided that the following conditions are met:
 *
 * * Redistributions of source code must retain the above copyright notice, 
 *   this list of conditions and the following disclaimer.
 * * Redistributions in binary form must reproduce the above copyright notice, 
 *   this list of conditions and the following disclaimer in the documentation 
 *   and/or other materials provided with the distribution.
 * * Neither the name of the University of Jyväskylä nor the names of its contributors 
 *   may be used to endorse or promote products derived from this software without specific
 *   prior written permission.
 *
 * THIS SOFTWARE IS PROVIDED BY THE COPYRIGHT HOLDERS AND CONTRIBUTORS "AS IS"
 * AND ANY EXPRESS OR IMPLIED WARRANTIES, INCLUDING, BUT NOT LIMITED TO, THE
 * IMPLIED WARRANTIES OF MERCHANTABILITY AND FITNESS FOR A PARTICULAR PURPOSE
 * ARE DISCLAIMED. IN NO EVENT SHALL THE COPYRIGHT HOLDER OR CONTRIBUTORS BE
 * LIABLE FOR ANY DIRECT, INDIRECT, INCIDENTAL, SPECIAL, EXEMPLARY, OR
 * CONSEQUENTIAL DAMAGES (INCLUDING, BUT NOT LIMITED TO, PROCUREMENT OF
 * SUBSTITUTE GOODS OR SERVICES; LOSS OF USE, DATA, OR PROFITS; OR BUSINESS
 * INTERRUPTION) HOWEVER CAUSED AND ON ANY THEORY OF LIABILITY, WHETHER IN
 * CONTRACT, STRICT LIABILITY, OR TORT (INCLUDING NEGLIGENCE OR OTHERWISE)
 * ARISING IN ANY WAY OUT OF THE USE OF THIS SOFTWARE, EVEN IF ADVISED OF THE
 * POSSIBILITY OF SUCH DAMAGE.
 */
package com.moveatis.groupkey;

import javax.ejb.Stateless;
import javax.persistence.EntityManager;
import javax.persistence.NoResultException;
import javax.persistence.PersistenceContext;
import javax.persistence.TypedQuery;

import com.moveatis.abstracts.AbstractBean;
import com.moveatis.interfaces.GroupKey;

/**
 * The EJB manages group keys that are used to access the event groups in a
 * "semi-public" fashion.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Stateless
public class GroupKeyBean extends AbstractBean<GroupKeyEntity> implements GroupKey {

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	private GroupKeyEntity groupKeyEntity;

	public GroupKeyBean() {
		super(GroupKeyEntity.class);
	}

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	/**
	 * The method returns the group key, which currently is associated with the
	 * instance.
	 * 
	 * @return The group key associated with the instance for groupkeyEJB.
	 */
	@Override
	public GroupKeyEntity getGroupKeyEntity() {
		return groupKeyEntity;
	}

	/**
	 * The method finds and returns the group key, which has the same value as the
	 * specified parameter.
	 * 
	 * @param key
	 *            String-representation of the key.
	 * @return the group key or null.
	 */
	@Override
	public GroupKeyEntity findByKey(String key) {
		TypedQuery<GroupKeyEntity> query = em.createNamedQuery("findKey", GroupKeyEntity.class);
		query.setParameter("key", key);

		try {
			groupKeyEntity = query.getSingleResult();
		} catch (NoResultException nre) {
			return null;
		}

		return groupKeyEntity;

	}

	/**
	 * The method removes the group keys permanently. Usually the entities are not
	 * removed from the database, as only their removal date is set. If the group
	 * key need to be reused, they need to be removed permanently before reuse.
	 * 
	 * @param groupKeyEntity
	 *            The group key to be removed.
	 */
	@Override
	public void removePermanently(GroupKeyEntity groupKeyEntity) {
		em.remove(em.merge(groupKeyEntity));
	}
}
