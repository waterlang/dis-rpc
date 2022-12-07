package com.dis.rpc.comm.serialization.protobuf;

import com.dis.rpc.comm.cache.ProtobufSchemaCache;
import com.dis.rpc.comm.serialization.MsgSerializeUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import lombok.extern.slf4j.Slf4j;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;

/**
 * 
 * 
 */

@Slf4j
public class ProtobufSerializeUtil implements MsgSerializeUtil {

    private static final ProtobufSchemaCache SCHEMA_CACHE = ProtobufSchemaCache.getInstance();

    private static final Objenesis OBJENESIS = new ObjenesisStd();

    @Override
    public <T> byte[] serialize(T obj) {
        Class<T> tClass = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try {
            Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(tClass);
            return ProtobufIOUtil.toByteArray(obj, schema, buffer);
        } catch (Exception e) {
            log.warn("e", e);
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            buffer.clear();
        }
    }

    @Override
    public <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            // 反射出对象
            T objType = OBJENESIS.newInstance(cls);

            Schema<T> schema = (Schema<T>) SCHEMA_CACHE.get(cls);
            ProtobufIOUtil.mergeFrom(data, objType, schema);
            return objType;
        } catch (Exception e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        }
    }

}
