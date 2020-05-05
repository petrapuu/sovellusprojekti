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
import java.text.DateFormat;
import java.text.MessageFormat;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Locale;
import java.util.ResourceBundle;

import javax.ejb.EJB;
import javax.enterprise.context.SessionScoped;
import javax.faces.application.FacesMessage;
import javax.faces.component.UIComponent;
import javax.faces.context.FacesContext;
import javax.faces.validator.ValidatorException;
import javax.inject.Inject;
import javax.inject.Named;

import org.jboss.logging.annotations.Message;
import org.primefaces.context.RequestContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.abstracts.AbstractCategoryEntity;
import com.moveatis.event.EventEntity;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategoryEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.interfaces.CategorySet;
import com.moveatis.interfaces.FeedbackAnalysisRecord;
import com.moveatis.interfaces.FeedbackAnalysis;
import com.moveatis.interfaces.Label;
import com.moveatis.interfaces.Session;
import com.moveatis.label.LabelEntity;
import com.moveatis.records.FeedbackAnalysisRecordEntity;

/**
 * The managed bean controlling the feedbackanalysis in view TODO: extract the
 * methods concerning only a certain view to new managed beans controlling said
 * views (mostly analyzer, but some recordtable and summary functionalities too)
 * 
 * @author Visa Nykänen
 * 
 */
@Named(value = "feedbackAnalysisManagedBean")
@SessionScoped
public class FeedbackAnalysisManagedBean implements Serializable {

	private static final Logger LOGGER = LoggerFactory.getLogger(ObservationManagedBean.class);

	private static final long serialVersionUID = 1L;

	@Inject
	private Label labelEJB;

	/**
	 * The feedbackanalysisentity being edited
	 */
	private FeedbackAnalysisEntity feedbackAnalysisEntity;

	/**
	 * The categorysets being used in the analysis event
	 */
	private List<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySetsInUse;

	/**
	 * The index(+1) of the record currently in view
	 */

	private int currentRecordNumber;

	/**
	 * If the analysis has some new categorysets they need to be saved, so
	 * CategorySetBean is needed
	 */
	@Inject
	private CategorySet categorySetEJB;

	/**
	 * The record currently in view
	 */
	private FeedbackAnalysisRecordEntity currentRecord;

	/**
	 * The event the analysis is performed for
	 */
	private EventEntity eventEntity;

	/**
	 * used to save the analysis to the database
	 */
	@EJB
	private FeedbackAnalysis feedbackAnalysisEJB;

	/**
	 * The categorysets are gotten from the session
	 */
	@Inject
	private Session sessionBean;

	private FeedbackAnalysisCategoryEntity selectedCategory;

	/**
	 * If new records are added to the analysis after it has already been saved to
	 * the database the records need to be saved individually so
	 * feedbackanalysisrecordbean is needed
	 */
	@Inject
	private FeedbackAnalysisRecord feedbackAnalysisRecordEJB;

	/**
	 * The timer value, set to be the duration of the analysis once navigating to
	 * the record table
	 */
	private long duration;

	/**
	 * Whether the timer is stopped
	 */
	private boolean isTimerStopped;

	private boolean isTimerEnabled;

	@Inject
	private UserManagedBean userManagedBean;

	private byte[] pieImage, tableImage, barImage, reportImage;

	private String reportCSV;

	public void setIsTimerEnabled(boolean timerEnabled) {
		this.isTimerEnabled = timerEnabled;
	}

	public boolean getIsTimerEnabled() {
		return isTimerEnabled;
	}

	public void setBarImage(byte[] img) {
		barImage = img;
	}

	public byte[] getBarImage() {
		return barImage;
	}

	public void setTableImage(byte[] img) {
		tableImage = img;
	}

	public byte[] getTableImage() {
		return tableImage;
	}

	public void setPieImage(byte[] img) {
		pieImage = img;
	}

