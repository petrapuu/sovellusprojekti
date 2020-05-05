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
package com.moveatis.interfaces;

import java.util.List;
import java.util.SortedSet;
import java.util.TimeZone;

import javax.ejb.Local;

import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.groupkey.GroupKeyEntity;
import com.moveatis.observation.ObservationCategorySet;
import com.moveatis.user.AbstractUser;
import com.moveatis.user.IdentifiedUserEntity;
import com.moveatis.user.TagUserEntity;

/**
 * The interface to manage the session entity. The session is a context, in
 * which a particular user is using Moveatis. The session has information, that
 * is meaningful in the current context, such as whether the user has been
 * identified or not.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Local(Session.class)
public interface Session {

	void setTagUser(TagUserEntity tagUser);

	void setIdentityProviderUser(IdentifiedUserEntity user);

	void setAnonymityUser();

	boolean isLoggedIn();

	boolean isIdentifiedUser();

	boolean isSaveable();

	SortedSet<Long> getSessionObservationsIds();

	void setSessionObservations(SortedSet<Long> observationsIds);

	AbstractUser getLoggedInUser();

	IdentifiedUserEntity getLoggedIdentifiedUser();

	GroupKeyEntity getGroupKey();

	TimeZone getSessionTimeZone();

	void setSessionTimeZone(TimeZone timeZone);

	boolean getIsLocalhost();

	void setReturnUri(String uri);

	String getReturnUri();

	void setCategorySetsInUse(List<ObservationCategorySet> categorySets);

	List<ObservationCategorySet> getCategorySetsInUse();

	void setFeedbackAnalysisCategorySetsInUse(List<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySets);

	List<FeedbackAnalysisCategorySetEntity> getFeedbackAnalysisCategorySetsInUse();
}
