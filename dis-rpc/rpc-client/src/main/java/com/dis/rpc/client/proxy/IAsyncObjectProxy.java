package com.dis.rpc.client.proxy;

import com.dis.rpc.client.RPCRespFuture;

public interface IAsyncObjectProxy {

    RPCRespFuture call(String funcName, Object... args);
}
