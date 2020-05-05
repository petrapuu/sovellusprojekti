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
package com.moveatis.session;

import java.io.Serializable;
import java.util.List;
import java.util.Locale;
import java.util.SortedSet;
import java.util.TimeZone;
import java.util.TreeSet;

import javax.enterprise.context.SessionScoped;
import javax.faces.context.FacesContext;
import javax.inject.Inject;
import javax.inject.Named;
import javax.servlet.http.HttpServletRequest;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.groupkey.GroupKeyEntity;
import com.moveatis.interfaces.Session;
import com.moveatis.managedbeans.FeedbackAnalysisManagedBean;
import com.moveatis.managedbeans.ObservationManagedBean;
import com.moveatis.observation.ObservationCategorySet;
import com.moveatis.timezone.TimeZoneInformation;
import com.moveatis.user.AbstractUser;
import com.moveatis.user.IdentifiedUserEntity;
import com.moveatis.user.TagUserEntity;

/**
 * The bean manages actions the user needs in the usage of Moveatis.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@SessionScoped
@Named
public class SessionBean implements Serializable, Session {

	private static final long serialVersionUID = 1L;

	private static final Logger LOGGER = LoggerFactory.getLogger(SessionBean.class);

	@Inject
	private ObservationManagedBean observationManagedBean;

	@Inject
	private FeedbackAnalysisManagedBean feedbackAnalysisManagedBean;

	private boolean loggedIn = false;

	private IdentifiedUserEntity userEntity;

	private TagUserEntity tagEntity;

	private SortedSet<Long> sessionObservations;

	private String returnUri;

	private TimeZone sessionTimeZone = TimeZoneInformation.getTimeZone();

	private Locale locale; // Locale switching based on BalusC's example:
							// http://stackoverflow.com/a/4830669

	public SessionBean() {

	}

	@Override
	public void setIdentityProviderUser(IdentifiedUserEntity user) {
		this.userEntity = user;
		commonSettingsForLoggedInUsers();
	}

	@Override
	public void setAnonymityUser() {
		// TODO: Doesn't set abstractUser. Is this ok?
		tagEntity = null;
		commonSettingsForLoggedInUsers();
		// If user wants to observe without selecting existing event group
		// (in control view or with a group key), we should reset the event.
		observationManagedBean.setEventEntity(null);
		feedbackAnalysisManagedBean.setEventEntity(null);
	}

	@Override
	public void setTagUser(TagUserEntity tagUser) {
		if (tagUser == null) {
			return;
		}
		this.tagEntity = tagUser;
		commonSettingsForLoggedInUsers();
		observationManagedBean.setEventEntity(tagUser.getGroupKey().getEventGroup().getEvent());
		feedbackAnalysisManagedBean.setEventEntity(tagUser.getGroupKey().getEventGroup().getEvent());
	}

	private void commonSettingsForLoggedInUsers() {
		this.loggedIn = true;
		// Make sure we don't modify earlier categories.
		observationManagedBean.resetCategorySetsInUse();
		feedbackAnalysisManagedBean.resetCategorySetsInUse();
	}

	@Override
	public boolean isLoggedIn() {
		return loggedIn;
	}

	@Override
	public String toString() {
		String userType = this.tagEntity != null ? "tag" : "anonymous";
		userType = this.userEntity != null ? "identified" : userType;
		return "SessionBean: userType -> " + userType + ", loggedIn -> " + isLoggedIn();
	}

	@Override
	public SortedSet<Long> getSessionObservationsIds() {
		if (this.sessionObservations == null) {
			return new TreeSet<>();
		}
		return this.sessionObservations;
	}

	/**
	 * Checks if the observation is in saveable state. It is used in checking if the
	 * Save button can be displayed.
	 * 
	 * @return true if the observation could be saved.
	 */
	@Override
	public boolean isSaveable() {
		return observationManagedBean.getObservationEntity() != null;
	}

	@Override
	public void setSessionObservations(SortedSet<Long> observationsIds) {
		this.sessionObservations = observationsIds;
	}

	@Override
	public AbstractUser getLoggedInUser() {
		return this.tagEntity;
	}

	@Override
	public TimeZone getSessionTimeZone() {
		return sessionTimeZone;
	}

	@Override
	public void setSessionTimeZone(TimeZone timeZone) {
		this.sessionTimeZone = timeZone;
	}

	@Override
	public IdentifiedUserEntity getLoggedIdentifiedUser() {
		return this.userEntity;
	}

	@Override
	public GroupKeyEntity getGroupKey() {
		return this.tagEntity.getGroupKey();
	}

	/**
	 * Returns true if the button that resets the current observation should be
	 * available to the user.
	 */
	public boolean isResetObsAvailable() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		boolean result = (viewId.equals("/app/observer/index.xhtml") || viewId.equals("/app/summary/index.xhtml"));
		return result;
	}

	/**
	 * Returns true if the button that redirects the user to the category selection
	 * view should be available to the user.
	 */
	public boolean isBackToCatEdAvailable() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		boolean result = (viewId.equals("/app/observer/index.xhtml"));
		return result;
	}

	/**
	 * Returns true if the button that redirects the user to the front page should
	 * be available to the user.
	 */
	public boolean isToFrontPageAvailable() {
		String viewId = FacesContext.getCurrentInstance().getViewRoot().getViewId();
		boolean result = !(viewId.equals("/index.xhtml"));
		return result;
	}

	/**
	 * Used in development of Moveatis to detect whether the application is running
	 * on the actual production server or on localhost.
	 * 
	 * @return true if Moveatis is running in localhost, false otherwise.
	 */
	@Override
	public boolean getIsLocalhost() {
		boolean isLocalhost = ((HttpServletRequest) FacesContext.getCurrentInstance().getExternalContext().getRequest())
				.getRequestURL().toString().contains("localhost");
		return isLocalhost;
	}

	/**
	 * Returns the URI in which the user was before he or she clicked the login
	 * button when not in the front page. Not implemented in version 1.0.
	 * 
	 * @return the URI to return the user to.
	 */
	@Override
	public String getReturnUri() {
		return returnUri;
	}

	/**
	 * Sets the URI into which the user will be returned when he or she clicks the
	 * login button outside of the front page. Not implemented in version 1.0.
	 * 
	 * @param returnUri
	 *            The URI that is set as return URI.
	 */
	@Override
	public void setReturnUri(String returnUri) {
		this.returnUri = returnUri;
	}

	@Override
	public boolean isIdentifiedUser() {
		return userEntity != null;
	}

	@Override
	public void setCategorySetsInUse(List<ObservationCategorySet> categorySets) {
		observationManagedBean.setCategorySetsInUse(categorySets);
	}

	@Override
	public List<ObservationCategorySet> getCategorySetsInUse() {
		return observationManagedBean.getCategorySetsInUse();
	}

	@Override
	public void setFeedbackAnalysisCategorySetsInUse(List<FeedbackAnalysisCategorySetEntity> categorySets) {
		feedbackAnalysisManagedBean.setFeedbackAnalysisCategorySetsInUse(categorySets);
	}

	@Override
	public List<FeedbackAnalysisCategorySetEntity> getFeedbackAnalysisCategorySetsInUse() {
		return feedbackAnalysisManagedBean.getFeedbackAnalysisCategorySetsInUse();
	}
}
