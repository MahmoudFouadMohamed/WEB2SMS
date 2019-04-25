/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.Admin;

/**
 *
 * @author yyaseen
 */
public interface AdminConst {
    String CLASS_NAME = Admin.class.getSimpleName();
    String PREFIX = CLASS_NAME + ".";
    
    String FIND_BY_ADMIN_NAME = PREFIX + "findByAdminName";
    String FIND_BY_USER_NAME = PREFIX + "findByUsername";
    String FIND_PASSWORD = PREFIX + "findPassword";
    
    String USERNAME = "username";
    
    
    
}
