package com.dis.rpc.comm.serialization;

public interface MsgSerializeUtil {

    /**
     * 序列化
     * 
     * @param obj
     * @param <T>
     * @return
     */
    <T> byte[] serialize(T obj);

    /**
     * 反序列化对象
     * 
     * @param data
     * @param cls
     * @param <T>
     * @return
     */
    <T> T deserialize(byte[] data, Class<T> cls);

}
