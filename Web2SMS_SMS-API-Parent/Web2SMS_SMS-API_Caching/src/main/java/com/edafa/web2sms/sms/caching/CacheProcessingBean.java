package com.edafa.web2sms.sms.caching;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.EJB;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.utils.loadbalancer.entity.Entity;
import com.edafa.utils.loadbalancer.exception.NoSuchEntityException;
import com.edafa.utils.loadbalancer.exception.NoSuchGroupException;
import com.edafa.web2sms.sms.enums.ResultStatus;
import com.edafa.web2sms.sms.enums.SMSResponseStatus;
import com.edafa.web2sms.sms.file.FileManagementBeanLocal;
import com.edafa.web2sms.sms.file.WritingCountMaxReachedException;
import com.edafa.web2sms.sms.model.Cacheable;
import com.edafa.web2sms.sms.model.ExpiredResponse;
import com.edafa.web2sms.sms.model.Retriable;
import com.edafa.web2sms.sms.model.SubmitCampaignResponse;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.model.SubmitSMSResponse;
import com.edafa.web2sms.sms.utils.lb.LoadBalancerLocal;
import com.edafa.web2sms.utils.XmlHttpClient;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import com.sun.jersey.api.client.ClientResponse;
import javax.ejb.Asynchronous;
import javax.ejb.DependsOn;
import javax.ejb.Stateless;

/**
 *
 * @author emad
 */
@Stateless
@DependsOn("LoggingManagerBean")
public class CacheProcessingBean implements CacheProcessing {

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    Logger smsCachingLogger = LogManager.getLogger(LoggersEnum.SMS_API_CACHING.name());

    @EJB
    private XmlHttpClient httpClient;

    @EJB
    private LoadBalancerLocal loadBalancer;

    @EJB
    private FileManagementBeanLocal fileManagementBean;

    @EJB
    private CachingManagementBeanLocal cachingManagementBean;

    private String groupId = null;
    private String name = null;
    private String logId;

    @PostConstruct
    @Override
    public void startCaching() {
        if (name == null) {
            name = cachingManagementBean.setCacheProcessingBeanName();
            logId = name + " | ";
        }
        groupId = (String) Configs.SMS_API_SERVICE_LB_GROUP_ID.getValue();
        smsCachingLogger.info(logId + "startCaching : initialized successfully.");
    }

    @PreDestroy
    public void destroy() {
        stopCaching();
    }

    @Override
    public void stopCaching() {
        smsCachingLogger.info(logId + "stopCaching");
    }

    @Override
    @Asynchronous
    public void processCache() {
        smsCachingLogger.info(logId + "START processCache");
        if (!cachingManagementBean.canRunCacheProcessingBean()) {
            // There is a running instances now, return
            if (smsCachingLogger.isTraceEnabled()) {
                smsCachingLogger.trace(logId + "There is a running " + ((int) Configs.CACHE_RETRY_THREAD_COUNT.getValue()) + " instances now, return");
            }
            return;
        }
        long time = System.currentTimeMillis();
        int requestsProcessed = processCacheChunk();
        time = System.currentTimeMillis() - time;
        cachingManagementBean.decrementRunningCacheProcessingBean();
        smsCachingLogger.info(logId + "END processCache .. " + requestsProcessed + " requests, time=" + time);
    }

