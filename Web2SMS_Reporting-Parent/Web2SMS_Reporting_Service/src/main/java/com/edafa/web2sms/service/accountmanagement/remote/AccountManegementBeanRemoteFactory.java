/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.service.accountmanagement.remote;

import javax.naming.InitialContext;
import javax.naming.NamingException;

import org.apache.commons.pool2.BasePooledObjectFactory;
import org.apache.commons.pool2.PooledObject;
import org.apache.commons.pool2.impl.DefaultPooledObject;

import com.edafa.web2sms.acc_manag.service.account.interfaces.Remote.AccountManegementBeanRemote;
import com.edafa.web2sms.utils.configs.enums.Configs;

/**
 *
 * @author mahmoud
 */
public class AccountManegementBeanRemoteFactory extends BasePooledObjectFactory<AccountManegementBeanRemote> {

	@Override
	public AccountManegementBeanRemote create() throws NamingException {
		String providerUrl = (String) Configs.ACCOUNT_MANAGEMENT_PROVIDER.getValue();
		String jndi = (String) Configs.ACCOUNT_MANAGEMENT_REMOTE_JNDI.getValue();

		InitialContext ic = new InitialContext();
		ic.addToEnvironment(javax.naming.Context.PROVIDER_URL, providerUrl);
		return (AccountManegementBeanRemote) ic.lookup(jndi);
	}

	/**
	 * Use the default PooledObject implementation.
	 *
	 * @param buffer
	 * @return
	 */
	@Override
	public PooledObject<AccountManegementBeanRemote> wrap(AccountManegementBeanRemote buffer) {
		return new DefaultPooledObject<>(buffer);
	}

	/**
	 * When an object is returned to the pool, clear the buffer.
	 *
	 * @param pooledObject
	 */
	@Override
	public void passivateObject(PooledObject<AccountManegementBeanRemote> pooledObject) {}

	// for all other methods, the no-op implementation
	// in BasePooledObjectFactory will suffice
}
