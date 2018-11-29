package com.dis.rpc.comm.vo;

import lombok.Data;

@Data
public class DisRpcRequest {

    /**
     * 请求ID 唯一
     */
    private String requestId;

    /**
     * 接口名称 全路径
     */
    private String interfaceName;

    /**
     * 要调用的方法名称
     */
    private String methodName;


    /**
     * 参数类型
     */
    private Class<?>[] parameterTypes;

    /**
     * 参数值
     */
    private Object[] parameters;
}
