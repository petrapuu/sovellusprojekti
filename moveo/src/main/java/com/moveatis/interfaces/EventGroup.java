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

import javax.ejb.Local;

import com.moveatis.abstracts.AbstractCategorySetEntity;
import com.moveatis.event.EventGroupEntity;
import com.moveatis.user.AbstractUser;

/**
 * The interface to manage the event group entity. The event group contains
 * different events that can be thought as a group. For example, an event group
 * named "Exploratory teacher" could contain events like "Teaching situation
 * number 1", "Teaching situation number 2", etc.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Local(EventGroup.class)
public interface EventGroup {

	void create(EventGroupEntity eventGroupEntity);

	void edit(EventGroupEntity eventGroupEntity);

	void remove(EventGroupEntity eventGroupEntity);

	EventGroupEntity find(Object id);

	List<EventGroupEntity> findAll();

	List<EventGroupEntity> findAllForOwner(AbstractUser owner);

	List<EventGroupEntity> findAllForUser(AbstractUser user);

	List<EventGroupEntity> findAllForPublicUser();

	List<EventGroupEntity> findRange(int[] range);

	void removeCategorySetEntityFromEventGroups(AbstractCategorySetEntity categorySetEntity);

	int count();

}
