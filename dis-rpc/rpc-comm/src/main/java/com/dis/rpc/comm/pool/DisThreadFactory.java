package com.dis.rpc.comm.pool;

import org.apache.commons.lang3.StringUtils;

import java.util.concurrent.ThreadFactory;
import java.util.concurrent.atomic.AtomicInteger;

/**
 * 
 */
public class DisThreadFactory implements ThreadFactory {

    private static final AtomicInteger POOL_NUMBER_ATOMIC = new AtomicInteger(1);

    private final ThreadGroup group;
    private final AtomicInteger threadNumber = new AtomicInteger(1);
    private final String namePrefix;

    DisThreadFactory(String name) {
        SecurityManager s = System.getSecurityManager();
        group = (s != null) ? s.getThreadGroup() : Thread.currentThread().getThreadGroup();
        if (StringUtils.isEmpty(name)) {
            name = "dis-pool";
        }

        namePrefix = name + "-" + POOL_NUMBER_ATOMIC.getAndIncrement() + "-thread-";
    }

    /**
     * 
     * @param
     * @return
     */

    @Override
    public Thread newThread(Runnable runnable) {
        Thread thread = new Thread(group, runnable, namePrefix + threadNumber.getAndIncrement(), 0);
        if (thread.isDaemon()) {
            thread.setDaemon(false);
        }
        if (thread.getPriority() != Thread.NORM_PRIORITY) {
            thread.setPriority(Thread.NORM_PRIORITY);
        }
        return thread;
    }
}