	public byte[] getPieImage() {
		return pieImage;
	}

	public void setReportCSV(String data) {
		reportCSV = data;
	}

	public String getReportCSV() {
		return reportCSV;
	}

	public void setReportImage(byte[] img) {
		this.reportImage = img;
	}

	public byte[] getReportImage() {
		return reportImage;
	}

	public void setEventEntity(EventEntity eventEntity) {
		this.eventEntity = eventEntity;
	}

	public EventEntity getEventEntity() {
		return this.eventEntity;
	}

	public FeedbackAnalysisEntity getFeedbackAnalysisEntity() {
		return feedbackAnalysisEntity;
	}

	public void setFeedbackAnalysisEntity(FeedbackAnalysisEntity feedbackAnalysisEntity) {
		this.feedbackAnalysisEntity = feedbackAnalysisEntity;
	}

	public void setFeedbackAnalysisName(String name) {
		this.feedbackAnalysisEntity.setAnalysisName(name);
	}

	public void setFeedbackAnalysisDuration(long duration) {
		this.feedbackAnalysisEntity.setDuration(duration);
	}

	public int getCurrentRecordNumber() {
		return currentRecordNumber;
	}

	public void setCurrentRecordNumber(int currentRecordNumber) {
		this.currentRecordNumber = currentRecordNumber;
	}

	public boolean getIsTimerStopped() {
		return isTimerStopped;
	}

	public void setIsTimerStopped(boolean timerStopped) {
		this.isTimerStopped = timerStopped;
	}

	public void pauseContinue() {
		isTimerStopped = !isTimerStopped;
	}

	public String getDurationAsString() {
		return getLongAsTimeStamp(duration);
	}

	public void setDuration(long duration) {
		this.duration = duration;
	}

	public void setSelectedCategory(FeedbackAnalysisCategoryEntity selectedCategory) {
		this.selectedCategory = selectedCategory;
	}

	public FeedbackAnalysisCategoryEntity getSelectedCategory() {
		return selectedCategory;
	}

	public FeedbackAnalysisRecordEntity getCurrentRecord() {
		return currentRecord;
	}

	public List<FeedbackAnalysisCategorySetEntity> getFeedbackAnalysisCategorySetsInUse() {
		return feedbackAnalysisCategorySetsInUse;
	}

	public void setFeedbackAnalysisCategorySetsInUse(
			List<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySetsInUse) {
		this.feedbackAnalysisCategorySetsInUse = feedbackAnalysisCategorySetsInUse;
	}

	/**
	 * Initializes all the necessary information for the analysis
	 */
	public void init() {
		if (feedbackAnalysisEntity == null) {
			Locale locale = userManagedBean.getLocale();
			ResourceBundle messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
			currentRecordNumber = 1;
			feedbackAnalysisEntity = new FeedbackAnalysisEntity();
			feedbackAnalysisEntity.setCreated();

			DateFormat dateFormat = DateFormat.getDateTimeInstance(DateFormat.SHORT, DateFormat.SHORT, locale);
			dateFormat.setTimeZone(sessionBean.getSessionTimeZone());

			feedbackAnalysisEntity.setAnalysisName(messages.getString("asum_anatitle") + " - "
					+ dateFormat.format(feedbackAnalysisEntity.getCreated()));
			feedbackAnalysisEntity.setRecords(new ArrayList<FeedbackAnalysisRecordEntity>());
			currentRecord = new FeedbackAnalysisRecordEntity();
			setOrderNumberForRecord();
			if (feedbackAnalysisCategorySetsInUse != null)
				for (FeedbackAnalysisCategorySetEntity facs : feedbackAnalysisCategorySetsInUse)
					for (AbstractCategoryEntity fac : facs.getCategoryEntitys().values())
						((FeedbackAnalysisCategoryEntity) fac).setInRecord(false);
			currentRecord.setFeedbackAnalysis(feedbackAnalysisEntity);
			feedbackAnalysisEntity.addRecord(currentRecord);
		} else
			setCurrentRecord(feedbackAnalysisEntity.getRecords().size());
		isTimerStopped = true;
		duration = feedbackAnalysisEntity.getDuration();
	}