    private int processCacheChunk() {
        int requestsProcessed = 0;
        try {
            smsCachingLogger.info(logId + "CacheSize=" + cachingManagementBean.getCacheSize());

            // Check service availability
            if (!isServiceAvailable()) {
                smsCachingLogger.warn(logId + "SMS API service NOT available.");
                return requestsProcessed;
            }

            // Check for available chunk size
            int chunkSize = (Integer) Configs.CACHE_RETRY_CHUNK_SIZE.getValue();

            int availableChunkSize = cachingManagementBean.getCacheSize() > chunkSize ? chunkSize
                    : cachingManagementBean.getCacheSize();
            if (smsCachingLogger.isDebugEnabled()) {
                if (availableChunkSize > 0) {
                    smsCachingLogger
                            .debug(logId + "There are cahched requests, will handle " + availableChunkSize + " request.");
                } else {
                    smsCachingLogger.debug(logId + "There are no cahched requests.");
                }
            }

            int i;
            // Loop on the available chunk size and retry sending
            for (i = 0; i < availableChunkSize; i++) {
                Object cachedElement = cachingManagementBean.getNextCachedElement();

                if (cachedElement == null) {
                    return requestsProcessed + i; //isMinimumTimePassed=false
                }

                if (smsCachingLogger.isTraceEnabled()) {
                    smsCachingLogger.trace(logId + "Will handle cached element : " + cachedElement);
                }

                if (!handleCachedRequest(cachedElement)) {
                    smsCachingLogger.warn(cachedElement + " NOT handeled successfuly, will re cache it again.");
                    reCacheRequest(cachedElement);
                    return requestsProcessed + i;
                }
            }
            requestsProcessed += i;

            if (cachingManagementBean.isCacheEmpty()) {
                if (smsCachingLogger.isDebugEnabled()) {
                    smsCachingLogger.debug(logId + "Try to handle requests from files.");
                }
                Object[] cachedDataChunk = fileManagementBean.readCachedDataChunk();
                if (cachedDataChunk != null) {
                    if (smsCachingLogger.isDebugEnabled()) {
                        smsCachingLogger.debug(logId + "Will handle requests from files, count=" + cachedDataChunk.length);
                    }
                    for (i = 0; i < cachedDataChunk.length; i++) {
                        try {
                            if (!handleCachedRequest(cachedDataChunk[i])) {
                                smsCachingLogger.warn(
                                        cachedDataChunk[i] + " NOT handeled successfuly, will re cache it again.");
                                reCacheRequest(cachedDataChunk[i]);
                            }
                        } catch (Exception e) {
                            smsCachingLogger.warn(
                                    "ERROR during handeling " + cachedDataChunk[i] + " , will re cache it again.");

                            if (cachedDataChunk[i] instanceof Retriable) {
                                ((Retriable) cachedDataChunk[i]).incrementRetryCount();
                            }

                            reCacheRequest(cachedDataChunk[i]);
                        }
                    }
                    smsCachingLogger.info(logId + "Requests from files handled successfuly, count=" + i);
                    requestsProcessed += i;
                } else {
                    Object[] expiredDataChunk = fileManagementBean.readExpiredDataChunk();
                    if (expiredDataChunk != null) {
                        if (smsCachingLogger.isDebugEnabled()) {
                            smsCachingLogger.debug(logId + "Will handle expired requests from files, count=" + expiredDataChunk.length);
                        }
                        for (i = 0; i < expiredDataChunk.length; i++) {
                            try {
                                if (!handleCachedRequest(expiredDataChunk[i])) {
                                    smsCachingLogger.warn(
                                            expiredDataChunk[i] + " NOT handeled successfuly, will re cache it again.");
                                    reCacheRequest(expiredDataChunk[i]);
                                }
                            } catch (Exception e) {
                                smsCachingLogger.warn(
                                        "ERROR during handeling " + expiredDataChunk[i] + " , will re cache it again.");

                                if (expiredDataChunk[i] instanceof Retriable) {
                                    ((Retriable) expiredDataChunk[i]).incrementRetryCount();
                                }

                                reCacheRequest(expiredDataChunk[i]);
                            }
                        }
                        smsCachingLogger.info(logId + "Expired requests from files handled successfuly, count=" + i);
                        requestsProcessed += i;
                    }
                }
            }
        } catch (NoSuchGroupException e) {
            smsCachingLogger.error(logId + e.getMessage() + " " + groupId);
            appLogger.error(logId + e.getMessage() + " " + groupId, e);
        } catch (Exception e) {
            smsCachingLogger.error(logId + "UnhandledException: " + e.getMessage());
            appLogger.error(logId + "UnhandledException: " + e.getMessage(), e);
        }
        return requestsProcessed;
    }

