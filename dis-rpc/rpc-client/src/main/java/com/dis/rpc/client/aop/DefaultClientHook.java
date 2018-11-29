package com.dis.rpc.client.aop;

import com.dis.rpc.comm.vo.DisRpcRequest;

public class DefaultClientHook implements RpcHook{

    @Override
    public void before(DisRpcRequest request) {
        System.out.println("aop before request");
    }

    @Override
    public void after(DisRpcRequest request) {
        System.out.println("aop after request");
    }
}
