package com.dis.rpc.server.annotation;


import org.springframework.stereotype.Service;

import java.lang.annotation.ElementType;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;
import java.lang.annotation.Target;

@Target({ElementType.TYPE})
@Retention(RetentionPolicy.RUNTIME)
@Service
public @interface  DisRpcService {

    /**
     * 服务接口类
     * @return
     */
    Class<?> value();
}
