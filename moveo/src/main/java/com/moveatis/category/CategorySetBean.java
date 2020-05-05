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
package com.moveatis.category;

import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.EntityManager;
import javax.persistence.PersistenceContext;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractBean;
import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.abstracts.AbstractCategorySetEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.interfaces.AnonUser;
import com.moveatis.interfaces.CategorySet;
import com.moveatis.interfaces.EventGroup;

/**
 * The EJB manages the CategorySet entities for both observation and analysis.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@Stateless
public class CategorySetBean extends AbstractBean<AbstractCategorySetEntity> implements CategorySet {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategorySetBean.class);

	@PersistenceContext(unitName = "MOVEATIS_PERSISTENCE")
	private EntityManager em;

	@Inject
	private AnonUser anonUserEJB;

	@Inject
	private EventGroup eventGroupEJB;

	@Override
	protected EntityManager getEntityManager() {
		return em;
	}

	public CategorySetBean() {
		super(AbstractCategorySetEntity.class);
	}

	public void detachCategorySet(AbstractCategorySetEntity categorySetEntity) {
		em.detach(categorySetEntity);
		categorySetEntity.setId(null);
		categorySetEntity.setEventGroupEntity(null);
		for (AbstractCategoryEntity cat : categorySetEntity.getCategoryEntitys().values()) {
			cat.setId(null);
		}
	}

	/**
	 * Sets the category set removal date and removes the category set from event
	 * groups.
	 * 
	 * @param categorySetEntity
	 *            The category set entity to be removed.
	 */
	@Override
	public void remove(AbstractCategorySetEntity categorySetEntity) {
		super.remove(categorySetEntity);
		eventGroupEJB.removeCategorySetEntityFromEventGroups(categorySetEntity);
		categorySetEntity.setEventGroupEntity(null);
		super.edit(categorySetEntity);
	}

	/**
	 * Finds the category sets that are set as public.
	 * 
	 * @return Set of all public category set entities.
	 */
	@Override
	public Set<AbstractCategorySetEntity> findPublicCategorySets() {

		List<EventGroupEntity> publicEventGroups = eventGroupEJB.findAllForPublicUser();
		Set<AbstractCategorySetEntity> publicCategorySets = new HashSet<>();

		for (EventGroupEntity eventGroupEntity : publicEventGroups) {
			Set<CategorySetEntity> eventGroupCategorySets = eventGroupEntity.getCategorySets();
			publicCategorySets.addAll(eventGroupCategorySets);
		}

		return publicCategorySets;
	}

	/**
	 * Removes the category from the category set.
	 * 
	 * @param categorySet
	 *            The category set from which the category is removed from.
	 * @param categoryEntity
	 *            The category to be removed from the category set.
	 */
	@Override
	public void removeCategoryFromCategorySet(AbstractCategorySetEntity categorySet,
			AbstractCategoryEntity categoryEntity) {
		Map<Integer, AbstractCategoryEntity> categories = categorySet.getCategoryEntitys();
		Integer keyFound = -1;

		for (Integer key : categories.keySet()) {
			if (categories.get(key).getId().equals(categoryEntity.getId())) {
				keyFound = key;
			}
		}

		if (keyFound > -1) {
			categories.remove(keyFound);
		}

		categorySet.setCategoryEntitys(categories);
		super.edit(categorySet);
	}
}
