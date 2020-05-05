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
package com.moveatis.feedbackanalysiscategory;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;

import java.io.Serializable;
import java.util.Map;

import javax.persistence.CollectionTable;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.MapKey;
import javax.persistence.OneToMany;
import javax.persistence.Table;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.abstracts.AbstractCategorySetEntity;

/**
 * The categorysets for feedback analysis
 * 
 * @author Visa Nykänen
 */
@Entity
@Table(name = "FEEDBACKANALYSISCATEGORYSET")
public class FeedbackAnalysisCategorySetEntity extends AbstractCategorySetEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	/**
	 * The categories in this categoryset
	 */
	@OneToMany(mappedBy = "feedbackAnalysisCategorySet", cascade = { PERSIST,
			MERGE }, fetch = FetchType.LAZY, targetEntity = FeedbackAnalysisCategoryEntity.class)
	@CollectionTable(name = "FEEDBACKANALYSISCATEGORYENTITIES")
	@MapKey(name = "orderNumber")
	@Column(name = "FEEDBACKANALYSISCATEGORYENTITY_ORDERNUMBER")
	private Map<Integer, AbstractCategoryEntity> categoryEntitys;

	@Override
	public Map<Integer, AbstractCategoryEntity> getCategoryEntitys() {
		return categoryEntitys;
	}

	@Override
	public void setCategoryEntitys(Map<Integer, AbstractCategoryEntity> categories) {
		this.categoryEntitys = categories;
	}

}