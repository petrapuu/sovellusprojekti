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

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeMap;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.event.EventEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.helpers.Validation;
import com.moveatis.interfaces.CategorySet;
import com.moveatis.interfaces.EventGroup;
import com.moveatis.interfaces.MessageBundle;
import com.moveatis.interfaces.Session;
import com.moveatis.label.LabelEntity;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The managed bean in control of the category selection for feedback analysis
 * 
 * @author Visa Nykänen
 */
@Named(value = "feedbackAnalysisCategorySelectionManagedBean")
@ViewScoped
public class FeedbackAnalysisCategorySelectionManagedBean implements Serializable {
	private static final Logger LOGGER = LoggerFactory.getLogger(FeedbackAnalysisCategorySelectionManagedBean.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	private String newFeedbackAnalysisCategorySetName;

	@Inject
	@MessageBundle // created MessageBundle to allow resourcebundle injection to CDI beans
	private transient ResourceBundle messages; // RequestBundle is not serializable

	private long selectedDefaultFeedbackAnalysisCategorySet;

	private long selectedPrivateFeedbackAnalysisCategorySet;

	private List<FeedbackAnalysisCategorySetEntity> defaultFeedbackAnalysisCategorySets; // From group key or event that
																							// was selected in control
																							// page // page.
	private List<FeedbackAnalysisCategorySetEntity> privateFeedbackAnalysisCategorySets;

	private List<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySetsInUse;

	private EventGroupEntity eventGroup;

	@Inject
	private Session sessionEJB;

	@Inject
	private EventGroup eventGroupEJB;

	@Inject
	private FeedbackAnalysisManagedBean feedbackAnalysisManagedBean;

	/**
	 * Gets the categorysets from the given groupkey, and if the user is logged in
	 * their own categorysets
	 */
	@PostConstruct
	public void init() {
		eventGroup = null;
		defaultFeedbackAnalysisCategorySets = new ArrayList<FeedbackAnalysisCategorySetEntity>();
		privateFeedbackAnalysisCategorySets = new ArrayList<FeedbackAnalysisCategorySetEntity>();
		feedbackAnalysisCategorySetsInUse = new ArrayList<FeedbackAnalysisCategorySetEntity>();
		if (feedbackAnalysisManagedBean.getEventEntity() != null) {
			EventEntity event = feedbackAnalysisManagedBean.getEventEntity();
			eventGroup = event.getEventGroup();
			if (eventGroup.getFeedbackAnalysisCategorySets() == null)
				eventGroup.setFeedbackAnalysisCategorySets(new HashSet<FeedbackAnalysisCategorySetEntity>());
			defaultFeedbackAnalysisCategorySets.addAll(eventGroup.getFeedbackAnalysisCategorySets());
		}

		if (sessionEJB.isIdentifiedUser()) {
			IdentifiedUserEntity user = sessionEJB.getLoggedIdentifiedUser();
			for (EventGroupEntity eg : eventGroupEJB.findAllForOwner(user))
				for (FeedbackAnalysisCategorySetEntity fba : eg.getFeedbackAnalysisCategorySets())
					privateFeedbackAnalysisCategorySets.add(fba);
		}

		List<FeedbackAnalysisCategorySetEntity> categorySets = sessionEJB.getFeedbackAnalysisCategorySetsInUse();
		if (categorySets != null) {
			feedbackAnalysisCategorySetsInUse = categorySets;
		} else {
			for (FeedbackAnalysisCategorySetEntity categorySet : defaultFeedbackAnalysisCategorySets) {
				feedbackAnalysisCategorySetsInUse.add(categorySet);
			}
		}
	}

	/**
	 * Adds a new category set for the analysis if
	 * newFeedbackAnalysisCategorySetName isn't empty.
	 */
	public void addNewCategorySet() {
		String name = Validation.validateForJsAndHtml(newFeedbackAnalysisCategorySetName);

		if (!name.isEmpty()) {
			for (FeedbackAnalysisCategorySetEntity set : feedbackAnalysisCategorySetsInUse) {
				if (name.equals(set.getLabel())) {
					showErrorMessage(messages.getString("cs_errorNotUniqueCategorySet"));
					return;
				}
			}

			FeedbackAnalysisCategorySetEntity categorySet = new FeedbackAnalysisCategorySetEntity();
			categorySet.setLabel(name);
			Map<Integer, AbstractCategoryEntity> newCategoryEntities = new TreeMap<Integer, AbstractCategoryEntity>();
			categorySet.setCategoryEntitys(newCategoryEntities);
			feedbackAnalysisCategorySetsInUse.add(categorySet);
			newFeedbackAnalysisCategorySetName = "";
		}
	}

	/**
	 * Adds a new category to the given categoryset
	 * 
	 * @param categorySet
	 *            the categoryset to which the new category is added
	 */
	public void addNewCategoryToCategorySet(FeedbackAnalysisCategorySetEntity categorySet) {
		FeedbackAnalysisCategoryEntity fac = new FeedbackAnalysisCategoryEntity();
		fac.setLabel(new LabelEntity());

		Map<Integer, AbstractCategoryEntity> categories = categorySet.getCategoryEntitys();
		fac.setOrderNumber(categories.keySet().size());
		fac.setCategorySet(categorySet);
		categories.put(categories.keySet().size(), fac);
	}

	/**
	 * removes the given category from the given categoryset
	 * 
	 * @param categorySet
	 *            the categoryset from which a category is to be removed
	 * @param category
	 *            the category to be removed
	 */
	public void removeCategoryFromCategorySet(FeedbackAnalysisCategorySetEntity categorySet,
			FeedbackAnalysisCategoryEntity category) {

		Map<Integer, AbstractCategoryEntity> categories = categorySet.getCategoryEntitys();
		Map<Integer, AbstractCategoryEntity> tmp_categories = new TreeMap<Integer, AbstractCategoryEntity>();

		categories.remove(category.getOrderNumber());
		List<Integer> keys=new ArrayList<>();
		keys.addAll(categories.keySet());
		Collections.sort(keys);
		int i = 0;
		for (int key : keys) {
			categories.get(key).setOrderNumber(i);
			tmp_categories.put(i, categories.get(key));
			i++;
		}
		categorySet.setCategoryEntitys(tmp_categories);
	}

	/**
	 * Finds the categoryset with the given id from the given list
	 * 
	 * @param categorySets
	 *            list of categorysets
	 * @param id
	 *            the ID of the categoryset that needs to be accessed
	 * @return the found categoryset or a new categoryset if one with the given ID
	 *         isn't found
	 */
	private FeedbackAnalysisCategorySetEntity findById(List<FeedbackAnalysisCategorySetEntity> categorySets, long id) {
		for (FeedbackAnalysisCategorySetEntity facs : categorySets) {
			if (facs.getId() == id) {
				return (facs);
			}
		}
		return new FeedbackAnalysisCategorySetEntity();
	}

	/**
	 * Adds the selected default category set for the analysis.
	 */
	public void addDefaultCategorySet() {
		FeedbackAnalysisCategorySetEntity sdc = findById(defaultFeedbackAnalysisCategorySets,
				selectedDefaultFeedbackAnalysisCategorySet);
		if (!feedbackAnalysisCategorySetsInUse.contains(sdc)) {
			feedbackAnalysisCategorySetsInUse.add(sdc);
		}
	}

	/**
	 * Adds the selected private category set for the analysis.
	 */
	public void addPrivateCategorySet() {
		FeedbackAnalysisCategorySetEntity spc = findById(privateFeedbackAnalysisCategorySets,
				selectedPrivateFeedbackAnalysisCategorySet);
		if (!feedbackAnalysisCategorySetsInUse.contains(spc)) {
			feedbackAnalysisCategorySetsInUse.add(spc);
		}
	}

	/**
	 * Removes the given categoryset from use
	 * 
	 * @param categorySet
	 *            the categoryset to be removed
	 */
	public void removeCategorySet(int categorySet) {
		feedbackAnalysisCategorySetsInUse.remove(categorySet);
	}

	/**
	 * Checks if the continue button should be disabled. The button is disabled if
	 * no category sets have been added for the observation or if some of the added
	 * category sets are empty.
	 * 
	 * @return True if the continue button should be disabled.
	 */
	public boolean isContinueDisabled() {
		for (FeedbackAnalysisCategorySetEntity categorySet : feedbackAnalysisCategorySetsInUse) {
			if (categorySet.getCategoryEntitys().isEmpty())
				return true;
		}
		return feedbackAnalysisCategorySetsInUse.isEmpty();
	}

	/**
	 * Shows given error message in primefaces message popup.
	 * 
	 * @param message
	 *            Error message to show.
	 */
	private void showErrorMessage(String message) {
		FacesContext context = FacesContext.getCurrentInstance();
		context.addMessage(null,
				new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("dialogErrorTitle"), message));
	}

