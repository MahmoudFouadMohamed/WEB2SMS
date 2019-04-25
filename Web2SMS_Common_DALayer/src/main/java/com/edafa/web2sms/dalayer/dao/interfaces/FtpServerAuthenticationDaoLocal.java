package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;

/**
 *
 * @author loay
 */
@Local
public interface FtpServerAuthenticationDaoLocal extends Cachable<String, String> {

    public String getUsername();

    public String getPassword();
}
