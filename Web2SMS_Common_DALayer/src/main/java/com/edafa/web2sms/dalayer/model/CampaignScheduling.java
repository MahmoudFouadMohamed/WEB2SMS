package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.NamedQuery;
import javax.persistence.OneToOne;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;

/**
 * The persistent class for the CAMPAIGNS_SCHEDULING database table.
 * 
 */
@Entity
@Table(name = "CAMPAIGNS_SCHEDULING")
@NamedQuery(name = "CampaignScheduling.findAll", query = "SELECT c FROM CampaignScheduling c")
public class CampaignScheduling implements Serializable {
	protected static final long serialVersionUID = 1L;

	@Id
	@SequenceGenerator(name = "CAMPAIGNS_SCHEDULING_ID_GENERATOR", sequenceName = "CAMP_SCHED_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "CAMPAIGNS_SCHEDULING_ID_GENERATOR")
	@Column(unique = true)
	protected long id;

	@Column(name = "SCHEDULE_START_TIMESTAMP")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date scheduleStartTimestamp;

	@Column(name = "SCHEDULE_END_DATE")
	@Temporal(TemporalType.DATE)
	protected Date scheduleEndDate;

	@Column(name = "SCHEDULE_STOP_TIME")
	@Temporal(TemporalType.TIMESTAMP)
	protected Date scheduleStopTime;

	@Column(name = "EXPECTED_EXE_COUNT")
	protected Integer expectedExecutionCount = 0;

	@JoinColumn(name = "SCHEDULE_FREQUENCY_ID", referencedColumnName = "SCHEDULE_FREQ_ID")
	@ManyToOne(optional = false, cascade = CascadeType.DETACH)
	protected ScheduleFrequency scheduleFrequency;

	@Column(name = "SCHEDULED_FLAG")
	protected Boolean scheduledFlag;

	// uni-directional many-to-one association to Campaign
	@OneToOne
	@JoinColumn(name = "CAMPAIGN_ID", nullable = false)
	protected Campaign campaign;

	public CampaignScheduling() {
	}

	public CampaignScheduling(Campaign campaign) {
		this.campaign = campaign;
	}

	public long getId() {
		return this.id;
	}

	public Boolean getScheduledFlag() {
		return scheduledFlag;
	}

	public void setScheduledFlag(Boolean scheduledFlag) {
		this.scheduledFlag = scheduledFlag;
	}

	public void setId(long id) {
		this.id = id;
	}

	public Date getScheduleEndDate() {
		return this.scheduleEndDate;
	}

	public void setScheduleEndDate(Date scheduleEndTimestamp) {
		this.scheduleEndDate = scheduleEndTimestamp;
	}

	public Date getScheduleStopTime() {
		return scheduleStopTime;
	}

	public void setScheduleStopTime(Date scheduleStopTime) {
		this.scheduleStopTime = scheduleStopTime;
	}

	public void setScheduleFrequency(ScheduleFrequency scheduleFrequency) {
		this.scheduleFrequency = scheduleFrequency;
	}

	public ScheduleFrequency getScheduleFrequency() {
		return scheduleFrequency;
	}

	public Date getScheduleStartTimestamp() {
		return this.scheduleStartTimestamp;
	}

	public void setScheduleStartTimestamp(Date scheduleStartTimestamp) {
		this.scheduleStartTimestamp = scheduleStartTimestamp;
	}

	public Campaign getCampaign() {
		return this.campaign;
	}

	public void setCampaign(Campaign campaign) {
		this.campaign = campaign;
	}

	public Integer getExpectedExecutionCount() {
		return expectedExecutionCount;
	}

	public void setExpectedExecutionCount(Integer expectedExecutionCount) {
		this.expectedExecutionCount = expectedExecutionCount;
	}

	@Override
	public boolean equals(Object obj) {
		if (!(obj instanceof CampaignScheduling))
			return false;
		CampaignScheduling other = (CampaignScheduling) obj;

		return (scheduledFlag.equals(other.getScheduledFlag())
				&& scheduleStartTimestamp.equals(other.getScheduleStartTimestamp())
				&& scheduleEndDate.equals(other.scheduleEndDate) && scheduleFrequency.equals(scheduleFrequency));

	}

	@Override
	public String toString() {
		return "(scheduleStartTimestamp=\"" + scheduleStartTimestamp + "\", scheduleEndDate=\"" + scheduleEndDate
				+ "\", scheduleStopTime=\"" + scheduleStopTime + "\", scheduleFrequency=\"" + scheduleFrequency
				+ "\", scheduledFlag=\"" + scheduledFlag + ")";
	}
}