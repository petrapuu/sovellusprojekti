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
package com.moveatis.records;

import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;

import com.moveatis.abstracts.AbstractRecordEntity;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;

/**
 * The entity for feedbackanalysis records, corresponds to the
 * feedbackanalysisrecord table in the database
 * 
 * @author Visa Nykänen
 */
@Entity
@Table(name = "FEEDBACKANALYSISRECORD")
public class FeedbackAnalysisRecordEntity extends AbstractRecordEntity {

	@ManyToOne
	private FeedbackAnalysisEntity feedbackAnalysis;

	/**
	 * The categories selected in this record
	 */
	@ManyToMany
	@JoinTable(name = "FeedbackAnalysisRecordSelectedCategories", joinColumns = @JoinColumn(name = "record_id"), inverseJoinColumns = @JoinColumn(name = "category_id"))
	private List<FeedbackAnalysisCategoryEntity> selectedCategories;

	/**
	 * Maintains the order of the records within an analysis
	 */
	private Integer orderNumber;

	public Integer getOrderNumber() {
		return orderNumber;
	}

	public void setOrderNumber(Integer orderNumber) {
		this.orderNumber = orderNumber;
	}

	/**
	 * Adds the given category to this record, makes sure that referential integrity
	 * is maintained by also editing the recordscontainingthiscategory-list of the
	 * category to be added
	 * 
	 * @param category
	 *            the category to be added
	 */
	public void addSelectedCategory(FeedbackAnalysisCategoryEntity category) {
		selectedCategories.add(category);
		if (category.getRecordsContainingThisFeedbackAnalysisCategory() == null)
			category.setRecordsContainingThisFeedbackAnalysisCategory(new ArrayList<FeedbackAnalysisRecordEntity>());
		category.getRecordsContainingThisFeedbackAnalysisCategory().add(this);
	}

	public void removeSelectedCategory(FeedbackAnalysisCategoryEntity category) {
		selectedCategories.remove(category);
		category.getRecordsContainingThisFeedbackAnalysisCategory().remove(this);
	}

	public List<FeedbackAnalysisCategoryEntity> getSelectedCategories() {
		return selectedCategories;
	}

	public void setSelectedCategories(List<FeedbackAnalysisCategoryEntity> selectedCategories) {
		this.selectedCategories = new ArrayList<FeedbackAnalysisCategoryEntity>();
		for (FeedbackAnalysisCategoryEntity selectedCategory : selectedCategories)
			addSelectedCategory(selectedCategory);
	}

	public FeedbackAnalysisEntity getFeedbackAnalysis() {
		return feedbackAnalysis;
	}

	public void setFeedbackAnalysis(FeedbackAnalysisEntity feedbackAnalysis) {
		this.feedbackAnalysis = feedbackAnalysis;
	}
}