	/**
	 * Resets the start time selected categories and the comment for the record
	 * currently in view
	 */
	public void resetCurrentRecord() {
		currentRecord.setComment(null);
		currentRecord.setStartTime(null);
		resetSelectedCategories();
		editRecord();
	}

	/**
	 * Finds the next record after the currently viewed one with timestamp set and
	 * returns its timestamp. If no such record exists returns currently elapsed
	 * time.
	 * 
	 * @return The maximum value for the currently shown records timestamp
	 */
	public long getMaxTimeStampForCurrentRecord() {
		if (currentRecordNumber == feedbackAnalysisEntity.getRecords().size())
			return duration;
		for (int i = currentRecordNumber + 1; i <= feedbackAnalysisEntity.getRecords().size(); i++) {
			Long start = findRecordByOrderNumber(i).getStartTime();
			if (start != null && start > 0)
				return start;
		}
		return duration;
	}

	/**
	 * Finds the previous record before the currently viewed one with timestamp set
	 * and returns its timestamp. If no such record exists returns 0.
	 * 
	 * @return The minimum value for the currently shown records timestamp
	 */
	public long getMinTimeStampForCurrentRecord() {
		if (currentRecordNumber == 1)
			return 0;
		for (int i = currentRecordNumber - 1; i >= 1; i--) {
			Long start = findRecordByOrderNumber(i).getStartTime();
			if (start != null && start > 0)
				return start;
		}
		return 0;
	}

	/**
	 * Returns the given number of seconds in a string showing the minutes and
	 * seconds
	 * 
	 * @param seconds
	 *            the value as seconds
	 * @return the timestamp as the amount of minutes and seconds in a string
	 */
	public String getLongAsTimeStamp(long seconds) {
		if (seconds == 0)
			return "--:--";
		return String.format("%02d:%02d", (seconds / 60), (seconds % 60));

	}

	/**
	 * increments the timer value every second if the timer is running
	 */
	public void increment() {
		if (!isTimerStopped)
			duration += 1;
	}

	/**
	 * Sets the currently shown record to be the record given in the parameter
	 * 
	 * @param currentRecord
	 *            the record to be shown
	 */
	public void setCurrentRecord(FeedbackAnalysisRecordEntity currentRecord) {
		currentRecordNumber = currentRecord.getOrderNumber();
		for (FeedbackAnalysisCategorySetEntity facs : feedbackAnalysisCategorySetsInUse)
			for (AbstractCategoryEntity fac : facs.getCategoryEntitys().values())
				((FeedbackAnalysisCategoryEntity) fac).setInRecord(false);

		List<FeedbackAnalysisCategoryEntity> selectedCategories = currentRecord.getSelectedCategories();
		for (FeedbackAnalysisCategoryEntity category : selectedCategories)
			category.setInRecord(true);
		this.currentRecord = currentRecord;
	}

	/**
	 * Sets the starttime of the currently viewed record based on the timer value if
	 * the record isn't in between other records and its starttime hasn't already
	 * been set
	 */
	public void setTimeStamp() {
		if (currentRecord.getStartTime() == null && currentRecordNumber == feedbackAnalysisEntity.getRecords().size())
			currentRecord.setStartTime(duration);
		editRecord();
	}

	/**
	 * Finds the record in the feedbackanalysis based on its ordernumber
	 * 
	 * @param orderNumber
	 *            the ordernumber of the record to be accessed
	 * @return the record with the given ordernumber
	 */
	private FeedbackAnalysisRecordEntity findRecordByOrderNumber(Integer orderNumber) {
		List<FeedbackAnalysisRecordEntity> records = feedbackAnalysisEntity.getRecords();
		for (FeedbackAnalysisRecordEntity record : records)
			if (record.getOrderNumber() == orderNumber)
				return record;
		return new FeedbackAnalysisRecordEntity();
	}

