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

import java.util.HashSet;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.moveatis.event.EventEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.groupkey.GroupKeyEntity;
import com.moveatis.interfaces.AnonUser;
import com.moveatis.interfaces.EventGroup;
import com.moveatis.interfaces.GroupKey;
import com.moveatis.interfaces.Session;
import com.moveatis.user.AbstractUser;
import com.moveatis.user.TagUserEntity;

/**
 * The bean is used to manage event groups in the appropriate views.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Named(value = "eventGroupManagedBean")
@RequestScoped
public class EventGroupManagedBean {

	private static final Logger LOGGER = LoggerFactory.getLogger(EventGroupManagedBean.class);

	private String eventGroupName;
	private String eventGroupKey;
	private String eventGroupDescription;
	private String visibility;

	private boolean groupKeySelected;

	private EventGroupEntity eventGroupEntity;

	@Inject
	private EventGroup eventGroupEJB;

	@Inject
	private GroupKey groupKeyEJB;

	@Inject
	private Session sessionBean;

	@Inject
	private AnonUser anonUserEJB;

	@Inject
	private ControlManagedBean controlManagedBean;

	public EventGroupManagedBean() {

	}

	@PostConstruct
	public void init() {
		this.visibility = "own";
	}

	public String getEventGroupName() {
		return eventGroupName;
	}

	public void setEventGroupName(String eventGroupName) {
		this.eventGroupName = eventGroupName;
	}

	public String getVisibility() {
		return visibility;
	}

	public void setVisibility(String visibility) {
		groupKeySelected = visibility.equalsIgnoreCase("groupKey");
		this.visibility = visibility;
	}

	public String getEventGroupDescription() {
		return eventGroupDescription;
	}

	public void setEventGroupDescription(String eventGroupDescription) {
		this.eventGroupDescription = eventGroupDescription;
	}

	public String getEventGroupKey() {
		return eventGroupKey;
	}

	public void setEventGroupKey(String eventGroupKey) {
		this.eventGroupKey = eventGroupKey;
	}

	public boolean isGroupKeySelected() {
		return groupKeySelected;
	}

	public void setGroupKeySelected(boolean groupKeySelected) {
		this.groupKeySelected = groupKeySelected;
	}

	public void addGroupKey() {
		groupKeySelected = this.visibility.equalsIgnoreCase("groupKey");
	}

	/**
	 * Adds the group key to the specified event group. The group key is used to
	 * identify the event group.
	 * 
	 * @param eventGroup
	 *            The event group into which the the group key is added.
	 */
	public void addGroupKey(EventGroupEntity eventGroup) {

		if (eventGroupKey != null) {

			GroupKeyEntity groupKey = new GroupKeyEntity();
			groupKey.setCreator(sessionBean.getLoggedIdentifiedUser());
			groupKey.setGroupKey(eventGroupKey);
			groupKey.setEventGroup(eventGroup);
			eventGroup.setGroupKey(groupKey);

			TagUserEntity tagUserEntity = new TagUserEntity();
			tagUserEntity.setCreator(sessionBean.getLoggedIdentifiedUser());
			tagUserEntity.setLabel(eventGroupKey);
			tagUserEntity.setGroupKey(groupKey);
			groupKey.setTagUser(tagUserEntity);

			groupKeyEJB.create(groupKey);

			eventGroupEJB.edit(eventGroup);

			controlManagedBean.fetchEventGroups();
		}
	}

	/**
	 * Sets a new group key for the event group.
	 * 
	 * @param eventGroup
	 *            The event group to edit.
	 * @param newGroupKey
	 *            The new group key for the event group.
	 */
	public void editGroupKey(EventGroupEntity eventGroup, String newGroupKey) {
		if (newGroupKey != null) {
			GroupKeyEntity groupKeyEntity = eventGroup.getGroupKey();
			groupKeyEntity.setCreator(sessionBean.getLoggedIdentifiedUser());
			groupKeyEntity.setGroupKey(newGroupKey);
			groupKeyEntity.setEventGroup(eventGroup);
			eventGroup.setGroupKey(groupKeyEntity);

			TagUserEntity tagUserEntity = groupKeyEntity.getTagUser();
			tagUserEntity.setGroupKey(groupKeyEntity);

			groupKeyEJB.edit(groupKeyEntity);
			controlManagedBean.fetchEventGroups();
		}
	}

	/**
	 * Removes the group key from the given event group.
	 */
	public void removeGroupKey(EventGroupEntity eventGroup) {
		GroupKeyEntity groupKey = eventGroup.getGroupKey();
		eventGroup.setGroupKey(null);
		eventGroupEJB.edit(eventGroup);
		groupKeyEJB.removePermanently(groupKey);
		controlManagedBean.fetchEventGroups();
	}

	/**
	 * Creates a new event group.
	 */
	public void createNewEventGroup() {

		eventGroupEntity = new EventGroupEntity();
		eventGroupEntity.setLabel(eventGroupName);
		eventGroupEntity.setDescription(eventGroupDescription);
		eventGroupEntity.setOwner(sessionBean.getLoggedIdentifiedUser());

		if (groupKeySelected) {
			GroupKeyEntity groupKey = new GroupKeyEntity();
			groupKey.setCreator(sessionBean.getLoggedIdentifiedUser());
			groupKey.setGroupKey(eventGroupKey);
			groupKey.setEventGroup(eventGroupEntity);

			TagUserEntity tagUser = new TagUserEntity();
			tagUser.setCreator(sessionBean.getLoggedIdentifiedUser());
			tagUser.setGroupKey(groupKey);

			groupKey.setTagUser(tagUser);

			eventGroupEntity.setGroupKey(groupKey);

		} else if (visibility.equalsIgnoreCase("public")) {
			Set<AbstractUser> users = new HashSet<>();
			users.add(anonUserEJB.find());
			eventGroupEntity.setUsers(users);
		}
		/*
		 * As agreed on meeting 5.5.2016, eventGroup will be renamed in the UI as
		 * "event", while eventgroups and events in the code stay the same.
		 */
		EventEntity eventEntity = new EventEntity();
		eventEntity.setCreator(sessionBean.getLoggedIdentifiedUser());
		eventEntity.setLabel("DEFAULT");
		eventEntity.setEventGroup(eventGroupEntity);

		eventGroupEntity.setEvent(eventEntity);
		eventGroupEJB.create(eventGroupEntity);

		controlManagedBean.addEventGroup(eventGroupEntity);

		controlManagedBean.setCreatingNewEventGroup(false);
	}
}
