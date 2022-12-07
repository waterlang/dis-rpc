package com.dis.rpc.client.aop;

import com.dis.rpc.comm.vo.DisRpcRequest;

/**
 * 
 */
public interface RpcHook {

    /**
     * 
     * @param request
     */
    void before(DisRpcRequest request);

    /**
     * 
     * @param request
     */
    void after(DisRpcRequest request);
}
