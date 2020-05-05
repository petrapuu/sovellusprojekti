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
package com.moveatis.groupkey;

import static javax.persistence.CascadeType.MERGE;
import static javax.persistence.CascadeType.PERSIST;
import static javax.persistence.CascadeType.REMOVE;

import java.io.Serializable;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.Table;
import javax.validation.constraints.NotNull;

import com.moveatis.abstracts.BaseEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.user.IdentifiedUserEntity;
import com.moveatis.user.TagUserEntity;

/**
 * The entity presents the group key that the event groups can be identified
 * with in the database.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Entity
@NamedQueries({
		@NamedQuery(name = "findKey", query = "SELECT groupkey FROM GroupKeyEntity groupkey WHERE groupkey.groupKey=:key") })
@Table(name = "GROUPKEY")
public class GroupKeyEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@OneToOne
	private EventGroupEntity eventGroup;

	@OneToOne(cascade = { PERSIST, MERGE, REMOVE })
	private TagUserEntity tagUser;

	@Column(unique = true)
	@NotNull
	private String groupKey;

	@ManyToOne
	private IdentifiedUserEntity creator;

	private String label;

	public EventGroupEntity getEventGroup() {
		return eventGroup;
	}

	public void setEventGroup(EventGroupEntity eventGroup) {
		this.eventGroup = eventGroup;
	}

	public String getGroupKey() {
		return groupKey;
	}

	public void setGroupKey(String groupKey) {
		this.groupKey = groupKey;
	}

	public IdentifiedUserEntity getCreator() {
		return creator;
	}

	public void setCreator(IdentifiedUserEntity creator) {
		this.creator = creator;
	}

	public String getLabel() {
		return label;
	}

	public void setLabel(String label) {
		this.label = label;
	}

	public TagUserEntity getTagUser() {
		return tagUser;
	}

	public void setTagUser(TagUserEntity tagUser) {
		this.tagUser = tagUser;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof GroupKeyEntity)) {
			return false;
		}
		GroupKeyEntity other = (GroupKeyEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.groupkey.GroupKeyEntity[ id=" + id + " ]";
	}

}
