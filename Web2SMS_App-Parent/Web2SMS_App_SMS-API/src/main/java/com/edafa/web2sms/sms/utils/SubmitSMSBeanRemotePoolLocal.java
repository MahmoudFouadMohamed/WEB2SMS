/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.sms.utils;

import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import javax.ejb.Local;

/**
 *
 * @author mahmoud
 */
@Local
public interface SubmitSMSBeanRemotePoolLocal {

    SubmitSMSBeanRemote getSubmitSMSBeanRemote() throws Exception;

    void returnSubmitSMSBeanRemote(SubmitSMSBeanRemote submitSMSBeanRemote) throws Exception;

}