    private boolean handleCachedRequest(Object cachedElement)
            throws NoSuchGroupException, WritingCountMaxReachedException, CacheLimitException {
        // Check cachedElement type [SMS or Campaign]
        if (cachedElement instanceof SubmitDetailedSMSRequest) {
            // Retry sending Submit SMS Request
            SubmitDetailedSMSRequest submitReq = (SubmitDetailedSMSRequest) cachedElement;
            if (isExpiredRequest(submitReq)) {
                handleExpiredRequest(submitReq);
            } else {
                Entity selectedEntity = loadBalancer.selectEntity(groupId);
                // check null for selectedEntity
                if (selectedEntity == null) {
                    return false;
                } else {
                    return retrySubmittingSms(submitReq, selectedEntity);
                }
            }
        } else if (cachedElement instanceof SubmitDetailedCampaignRequest) {
            SubmitDetailedCampaignRequest request = (SubmitDetailedCampaignRequest) cachedElement;
            if (isExpiredRequest(request)) {
                handleExpiredCampaign(request);
            } else {
                Entity selectedEntity = loadBalancer.selectEntity(groupId);
                // check null for selectedEntity
                if (selectedEntity == null) {
                    return false;
                } else {
                    return retrySubmittingCampaign(request, selectedEntity);
                }
            }
        }

        return true;
    }

    private void reCacheRequest(Object cachedElement) {
        try {
            cachingManagementBean.cacheRequest(cachedElement);
        } catch (CacheLimitException | WritingCountMaxReachedException e) {
            if (cachedElement instanceof SubmitDetailedSMSRequest) {
                smsCachingLogger
                        .error(logId + "Can NOT recache SMS request" + ((SubmitDetailedSMSRequest) cachedElement).getTrxId());
            } else if (cachedElement instanceof SubmitDetailedCampaignRequest) {
                smsCachingLogger.error(logId + "Can NOT recache Campaign request"
                        + ((SubmitDetailedCampaignRequest) cachedElement).getTrxId());
            }
        }
    }

    private void handleExpiredCampaign(SubmitDetailedCampaignRequest request) {
        ResultStatus result = null;

        if (!loadBalancer.isTotalOutage()) {
            try {
                Entity selectedEntity = loadBalancer.selectEntity(groupId);
                if (selectedEntity != null) {
                    String path = (String) Configs.SMS_API_LOG_CAMPAIN_SERVICE_PATH.getValue();

                    result = logRequest(request, selectedEntity, path);
                }
            } catch (NoSuchGroupException e) {
                smsCachingLogger.error(logId + "NoSuchGroupException[" + groupId + "]");
                reCacheRequest(request);
            }
        }

        if (result == ResultStatus.SUCCESS) {
            smsCachingLogger.info(logId + request.getTrxId() + " | logged to DB succesfully");
        } else {
            smsCachingLogger.info(logId + request.getTrxId() + " | Can NOT log to DB, try to recache it again, result= " + result);
            try {
                if (result == ResultStatus.INTERNAL_SERVER_ERROR) {
                    request.incrementExpiredRetryCount();
                }
                if (!isFailedRequest(request)) {
                    boolean addedToExpiredFiles = fileManagementBean.addExpiredRequest(request);
                    if (addedToExpiredFiles) {
                        if (smsCachingLogger.isDebugEnabled()) {
                            smsCachingLogger.debug(logId + request.getTrxId() + " | Recached as Expired request.");
                        }
                    } else {
                        smsCachingLogger.warn(logId + request.getTrxId() + " | Can NOT Recach Expired request " + request);
                    }
                } else {
                    smsCachingLogger.warn(logId + request.getTrxId() + " | Can NOT handle " + request);
                }
            } catch (WritingCountMaxReachedException ex) {
                reCacheRequest(request);
            }
        }
    }

    private boolean isServiceAvailable() {
        try {
            return loadBalancer.hasAvailableEntity(groupId);
        } catch (NoSuchEntityException e) {
            smsCachingLogger.error(logId + e.getMessage() + groupId, e);
        } catch (NoSuchGroupException e) {
            smsCachingLogger.error(logId + e.getMessage() + groupId, e);
        }

        return false;
    }

