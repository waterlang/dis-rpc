package com.dis.rpc.comm.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ArrayBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;

/**
 * 线程池工具类
 */
@Slf4j
public class DisExecutors {

    /**
     * 
     * @param threadsize
     * @param keepAliveTime
     * @param poolName
     * @return
     */
    public static ThreadPoolExecutor creatFixedThreadPool(int threadsize, Integer keepAliveTime, String poolName) {
        ThreadPoolExecutor executorOne = new ThreadPoolExecutor(threadsize, threadsize, keepAliveTime, TimeUnit.MINUTES,
            new ArrayBlockingQueue<>(1024), new DisThreadFactory(poolName));
        return executorOne;
    }

}
