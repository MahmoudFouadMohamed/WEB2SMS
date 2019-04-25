package com.edafa.web2sms.sms.file;

import java.util.Collection;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

import com.edafa.web2sms.sms.model.SubmitDetailedCampaignRequest;
import com.edafa.web2sms.sms.model.SubmitDetailedSMSRequest;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;

/**
 *
 * @author mfouad
 */
public class FileManagementBean implements FileManagementBeanLocal {

    private static final String MOVED_FILE_EXTENTION = "done";

    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());
    Logger logger = LogManager.getLogger(LoggersEnum.SMS_API_CACHING.name());

    private FileManager expiredFileManager;
    private FileManager cacheFileManager;

    private String name = null;
    private String logId;

    @Override
    public void startFiles() {
        if (name == null) {
            name = "FileManagementBean";
            logId = name + " | ";
        }

        if (expiredFileManager == null && cacheFileManager == null) {
            logger.info(logId + "startFiles : Read Configuration");

            String cacheBaseDir = (String) Configs.CACHE_RECORDS_BASE_DIR.getValue();
            String cacheFileName = (String) Configs.CACHE_RECORDS_FILE_NAME.getValue();
            String cacheFileExtention = (String) Configs.CACHE_RECORDS_FILE_EXTENTION.getValue();
            int cacheRecordsFileMaxCount = (Integer) Configs.CACHE_RECORDS_FILE_MAX_COUNT.getValue();
            int cacheRecordsFileReadChunkCount = (Integer) Configs.CACHE_RECORDS_FILE_READ_CHUNK_COUNT.getValue();
            int cacheRecordsFileWriteChunkCount = (Integer) Configs.CACHE_RECORDS_FILE_WRITE_CHUNK_COUNT.getValue();

            String expiredBaseDir = (String) Configs.EXPIRED_RECORDS_BASE_DIR.getValue();
            String expiredFileName = (String) Configs.EXPIRED_RECORDS_FILE_NAME.getValue();
            String expiredFileExtention = (String) Configs.EXPIRED_RECORDS_FILE_EXTENTION.getValue();
            int expiredRecordsFileMaxCount = (Integer) Configs.EXPIRED_RECORDS_FILE_MAX_COUNT.getValue();
            int expiredRecordsFileReadChunkCount = (Integer) Configs.EXPIRED_RECORDS_FILE_READ_CHUNK_COUNT.getValue();
            int expiredRecordsFileWriteChunkCount = (Integer) Configs.EXPIRED_RECORDS_FILE_WRITE_CHUNK_COUNT.getValue();

            try {
                logger.info(logId + "startFiles : create expiredFileManager");
                logger.info(logId + "startFiles : expiredBaseDir=" + expiredBaseDir + ", expiredFileName="
                        + expiredFileName + ", expiredFileExtention=" + expiredFileExtention
                        + ", expiredRecordsFileMaxCount=" + expiredRecordsFileMaxCount
                        + ", expiredRecordsFileWriteChunkCount=" + expiredRecordsFileWriteChunkCount
                        + ", expiredRecordsFileReadChunkCount=" + expiredRecordsFileReadChunkCount);
                expiredFileManager = new FileManager(expiredBaseDir, expiredFileName, expiredFileExtention,
                        MOVED_FILE_EXTENTION, logger);
                expiredFileManager.setMaxCount(expiredRecordsFileMaxCount);
                expiredFileManager.setChunkSize(expiredRecordsFileWriteChunkCount);
                expiredFileManager.setDataReadChunkSize(expiredRecordsFileReadChunkCount);

                logger.info(logId + "startFiles : create cacheRequestsWriter Thread");
                logger.info(logId + "startFiles : cacheBaseDir=" + cacheBaseDir + ", cacheFileName="
                        + cacheFileName + ", cacheFileExtention=" + cacheFileExtention + ", cacheRecordsFileMaxCount="
                        + cacheRecordsFileMaxCount + ", cacheRecordsFileWriteChunkCount="
                        + cacheRecordsFileWriteChunkCount + ", cacheRecordsFileReadChunkCount="
                        + cacheRecordsFileReadChunkCount);
                cacheFileManager = new FileManager(cacheBaseDir, cacheFileName, cacheFileExtention,
                        MOVED_FILE_EXTENTION, logger);
                cacheFileManager.setMaxCount(cacheRecordsFileMaxCount);
                cacheFileManager.setChunkSize(cacheRecordsFileWriteChunkCount);
                cacheFileManager.setDataReadChunkSize(cacheRecordsFileReadChunkCount);

                logger.info(logId + "startFiles : initialized successfully.");
            } catch (Exception e) {
                logger.error(logId + "startFiles : Exception : " + e.getMessage());
                appLogger.error(logId + "startFiles : Exception", e);

                if (expiredFileManager != null) {
                    expiredFileManager.stop();
                    expiredFileManager = null;
                }

                if (cacheFileManager != null) {
                    cacheFileManager.stop();
                    cacheFileManager = null;
                }
            }
            logger.info(logId + "startFiles : initialized successfully.");
        } else {
            logger.warn(logId + "startFiles : NO File Manager created beacause already running !!!");
        }
    }

    public void destroy() {
        stopFiles();
    }

    @Override
    public void stopFiles() {
        if (expiredFileManager != null && cacheFileManager != null) {
            logger.info(logId + "stopFiles : stopping expiredFileManager Threads");
            expiredFileManager.stop();

            logger.info(logId + "stopFiles : stopping cacheFileManager Threads");
            cacheFileManager.stop();

            //wait until threads finish
            try {
                while (expiredFileManager.isRunning() || cacheFileManager.isRunning()) {
                    Thread.sleep(100l);
                }
            } catch (InterruptedException ex) {
                logger.info(logId + "stopFiles : InterruptedException");
                appLogger.info(logId + "stopFiles : InterruptedException", ex);
            }

            expiredFileManager = null;
            cacheFileManager = null;
        } else {
            logger.warn(logId + "stopFiles : NO File Manager created to destroy !!!");
        }
    }

    @Override
    public boolean addExpiredCampaignRequest(SubmitDetailedCampaignRequest request)
            throws WritingCountMaxReachedException {
        return expiredFileManager.addToQueue(request);
    }

    @Override
    public boolean addExpiredSmsRequest(SubmitDetailedSMSRequest request) throws WritingCountMaxReachedException {
        return expiredFileManager.addToQueue(request);
    }

    @Override
    public boolean addExpiredCampaignRequests(Collection<? extends SubmitDetailedCampaignRequest> requests)
            throws WritingCountMaxReachedException {
        return expiredFileManager.addAllToQueue(requests);
    }

    @Override
    public boolean addExpiredSmsRequests(Collection<? extends SubmitDetailedSMSRequest> requests)
            throws WritingCountMaxReachedException {
        return expiredFileManager.addAllToQueue(requests);
    }

    @Override
    public boolean addExpiredRequest(Object request) throws WritingCountMaxReachedException {
        return expiredFileManager.addToQueue(request);
    }

    @Override
    public boolean addExpiredRequests(Collection<? extends Object> requests) throws WritingCountMaxReachedException {
        return expiredFileManager.addAllToQueue(requests);
    }

    @Override
    public Object[] readExpiredDataChunk() {
        return expiredFileManager.getReadData();
    }

    @Override
    public boolean addCachedCampaignRequest(SubmitDetailedCampaignRequest request)
            throws WritingCountMaxReachedException {
        return cacheFileManager.addToQueue(request);
    }

    @Override
    public boolean addCachedSmsRequest(SubmitDetailedSMSRequest request) throws WritingCountMaxReachedException {
        return cacheFileManager.addToQueue(request);
    }

    @Override
    public boolean addCachedCampaignRequests(Collection<? extends SubmitDetailedCampaignRequest> requests)
            throws WritingCountMaxReachedException {
        return cacheFileManager.addAllToQueue(requests);
    }

    @Override
    public boolean addCachedSmsRequests(Collection<? extends SubmitDetailedSMSRequest> requests)
            throws WritingCountMaxReachedException {
        return cacheFileManager.addAllToQueue(requests);
    }

    @Override
    public boolean addCachedRequest(Object request) throws WritingCountMaxReachedException {
        return cacheFileManager.addToQueue(request);
    }

    @Override
    public boolean addCachedRequests(Collection<? extends Object> requests) throws WritingCountMaxReachedException {
        boolean addAllToQueue = false;
        try {
            addAllToQueue = cacheFileManager.addAllToQueue(requests);
        } catch (Exception e) {
            logger.error(logId + "addCachedRequests : Unhandled Exception : " + e.getMessage());
            appLogger.error(logId + "addCachedRequests : Unhandled Exception : ", e);
        }
        return addAllToQueue;
    }

    @Override
    public Object[] readCachedDataChunk() {
        return cacheFileManager.getReadData();
    }

    @Override
    public boolean isExpiredMaxWriteReatched() {
        return expiredFileManager.isMaxWriteReatched();
    }

    @Override
    public boolean isCachedMaxWriteReatched() {
        return cacheFileManager.isMaxWriteReatched();
    }

}
