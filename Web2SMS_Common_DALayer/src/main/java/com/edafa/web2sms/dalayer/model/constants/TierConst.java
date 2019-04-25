/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Tier;

/**
 * 
 * @author yyaseen
 */
public interface TierConst {
	String CLASS_NAME = Tier.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_ALL = PREFIX + "findAll";
	String FIND_BY_RATE_PLAN = PREFIX + "findByRatePlan";
	String FIND_BY_TIER_TYPE = PREFIX + "findByTierType";
	String FIND_BY_TIER_ID = PREFIX + "findByTierId";
	String REMOVE_BY_TIER_ID = PREFIX + "removeByTierId";
	String FIND_TOTAL_QUOTA_BY_TIER_ID = PREFIX + "findTotalQuotaByTierId";

	String TIER_Type = "tierType";
	String TIER_ID = "tierId";
	String RATE_PLAN  = "ratePlan";
}