	/**
	 * Sets the ordernumber for the currently edited record If the record is added
	 * between records, sets the following records ordernumbers to be one higher
	 */
	private void setOrderNumberForRecord() {
		for (int i = feedbackAnalysisEntity.getRecords().size(); i >= currentRecordNumber; i--)
			findRecordByOrderNumber(i).setOrderNumber(i + 1);
		currentRecord.setOrderNumber(currentRecordNumber);
	}

	/**
	 * Saves the changes made to the current record if it exists, then sets record
	 * to be shown in the view based on the given ordernumber
	 * 
	 * @param recordNumber
	 *            The ordernumber of the record to be accessed
	 */
	public void setCurrentRecord(int recordNumber) {
		if (currentRecord != null)
			editRecord();
		resetSelectedCategories();
		currentRecordNumber = recordNumber;
		currentRecord = findRecordByOrderNumber(recordNumber);
		List<FeedbackAnalysisCategoryEntity> selectedCategories = currentRecord.getSelectedCategories();
		for (FeedbackAnalysisCategorySetEntity facs : feedbackAnalysisCategorySetsInUse)
			for (AbstractCategoryEntity fac : facs.getCategoryEntitys().values()) {
				((FeedbackAnalysisCategoryEntity) fac).setInRecord(false);
				for (FeedbackAnalysisCategoryEntity category : selectedCategories)
					if (category.getCategorySet().getLabel().contentEquals(fac.getCategorySet().getLabel())
							&& category.getLabel().getText().contentEquals(fac.getLabel().getText())) {
						((FeedbackAnalysisCategoryEntity) fac).setInRecord(true);
						break;
					}

			}

	}

	/**
	 * Tells whether there are records before or after the one currently shown based
	 * on the parameter.
	 * 
	 * @param isLeft
	 *            whether to check before or after the current record
	 * @return whether there are records before or after the one currently shown
	 */
	public boolean isNavigationDisabled(boolean isLeft) {
		if (isLeft)
			return currentRecordNumber == 1;
		else
			return currentRecordNumber == feedbackAnalysisEntity.getRecords().size();
	}

	/**
	 * saves the changes to the record currently in view and initializes a new
	 * record
	 */
	public void addRecord() {
		if (feedbackAnalysisEntity == null)
			feedbackAnalysisEntity = new FeedbackAnalysisEntity();

		editRecord();
		resetSelectedCategories();

		currentRecord = new FeedbackAnalysisRecordEntity();
		currentRecord.setSelectedCategories(new ArrayList<FeedbackAnalysisCategoryEntity>());
		currentRecordNumber++;
		setOrderNumberForRecord();
		currentRecord.setFeedbackAnalysis(feedbackAnalysisEntity);
		feedbackAnalysisEntity.addRecord(currentRecord);
	}

	/**
	 * Resets the selected categories to not be selected
	 */
	private void resetSelectedCategories() {
		for (FeedbackAnalysisCategorySetEntity facs : feedbackAnalysisCategorySetsInUse)
			for (AbstractCategoryEntity fac : facs.getCategoryEntitys().values())
				if (((FeedbackAnalysisCategoryEntity) fac).getInRecord()) {
					((FeedbackAnalysisCategoryEntity) fac).setInRecord(false);
				}
	}

	/**
	 * Saves the changes made to the categories in the currently shown record
	 */
	private void editRecord() {
		List<FeedbackAnalysisCategoryEntity> selectedCategories = new ArrayList<FeedbackAnalysisCategoryEntity>();
		for (FeedbackAnalysisCategorySetEntity facs : feedbackAnalysisCategorySetsInUse)
			for (AbstractCategoryEntity fac : facs.getCategoryEntitys().values())
				if (((FeedbackAnalysisCategoryEntity) fac).getInRecord()) {
					selectedCategories.add((FeedbackAnalysisCategoryEntity) fac);
				}
		currentRecord.setSelectedCategories(selectedCategories);
	}

