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
package com.moveatis.managedbeans;

import java.io.Serializable;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.ResourceBundle;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.category.CategoryEntity;
import com.moveatis.category.CategorySetEntity;
import com.moveatis.event.EventEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.helpers.Validation;
import com.moveatis.interfaces.EventGroup;
import com.moveatis.interfaces.MessageBundle;
import com.moveatis.interfaces.Session;
import com.moveatis.observation.ObservationCategory;
import com.moveatis.observation.ObservationCategorySet;
import com.moveatis.observation.ObservationCategorySetList;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The bean that serves the category selection view.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Ilari Paananen <ilari.k.paananen at student.jyu.fi>
 */
@Named(value = "categorySelectionBean")
@ViewScoped
public class CategorySelectionManagedBean implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(CategorySelectionManagedBean.class);
	private static final long serialVersionUID = 1L;

	private String newCategorySetName;

	private Long selectedDefaultCategorySet;
	private Long selectedPrivateCategorySet;

	private ObservationCategorySetList defaultCategorySets; // From group key or event that was selected in control
															// page.
	private ObservationCategorySetList privateCategorySets;
	private ObservationCategorySetList categorySetsInUse;

	private EventGroupEntity eventGroup;

	@Inject
	private Session sessionBean;
	@Inject
	private EventGroup eventGroupEJB;
	@Inject
	private ObservationManagedBean observationManagedBean;

	// TODO: Messages aren't updated to match language selection. Get ResourceBundle
	// some other way?
	@Inject
	@MessageBundle // created MessageBundle to allow resourcebundle injection to CDI beans
	private transient ResourceBundle messages; // RequestBundle is not serializable

	private Long addedCategorySetTag = 0L;

	/**
	 * Creates a new instance of CategorySelectionManagedBean.
	 */
	public CategorySelectionManagedBean() {
	}

	/**
	 * Adds all category sets from given event group to given observation category
	 * set list.
	 */
	private void addAllCategorySetsFromEventGroup(ObservationCategorySetList addTo, EventGroupEntity eventGroup) {

		Set<CategorySetEntity> categorySets = eventGroup.getCategorySets();

		if (categorySets == null)
			return;

		for (CategorySetEntity categorySetEntity : categorySets) {
			ObservationCategorySet categorySet = new ObservationCategorySet(categorySetEntity.getId(),
					categorySetEntity.getLabel());
			Map<Integer, AbstractCategoryEntity> categories = categorySetEntity.getCategoryEntitys();
			for (AbstractCategoryEntity category : categories.values()) {
				categorySet.add(((CategoryEntity) category).getCategoryType(), observationManagedBean.getNextTag(),
						category.getLabel().getText());
			}

			addTo.add(categorySet);
		}
	}

	/**
	 * Adds all category sets from all the given event groups and puts them in a
	 * category set list.
	 */
	private void addAllCategorySetsFromEventGroups(ObservationCategorySetList categorySets,
			List<EventGroupEntity> eventGroups) {
		for (EventGroupEntity eventGroup_ : eventGroups) {
			addAllCategorySetsFromEventGroup(categorySets, eventGroup_);
		}
	}

	/**
	 * Initializes properly all the members needed for category selection.
	 */
	@PostConstruct
	public void init() {
		eventGroup = null;
		defaultCategorySets = new ObservationCategorySetList();
		privateCategorySets = new ObservationCategorySetList();
		categorySetsInUse = new ObservationCategorySetList();

		if (observationManagedBean.getEventEntity() != null) {
			EventEntity event = observationManagedBean.getEventEntity();
			eventGroup = event.getEventGroup();
			addAllCategorySetsFromEventGroup(defaultCategorySets, eventGroup);
		}

		if (sessionBean.isIdentifiedUser()) {
			IdentifiedUserEntity user = sessionBean.getLoggedIdentifiedUser();
			addAllCategorySetsFromEventGroups(privateCategorySets, eventGroupEJB.findAllForOwner(user));
		}

		List<ObservationCategorySet> categorySets = sessionBean.getCategorySetsInUse();
		if (categorySets != null) {
			categorySetsInUse.setCategorySets(categorySets);
		} else {
			for (ObservationCategorySet categorySet : defaultCategorySets.getCategorySets()) {
				categorySetsInUse.addClone(categorySet);
			}
		}
	}

	/**
	 * Gets the new category set name.
	 */
	public String getNewCategorySetName() {
		return newCategorySetName;
	}

	/**
	 * Sets the new category set name.
	 */
	public void setNewCategorySetName(String newCategorySetName) {
		this.newCategorySetName = newCategorySetName;
	}

	/**
	 * Gets the selected default category set.
	 */
	public Long getSelectedDefaultCategorySet() {
		return selectedDefaultCategorySet;
	}

	/**
	 * Sets the selected default category set.
	 */
	public void setSelectedDefaultCategorySet(Long selectedDefaultCategorySet) {
		this.selectedDefaultCategorySet = selectedDefaultCategorySet;
	}

	/**
	 * Gets the selected private category set.
	 */
	public Long getSelectedPrivateCategorySet() {
		return selectedPrivateCategorySet;
	}

	/**
	 * Sets the selected private category set.
	 */
	public void setSelectedPrivateCategorySet(Long selectedPrivateCategorySet) {
		this.selectedPrivateCategorySet = selectedPrivateCategorySet;
	}

	/**
	 * Gets the default category sets.
	 */
	public List<ObservationCategorySet> getDefaultCategorySets() {
		return defaultCategorySets.getCategorySets();
	}

	/**
	 * Gets the private category sets.
	 */
	public List<ObservationCategorySet> getPrivateCategorySets() {
		return privateCategorySets.getCategorySets();
	}

	/**
	 * Gets the category sets in use.
	 */
	public List<ObservationCategorySet> getCategorySetsInUse() {
		return categorySetsInUse.getCategorySets();
	}

	/**
	 * Gets the event group.
	 */
	public EventGroupEntity getEventGroup() {
		return eventGroup;
	}

	/**
	 * Adds a new category set for the observation if newCategorySetName isn't
	 * empty.
	 */
	public void addNewCategorySet() {
		String name = Validation.validateForJsAndHtml(newCategorySetName);

		if (!name.isEmpty()) {
			// TODO: Is this wanted? What about default category sets?
			// Can they be added if there is already category with same name in use?
			for (ObservationCategorySet set : categorySetsInUse.getCategorySets()) {
				if (name.equals(set.getName())) {
					showErrorMessage(messages.getString("cs_errorNotUniqueCategorySet"));
					return;
				}
			}

			ObservationCategorySet categorySet = new ObservationCategorySet(addedCategorySetTag++, name);
			categorySetsInUse.add(categorySet);
			newCategorySetName = "";
		}
	}

	/**
	 * Adds the selected default category set for the observation.
	 */
	public void addDefaultCategorySet() {
		if (categorySetsInUse.find(selectedDefaultCategorySet) == null) {
			ObservationCategorySet categorySet = defaultCategorySets.find(selectedDefaultCategorySet);
			if (categorySet != null)
				categorySetsInUse.addClone(categorySet);
			else
				LOGGER.debug("Selected default category set not found!");
		}
	}

	/**
	 * Adds the selected private category set for the observation.
	 */
	public void addPrivateCategorySet() {
		if (categorySetsInUse.find(selectedPrivateCategorySet) == null) {
			ObservationCategorySet categorySet = privateCategorySets.find(selectedPrivateCategorySet);
			if (categorySet != null)
				categorySetsInUse.addClone(categorySet);
			else
				LOGGER.debug("Selected private category set not found!");
		}
	}

	/**
	 * Removes the given category set from the observation.
	 */
	public void removeCategorySet(ObservationCategorySet categorySet) {
		categorySetsInUse.remove(categorySet);
	}

	/**
	 * Checks if the continue button should be disabled. The button is disabled if
	 * no category sets have been added for the observation or if some of the added
	 * category sets are empty.
	 * 
	 * @return True if the continue button should be disabled.
	 */
	public boolean isContinueDisabled() {
		for (ObservationCategorySet categorySet : categorySetsInUse.getCategorySets()) {
			if (categorySet.getCategories().isEmpty())
				return true;
		}
		return categorySetsInUse.getCategorySets().isEmpty();
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
	 * @return "categoriesok" if the categories were ok, otherwise an empty string.
	 */
	public String checkCategories() {
		boolean atLeastOneCategorySelected = false;

		for (ObservationCategorySet categorySet : categorySetsInUse.getCategorySets()) {

			List<ObservationCategory> categories = categorySet.getCategories();

			if (hasDuplicate(categories)) {
				showErrorMessage(messages.getString("cs_errorNotUniqueCategories"));
				return "";
			}

			if (!categories.isEmpty()) {
				atLeastOneCategorySelected = true;
			} else {
				showErrorMessage(messages.getString("cs_warningEmptyCategorySets"));
				return ""; // TODO: Show confirmation or something and let user continue.
			}

			for (ObservationCategory category : categories) {

				if (category.getName().isEmpty()) {
					showErrorMessage(messages.getString("cs_warningEmptyCategories"));
					return ""; // TODO: Show confirmation or something and let user continue.
				}
			}
		}

		if (!atLeastOneCategorySelected) {
			showErrorMessage(messages.getString("cs_errorNoneSelected"));
			return "";
		}

		observationManagedBean.setCategorySetsInUse(categorySetsInUse.getCategorySets());
		return "categoriesok";
	}

	/**
	 * Checks if given categories contain duplicate names.
	 * 
	 * @param categories
	 *            List of categories to check.
	 * @return True if categories contain duplicates, otherwise false.
	 */
	private static boolean hasDuplicate(List<ObservationCategory> categories) {
		Set<String> set = new HashSet<>();
		for (ObservationCategory category : categories) {
			String name = category.getName();
			if (!name.isEmpty() && !set.add(category.getName())) {
				return true;
			}
		}
		return false;
	}
}
