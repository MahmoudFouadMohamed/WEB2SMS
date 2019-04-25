package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.FtpServer;

/**
 *
 * @author loay
 */
@Local
public interface FtpServerDaoLocal extends Cachable<FtpServer, Integer> {

}
