package com.edafa.web2sms.sms.utils.lb;

import javax.ejb.Local;

import com.edafa.utils.loadbalancer.LoadBalancer;
import com.edafa.utils.loadbalancer.exception.NoSuchGroupException;

/**
 *
 * @author Emad
 */
@Local
public interface LoadBalancerLocal extends LoadBalancer {

    boolean hasAvailableEntity(String groupID) throws NoSuchGroupException;

    boolean isTotalOutage();

    public void cleanup();

    public void startLoadBalancer();

}
