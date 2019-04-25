package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.AlarmDefinitions;
import com.edafa.web2sms.dalayer.model.Categories;

/**
 *
 * @author loay
 */
@Local
public interface AlarmDefinitionsDaoLocal extends Cachable<AlarmDefinitions, Integer> {

    public AlarmDefinitions getCachedObjectByNodeId(Categories category);
}
