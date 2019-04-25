package com.edafa.web2sms.sms.caching;

import java.util.ArrayList;
import java.util.Date;
import java.util.LinkedList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.annotation.Resource;
import javax.ejb.DependsOn;
import javax.ejb.EJB;
import javax.ejb.Singleton;
import javax.ejb.Startup;
import javax.ejb.Timeout;
import javax.ejb.Timer;
import javax.ejb.TimerService;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.utils.loadbalancer.entity.Entity;
import com.edafa.utils.loadbalancer.exception.DuplicateEntityException;
import com.edafa.utils.loadbalancer.exception.InvalidParameterException;
import com.edafa.web2sms.sms.file.FileManagementBean;
import com.edafa.web2sms.sms.file.FileManagementBeanLocal;
import com.edafa.web2sms.sms.file.WritingCountMaxReachedException;
import com.edafa.web2sms.sms.model.Cacheable;
import com.edafa.web2sms.sms.model.SMSDetails;
import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.sms.utils.lb.LoadBalancerLocal;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import java.util.concurrent.atomic.AtomicBoolean;
import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author loay, emad, fouad
 */
@Singleton
@Startup
@DependsOn("LoggingManagerBean")
public class CachingManagementBean extends FileManagementBean implements CachingManagementBeanLocal, FileManagementBeanLocal {

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    Logger smsCachingLogger = LogManager.getLogger(LoggersEnum.SMS_API_CACHING.name());

    @EJB
    private LoadBalancerLocal loadBalancer;

    @Resource
    private TimerService timerService;

    @EJB
    private CacheProcessing cacheProcessing;

    private LinkedList<Object> cache;
    private String groupId;
    private AtomicBoolean cacheRunning;
    private final AtomicInteger cacheProcessingBeanNumber = new AtomicInteger();
    private final AtomicInteger runningCacheProcessingBean = new AtomicInteger();

    private String name = null;
    private String logId;
    private boolean destroy;

    @PostConstruct
    public void init() {
        startFiles();
        if (name == null) {
            name = "CachingManagementBean";
            logId = name + " | ";
        }
        destroy = false;
        cancelActiveTimers();

        startTimer();
        startCaching();
    }

    private void startCaching() {
        try {
            // read values from configuration
            smsCachingLogger.info(logId + "init");
            cacheRunning = new AtomicBoolean(true);

            smsCachingLogger.info(logId + "startCaching : read config");
            smsCachingLogger.info(logId + "startCaching : cacheMaxCount="
                    + Configs.CACHE_RECORDS_MAX_COUNT.getValue());

            smsCachingLogger.info(logId + "startCaching : create cache");
            cache = new LinkedList<Object>();

            // init load balancing with app entities
            addEntitiesToLoadBalancer();
        } catch (Exception e) {
            smsCachingLogger.error(logId + "startCaching : Exception : " + e.getMessage());
            appLogger.error(logId + "startCaching : Exception", e);
        }
        smsCachingLogger.info(logId + "startCaching : start caching successfully.");
    }

    private void startTimer() {
        smsCachingLogger.info(logId + "startTimer : read timer config");
        int initialDuration = (int) Configs.CACHE_RETRY_INITIAL_DURATION.getValue();
        int intervalDuration = (int) Configs.CACHE_RETRY_INTERVAL_DURATION.getValue();
        smsCachingLogger.info(logId + "startTimer : initialDuration=" + initialDuration,
                "intervalDuration=" + intervalDuration);

        timerService.createIntervalTimer(initialDuration, intervalDuration, null);
        smsCachingLogger.info(logId + "startTimer : timer created, will process caching requests after "
                + initialDuration);
    }

