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

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.FetchType.EAGER;

import javax.persistence.ManyToOne;
import javax.persistence.MappedSuperclass;

import com.moveatis.category.CategoryEntity;
import com.moveatis.label.LabelEntity;

/**
 * Abstracts the categories to allow for categories in simple observations and
 * in feedback analysis
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@MappedSuperclass
public abstract class AbstractCategoryEntity extends BaseEntity {

	/**
	 * Label entity for the category, allows for categories with the same name to
	 * use the same label entity
	 */
	@ManyToOne(fetch = EAGER, cascade = { MERGE })
	private LabelEntity label;

	/**
	 * Description of the category
	 */
	private String description;

	/**
	 * Maintains the order of the categories within their categoryset
	 */
	private Integer orderNumber;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public LabelEntity getLabel() {
		return label;
	}

	public void setLabel(LabelEntity label) {
		label.addCategoryEntity(this);
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		return object instanceof CategoryEntity;
	}

	@Override
	public String toString() {
		return "com.moveatis.category.Category[ id=" + id + " ]";
	}

	public abstract void setCategorySet(AbstractCategorySetEntity abstractCategorySetEntity);

	public abstract AbstractCategorySetEntity getCategorySet();

}
