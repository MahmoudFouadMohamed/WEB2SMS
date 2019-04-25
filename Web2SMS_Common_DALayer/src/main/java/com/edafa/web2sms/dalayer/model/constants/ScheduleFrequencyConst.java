/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.ScheduleFrequency;

/**
 * 
 * @author yyaseen
 */
public interface ScheduleFrequencyConst {
	String CLASS_NAME = ScheduleFrequency.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_SCHEDULE_FREQ_ID = PREFIX + "findByScheduleFreqId";
	String FIND_BY_SCHEDULE_FREQ_NAME = PREFIX + "findByScheduleFreqName";

	String ID = "scheduleFreqId";
	String NAME = "scheduleFreqName";
}
