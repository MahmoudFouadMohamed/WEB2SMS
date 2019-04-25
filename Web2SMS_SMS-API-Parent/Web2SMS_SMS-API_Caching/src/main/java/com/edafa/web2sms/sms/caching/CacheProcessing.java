package com.edafa.web2sms.sms.caching;

import javax.ejb.Local;

/**
 *
 * Emad
 */
@Local
public interface CacheProcessing {

    public void processCache();

    public void startCaching();

    public void stopCaching();

}
