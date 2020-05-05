package com.moveatis.abstracts;

import javax.persistence.MappedSuperclass;

import com.moveatis.records.RecordEntity;

/**
 * @author Visa Nykänen, Sami Kallio Superclass for the common features of
 *         records in observations and feedback analysis
 */
@MappedSuperclass
public abstract class AbstractRecordEntity extends BaseEntity {

	/**
	 * The time at which the record takes place
	 */
	private Long startTime;

	/**
	 * Written description of the recorded event
	 */
	private String comment;

	public Long getStartTime() {
		return startTime;
	}

	public void setStartTime(Long startTime) {
		this.startTime = startTime;
	}

	public String getComment() {
		return comment;
	}

	public void setComment(String comment) {
		this.comment = comment;
	}

	@Override
	public int hashCode() {
		int hash = 0;
		hash += (id != null ? id.hashCode() : 0);
		return hash;
	}

	@Override
	public boolean equals(Object object) {
		if (!(object instanceof RecordEntity)) {
			return false;
		}
		RecordEntity other = (RecordEntity) object;
		return !((this.id == null && other.id != null) || (this.id != null && !this.id.equals(other.id)));
	}

	@Override
	public String toString() {
		return "com.moveatis.records.RecordEntity[ id=" + id + " ]";
	}

}