	/**
	 * Checks the categories in use before letting the user continue to observation.
	 * The categories in the same category set should have different names. The
	 * categories shouldn't have empty names. At least one category should be
	 * selected for the observation. It shows an error message if the categories
	 * aren't ok.
	 * 
	 * @return "analysiscategoriesok" if the categories were ok, otherwise an empty
	 *         string.
	 */
	public String checkCategories() {
		boolean atLeastOneCategorySelected = false;

		for (FeedbackAnalysisCategorySetEntity categorySet : feedbackAnalysisCategorySetsInUse) {

			Map<Integer, AbstractCategoryEntity> categories = categorySet.getCategoryEntitys();

			if (hasDuplicate(categories)) {
				showErrorMessage(messages.getString("cs_errorNotUniqueCategories"));
				return "";
			}

			if (!categories.isEmpty()) {
				atLeastOneCategorySelected = true;
			} else {
				showErrorMessage(messages.getString("cs_warningEmptyCategorySets"));
				return "";
			}

			for (AbstractCategoryEntity category : categories.values()) {

				if (category.getLabel().getText().isEmpty()) {
					showErrorMessage(messages.getString("cs_warningEmptyCategories"));
					return "";
				}
			}
		}

		if (!atLeastOneCategorySelected) {
			showErrorMessage(messages.getString("cs_errorNoneSelected"));
			return "";
		}
		feedbackAnalysisManagedBean.setFeedbackAnalysisCategorySetsInUse(feedbackAnalysisCategorySetsInUse);
		feedbackAnalysisManagedBean.setFeedbackAnalysisEntity(null);
		feedbackAnalysisManagedBean.init();
		feedbackAnalysisManagedBean.setIsTimerEnabled(isTimerEnabled);

		return "analysiscategoriesok";
	}

