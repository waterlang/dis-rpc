package com.dis.rpc.comm.serialization.protobuf;

import com.dis.rpc.comm.cache.ProtobufSchemaCache;
import com.dis.rpc.comm.serialization.MsgSerializeUtil;
import io.protostuff.LinkedBuffer;
import io.protostuff.ProtobufIOUtil;
import io.protostuff.Schema;
import org.objenesis.Objenesis;
import org.objenesis.ObjenesisStd;


public class ProtobufSerializeUtil implements MsgSerializeUtil {

    private static ProtobufSchemaCache cachedSchema = ProtobufSchemaCache.getInstance();

    private static final Objenesis objenesis = new ObjenesisStd();

    public <T> byte[] serialize(T obj) {
        Class<T> tClass  = (Class<T>) obj.getClass();
        LinkedBuffer buffer = LinkedBuffer.allocate(LinkedBuffer.DEFAULT_BUFFER_SIZE);
        try{
            Schema<T> schema =  (Schema<T>)cachedSchema.get(tClass);
            return ProtobufIOUtil.toByteArray(obj,schema,buffer);
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }finally {
            buffer.clear();
        }
    }

    public <T> T deserialize(byte[] data, Class<T> cls) {
        try {
            //反射出对象
            T mesg = objenesis.newInstance(cls);

            Schema<T> schema =  (Schema<T>)cachedSchema.get(cls);
            ProtobufIOUtil.mergeFrom(data,mesg,schema);
            return mesg;
        }catch (Exception e){
            throw new IllegalArgumentException(e.getMessage(),e);
        }
    }
}