    private void addEntitiesToLoadBalancer() {
        groupId = (String) Configs.SMS_API_SERVICE_LB_GROUP_ID.getValue();
        smsCachingLogger.info(logId + "addEntitiesToLoadBalancer : groupId=" + groupId);

        List<?> entityListConfig = (List<?>) Configs.SMS_API_SERVICE_LB_GROUP_ENTITES.getValue();
        String separator = (String) Configs.SMS_API_SERVICE_LB_GROUP_SEPARATOR.getValue();

        List<Entity> entityList = new ArrayList<>();
        for (Object object : entityListConfig) {
            Entity entity = createEntity((String) object, separator);
            entityList.add(entity);
            smsCachingLogger.info(logId + "entityList.add : entity=" + entity);
        }

        try {
            loadBalancer.addGroupList(groupId, entityList);
        } catch (InvalidParameterException | DuplicateEntityException e) {
            smsCachingLogger.error("Error initializing the load balancer, groupId=" + groupId);
            appLogger.error("Error initializing the load balancer", e);
        }
    }

    @PreDestroy
    public void preDestroy() {
        smsCachingLogger.info(logId + "destroy");
        destroy = true;
        stopCaching();
        cancelActiveTimers();
        destroy();
    }

    private void stopCaching() {
        smsCachingLogger.info(logId + "stopCaching : dumping cache into cache file");
        if (cache != null) {
            smsCachingLogger
                    .info(logId + "stopCaching: Will stop caching requests, cache.size=" + cache.size());

            try {
                smsCachingLogger.info(logId + "stopCaching : dumping cache into cache file");
                addCachedRequests(cache);
                cache.clear();
                cache = null;
            } catch (Exception e) {
                smsCachingLogger.error(logId + "stopCaching : Unhandled Exception : " + e.getMessage());
                appLogger.error(logId + "stopCaching : Unhandled Exception : ", e);
            }
        }
        smsCachingLogger.info(logId + "stopCaching: Caching requests stopped successfully.");
    }

    private void cancelActiveTimers() {
        try {
            if (!timerService.getTimers().isEmpty()) {
                for (Object obj : timerService.getTimers()) {
                    Timer t = (Timer) obj;
                    t.cancel();
                }
            }
        } catch (Exception e) {
            appLogger.error("Unhandled Exception during cancelActiveTimers: ", e);
        }
    }

    @Timeout
    public void atSchedule() {
        if (destroy) {
            return;
        }
        if ((boolean) Configs.CACHE_REQUEST.getValue()) {
            if (cacheRunning.compareAndSet(false, true)) {
                smsCachingLogger.info(logId + "atSchedule: Will RESUME caching");

//				loadBalancer.startLoadBalancer();
//				smsCachingLogger.info(logId + "atSchedule: LoadBalancerBean resumed");
                startFiles();
                smsCachingLogger.info(logId + "atSchedule: FileManagementBean resumed");
                cacheProcessing.startCaching();
                smsCachingLogger.info(logId + "atSchedule: CacheProcessingBean resumed");
                startCaching();
                smsCachingLogger.info(logId + "atSchedule: CachingManagementBean resumed");

                smsCachingLogger.info(logId + "atSchedule: Caching RESUMED successfully.");
            }

            if (smsCachingLogger.isTraceEnabled()) {
                smsCachingLogger.trace(logId + "atSchedule: cacheProcessing.processCache");
            }
            for (int i = 0; i < ((int) Configs.CACHE_RETRY_THREAD_COUNT.getValue()); i++) {
                cacheProcessing.processCache();
            }

        } else if (cacheRunning.compareAndSet(true, false)) {
            smsCachingLogger.info(logId + "atSchedule: Will STOP caching");

            stopCaching();
            smsCachingLogger.info(logId + "atSchedule: CachingManagementBean stopped");
            cacheProcessing.stopCaching();
            smsCachingLogger.info(logId + "atSchedule: CacheProcessingBean stopped");
//			loadBalancer.cleanup();
//			smsCachingLogger.info(logId + "atSchedule: LoadBalancerBean stopped");
            stopFiles();
            smsCachingLogger.info(logId + "atSchedule: FileManagementBean stopped");

            smsCachingLogger.info(logId + "atSchedule: Caching STOPPED successfully.");
        } else {
            if (smsCachingLogger.isTraceEnabled()) {
                smsCachingLogger.trace(logId + "atSchedule: There are NO caching process.");
            }
        }
    }