	/**
	 * Checks if given categories contain duplicate names.
	 * 
	 * @param categories
	 *            List of categories to check.
	 * @return True if categories contain duplicates, otherwise false.
	 */
	private static boolean hasDuplicate(Map<Integer, AbstractCategoryEntity> categories) {
		Set<String> set = new HashSet<>();
		for (AbstractCategoryEntity category : categories.values()) {
			String name = category.getLabel().getText();
			if (!name.isEmpty() && !set.add(name)) {
				return true;
			}
		}
		return false;
	}

	private boolean isTimerEnabled;

	public boolean getIsTimerEnabled() {
		return isTimerEnabled;
	}

	public void setIsTimerEnabled(boolean timerEnabled) {
		isTimerEnabled = timerEnabled;
	}

	public String getNewFeedbackAnalysisCategorySetName() {
		return newFeedbackAnalysisCategorySetName;
	}

	public void setNewFeedbackAnalysisCategorySetName(String newFeedbackAnalysisCategorySetName) {
		this.newFeedbackAnalysisCategorySetName = newFeedbackAnalysisCategorySetName;
	}

	public long getSelectedDefaultFeedbackAnalysisCategorySet() {
		return selectedDefaultFeedbackAnalysisCategorySet;
	}

	public void setSelectedDefaultFeedbackAnalysisCategorySet(long selectedDefaultFeedbackAnalysisCategorySet) {
		this.selectedDefaultFeedbackAnalysisCategorySet = selectedDefaultFeedbackAnalysisCategorySet;
	}

	public long getSelectedPrivateFeedbackAnalysisCategorySet() {
		return selectedPrivateFeedbackAnalysisCategorySet;
	}

	public void setSelectedPrivateFeedbackAnalysisCategorySet(long selectedPrivateFeedbackAnalysisCategorySet) {
		this.selectedPrivateFeedbackAnalysisCategorySet = selectedPrivateFeedbackAnalysisCategorySet;
	}

	public List<FeedbackAnalysisCategorySetEntity> getDefaultFeedbackAnalysisCategorySets() {
		return defaultFeedbackAnalysisCategorySets;
	}

	public void setDefaultFeedbackAnalysisCategorySets(
			List<FeedbackAnalysisCategorySetEntity> defaultFeedbackAnalysisCategorySets) {
		this.defaultFeedbackAnalysisCategorySets = defaultFeedbackAnalysisCategorySets;
	}

	public List<FeedbackAnalysisCategorySetEntity> getPrivateFeedbackAnalysisCategorySets() {
		return privateFeedbackAnalysisCategorySets;
	}

	public void setPrivateFeedbackAnalysisCategorySets(
			List<FeedbackAnalysisCategorySetEntity> privateFeedbackAnalysisCategorySets) {
		this.privateFeedbackAnalysisCategorySets = privateFeedbackAnalysisCategorySets;
	}

	public List<FeedbackAnalysisCategorySetEntity> getFeedbackAnalysisCategorySetsInUse() {
		return feedbackAnalysisCategorySetsInUse;
	}

	public void setFeedbackAnalysisCategorySetsInUse(
			List<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySetsInUse) {
		this.feedbackAnalysisCategorySetsInUse = feedbackAnalysisCategorySetsInUse;
	}

	public EventGroupEntity getEventGroup() {
		return eventGroup;
	}

	public void setEventGroup(EventGroupEntity eventGroup) {
		this.eventGroup = eventGroup;
	}

	public FeedbackAnalysisManagedBean getFeedbackAnalysisManagedBean() {
		return feedbackAnalysisManagedBean;
	}

	public void setFeedbackAnalysisManagedBean(FeedbackAnalysisManagedBean feedbackAnalysisManagedBean) {
		this.feedbackAnalysisManagedBean = feedbackAnalysisManagedBean;
	}

}
