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
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.ResourceBundle;
import java.util.Set;
import java.util.TreeSet;
import java.util.concurrent.TimeUnit;

import javax.annotation.PostConstruct;
import javax.faces.application.FacesMessage;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.faces.view.ViewScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.primefaces.event.ReorderEvent;
import org.primefaces.event.RowEditEvent;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.abstracts.AbstractCategorySetEntity;
import com.moveatis.abstracts.AbstractObservationEntity;
import com.moveatis.category.CategoryEntity;
import com.moveatis.category.CategorySetEntity;
import com.moveatis.category.CategoryType;
import com.moveatis.event.EventEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.interfaces.Category;
import com.moveatis.interfaces.CategorySet;
import com.moveatis.interfaces.Event;
import com.moveatis.interfaces.EventGroup;
import com.moveatis.interfaces.FeedbackAnalysis;
import com.moveatis.interfaces.MessageBundle;
import com.moveatis.interfaces.Observation;
import com.moveatis.interfaces.Session;
import com.moveatis.label.LabelEntity;
import com.moveatis.observation.ObservationEntity;
import com.moveatis.user.AbstractUser;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The bean class for managing the control page view.
 *
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Juha Moisio <juha.pa.moisio at student.jyu.fi>
 */
@Named(value = "controlBean")
@ViewScoped
public class ControlManagedBean implements Serializable {

	private static final long serialVersionUID = 1L;

	// private static final Logger LOGGER =
	// LoggerFactory.getLogger(ControlManagedBean.class);

	private List<EventGroupEntity> eventGroups;

	private List<AbstractCategoryEntity> categories;

	private List<ObservationEntity> otherObservations;

	private List<FeedbackAnalysisEntity> otherAnalyses;

	private EventGroupEntity selectedEventGroup;

	private AbstractCategorySetEntity selectedCategorySet;

	private AbstractCategoryEntity selectedCategory;

	private ObservationEntity selectedObservation;

	private FeedbackAnalysisEntity selectedAnalysis;

	private boolean creatingNewEventGroup = false;

	@Inject
	private EventGroup eventGroupEJB;

	@Inject
	private Event eventEJB;

	@Inject
	private CategorySet categorySetEJB;

	@Inject
	private Category categoryEJB;

	@Inject
	private Observation observationEJB;

	@Inject
	private FeedbackAnalysis feedbackAnalysisEJB;

	@Inject
	private CategorySetManagedBean categorySetBean;

	@Inject
	private EventGroupManagedBean eventGroupBean;

	@Inject
	private Session sessionBean;

	@Inject
	private ObservationManagedBean observationBean;

	@Inject
	private FeedbackAnalysisManagedBean feedbackAnalysisManagedBean;

	@Inject
	@MessageBundle
	private transient ResourceBundle messages;

	private AbstractUser user;

	public List<FeedbackAnalysisEntity> getOtherAnalyses() {
		return otherAnalyses;
	}

	public void setOtherAnalyses(List<FeedbackAnalysisEntity> otherAnalyses) {
		this.otherAnalyses = otherAnalyses;
	}

	/**
	 * Initializes the bean appropriately.
	 */
	@PostConstruct
	public void init() {
		user = sessionBean.getLoggedIdentifiedUser();
		ResourceBundle.clearCache();
		fetchEventGroups();
		fetchOtherObservations();
		fetchOtherAnalyses();
	}

	/**
	 * Fetches the event groups of the user.
	 */
	protected void fetchEventGroups() {
		eventGroups = eventGroupEJB.findAllForOwner(user);
		// Re order event groups by created date
		Collections.sort(eventGroups, new Comparator<EventGroupEntity>() {
			@Override
			public int compare(EventGroupEntity one, EventGroupEntity other) {
				return one.getCreated().compareTo(other.getCreated());
			}
		});
	}

	/**
	 * Fetches the other observations of the user.
	 */
	private void fetchOtherObservations() {
		otherObservations = observationEJB.findWithoutEvent(user);
		otherObservations.addAll(observationEJB.findByEventsNotOwned(user));
	}

