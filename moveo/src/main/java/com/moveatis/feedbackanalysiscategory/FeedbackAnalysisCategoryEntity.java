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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.ManyToMany;
import javax.persistence.ManyToOne;
import javax.persistence.Table;
import javax.persistence.Transient;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.abstracts.AbstractCategorySetEntity;
import com.moveatis.records.FeedbackAnalysisRecordEntity;

/**
 * The Categories for feedback analysis
 * 
 * @author Visa Nykänen
 *
 */
@Table(name = "FEEDBACKANALYSISCATEGORY")
@Entity
public class FeedbackAnalysisCategoryEntity extends AbstractCategoryEntity implements Serializable {
	private static final long serialVersionUID = 1L;
	/**
	 * The categoryset to which this category belongs
	 */
	@ManyToOne
	private FeedbackAnalysisCategorySetEntity feedbackAnalysisCategorySet;

	/**
	 * The records in which this category is being used many to many relation with
	 * feedbackanalysisrecord
	 */
	@ManyToMany(mappedBy = "selectedCategories")
	private List<FeedbackAnalysisRecordEntity> recordsContainingThisFeedbackAnalysisCategory;

	/**
	 * A boolean that is used in analyzer view to see which categories are selected
	 * in the currently shown record not saved to database and not very useful
	 * outside of analyzer view
	 */
	@Transient
	private boolean inRecord = false;

	public List<FeedbackAnalysisRecordEntity> getRecordsContainingThisFeedbackAnalysisCategory() {
		return recordsContainingThisFeedbackAnalysisCategory;
	}

	/**
	 * sets the records that contain this category, makes sure that the connections
	 * between the category and feedbackanalysisrecord are correct
	 * 
	 * @param recordsContainingThisFeedbackAnalysisCategory list that stores the records containing this category.
	 */
	public void setRecordsContainingThisFeedbackAnalysisCategory(
			List<FeedbackAnalysisRecordEntity> recordsContainingThisFeedbackAnalysisCategory) {
		this.recordsContainingThisFeedbackAnalysisCategory = new ArrayList<FeedbackAnalysisRecordEntity>();
		for (FeedbackAnalysisRecordEntity far : recordsContainingThisFeedbackAnalysisCategory) {
			far.addSelectedCategory(this);
			this.recordsContainingThisFeedbackAnalysisCategory.add(far);
		}
	}

	public boolean getInRecord() {
		return inRecord;
	}

	/**
	 * Sets the category to be in the currently shown record, makes sure that no
	 * other categories from the same categoryset are also selected
	 * 
	 * @param inRecord
	 *            whether the category is in the currently shown record
	 */
	public void setInRecord(boolean inRecord) {
		if (inRecord)
			for (AbstractCategoryEntity cat : this.getCategorySet().getCategoryEntitys().values())
				((FeedbackAnalysisCategoryEntity) cat).setInRecord(false);
		this.inRecord = inRecord;
	}

	@Override
	public FeedbackAnalysisCategorySetEntity getCategorySet() {
		return feedbackAnalysisCategorySet;
	}

	@Override
	public void setCategorySet(AbstractCategorySetEntity categorySetEntity) {
		this.feedbackAnalysisCategorySet = (FeedbackAnalysisCategorySetEntity) categorySetEntity;
	}

}