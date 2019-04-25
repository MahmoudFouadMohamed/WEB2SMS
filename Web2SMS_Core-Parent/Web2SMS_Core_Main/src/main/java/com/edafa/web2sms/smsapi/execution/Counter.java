package com.edafa.web2sms.smsapi.execution;

import java.util.concurrent.atomic.AtomicInteger;

/**
 *
 * @author mahmoud
 */
public class Counter {

    private AtomicInteger maxCount;
    private AtomicInteger currentCount;

    public Counter(int maxCount) {
        this.maxCount = new AtomicInteger(maxCount);
        this.currentCount = new AtomicInteger(0);
    }

    synchronized public boolean increment() {
        if (currentCount.get() < maxCount.get()) {
            currentCount.incrementAndGet();
            return true;
        } else {
            return false;
        }
    }

    synchronized public void decrement() {
        if (currentCount.get() != 0) {
            currentCount.decrementAndGet();
        }
    }

    synchronized public void setMaxCount(int maxCount) {
        this.maxCount.set(maxCount);
    }
    
    public int get(){
        return currentCount.get();
    }

    @Override
    public String toString() {
        return "Counter{" + "maxCount=" + maxCount + ", currentCount=" + currentCount + '}';
    }

}