    private Entity createEntity(String entitySpec, String separator) {
        Entity newEntity = null;
        String[] entitySpecArr = entitySpec.split(separator);
        if (entitySpecArr.length == 1) {
            newEntity = new Entity(entitySpecArr[0]);
        } else if (entitySpecArr.length == 2) {
            newEntity = new Entity(entitySpecArr[0], Long.parseLong(entitySpecArr[1]));
        } else if (entitySpecArr.length == 3) {
            newEntity = new Entity(entitySpecArr[0], entitySpecArr[1], Long.parseLong(entitySpecArr[2]));
        } else if (entitySpecArr.length == 4) {
            newEntity = new Entity(entitySpecArr[0], entitySpecArr[1], Long.parseLong(entitySpecArr[2]),
                    Long.parseLong(entitySpecArr[3]));
        }
        return newEntity;
    }

    @Override
    public boolean cacheCampaignRequest(SubmitDetailedCampaignRequest request)
            throws CacheLimitException, WritingCountMaxReachedException {
        request.setCachedRequest(true);
        request.setCacheDate(new Date());
        request.setValidityMinutes((int) Configs.CACHE_REQUEST_VALIDITY_MINUTES.getValue());
        return cacheRequest(request);
    }

    @Override
    public boolean cacheSmsRequest(SubmitDetailedSMSRequest request, boolean all)
            throws CacheLimitException, WritingCountMaxReachedException {
        request.setCachedRequest(true);
        request.setCacheDate(new Date());
        request.setValidityMinutes((int) Configs.CACHE_REQUEST_VALIDITY_MINUTES.getValue());
        if (all) {
            for (SMSDetails sMSDetails : request.getSMSs()) {
                sMSDetails.setCachedSMS(true);
            }
        }
        return cacheRequest(request);
    }

    @Override
    public boolean cacheRequest(Object request) throws CacheLimitException, WritingCountMaxReachedException {
        synchronized (cache) {
            Integer cacheMaxCount = (Integer) Configs.CACHE_RECORDS_MAX_COUNT.getValue();

            if (cache.size() < cacheMaxCount) {
                return cache.add(request);
            } else if (!isCachedMaxWriteReatched()) {
                return addCachedRequest(request);
            } else {
                throw new CacheLimitException("Cache reatched limit cacheMaxCount=" + cacheMaxCount);
            }
        }
    }

    @Override
    public Object getNextCachedElement() {
        synchronized (cache) {

            Cacheable cachedObj = (Cacheable) cache.poll();
            if (cachedObj == null) {
                return null;
            }
            if (isMinimumTimePassed(cachedObj)) {
                return cachedObj;
            } else {
                cache.addFirst(cachedObj);
                return null;
            }
        }
    }

    private boolean isMinimumTimePassed(Cacheable cachedObj) {
        long time = cachedObj.getCacheDate().getTime();
        long now = System.currentTimeMillis();
        int minimumMinutes = (int) Configs.CACHE_RETRY_MINIMUM_SECONDS.getValue();
        return ((now - time) >= (minimumMinutes * 1000));
    }

    @Override
    public int getCacheSize() {
        return cache.size();
    }

    @Override
    public boolean isCacheEmpty() {
        return cache.isEmpty();
    }

    @Override
    public void updateConfiguration() {
        smsCachingLogger.info(logId + "updateConfiguration : read config");
        smsCachingLogger.info(logId + "updateConfiguration : cacheMaxCount="
                + Configs.CACHE_RECORDS_MAX_COUNT.getValue());

        addEntitiesToLoadBalancer();

        cancelActiveTimers();

        startTimer();
    }

    @Override
    public int getCacheMaxCount() {
        return (Integer) Configs.CACHE_RECORDS_MAX_COUNT.getValue();
    }

    @Override
    public String setCacheProcessingBeanName() {
        return "CacheProcessingBean(" + cacheProcessingBeanNumber.incrementAndGet() + ")";
    }

    @Override
    public boolean canRunCacheProcessingBean() {
        synchronized (runningCacheProcessingBean) {
            if (runningCacheProcessingBean.get() < ((int) Configs.CACHE_RETRY_THREAD_COUNT.getValue())) {
                runningCacheProcessingBean.incrementAndGet();
                return true;
            }
        }
        return false;
    }

    @Override
    public void decrementRunningCacheProcessingBean() {
        runningCacheProcessingBean.decrementAndGet();
    }

}
