package com.dis.rpc.client.aop;

import com.dis.rpc.comm.vo.DisRpcRequest;

public interface RpcHook {

     void before(DisRpcRequest request);
     void after(DisRpcRequest request);
}
