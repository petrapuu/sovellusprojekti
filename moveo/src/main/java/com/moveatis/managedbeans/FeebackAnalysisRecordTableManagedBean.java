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
package com.moveatis.managedbeans;

import java.io.File;
import java.io.Serializable;
import java.text.MessageFormat;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;
import java.util.ResourceBundle;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.context.RequestContext;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.helpers.DownloadTools;
import com.moveatis.interfaces.FeedbackAnalysis;
import com.moveatis.interfaces.MessageBundle;
import com.moveatis.records.FeedbackAnalysisRecordEntity;

/**
 * The managed bean in control of the record table for feedback analysis
 * 
 * @author Tuomas Moisio
 */
@Named(value = "analysisRecordTable")
@ViewScoped
public class FeebackAnalysisRecordTableManagedBean implements Serializable {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	@Inject
	private FeedbackAnalysisManagedBean feedbackAnalysisManagedBean;

	private List<String> selectedSaveOptions;

	private FeedbackAnalysisEntity feedbackAnalysis;

	private String fileName;

	@Inject
	private FeedbackAnalysis feedbackAnalysisEJB;

	@Inject
	@MessageBundle
	private transient ResourceBundle messages;

	/**
	 * The post constructor creates the feedback analysis
	 */
	@PostConstruct
	protected void initialize() {
		feedbackAnalysisManagedBean.setIsTimerStopped(true);
		feedbackAnalysis = feedbackAnalysisManagedBean.getFeedbackAnalysisEntity();
	}

	/**
	 * Gets the name of selected category from current category set
	 * 
	 * @param selectedCategories
	 *            Users selected categories
	 * @param categorySet
	 *            Category set in use
	 * @return name of the selected category, empty if no category is selected
	 */
	public String getSelectedCategorysName(List<FeedbackAnalysisCategoryEntity> selectedCategories,
			FeedbackAnalysisCategorySetEntity categorySet) {
		for (FeedbackAnalysisCategoryEntity cat_comp : selectedCategories) {
			for (AbstractCategoryEntity cat : categorySet.getCategoryEntitys().values()) {
				if (cat.getLabel().getText().contentEquals(cat_comp.getLabel().getText())
						&& cat.getCategorySet().getLabel().contentEquals(cat_comp.getCategorySet().getLabel())) {
					return cat.getLabel().getText();
				}
			}
		}

		return "------";
	}

	/**
	 * Gets the selected category from current category set
	 * 
	 * @param selectedCategories
	 *            Users selected categories
	 * @param categorySet
	 *            Category set in use
	 * @return the selected category, new category if the category is not selected
	 */
	public FeedbackAnalysisCategoryEntity getSelectedCategory(List<FeedbackAnalysisCategoryEntity> selectedCategories,
			FeedbackAnalysisCategorySetEntity categorySet) {
		for (FeedbackAnalysisCategoryEntity cat_comp : selectedCategories) {
			for (AbstractCategoryEntity cat : categorySet.getCategoryEntitys().values()) {
				if (cat.getLabel().getText().contentEquals(cat_comp.getLabel().getText())
						&& cat.getCategorySet().getLabel().contentEquals(cat_comp.getCategorySet().getLabel())) {
					return cat_comp;
				}
			}
		}
		FeedbackAnalysisCategoryEntity value = new FeedbackAnalysisCategoryEntity();
		return value;
	}

	/**
	 * Sends the user to the analyzer page with the selected record as main record.
	 * 
	 * @param orderNumber
	 *            order number of the selected record
	 * @return String that faces-config uses to control navigation
	 */
	public String edit(Integer orderNumber) {
		List<FeedbackAnalysisRecordEntity> list = feedbackAnalysisManagedBean.getFeedbackAnalysisEntity().getRecords();
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getOrderNumber().intValue() == orderNumber.intValue()) {
				feedbackAnalysisManagedBean.setCurrentRecord(i + 1);
			}
		}
		return "editrow";
	}

	public List<String> getSelectedSaveOptions() {
		return selectedSaveOptions;
	}

	public void setSelectedSaveOptions(List<String> selectedSaveOptions) {
		this.selectedSaveOptions = selectedSaveOptions;
	}

	/**
	 * Downloads the report-page table as an image.
	 */
	public void downloadImage() {
		String fileName = feedbackAnalysisManagedBean.getFeedbackAnalysisEntity().getAnalysisName() + "_report_";
		fileName = convertToFilename(fileName);

		File img = DownloadTools.getImageFromByteArr(fileName, feedbackAnalysisManagedBean.getReportImage());
		DownloadTools.downloadFile(img, "image/png",
				img.getName().substring(0, img.getName().lastIndexOf("_")) + ".png");
		img.delete();
	}

	private static String convertToFilename(String s) {
		if (s == null || s.isEmpty()) {
			return "unnamed";
		}
		return s.replaceAll("[^a-zA-Z0-9_]", "_");
	}
	
	/**
	 * Gets the confirmation message for deletion, includes the orderNumber of the row to be deleted
	 * 
	 * @param orderNumber
	 * @return
	 */
	public String getConfirm(int orderNumber) {
		FacesContext currentInstance = FacesContext.getCurrentInstance();
		return MessageFormat.format(currentInstance.getApplication().getResourceBundle(currentInstance, "msg").getString("repo_confirm"),orderNumber);
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

}
