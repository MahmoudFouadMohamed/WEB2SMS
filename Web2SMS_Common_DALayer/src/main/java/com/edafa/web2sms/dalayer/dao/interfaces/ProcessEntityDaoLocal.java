package com.edafa.web2sms.dalayer.dao.interfaces;

import javax.ejb.Local;

import com.edafa.web2sms.dalayer.interfaces.Cachable;
import com.edafa.web2sms.dalayer.model.Component;
import com.edafa.web2sms.dalayer.model.Node;
import com.edafa.web2sms.dalayer.model.ProcessEntity;
import com.edafa.web2sms.dalayer.model.Service;

/**
 *
 * @author loay
 */
@Local
public interface ProcessEntityDaoLocal extends Cachable<ProcessEntity, String> {

    public ProcessEntity getCachedObjectByComponentId(Component component);

    public ProcessEntity getCachedObjectByServiceId(Service service);

    public ProcessEntity getCachedObjectByNodeId(Node node);

}
