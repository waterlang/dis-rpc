package com.dis.rpc.client.aop;

import com.dis.rpc.comm.vo.DisRpcRequest;
import lombok.extern.slf4j.Slf4j;

/**
 *
 */

@Slf4j
public class DefaultClientHook implements RpcHook {

    @Override
    public void before(DisRpcRequest request) {
        log.info("rpc execute before..");
    }

    @Override
    public void after(DisRpcRequest request) {
        log.info("rpc execute after..");
    }
}
