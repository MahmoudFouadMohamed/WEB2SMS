package com.edafa.web2sms.sms.caching;

import javax.ejb.Local;

import com.edafa.web2sms.sms.file.WritingCountMaxReachedException;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;

/**
 *
 * @author loay, emad, fouad
 */
@Local
public interface CachingManagementBeanLocal {

    public void updateConfiguration();

    public int getCacheMaxCount();

    public boolean cacheCampaignRequest(SubmitDetailedCampaignRequest request)
            throws CacheLimitException, WritingCountMaxReachedException;

    public boolean cacheSmsRequest(SubmitDetailedSMSRequest request, boolean all)
            throws CacheLimitException, WritingCountMaxReachedException;

    public boolean cacheRequest(Object request) throws CacheLimitException, WritingCountMaxReachedException;

    public Object getNextCachedElement();

    public int getCacheSize();

    public boolean isCacheEmpty();

    public String setCacheProcessingBeanName();

    public boolean canRunCacheProcessingBean();

    public void decrementRunningCacheProcessingBean();
}