	/**
	 * Checks if the analysis has any categories selected, prevents the user from
	 * making empty analyses.
	 * 
	 * @return True if there are no categories selected in any of the records,
	 *         otherwise false
	 */
	public boolean checkNoCategoriesSelected() {
		for (FeedbackAnalysisRecordEntity record : feedbackAnalysisEntity.getRecords()) {
			if (record.getSelectedCategories() == null || record.getSelectedCategories().size() == 0)
				continue;
			else
				return false;
		}
		return true;
	}

	/**
	 * Checks if the analysis has records with no categories selected. The user is
	 * asked to confirm that they want to continue if that is the case
	 * 
	 * @return True if there is at least one record that has not been classified
	 *         based on at least one categoryset, false otherwise
	 */
	public boolean containsUnclassifiedRecords() {
		for (FeedbackAnalysisRecordEntity record : feedbackAnalysisEntity.getRecords()) {
			if (feedbackAnalysisCategorySetsInUse.size() > record.getSelectedCategories().size())
				return true;
		}
		return false;
	}

	/**
	 * navigates to the summary page
	 * 
	 * @return the key string that is used by facesconfig.xml to navigate to the
	 *         correct page
	 */
	public String toSummary() {
		if (checkNoCategoriesSelected()) {
			Locale locale = userManagedBean.getLocale();
			ResourceBundle messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("ana_continuefailheader"), messages.getString("ana_continuefail")));
			return "";
		}
		return "summary";
	}

	/**
	 * Validates the current record, checks if it has no selected categories or no
	 * written feedback and throws an error if that is the case
	 * 
	 * @param context
	 * @param component
	 * @param value
	 */
	public void validateNonEmptyRecord(FacesContext context, UIComponent component, Object value) {
		Locale locale = userManagedBean.getLocale();
		ResourceBundle messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
		String message = messages.getString("ana_emptyRecord");
		if (isRecordEmpty(currentRecord))
			throw new ValidatorException(
					new FacesMessage(FacesMessage.SEVERITY_ERROR, messages.getString("dialogErrorTitle"), message));
	}

	/**
	 * Checks whether the given record has categories selected
	 * 
	 * @param record
	 *            the record to check
	 * @return whether the given record has categories selected
	 */
	private boolean isRecordEmpty(FeedbackAnalysisRecordEntity record) {
		return (record.getSelectedCategories() == null || record.getSelectedCategories().isEmpty())
				&& (record.getComment() == null || record.getComment().isEmpty());
	}

	/**
	 * Checks if there are empty records returns the order number of the first found
	 * empty record, if they exist -1 if all no empty records are present
	 * 
	 * @return order number of the first empty record
	 */
	private int getEmptyRecordNumber() {
		for (FeedbackAnalysisRecordEntity rec : feedbackAnalysisEntity.getRecords())
			if (isRecordEmpty(rec))
				return rec.getOrderNumber();
		return -1;
	}

	/**
	 * Makes sure changes to the currently shown record are saved, stops the timer,
	 * sets the duration of the analysis and checks wheter there are some empty
	 * records and whether none of the records has any categories selected navigates
	 * to the recordtable page if not.
	 * 
	 * @return the key string that is used by facesconfig.xml to navigate to the
	 *         correct page
	 */
	public String toRecordTable() {
		Locale locale = userManagedBean.getLocale();
		ResourceBundle messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
		editRecord();
		if (checkNoCategoriesSelected()) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("ana_continuefailheader"), messages.getString("ana_continuefail")));
			return "";
		}
		if (getEmptyRecordNumber() > 0) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("ana_continuefailheader"),
					MessageFormat.format(messages.getString("ana_emptyRecordWithNumber"), getEmptyRecordNumber())));
			return "";
		}

		feedbackAnalysisEntity.setDuration(duration);
		isTimerStopped = true;
		return "recordtable";
	}

	/**
	 * Delete's the selected record from the datatable and the database, if the
	 * analysis has already been saved.
	 * 
	 * @param record
	 *            selected row
	 */
	public void delete(int orderNumber) {
		Locale locale = userManagedBean.getLocale();
		ResourceBundle messages = ResourceBundle.getBundle("com.moveatis.messages.Messages", locale);
		if (feedbackAnalysisEntity.getRecords().size() == 1) {
			RequestContext.getCurrentInstance().showMessageInDialog(new FacesMessage(FacesMessage.SEVERITY_ERROR,
					messages.getString("repo_deletefailheader"), messages.getString("repo_deletefail")));
			return;
		}

		List<FeedbackAnalysisRecordEntity> list = feedbackAnalysisEntity.getRecords();
		FeedbackAnalysisRecordEntity record = null;
		for (int i = 0; i < list.size(); i++) {
			if (list.get(i).getOrderNumber() != null && list.get(i).getOrderNumber().intValue() == orderNumber) {
				record = list.get(i);
				list.remove(i);
				break;
			}
		}
		setOrderNumbers(list);
		feedbackAnalysisEntity.setRecords(list);
		if (record != null && record.getId() != null)
			feedbackAnalysisEJB.removeRecordFromAnalysis(feedbackAnalysisEntity, record);
		if (orderNumber > list.size())
			setCurrentRecord(list.size());
		else
			setCurrentRecord(orderNumber);
	}

	/**
	 * Updates order numbers to records list.
	 * 
	 * @param list
	 *            users records
	 */
	private void setOrderNumbers(List<FeedbackAnalysisRecordEntity> list) {
		list.sort((a, b) -> a.getOrderNumber().compareTo(b.getOrderNumber()));
		Integer newOrderNumber = 1;
		for (int i = 0; i < list.size(); i++) {
			list.get(i).setOrderNumber(i + 1);
			newOrderNumber++;
		}
	}

	/**
	 * The method saves the analysis to the database. Copies are made of all the
	 * categorysets used by the analysis, so that later edits to the categorysets
	 * won't affect old analyses.
	 */
	public void saveFeedbackAnalysis() {
		if (feedbackAnalysisEntity.getId() == null) {

			for (FeedbackAnalysisCategorySetEntity categorySet : feedbackAnalysisCategorySetsInUse) {

				if (categorySet.getId() != null)
					categorySetEJB.detachCategorySet(categorySet);

				for (AbstractCategoryEntity cat : categorySet.getCategoryEntitys().values()) {
					LabelEntity label = labelEJB.findByLabel(cat.getLabel().getText());
					cat.setCategorySet(categorySet);
					if (label == null) {
						cat.setLabel(new LabelEntity(cat.getLabel().getText()));
						labelEJB.create(cat.getLabel());
					} else
						cat.setLabel(label);
				}
				categorySetEJB.create(categorySet);
			}
			feedbackAnalysisEntity.setEvent(eventEntity);
			feedbackAnalysisEntity.setObserver(sessionBean.getLoggedIdentifiedUser());
			feedbackAnalysisEJB.create(feedbackAnalysisEntity);
		} else {
			for (FeedbackAnalysisRecordEntity record : feedbackAnalysisEntity.getRecords()) {
				if (record.getId() == null)
					feedbackAnalysisRecordEJB.create(record);
				else
					feedbackAnalysisRecordEJB.edit(record);
			}
			feedbackAnalysisEJB.edit(feedbackAnalysisEntity);
		}
	}

	/**
	 * Sets the categorysetsinuse to be null,
	 */
	public void resetCategorySetsInUse() {
		this.feedbackAnalysisCategorySetsInUse = null;
	}

}