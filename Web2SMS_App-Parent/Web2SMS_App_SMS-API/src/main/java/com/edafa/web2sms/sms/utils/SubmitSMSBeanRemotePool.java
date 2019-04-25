package com.edafa.web2sms.sms.utils;

import javax.annotation.PostConstruct;
import javax.ejb.DependsOn;
import javax.ejb.Singleton;

import org.apache.commons.pool2.ObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPool;
import org.apache.commons.pool2.impl.GenericObjectPoolConfig;

import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.utils.configs.enums.Configs;
import com.edafa.web2sms.utils.configs.enums.LoggersEnum;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;

/**
 *
 * @author mahmoud saad
 */
@Singleton
@DependsOn("ConfigsManagerBean")
public class SubmitSMSBeanRemotePool implements SubmitSMSBeanRemotePoolLocal {

    private ObjectPool<SubmitSMSBeanRemote> clientsPool;
    Logger appLogger = LogManager.getLogger(LoggersEnum.APP_UTILS.name());

    @PostConstruct
    public void initPool() {
        try {
            GenericObjectPoolConfig submitSMSBeanRemotePoolConfig = new GenericObjectPoolConfig();
            submitSMSBeanRemotePoolConfig.setMaxTotal((int) Configs.SUBMIT_SMS_BEAN_REMOTE_POOL_MAXTOTAL.getValue());
            submitSMSBeanRemotePoolConfig.setMaxIdle((int) Configs.SUBMIT_SMS_BEAN_REMOTE_POOL_MAXIDLE.getValue());
            clientsPool = new GenericObjectPool<SubmitSMSBeanRemote>(new SubmitSMSBeanRemoteFactory(), submitSMSBeanRemotePoolConfig);
        } catch (Exception e) {
            appLogger.error("Failed to create SubmitSMSBeanRemotePool", e);
            throw e;
        }
    }

    /**
     *
     * @return @throws Exception
     */
    @Override
    public SubmitSMSBeanRemote getSubmitSMSBeanRemote() throws Exception {
        return clientsPool.borrowObject();
    }

    /**
     *
     * @param submitSMSBeanRemote
     * @throws Exception
     */
    @Override
    public void returnSubmitSMSBeanRemote(SubmitSMSBeanRemote submitSMSBeanRemote) throws Exception {
        clientsPool.returnObject(submitSMSBeanRemote);
    }

}
