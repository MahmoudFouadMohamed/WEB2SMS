/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */
package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.SendingRateLimiter;

/**
 *
 * @author mahmoud & may
 */
public interface SendingRateLimiterDaoLocal {
    void create(SendingRateLimiter sendingRateLimiter) throws DBException;

	void edit(SendingRateLimiter sendingRateLimiter) throws DBException;

	void remove(SendingRateLimiter sendingRateLimiter) throws DBException;

	SendingRateLimiter find(Object id) throws DBException;

	List<SendingRateLimiter> findAll() throws DBException;

	List<SendingRateLimiter> findRange(int[] range) throws DBException;

	int count() throws DBException;

	void create(List<SendingRateLimiter> limiters) throws DBException;

	void edit(List<SendingRateLimiter> limiters) throws DBException;
}
