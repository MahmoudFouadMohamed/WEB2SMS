package com.edafa.web2sms.sms.utils.lb;

import java.util.List;
import java.util.concurrent.atomic.AtomicBoolean;

import javax.annotation.PostConstruct;
import javax.annotation.PreDestroy;
import javax.ejb.Singleton;

import com.edafa.utils.loadbalancer.LoadBalancer;
import com.edafa.utils.loadbalancer.LoadBalancerImpl;
import com.edafa.utils.loadbalancer.entity.Entity;
import com.edafa.utils.loadbalancer.entity.EntityStatus;
import com.edafa.utils.loadbalancer.exception.DuplicateEntityException;
import com.edafa.utils.loadbalancer.exception.InvalidParameterException;
import com.edafa.utils.loadbalancer.exception.NoSuchEntityException;
import com.edafa.utils.loadbalancer.exception.NoSuchGroupException;
import com.edafa.web2sms.utils.configs.enums.Configs;

@Singleton
public class LoadBalancerBean implements LoadBalancerLocal {

    LoadBalancer lb = null;
    private AtomicBoolean totalOutage = new AtomicBoolean(false);

    @PostConstruct
    @Override
    public void startLoadBalancer() {

        int invalidityPeriodMillis = (int) Configs.LB_INVALIDITY_PERIOD_MILLIS.getValue();
        int defaultWeight = (int) Configs.LB_DEFAULT_WEIGHT.getValue();
        int maximumErrorCounter = (int) Configs.LB_MAXIMUM_ERROR_COUNTER.getValue();
        int initialCapacity = (int) Configs.LB_INITIAL_CAPACITY.getValue();
        int maximumThroughput = (int) Configs.LB_MAXIMUM_THROUGHPUT.getValue();

        lb = new LoadBalancerImpl(defaultWeight, invalidityPeriodMillis, maximumErrorCounter, initialCapacity,
                maximumThroughput);

    }

    @Override
    public void setDefaultWeight(long defaultWeight) {
        lb.setDefaultWeight(defaultWeight);

    }

    @Override
    public void setInvalidityPeriodMillis(long invalidityPeriodMillis) {

        lb.setInvalidityPeriodMillis(invalidityPeriodMillis);

    }

    @Override
    public boolean addGroupList(String groupId, List<Entity> entityList)
            throws InvalidParameterException, DuplicateEntityException {
        return lb.addGroupList(groupId, entityList);
    }

    @Override
    public void addEntity(String groupId, Entity entity)
            throws NoSuchGroupException, DuplicateEntityException, InvalidParameterException {
        lb.addEntity(groupId, entity);
    }

    @Override
    public void removeGroupList(String groupId) throws NoSuchGroupException {
        lb.removeGroupList(groupId);
    }

    @Override
    public void updateGroupList(String groupId, List<Entity> entityList)
            throws NoSuchGroupException, InvalidParameterException {
        lb.updateGroupList(groupId, entityList);
    }

    @Override
    public Entity selectEntity(String groupId) throws NoSuchGroupException {

        Entity selectedEntity = lb.selectEntity(groupId);
        if (selectedEntity == null) {
            totalOutage.compareAndSet(false, true);
        }
        return selectedEntity;
    }

    @Override
    public void reportFailure(String groupId, Entity entity) throws NoSuchGroupException, NoSuchEntityException {
        lb.reportFailure(groupId, entity);
    }

    @Override
    public void reportSuccess(String groupId, Entity entity) throws NoSuchGroupException, NoSuchEntityException {
        lb.reportSuccess(groupId, entity);
        totalOutage.compareAndSet(true, false);
    }

    @Override
    public boolean isGroupExist(String groupId) {
        return lb.isGroupExist(groupId);
    }

    @Override
    public EntityStatus getEntityStatus(String groupId, Entity entity)
            throws NoSuchGroupException, NoSuchEntityException {
        return lb.getEntityStatus(groupId, entity);
    }

    @Override
    public List<Entity> getEntityList(String groupId) throws NoSuchGroupException {
        return lb.getEntityList(groupId);
    }

    @Override
    public Entity getEntity(String groupId, String entityName) throws NoSuchGroupException, NoSuchEntityException {
        return lb.getEntity(groupId, entityName);
    }

    @PreDestroy
    public void destroy() {
        cleanup();
    }

    @Override
    public void cleanup() {
        lb = null;
    }

    @Override
    public boolean hasAvailableEntity(String groupId) throws NoSuchGroupException {
        List<Entity> entityList = lb.getEntityList(groupId);

        for (Entity entity : entityList) {

            if (lb.getEntityStatus(groupId, entity) == EntityStatus.AVAILABLE) {
                return true;
            }
        }

        totalOutage.compareAndSet(false, true);
        return false;
    }

    @Override
    public boolean isTotalOutage() {
        return totalOutage.get();
    }

}
