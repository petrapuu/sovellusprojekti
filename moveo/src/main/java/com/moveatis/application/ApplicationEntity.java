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
package com.moveatis.application;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.persistence.Entity;
import javax.persistence.OneToMany;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.validation.constraints.NotNull;

import com.moveatis.abstracts.BaseEntity;
import com.moveatis.roles.SuperUserRoleEntity;

/**
 * The entity represents the data of the application in the database.
 * 
 * @author Sami Kallio <phinaliumz at outlook.com>
 */
@Table(name = "APPLICATION")
@Entity
public class ApplicationEntity extends BaseEntity implements Serializable {

	private static final long serialVersionUID = 1L;

	@Temporal(TemporalType.DATE)
	@NotNull
	private Date applicationInstalled;

	@OneToMany
	private List<SuperUserRoleEntity> superUsers;

	private String reportEmail;

	/**
	 * Gets the list of the users with the superuser role.
	 */
	public List<SuperUserRoleEntity> getSuperUsers() {
		if (superUsers == null) {
			superUsers = new ArrayList<>();
		}
		return superUsers;
	}

	public void setSuperUsers(List<SuperUserRoleEntity> superUsers) {
		this.superUsers = superUsers;
	}

	/**
	 * Returns the date when the application was installed.
	 */
	public Date getApplicationInstalled() {
		return applicationInstalled;
	}

	/**
	 * Sets the date when the application was installed.
	 */
	public void setApplicationInstalled(Date applicationInstalled) {
		this.applicationInstalled = applicationInstalled;
	}

	public String getReportEmail() {
		return reportEmail;
	}

	public void setReportEmail(String reportEmail) {
		this.reportEmail = reportEmail;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof ApplicationEntity)) {
			return false;
		}
		ApplicationEntity other = (ApplicationEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.application.ApplicationEntity[ id=" + id + " ]";
	}

}
