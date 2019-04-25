/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.model.constants;

import com.edafa.web2sms.dalayer.model.SystemRole;

/**
 *
 * @author yyaseen
 */
public interface SystemRoleConst {
    String CLASS_NAME = SystemRole.class.getSimpleName(); 
    String PREFIX = CLASS_NAME + ".";
    
    String FIND_BY_ROLE_ID =PREFIX + "findByRoleId";
    
    
    String ROLE_ID = "roleId";
}
