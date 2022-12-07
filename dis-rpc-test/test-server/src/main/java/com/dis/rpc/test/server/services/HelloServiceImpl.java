package com.dis.rpc.test.server.services;

import com.dis.rpc.server.annotation.DisRpcService;
import com.dis.rpc.test.api.HelloService;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.atomic.AtomicInteger;

@Slf4j
@DisRpcService(HelloService.class)
public class HelloServiceImpl implements HelloService {

    private final AtomicInteger integer = new AtomicInteger(0);

    @Override
    public String say(String name) {
        int count = integer.addAndGet(1);
        log.info("----req:{},count:{}", name, count);
        return "hello :" + count;
    }

}
