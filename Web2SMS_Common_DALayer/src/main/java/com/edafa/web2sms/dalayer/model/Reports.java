
package com.edafa.web2sms.dalayer.model;

import java.io.Serializable;
import java.util.Date;

import javax.persistence.Basic;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.NamedQueries;
import javax.persistence.NamedQuery;
import javax.persistence.SequenceGenerator;
import javax.persistence.Table;
import javax.persistence.Temporal;
import javax.persistence.TemporalType;
import javax.persistence.Transient;
import javax.xml.bind.annotation.XmlRootElement;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;

import com.edafa.web2sms.dalayer.enums.ReportStatus;
import com.edafa.web2sms.dalayer.enums.ReportType;

/**
 *
 * @author Mahmoud Fouad <mahmoud.fouad@edafa.com>
 */
@Entity
@Table(name = "REPORTS")
@XmlRootElement
@NamedQueries({ @NamedQuery(name = "Reports.findAll", query = "SELECT r FROM Reports r"),
		@NamedQuery(name = "Reports.findById", query = "SELECT r FROM Reports r WHERE r.id = :id"),
		@NamedQuery(name = "Reports.findByStatus", query = "SELECT r FROM Reports r WHERE r.status = :status"),
		@NamedQuery(name = "Reports.findByType", query = "SELECT r FROM Reports r WHERE r.type = :type"),
		@NamedQuery(name = "Reports.findByCampaign", query = "SELECT r FROM Reports r WHERE r.campaign = :campaign"),
		@NamedQuery(name = "Reports.findBySenderName", query = "SELECT r FROM Reports r WHERE r.senderName = :senderName"),
		@NamedQuery(name = "Reports.findByAdmin", query = "SELECT r FROM Reports r WHERE r.admin = true"),
		@NamedQuery(name = "Reports.findByOwner", query = "SELECT r FROM Reports r WHERE r.owner = :ownerId"),
		@NamedQuery(name = "Reports.countByOwner", query = "SELECT count(r) FROM Reports r WHERE r.owner = :ownerId"),
		@NamedQuery(name = "Reports.countAdmin", query = "SELECT count(r) FROM Reports r WHERE r.admin = true"),
		@NamedQuery(name = "Reports.findByStatusOrdered", query = "SELECT r FROM Reports r WHERE r.status = :status order by r.requestDate asc") })
public class Reports implements Serializable {

	private static final long serialVersionUID = -151190254018741145L;

	@Id
	@Basic(optional = false)
	@Column(name = "ID", nullable = false, precision = 0, scale = -127)
	@SequenceGenerator(name = "ReportsIdSeq", sequenceName = "REPORTS_ID_SEQ", allocationSize = 1)
	@GeneratedValue(strategy = GenerationType.SEQUENCE, generator = "ReportsIdSeq")
	private Long id;

	@Column(name = "STATUS")
	@Enumerated(EnumType.ORDINAL)
	private ReportStatus status;

	@Column(name = "FROM_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapterWithNullOverride.class)
	private Date from;

	@Column(name = "TO_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapterWithNullOverride.class)
	private Date to;

	@Column(name = "TYPE")
	@Enumerated(EnumType.ORDINAL)
	private ReportType type;

	@Column(name = "OWNER", length = 200)
	private String owner;

	@Column(name = "USER_NAME", length = 200)
	private String userName;

	@Column(name = "TRX", length = 200)
	private String trx;

	@Column(name = "RETRY_COUNT")
	private int retryCount;

	@Column(name = "CAMPAIGN", length = 200)
	private String campaign;

	@Transient
	private String campaignName;

	@Column(name = "SENDER_NAME", length = 200)
	private String senderName;

	@Column(name = "ADMIN")
	private boolean admin;

	@Column(name = "FILE_NAME", length = 200)
	private String fileName;

	@Column(name = "REQUEST_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date requestDate;

	@Column(name = "PROCESSING_DATE")
	@Temporal(TemporalType.TIMESTAMP)
	@XmlJavaTypeAdapter(DateTimeAdapter.class)
	private Date processingDate;

	public Reports() {
		this.requestDate = new Date();
		this.status = ReportStatus.PENDING;
	}

	public Reports(Long id) {
		this.id = id;
	}

	public Long getId() {
		return id;
	}

	public void setId(Long id) {
		this.id = id;
	}

	public ReportStatus getStatus() {
		return status;
	}

	public void setStatus(ReportStatus status) {
		this.status = status;
	}

	public Date getFrom() {
		return from;
	}

	public void setFrom(Date from) {
		this.from = from;
	}

	public Date getTo() {
		return to;
	}

	public void setTo(Date to) {
		this.to = to;
	}

	public ReportType getType() {
		return type;
	}

	public void setType(ReportType type) {
		this.type = type;
	}

	public String getOwner() {
		return owner;
	}

	public void setOwner(String owner) {
		this.owner = owner;
	}

	public String getCampaign() {
		return campaign;
	}

	public void setCampaign(String campaign) {
		this.campaign = campaign;
	}

	public int getRetryCount() {
		return retryCount;
	}

	public void setRetryCount(int retryCount) {
		this.retryCount = retryCount;
	}

	public String getCampaignName() {
		return campaignName;
	}

	public void setCampaignName(String campaignName) {
		this.campaignName = campaignName;
	}

	public String getSenderName() {
		return senderName;
	}

	public void setSenderName(String senderName) {
		this.senderName = senderName;
	}

	public boolean getAdmin() {
		return admin;
	}

	public void setAdmin(boolean admin) {
		this.admin = admin;
	}

	public String getFileName() {
		return fileName;
	}

	public void setFileName(String fileName) {
		this.fileName = fileName;
	}

	public Date getRequestDate() {
		return requestDate;
	}

	public void setRequestDate(Date requestDate) {
		this.requestDate = requestDate;
	}

	public String getUserName() {
		return userName;
	}

	public void setUserName(String userName) {
		this.userName = userName;
	}

	public String getTrx() {
		return trx;
	}

	public void setTrx(String trx) {
		this.trx = trx;
	}

	public int incrementAndGetRetryCount() {
		return ++this.retryCount;
	}

	public Date getProcessingDate() {
		return processingDate;
	}

	public void setProcessingDate(Date processingDate) {
		this.processingDate = processingDate;
	}

	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result + ((id == null) ? 0 : id.hashCode());
		return result;
	}

	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null)
			return false;
		if (getClass() != obj.getClass())
			return false;
		Reports other = (Reports) obj;
		if (id == null) {
			if (other.id != null)
				return false;
		} else if (!id.equals(other.id))
			return false;
		return true;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("{id=").append(id).append(", status=").append(status).append(", from=").append(from)
				.append(", to=").append(to).append(", type=").append(type).append(", owner=").append(owner)
				.append(", userName=").append(userName).append(", trx=").append(trx).append(", retryCount=")
				.append(retryCount).append(", campaign=").append(campaign).append(", campaignName=")
				.append(campaignName).append(", senderName=").append(senderName).append(", admin=").append(admin)
				.append(", fileName=").append(fileName).append(", requestDate=").append(requestDate)
				.append(", processingDate=").append(processingDate).append("}");
		return builder.toString();
	}

}