    private boolean retrySubmittingCampaign(SubmitDetailedCampaignRequest request, Entity selectedEntity)
            throws CacheLimitException, NoSuchGroupException, WritingCountMaxReachedException {

        String path = (String) Configs.SMS_API_SUBMIT_CAMPAIGN_SERVICE_PATH.getValue();

        String trxId = request.getTrxId();
        request.setTrxId(trxId + "-" + request.getRetryCount());

        ClientResponse cr = sendHttpRequest(request, selectedEntity, path);

        request.setTrxId(trxId);

        if (cr != null) {
            SubmitCampaignResponse response = cr.getEntity(SubmitCampaignResponse.class);
            if (smsCachingLogger.isDebugEnabled()) {
                smsCachingLogger.debug(logId + trxId + response);
            }
            ResultStatus status = response.getResultStatus();

            checkClientResponse(request, selectedEntity, status, response);
            return true;
        } else {
            smsCachingLogger.error(logId + trxId + " | Client response is null");
            return false;
        }

    }

    private boolean retrySubmittingSms(SubmitDetailedSMSRequest submitReq, Entity selectedEntity)
            throws CacheLimitException, NoSuchGroupException, WritingCountMaxReachedException {

        String path = (String) Configs.SMS_API_SUBMIT_SMS_SERVICE_PATH.getValue();

        String trxId = submitReq.getTrxId();
        submitReq.setTrxId(trxId + "-" + (submitReq.getRetryCount() + 1));

        ClientResponse cr = sendHttpRequest(submitReq, selectedEntity, path);

        submitReq.setTrxId(trxId);

        if (cr != null) {
            SubmitSMSResponse response = cr.getEntity(SubmitSMSResponse.class);
            if (smsCachingLogger.isDebugEnabled()) {
                smsCachingLogger.debug(logId + trxId + " | " + response);
            }
            ResultStatus status = response.getResultStatus();

            checkClientResponse(submitReq, selectedEntity, status, response);
            return true;

        } else {
            smsCachingLogger.error(logId + trxId + " | Client response is null");
            return false;
        }
    }

    private ClientResponse sendHttpRequest(Object submitReq, Entity selectedEntity, String path) {
        String smsServiceURI = selectedEntity.getEntityData() + path;
        smsCachingLogger.info(logId + "Will call SMSAPI service");

        if (smsCachingLogger.isTraceEnabled()) {
            smsCachingLogger.trace(logId + "SMSAPI service URI: " + smsServiceURI + ", will send : " + submitReq);
        }

        ClientResponse cr = httpClient.sendHttpXmlRequest(smsServiceURI, submitReq, "SMS");
        if (smsCachingLogger.isTraceEnabled()) {
            smsCachingLogger.trace(logId + "Returned from API Service with: " + cr);
        }

        return cr;
    }

    private void checkClientResponse(Cacheable request, Entity selectedEntity, ResultStatus status, Object response)
            throws CacheLimitException, NoSuchGroupException, WritingCountMaxReachedException {
        //check response
        smsCachingLogger.info(logId + "Returned from SMSAPI service with status " + status);

        if (status == ResultStatus.INTERNAL_SERVER_ERROR) {
            if (request instanceof SubmitDetailedSMSRequest) {
                SubmitDetailedSMSRequest req = checkInvalidClientResponse(response, request);
                req.incrementRetryCount();
            } else if (request instanceof SubmitDetailedCampaignRequest) {
                smsCachingLogger.warn(logId + "Response status is " + status + ", will cache the request again");
                request.incrementRetryCount();
                reCacheRequest(request);
            } else {
                smsCachingLogger.warn("Invalid element=" + request);
            }
        } else if (status == ResultStatus.RESEND_CACHE_REQUEST_FAILED) {
            if (request instanceof SubmitDetailedSMSRequest) {
                checkInvalidClientResponse(response, request);
            } else {
                smsCachingLogger.warn(logId + "Invalid element=" + request);
            }
        } else {
            // report success
            loadBalancer.reportSuccess(groupId, selectedEntity);
        }
    }

    private SubmitDetailedSMSRequest checkInvalidClientResponse(Object response, Cacheable request) {
        SubmitSMSResponse res = (SubmitSMSResponse) response;
        SubmitDetailedSMSRequest req = (SubmitDetailedSMSRequest) request;
        if (res.getSmsStatus() != null && !res.getSmsStatus().isEmpty()) {
            for (int i = 0; i < res.getSmsStatus().size(); i++) {
                SMSResponseStatus smsStatus = res.getSmsStatus().get(i);
                if (SMSapiUtils.checkSMSFailed(smsStatus)) {
                    req.getSMSs().get(i).setCachedSMS(true);
                } else {
                    req.getSMSs().get(i).setCachedSMS(false);
                }
            }
        }
        reCacheRequest(req);
        return req;
    }

