package com.dis.rpc.server.pool;

import com.dis.rpc.comm.config.DisConfig;
import com.dis.rpc.comm.pool.DisExecutors;

import java.util.concurrent.ThreadPoolExecutor;

/**
 * 
 */
public class RpcRecivePool {

    /**
     * 
     */
    public static final ThreadPoolExecutor POOL_EXECUTOR =
        DisExecutors.creatFixedThreadPool(DisConfig.SYSTEM_PROPERTY_CORE, 1, "rpc-server-recive-handler");
}
