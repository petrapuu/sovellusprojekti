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

import java.util.Date;
import java.util.List;

import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.inject.Named;

import com.moveatis.interfaces.Role;
import com.moveatis.roles.SuperUserRoleEntity;
import com.moveatis.user.IdentifiedUserEntity;

/**
 * The bean sets the superuser role for the identified users. It is not used in
 * the current version of Moveatis.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com
 */
@Named(value = "superUserBean")
@RequestScoped
public class SuperUserManagedBean {

	@Inject
	private Role roleBean;

	public SuperUserManagedBean() {

	}

	/**
	 * Adds the superuser role to the given identified user.
	 */
	public void addSuperUserRights(IdentifiedUserEntity userEntity) {
		roleBean.addSuperuserRoleToUser(userEntity);
	}

	/**
	 * Adds the superuser role for the given time period to the given identified
	 * user.
	 */
	public void addSuperUserRights(IdentifiedUserEntity userEntity, Date startDate, Date endDate) {
		roleBean.addSuperuserRoleToUser(userEntity, startDate, endDate);
	}

	/**
	 * Removes the superuser role from the given identified user.
	 */
	public void removeSuperUserRights(IdentifiedUserEntity userEntity) {
		roleBean.removeSuperuserRoleFromUser(userEntity);
	}

	public List<SuperUserRoleEntity> listSuperUsers() {
		return roleBean.listSuperusers();
	}

}
