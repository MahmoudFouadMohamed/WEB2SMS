package com.edafa.web2sms.sms.file;

import java.util.Collection;

import javax.ejb.Local;

import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;

/**
 *
 * @author mfouad
 */
@Local
public interface FileManagementBeanLocal {

    public boolean addExpiredCampaignRequest(SubmitDetailedCampaignRequest request)
            throws WritingCountMaxReachedException;

    public boolean addExpiredSmsRequest(SubmitDetailedSMSRequest request) throws WritingCountMaxReachedException;

    public boolean addExpiredCampaignRequests(Collection<? extends SubmitDetailedCampaignRequest> requests)
            throws WritingCountMaxReachedException;

    public boolean addExpiredSmsRequests(Collection<? extends SubmitDetailedSMSRequest> requests)
            throws WritingCountMaxReachedException;

    public boolean addExpiredRequest(Object request) throws WritingCountMaxReachedException;

    public boolean addExpiredRequests(Collection<? extends Object> requests) throws WritingCountMaxReachedException;

    public boolean isExpiredMaxWriteReatched();

    public Object[] readExpiredDataChunk();

    public boolean addCachedCampaignRequest(SubmitDetailedCampaignRequest request)
            throws WritingCountMaxReachedException;

    public boolean addCachedSmsRequest(SubmitDetailedSMSRequest request) throws WritingCountMaxReachedException;

    public boolean addCachedCampaignRequests(Collection<? extends SubmitDetailedCampaignRequest> requests)
            throws WritingCountMaxReachedException;

    public boolean addCachedSmsRequests(Collection<? extends SubmitDetailedSMSRequest> requests)
            throws WritingCountMaxReachedException;

    public boolean addCachedRequest(Object request) throws WritingCountMaxReachedException;

    public boolean addCachedRequests(Collection<? extends Object> requests) throws WritingCountMaxReachedException;

    public boolean isCachedMaxWriteReatched();

    public Object[] readCachedDataChunk();

    public void startFiles();

    public void stopFiles();
}