    private void handleExpiredRequest(SubmitDetailedSMSRequest submitReq) {
        ResultStatus result = null;
        if (!loadBalancer.isTotalOutage()) {

            try {
                Entity selectedEntity = loadBalancer.selectEntity(groupId);
                if (selectedEntity != null) {
                    String path = (String) Configs.SMS_API_LOG_SMS_SERVICE_PATH.getValue();

                    result = logRequest(submitReq, selectedEntity, path);
                }
            } catch (NoSuchGroupException e) {
                smsCachingLogger.error(logId + "NoSuchGroupException[" + groupId + "]");
                reCacheRequest(submitReq);
            }

        }

        if (result == ResultStatus.SUCCESS) {
            smsCachingLogger.info(logId + submitReq.getTrxId() + " | logged to DB succesfully");
        } else {
            smsCachingLogger.info(logId + submitReq.getTrxId() + " | Can NOT log to DB, try to recache it again, result= " + result);
            try {
                if (result == ResultStatus.INTERNAL_SERVER_ERROR) {
                    submitReq.incrementExpiredRetryCount();
                }
                if (!isFailedRequest(submitReq)) {
                    boolean addedToExpiredFiles = fileManagementBean.addExpiredRequest(submitReq);
                    if (addedToExpiredFiles) {
                        if (smsCachingLogger.isDebugEnabled()) {
                            smsCachingLogger.debug(logId + submitReq.getTrxId() + " | Recached as Expired request.");
                        }
                    } else {
                        smsCachingLogger.warn(logId + submitReq.getTrxId() + " | Can NOT Recach Expired request " + submitReq);
                    }
                } else {
                    smsCachingLogger.warn(logId + submitReq.getTrxId() + " | Can NOT handle " + submitReq);
                }
            } catch (WritingCountMaxReachedException ex) {
                reCacheRequest(submitReq);
            }
        }
    }

    private static boolean isExpiredRequest(Cacheable request) {
        long submitTime = request.getCacheDate().getTime();
        return isExpired0(request.getValidityMinutes(), submitTime, request.getRetryCount());
    }

    private static boolean isExpired0(int validityMinutes, long time, int retryCount) {
        long now = System.currentTimeMillis();
        return (retryCount >= ((int) Configs.CACHE_RETRY_COUNT.getValue()))
                || ((now - time) >= (validityMinutes * 60 * 1000));
    }

    private boolean isFailedRequest(SubmitDetailedCampaignRequest request) {
        return isFailed0(request.getRetryExpiredCount());
    }

    private static boolean isFailedRequest(SubmitDetailedSMSRequest request) {
        return isFailed0(request.getRetryExpiredCount());
    }

    private static boolean isFailed0(int retryExpiredCount) {
        return retryExpiredCount >= ((int) Configs.CACHE_RETRY_EXPIRED_COUNT.getValue());
    }

    private ResultStatus logRequest(Object req, Entity selectedEntity, String path) {
        ClientResponse cr = sendHttpRequest(req, selectedEntity, path);

        if (cr != null) {
            if (cr.getStatus() != ClientResponse.Status.OK.getStatusCode()) {
                smsCachingLogger.warn(logId + "ClientResponse.Status : " + cr.getStatus());
                return ResultStatus.GENERIC_ERROR;
            }

            ExpiredResponse response = cr.getEntity(ExpiredResponse.class);

            if (response.getResultStatus() == ResultStatus.SUCCESS) {
                if (smsCachingLogger.isDebugEnabled()) {
                    smsCachingLogger.debug(logId + "logged succesfully.");
                }
            } else {
                smsCachingLogger.info(logId + "NOT logged to DB, status=" + response.getResultStatus());
            }
            if (response.getResultStatus() == null) {
                response.setResultStatus(ResultStatus.GENERIC_ERROR);
            }
            return response.getResultStatus();
        } else {
            smsCachingLogger.error(logId + "Client response is null");
            return ResultStatus.GENERIC_ERROR;
        }
    }

}
