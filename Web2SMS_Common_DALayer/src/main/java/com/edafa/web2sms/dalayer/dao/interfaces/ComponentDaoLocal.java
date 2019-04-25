package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.Component;

/**
 *
 * @author loay
 */
@Local
public interface ComponentDaoLocal extends Cachable<Component, String> {

}
