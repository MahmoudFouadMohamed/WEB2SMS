/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Action;

/**
 *
 * @author mahmoud
 */
public interface ActionConst {
    
    	String CLASS_NAME = Action.class.getSimpleName();
	String PREFIX = CLASS_NAME + ".";

	String FIND_BY_ACCOUNT_ID_AND_USERNAME = PREFIX + "findByAccountIdAndUserName";
    
}
