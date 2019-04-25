package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.Service;

/**
 *
 * @author loay
 */
@Local
public interface ServiceDaoLocal extends Cachable<Service, String> {

}