	/**
	 * Gets the observations of the event group.
	 */
	public Set<ObservationEntity> getObservations(EventGroupEntity eventGroup) {
		if (eventGroup != null && eventGroup.getEvent() != null) {
			return eventGroup.getEvent().getObservations();
		}
		return new TreeSet<>();
	}

	/**
	 * Gets the analyses for the given eventgroup
	 * 
	 * @param eventGroup
	 *            the eventgroup of which the analyses are being accessed
	 * @return the analyses of the given eventgroup
	 */
	public Set<FeedbackAnalysisEntity> getAnalyses(EventGroupEntity eventGroup) {
		if (eventGroup != null && eventGroup.getEvent() != null) {
			return eventGroup.getEvent().getAnalyses();
		}
		return new TreeSet<>();
	}

	/**
	 * Fetches the analyses of the user not connected to an event or connected to an
	 * event that's been accessed through a group key.
	 */
	private void fetchOtherAnalyses() {
		otherAnalyses = feedbackAnalysisEJB.findWithoutEvent(user);
		otherAnalyses.addAll(feedbackAnalysisEJB.findByEventsNotOwned(user));
	}

	/**
	 * Returns true if a new event group is being created.
	 */
	public boolean isCreatingNewEventGroup() {
		return creatingNewEventGroup;
	}

	/**
	 * Sets whether a new event group is being created or not.
	 */
	public void setCreatingNewEventGroup(boolean creatingNewEventGroup) {
		this.creatingNewEventGroup = creatingNewEventGroup;
	}

	/**
	 * Checks if the event group entity has a group key.
	 */
	public boolean hasGroupKey(EventGroupEntity eventGroup) {
		return eventGroup != null && eventGroup.getGroupKey() != null;
	}

	/**
	 * Adds a new category set in the view.
	 */
	public void addNewCategorySet() {
		selectedCategorySet = new CategorySetEntity();
		categories = new ArrayList<>();
	}

	/**
	 * Adds a new feedbackanalysiscategory set in the view.
	 */
	public void addNewFeedbackAnalysisCategorySet() {
		selectedCategorySet = new FeedbackAnalysisCategorySetEntity();
		categories = new ArrayList<>();
	}

	/**
	 * Adds a new category in the view.
	 */
	public void addNewCategory() {
		CategoryEntity category = new CategoryEntity();
		LabelEntity label = new LabelEntity();
		label.setText("");
		category.setOrderNumber(categories.size());
		category.setLabel(label);

		category.setCategoryType(CategoryType.TIMED);
		categories.add(category);
		selectedCategory = category;
	}

	/**
	 * Adds a new feedback analysis category in the view.
	 */
	public void addNewFeedbackAnalysisCategory() {
		FeedbackAnalysisCategoryEntity category = new FeedbackAnalysisCategoryEntity();
		LabelEntity label = new LabelEntity();
		label.setText("");
		category.setOrderNumber(categories.size());
		category.setLabel(label);

		categories.add(category);
		selectedCategory = category;
	}

	/**
	 * Listener for event group row edit.
	 */
	public void onEditEventGroup(RowEditEvent event) {
		EventGroupEntity eventGroup = (EventGroupEntity) event.getObject();
		eventGroupEJB.edit(eventGroup);
	}

	/**
	 * Gets the event groups.
	 */
	public List<EventGroupEntity> getEventGroups() {
		return eventGroups;
	}

	/**
	 * Sets the event groups.
	 */
	public void setEventGroups(List<EventGroupEntity> eventGroups) {
		this.eventGroups = eventGroups;
	}

	/**
	 * Gets the selected event group.
	 */
	public EventGroupEntity getSelectedEventGroup() {
		return selectedEventGroup;
	}

	/**
	 * Sets the selected event group.
	 */
	public void setSelectedEventGroup(EventGroupEntity selectedEventGroup) {
		this.selectedEventGroup = selectedEventGroup;
	}

	/**
	 * Gets the categories.
	 */
	public List<AbstractCategoryEntity> getCategories() {
		return categories;
	}

