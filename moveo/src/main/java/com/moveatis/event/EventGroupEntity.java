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
package com.moveatis.event;

import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.FetchType.EAGER;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.moveatis.abstracts.BaseEntity;
import com.moveatis.category.CategorySetEntity;
import com.moveatis.feedbackanalysiscategory.FeedbackAnalysisCategorySetEntity;
import com.moveatis.groupkey.GroupKeyEntity;
import com.moveatis.user.AbstractUser;

/**
 * The entity represents the information for the event groups of the
 * application. The event group has just one event, but the class could be
 * extended to support multiple events. The event group can be identified with a
 * groupkey, which allows semi-public usage.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 * @author Visa Nykänen
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "findEventGroupByOwner", query = "SELECT eventGroup FROM EventGroupEntity eventGroup WHERE eventGroup.owner=:ownerEntity "
				+ "AND eventGroup.removed IS NULL") })
@Table(name = "EVENTGROUP")
public class EventGroupEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@OneToMany(mappedBy = "eventGroupEntity", cascade = PERSIST, fetch = EAGER)
	private Set<CategorySetEntity> categorySets;

	@OneToMany(mappedBy = "eventGroupEntity", cascade = PERSIST, fetch = EAGER)
	private Set<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySets;

	public Set<FeedbackAnalysisCategorySetEntity> getFeedbackAnalysisCategorySets() {
		return feedbackAnalysisCategorySets;
	}

	public void setFeedbackAnalysisCategorySets(Set<FeedbackAnalysisCategorySetEntity> feedbackAnalysisCategorySets) {
		this.feedbackAnalysisCategorySets = feedbackAnalysisCategorySets;
	}

	@OneToOne(mappedBy = "eventGroup", cascade = PERSIST)
	private EventEntity event;

	@OneToOne(cascade = PERSIST)
	private GroupKeyEntity groupKey;

	@ManyToOne
	private AbstractUser owner;

	@OneToMany
	private Set<AbstractUser> users;

	private String label;
	private String description;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public AbstractUser getOwner() {
		return owner;
	}

	public void setOwner(AbstractUser owner) {
		this.owner = owner;
	}

	public Set<AbstractUser> getUsers() {
		return users;
	}

	public void setUsers(Set<AbstractUser> users) {
		this.users = users;
	}

	public Set<CategorySetEntity> getCategorySets() {
		return categorySets;
	}

	public void setCategorySets(Set<CategorySetEntity> categorySets) {
		this.categorySets = categorySets;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public EventEntity getEvent() {
		return event;
	}

	public void setEvent(EventEntity event) {
		this.event = event;
	}

	public GroupKeyEntity getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(GroupKeyEntity groupKey) {
		this.groupKey = groupKey;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EventGroupEntity)) {
			return false;
		}
		EventGroupEntity other = (EventGroupEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.event.EventGroupEntity[ id=" + id + " ]";
	}

}
