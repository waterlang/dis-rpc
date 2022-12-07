package com.dis.rpc.comm.vo;

import lombok.Data;

/**
 * 
 * @param <T>
 */

@Data
public class DisRpcResponse<T> {

    /**
     * 请求ID 唯一
     */
    private String requestId;

    /**
     * 返回结果
     */
    private T result;

    /**
     * 异常信息
     */
    private Exception exception;

    /**
     * 是否有异常信息
     * 
     * @return
     */
    public boolean hasException() {
        return exception != null;
    }
}