	/**
	 * Sets the categories.
	 */
	public void setCategories(List<AbstractCategoryEntity> categories) {
		this.categories = categories;
	}

	/**
	 * Gets the selected category set.
	 */
	public AbstractCategorySetEntity getSelectedCategorySet() {
		return selectedCategorySet;
	}

	/**
	 * Sets the selected category set.
	 */
	public void setSelectedCategorySet(AbstractCategorySetEntity selectedCategorySet) {
		this.selectedCategorySet = selectedCategorySet;
		this.selectedEventGroup = this.selectedCategorySet.getEventGroupEntity();
		categories = new ArrayList<>(selectedCategorySet.getCategoryEntitys().values());
	}

	/**
	 * Gets the selected category.
	 */
	public AbstractCategoryEntity getSelectedCategory() {
		return selectedCategory;
	}

	/**
	 * Sets the selected category.
	 */
	public void setSelectedCategory(AbstractCategoryEntity selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	/**
	 * Gets the selected observation.
	 */
	public ObservationEntity getSelectedObservation() {
		return selectedObservation;
	}

	/**
	 * Sets the selected observation.
	 */
	public void setSelectedObservation(ObservationEntity selectedObservation) {
		this.selectedObservation = selectedObservation;
	}

	public FeedbackAnalysisEntity getSelectedAnalysis() {
		return selectedAnalysis;
	}

	public void setSelectedAnalysis(FeedbackAnalysisEntity selectedAnalysis) {
		this.selectedAnalysis = selectedAnalysis;
	}

	/**
	 * Gets the other observations.
	 */
	public List<ObservationEntity> getOtherObservations() {
		return otherObservations;
	}

	/**
	 * Sets the other observations.
	 */
	public void setOtherObservations(List<ObservationEntity> otherObservations) {
		this.otherObservations = otherObservations;
	}

	/**
	 * Gets the category types.
	 */
	public CategoryType[] getCategoryTypes() {
		return CategoryType.values();
	}

	/**
	 * Gets the name of the observer of the selected observation entity.
	 */
	public String getObserverName() {
		if (selectedObservation == null) {
			return "";
		} else if (selectedObservation.getObserver() instanceof IdentifiedUserEntity) {
			return ((IdentifiedUserEntity) selectedObservation.getObserver()).getGivenName();
		} else {
			return messages.getString("con_publicUser");
		}
	}

	public String getAnalyzerName() {
		if (selectedAnalysis == null) {
			return "";
		} else if (selectedAnalysis.getObserver() instanceof IdentifiedUserEntity) {
			return ((IdentifiedUserEntity) selectedAnalysis.getObserver()).getGivenName();
		} else {
			return messages.getString("con_publicUser");
		}
	}

	/**
	 * Initializes a new observation and redirects to the category selection view.
	 *
	 * @return The navigation rule string that redirects to the category selection
	 *         view.
	 */
	public String newObservation() {
		observationBean.setEventEntity(selectedEventGroup.getEvent());
		// Make sure we don't modify earlier categories.
		observationBean.resetCategorySetsInUse();
		return "newobservation";
	}

	/**
	 * Initializes a new analysis and redirects to the category selection view.
	 *
	 * @return The navigation rule string that redirects to the category selection
	 *         view.
	 */
	public String newAnalysis() {
		feedbackAnalysisManagedBean.setEventEntity(selectedEventGroup.getEvent());
		// Make sure we don't modify earlier categories.
		feedbackAnalysisManagedBean.resetCategorySetsInUse();
		return "newanalysis";
	}

	/**
	 * Removes the event group from the database.
	 */
	public void removeEventGroup(EventGroupEntity eventGroup) {
		if (eventGroup != null) {
			// remove group key first
			if (eventGroup.getGroupKey() != null) {
				eventGroupBean.removeGroupKey(eventGroup);
			}
			eventGroupEJB.remove(eventGroup);
			eventGroups.remove(eventGroup);
		}
		setSelectedEventGroup(null);
	}

	/**
	 * Removes the selected category set from the database.
	 */
	public void removeCategorySet() {
		if (selectedCategorySet != null) {
			categorySetEJB.remove(selectedCategorySet);
			selectedCategorySet = null;
			selectedCategory = null;
			// refetch eventgroups, maybe other way to update it?
			fetchEventGroups();
		}
	}

	/**
	 * Removes the selected category from the view, reorders the categories and
	 * selects a new category.
	 */
	public void removeCategory() {
		if (selectedCategory != null) {
			int index = selectedCategory.getOrderNumber();
			categories.remove(index);
			int i = 0;
			for (AbstractCategoryEntity category : categories) {
				category.setOrderNumber(i);
				i++;
			}
			if (categories.isEmpty()) {
				selectedCategory = null;
			} else if (index > 0) {
				selectedCategory = categories.get(index - 1);
			} else {
				selectedCategory = categories.get(0);
			}
		}
	}

	/**
	 * Removes the selected observation from the database.
	 */
	public void removeObservation() {
		if (selectedObservation != null) {
			observationEJB.remove(selectedObservation);
			selectedObservation = null;
			fetchEventGroups();
			fetchOtherObservations();
		}
	}

	/**
	 * removes the currently selected analysis
	 */
	public void removeAnalysis() {
		if (selectedAnalysis != null) {
			feedbackAnalysisEJB.remove(selectedAnalysis);
			selectedAnalysis = null;
			fetchEventGroups();
			fetchOtherAnalyses();
		}
	}

	/**
	 * ReorderEvent listener for categories reorder.
	 */
	public void onCategoryReorder(ReorderEvent event) {
		int i = 0;
		for (AbstractCategoryEntity category : categories) {
			category.setOrderNumber(i);
			i++;
		}
	}

	/**
	 * Listener for observation editing.
	 */
	public void onEditObservation() {
		if (selectedObservation != null) {
			observationEJB.edit(selectedObservation);
		}
	}

	/**
	 * Listener for analysis editing
	 */
	public void onEditAnalysis() {
		if (selectedObservation != null) {
			feedbackAnalysisEJB.edit(selectedAnalysis);
		}
	}

	/**
	 * Adds a new event group to the event groups.
	 */
	public void addEventGroup(EventGroupEntity eventGroup) {
		eventGroups.add(eventGroup);
	}

	/**
	 * Validates and saves the selected categoryset.
	 * Validation includes checking for invalid characters and duplicate categories and categorysets
	 */
	public void saveCategorySet() {
		if (selectedEventGroup != null && selectedCategorySet != null) {
			String error;
			if (!categoryHasDuplicate() && !categorySetHasDuplicate()&& getCategoryError().isEmpty()) {
				categorySetBean.createAndEditCategorySet(selectedEventGroup, selectedCategorySet, categories);
				fetchEventGroups();
				return;
			} else if (categoryHasDuplicate()) {
				error = messages.getString("facs_errorNotUniqueCategories");
			} 
			else if(categorySetHasDuplicate()){
				error = messages.getString("cs_errorNotUniqueCategorySet");
			}else {
				error=getCategoryError();
			}
			FacesContext.getCurrentInstance().validationFailed();
			FacesContext.getCurrentInstance().addMessage(null,
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("dialogErrorTitle"), error));
		}
	}

