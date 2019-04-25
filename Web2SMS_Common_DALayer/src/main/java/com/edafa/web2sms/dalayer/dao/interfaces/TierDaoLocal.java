/*
 * To change this license header, choose License Headers in Project Properties.
 * To change this template file, choose Tools | Templates
 * and open the template in the editor.
 */

package com.edafa.web2sms.dalayer.dao.interfaces;

import java.util.List;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.exception.DBException;
import com.edafa.web2sms.dalayer.model.Tier;
import com.edafa.web2sms.dalayer.model.TierType;

/**
 *
 * @author yyaseen
 */
@Local
public interface TierDaoLocal {

    void create(Tier tiers) throws DBException;

    void edit(Tier tiers) throws DBException;

    void remove(Tier tiers) throws DBException;

    Tier find(Object id) throws DBException;

    List<Tier> findAll() throws DBException;
    
    List<Tier> findAll(int first, int max) throws DBException;

    List<Tier> findRange(int[] range) throws DBException;

    int count() throws DBException;

	Tier findByRatePlan(String ratePlan) throws DBException;
	
	Tier findByTierId(Integer tierId) throws DBException;

	List<Tier> findByTierType(TierType tierType) throws DBException;

	long findTotalQuotaByTierId(Integer tierId) throws DBException;
	
	
	
    
}
