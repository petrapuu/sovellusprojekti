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
package com.moveatis.abstracts;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.moveatis.event.EventGroupEntity;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * Common features of the categorysets for feedback analysis and observations
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@MappedSuperclass
public abstract class AbstractCategorySetEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * creator and owner of the categoryset
	 */
	@ManyToOne
	private IdentifiedUserEntity creator;

	/**
	 * Eventgroup for which the categoryset is made
	 */
	@ManyToOne
	private EventGroupEntity eventGroupEntity;

	/**
	 * The name of the categoryset
	 */
	private String label;

	/**
	 * Description of the categoryset
	 */
	private String description;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public IdentifiedUserEntity getCreator() {
		return creator;
	}

	public void setCreator(IdentifiedUserEntity creator) {
		this.creator = creator;
	}

	public EventGroupEntity getEventGroupEntity() {
		return eventGroupEntity;
	}

	public void setEventGroupEntity(EventGroupEntity eventGroupEntity) {
		this.eventGroupEntity = eventGroupEntity;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof AbstractCategorySetEntity)) {
			return false;
		}
		AbstractCategorySetEntity other = (AbstractCategorySetEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.category.CategoryTemplateEntity[ id=" + id + " ]";
	}

	public abstract Map<Integer, AbstractCategoryEntity> getCategoryEntitys();

	public abstract void setCategoryEntitys(Map<Integer, AbstractCategoryEntity> categories);

}
