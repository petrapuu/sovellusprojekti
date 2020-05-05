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
package com.moveatis.event;

import java.io.Serializable;
import java.util.Set;

import javax.persistence.Entity;
import javax.persistence.FetchType;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToMany;
import javax.persistence.OneToOne;
import javax.persistence.Table;

import com.moveatis.abstracts.BaseEntity;
import com.moveatis.feedbackanalysis.FeedbackAnalysisEntity;
import com.moveatis.observation.ObservationEntity;
import com.moveatis.user.AbstractUser;

/**
 * The entity represents the information of the events.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Entity
@Table(name = "EVENT")
@NamedQueries(@NamedQuery(name = "EventEntity.findByCreator", query = "SELECT event FROM EventEntity event WHERE event.creator = :user"))
public class EventEntity extends BaseEntity implements Serializable {

	@OneToOne
	private EventGroupEntity eventGroup;

	private static final long serialVersionUID = 1L;

	@ManyToOne
	private AbstractUser creator;

	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<ObservationEntity> observations;

	@OneToMany(mappedBy = "event", fetch = FetchType.LAZY)
	private Set<FeedbackAnalysisEntity> analyses;

	public Set<FeedbackAnalysisEntity> getAnalyses() {
		return analyses;
	}

	public void setAnalyses(Set<FeedbackAnalysisEntity> analyses) {
		this.analyses = analyses;
	}

	private String description;
	private String label;

	@Override
	public Long getId() {
		return id;
	}

	@Override
	public void setId(Long id) {
		this.id = id;
	}

	public EventGroupEntity getEventGroup() {
		return eventGroup;
	}

	public void setEventGroup(EventGroupEntity eventGroup) {
		this.eventGroup = eventGroup;
	}

	public AbstractUser getCreator() {
		return creator;
	}

	public void setCreator(AbstractUser creator) {
		this.creator = creator;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public Set<ObservationEntity> getObservations() {
		return observations;
	}

	public void setObservations(Set<ObservationEntity> observations) {
		this.observations = observations;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof EventEntity)) {
			return false;
		}
		EventEntity other = (EventEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.event.EventEntity[ id=" + id + " ]";
	}

}
