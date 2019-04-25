/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.sms.utils;

import com.edafa.web2sms.sms.interfaces.SubmitSMSBeanRemote;
import com.edafa.web2sms.utils.configs.enums.Configs;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

/**
 *
 * @author mahmoud
 */
public class SubmitSMSBeanRemoteFactory extends BasePooledObjectFactory<SubmitSMSBeanRemote> {

    @Override
    public SubmitSMSBeanRemote create() throws NamingException {
        String providerUrl = (String) Configs.CORE_PROVIDER_URL.getValue();
        String jndi = (String) Configs.SUBMIT_SMS_BEAN_JNDI.getValue();

        InitialContext ic = new InitialContext();
        ic.addToEnvironment(javax.naming.Context.PROVIDER_URL, providerUrl);
        return (SubmitSMSBeanRemote) ic.lookup(jndi);
    }

    /**
     * Use the default PooledObject implementation.
     *
     * @param buffer
     * @return
     */
    @Override
    public PooledObject<SubmitSMSBeanRemote> wrap(SubmitSMSBeanRemote buffer) {
        return new DefaultPooledObject<>(buffer);
    }

    /**
     * When an object is returned to the pool, clear the buffer.
     *
     * @param pooledObject
     */
    @Override
    public void passivateObject(PooledObject<SubmitSMSBeanRemote> pooledObject) {
    }

    // for all other methods, the no-op implementation
    // in BasePooledObjectFactory will suffice
}
