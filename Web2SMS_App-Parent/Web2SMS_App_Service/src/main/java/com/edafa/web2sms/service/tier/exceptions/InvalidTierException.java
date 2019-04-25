package com.edafa.web2sms.service.tier.exceptions;

import com.edafa.web2sms.dalayer.model.Tier;

public class InvalidTierException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 2192037583201945905L;

	Tier tier;

	public InvalidTierException(Tier tier) {
		this.tier = tier;
	}

	@Override
	public String getMessage() {
		StringBuilder sb = new StringBuilder();
		sb.append(tier + " Invalid [");

		if (tier.getTierName() == null) {

			sb.append(sb.charAt(sb.length())== '[' ? " tier name: " + tier.getTierName():", tier name: " + tier.getTierName());
		}
		if (tier.getRatePlan() == null) {
			sb.append(sb.charAt(sb.length())== '[' ? " tier name: " + tier.getRatePlan():", tier name: " + tier.getRatePlan());
		}
		if (tier.getTierId() == null) {
			sb.append(sb.charAt(sb.length())== '[' ? " tier ID: " + tier.getTierId():", tier ID: " + tier.getTierId());
		}
		sb.append("]. ");

		return sb.toString();

	}
}