	/**
	 * Sets the analysis for feedbackanalysismanagedbean and shows it in summary
	 * view
	 * 
	 * @return navigation rule string to navigate to the summary page
	 */
	public String showAnalysisInSummaryPage() {
		feedbackAnalysisManagedBean.setFeedbackAnalysisEntity(selectedAnalysis);
		feedbackAnalysisManagedBean.setIsTimerEnabled(selectedAnalysis.getDuration() > 0);
		feedbackAnalysisManagedBean
				.setFeedbackAnalysisCategorySetsInUse(selectedAnalysis.getFeedbackAnalysisCategorySets());
		feedbackAnalysisManagedBean.init();
		return "feedbackanalysissummary";
	}

	@Inject
	private ValidationManagedBean validationManagedBean;
	
	/**
	 * Shows the selected observation in the summary page.
	 *
	 * @return The navigation rule string that redirects to the summary page
	 */
	public String showObservationInSummaryPage() {
		observationBean.setObservationEntity(selectedObservation);
		observationBean.setCategorySetsInUse(new ArrayList<>(selectedObservation.getObservationCategorySets()));
		return "summary";
	}

	/**
	 * Checks if the categories have too long names, or invalid characters and returns the error message accordingly
	 * if no problems are present returns an empty string
	 * 
	 * @return
	 */
	public String getCategoryError() {
		for  (AbstractCategoryEntity categoryEntity : categories) {
			String categoryText = categoryEntity.getLabel().getText();
			try{
				validationManagedBean.validateShortString(null, null, categoryText);
				}catch(ValidatorException e) {
					return categoryText+": "+ e.getFacesMessage().getDetail() ;
				};
		}
		return "";
	}
	
