package com.dis.rpc.comm.pool;

import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.LinkedBlockingQueue;
import java.util.concurrent.ThreadPoolExecutor;
import java.util.concurrent.TimeUnit;


/**
 * 木马课堂 线程池工具类
 */
@Slf4j
public class DisExecutors {

    public static ThreadPoolExecutor creatFixedThreadPool(int threadsize,Integer keepAliveTime,String poolName){
        ThreadPoolExecutor executorOne = new ThreadPoolExecutor(threadsize ,threadsize, keepAliveTime, TimeUnit.MINUTES,
                new LinkedBlockingQueue<>(), new DisThreadFactory(poolName));
        return executorOne;
    }



}
