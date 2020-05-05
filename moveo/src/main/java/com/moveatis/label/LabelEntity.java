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
package com.moveatis.label;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.abstracts.BaseEntity;
import com.moveatis.category.CategoryEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;

/**
 * The entity represents the information for the category's label in the
 * database. If there are several categories with a similar name, like
 * "Teaching", they can all have the same label entity.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@Table(name = "LABEL")
@Entity
@NamedQueries(@NamedQuery(name = "findByText", query = "SELECT l FROM LabelEntity l WHERE l.text = :text"))
public class LabelEntity extends BaseEntity implements Serializable {

	@NotNull
	private String text;

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "label", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<CategoryEntity> categoryEntities;

	@OneToMany(mappedBy = "label", orphanRemoval = true, fetch = FetchType.LAZY)
	private List<FeedbackAnalysisCategoryEntity> feedbackAnalysisCategoryEntities;

	public LabelEntity() {

	}

	public LabelEntity(String text) {
		this.text = text;
	}

	public void setFeedbackAnalysisCategoryEntities(
			List<FeedbackAnalysisCategoryEntity> feedbackAnalysisCategoryEntities) {
		this.feedbackAnalysisCategoryEntities = new ArrayList<FeedbackAnalysisCategoryEntity>();
		for (AbstractCategoryEntity cat : feedbackAnalysisCategoryEntities)
			this.addCategoryEntity(cat);
	}

	public List<CategoryEntity> getCategoryEntities() {
		return categoryEntities;
	}

	public void setCategoryEntities(List<CategoryEntity> categoryEntities) {
		this.categoryEntities = new ArrayList<CategoryEntity>();
		for (AbstractCategoryEntity cat : categoryEntities)
			this.addCategoryEntity(cat);
	}

	public List<FeedbackAnalysisCategoryEntity> getFeedbackAnalysisCategoryEntities() {
		return feedbackAnalysisCategoryEntities;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	public String getText() {
		return text;
	}

	public void setText(String text) {
		this.text = text;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof LabelEntity)) {
			return false;
		}
		LabelEntity other = (LabelEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.category.LabelEntity[ id=" + id + " ]";
	}

	public void addCategoryEntity(AbstractCategoryEntity abstractCategoryEntity) {
		if (feedbackAnalysisCategoryEntities == null) {
			feedbackAnalysisCategoryEntities = new ArrayList<FeedbackAnalysisCategoryEntity>();
		}
		if (categoryEntities == null) {
			categoryEntities = new ArrayList<CategoryEntity>();
		}
		if (abstractCategoryEntity instanceof FeedbackAnalysisCategoryEntity)
			feedbackAnalysisCategoryEntities.add((FeedbackAnalysisCategoryEntity) abstractCategoryEntity);
		else
			categoryEntities.add((CategoryEntity) abstractCategoryEntity);
	}

}