	/**
	 * Checks if the categories have duplicates.
	 */
	private boolean categoryHasDuplicate() {
		Set<String> duplicates = new HashSet<>();
		for (AbstractCategoryEntity categoryEntity : categories) {
			String categoryText = categoryEntity.getLabel().getText();
			if ( !duplicates.add(categoryText)) {
				selectedCategory = categoryEntity;
				return true;
			}
		}
		return false;
	}

	/**
	 * Checks if the categoryset has a duplicate within the eventgroup being edited.
	 * 
	 * @return Whether the categoryset has a duplicate
	 */
	private boolean categorySetHasDuplicate() {
		boolean hasDuplicate = false;
		if (selectedCategorySet instanceof FeedbackAnalysisCategorySetEntity) {
			Set<FeedbackAnalysisCategorySetEntity> sets = selectedEventGroup.getFeedbackAnalysisCategorySets();
			if (sets == null)
				return false;
			for (AbstractCategorySetEntity catset : sets)
				if (!catset.getId().equals(selectedCategorySet.getId())
						&& catset.getLabel().contentEquals(selectedCategorySet.getLabel())) {
					hasDuplicate = true;
					break;
				}
		} else {
			Set<CategorySetEntity> sets = selectedEventGroup.getCategorySets();
			if (sets == null)
				return false;
			for (AbstractCategorySetEntity catset : sets)
				if (!catset.getId().equals(selectedCategorySet.getId())
						&& catset.getLabel().contentEquals(selectedCategorySet.getLabel())) {
					hasDuplicate = true;
					break;
				}
		}
		return hasDuplicate;
	}

	/**
	 * Converts milliseconds to string with time units h, m, s.
	 *
	 * @param ms
	 *            The time to be converted in milliseconds.
	 * @return String of the converted time units.
	 */
	public String msToUnits(long ms) {
		if (ms <= 0) {
			return "0 s";
		}
		if (ms < 1000) {
			return "~1 s";
		}
		String hms = String.format("%d h %d m %d s", TimeUnit.MILLISECONDS.toHours(ms),
				TimeUnit.MILLISECONDS.toMinutes(ms) % TimeUnit.HOURS.toMinutes(1),
				TimeUnit.MILLISECONDS.toSeconds(ms) % TimeUnit.MINUTES.toSeconds(1));
		hms = hms.replaceFirst("0 h ", "");
		hms = hms.replaceFirst("0 m ", "");
		return hms;
	}

	/**
	 * Gets the name of the event group of the observation or analysis.
	 */
	public String getAbstractObservationEventGroupName(AbstractObservationEntity observationEntity) {
		EventEntity eventEntity = observationEntity.getEvent();
		if (eventEntity != null && eventEntity.getEventGroup() != null) {
			return eventEntity.getEventGroup().getLabel();
		}
		return "";
	}

	public String redirectObservation() {
		return "observationselected";
	}

	/**
	 * Starts a new analysis in the selected event
	 * 
	 * @return navigation rule string to redirect to the categoryselection page
	 */
	public String redirectAnalyze() {
		return "analysisselected";
	}
}
