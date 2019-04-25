package com.edafa.web2sms.ui.models;

import java.util.ArrayList;
import java.util.List;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.ui.util.SystemLimiters;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.edafa.ws.utils.configs.SendingRateLimiter;

public class LimiterModel {

	private boolean campEnabled;
	private String description;
	private String limiterId;
	private Integer maxPermits;
	private boolean smsapiEnabled;
	private Integer runningMaxPermits;
	private boolean applied;
	private final Logger logger = LogManager.getLogger(LoggersEnum.ADMIN_UI
			.name());

	public LimiterModel(SendingRateLimiter dbLimits,
			SendingRateLimiter runningLimits) {
		if (dbLimits.getLimiterId().equals(runningLimits.getLimiterId())) {
			campEnabled = dbLimits.isCampEnabled();
			description = dbLimits.getDescription();
			limiterId = dbLimits.getLimiterId();
			maxPermits = dbLimits.getMaxPermits();
			smsapiEnabled = dbLimits.isSmsapiEnabled();
			runningMaxPermits = runningLimits.getMaxPermits();
			// System.out.println("maxPermits: " + maxPermits +
			// "== runningMaxPermits: " +runningMaxPermits);
			if (maxPermits.equals(runningMaxPermits)) {
				// System.out.println(" applied true");
				applied = true;
			} else {
				// System.out.println(" applied false");
				applied = false;
			}

		}
	}

	public LimiterModel(SendingRateLimiter runningLimits) {
		if(runningLimits.isCampEnabled()!=null)
		campEnabled = runningLimits.isCampEnabled();
		description = runningLimits.getDescription();
		limiterId = runningLimits.getLimiterId();
		maxPermits = runningLimits.getMaxPermits();
		if(runningLimits.isSmsapiEnabled()!=null)
		smsapiEnabled = runningLimits.isSmsapiEnabled();
		runningMaxPermits = runningLimits.getMaxPermits();
	}

	public LimiterModel() {

	}

	public List<LimiterModel> convertToLimiterModelList(
			List<SendingRateLimiter> dbLimits,
			List<SendingRateLimiter> runningLimits) {
		List<LimiterModel> result = new ArrayList<LimiterModel>();
		try {
			for (int i = 0; i < dbLimits.size(); i++) {
				for (int j = 0; j < runningLimits.size(); j++) {
					if (dbLimits.get(i).getLimiterId()
							.equals(runningLimits.get(j).getLimiterId())) {
						result.add(new LimiterModel(dbLimits.get(i),
								runningLimits.get(j)));
					}
				}
			}
		} catch (Exception e) {
			logger.error("Ecxception while convert to limiter model" + e);
		}
		return result;

	}

	public List<LimiterModel> getDefaultLimiters(
			List<SendingRateLimiter> runningLimiters) {
		List<LimiterModel> result = new ArrayList<LimiterModel>();

		for (int j = 0; j < runningLimiters.size(); j++) {
			
			if (runningLimiters.get(j).getLimiterId()
					.equals(SystemLimiters.SmsapiDefaultLimiter.name())
					|| runningLimiters.get(j).getLimiterId()
							.equals(SystemLimiters.CampDefaultLimiter.name())) {
				LimiterModel temp = new LimiterModel(runningLimiters.get(j));
				result.add(temp);
			}
		}
		return result;

	}
	
	
	public List<LimiterModel> getSystemLimiters(
			List<SendingRateLimiter> runningLimiters) {
		List<LimiterModel> result = new ArrayList<LimiterModel>();

		for (int j = 0; j < runningLimiters.size(); j++) {
			
			if (runningLimiters.get(j).getLimiterId()
					.equals(SystemLimiters.CampSystemLimiter.name())
					|| runningLimiters.get(j).getLimiterId()
							.equals(SystemLimiters.SmsapiSystemLimiter.name())) {
				LimiterModel temp = new LimiterModel(runningLimiters.get(j));
				result.add(temp);
			}
		}
		return result;

	}

	public SendingRateLimiter convertToEntity(LimiterModel model) {
		SendingRateLimiter entity = new SendingRateLimiter();
		entity.setCampEnabled(model.campEnabled);
		entity.setDescription(model.description);
		if(model.getLimiterId()!=null)
		 entity.setLimiterId(model.limiterId);
		entity.setMaxPermits(model.maxPermits);
		entity.setSmsapiEnabled(model.smsapiEnabled);
		// System.out.println("model : " + model.toString());
		return entity;
	}

	public boolean isCampEnabled() {
		return campEnabled;
	}

	public void setCampEnabled(boolean campEnabled) {
		this.campEnabled = campEnabled;
	}

	public String getDescription() {
		return description;
	}

	public void setDescription(String description) {
		this.description = description;
	}

	public String getLimiterId() {
		return limiterId;
	}

	public void setLimiterId(String limiterId) {
		this.limiterId = limiterId;
	}

	public Integer getMaxPermits() {
		return maxPermits;
	}

	public void setMaxPermits(Integer maxPermits) {
		this.maxPermits = maxPermits;
	}

	public boolean isSmsapiEnabled() {
		return smsapiEnabled;
	}

	public void setSmsapiEnabled(boolean smsapiEnabled) {
		this.smsapiEnabled = smsapiEnabled;
	}

	public Integer getRunningMaxPermits() {
		return runningMaxPermits;
	}

	public void setRunningMaxPermits(Integer runningMaxPermits) {
		this.runningMaxPermits = runningMaxPermits;
	}

	public boolean isApplied() {
		return applied;
	}

	public void setApplied(boolean applied) {
		this.applied = applied;
	}

	@Override
	public String toString() {
		StringBuilder builder = new StringBuilder();
		builder.append("LimiterModel [campEnabled=");
		builder.append(campEnabled);
		builder.append(", description=");
		builder.append(description);
		builder.append(", limiterId=");
		builder.append(limiterId);
		builder.append(", maxPermits=");
		builder.append(maxPermits);
		builder.append(", smsapiEnabled=");
		builder.append(smsapiEnabled);
		builder.append(", runningMaxPermits=");
		builder.append(runningMaxPermits);
		builder.append(", applied=");
		builder.append(applied);
		builder.append("]");
		return builder.toString();
	}
}
