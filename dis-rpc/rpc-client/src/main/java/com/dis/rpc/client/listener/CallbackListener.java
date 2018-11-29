package com.dis.rpc.client.listener;

public interface CallbackListener {

    /**
     * 成功时被调用
     * @return
     */
    void   onSucces(Object resp);

    /**
     * 调用失败
     * @return
     */
    void  onException(Object resp);
}
