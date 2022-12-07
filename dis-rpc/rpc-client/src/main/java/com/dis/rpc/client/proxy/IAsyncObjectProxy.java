package com.dis.rpc.client.proxy;

import com.dis.rpc.client.RPCRespFuture;

/**
 * 
 */
public interface IAsyncObjectProxy {

    /**
     * 
     * @param funcName
     * @param args
     * @return
     */
    RPCRespFuture call(String funcName, Object... args);

}
