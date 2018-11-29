package com.dis.rpc.comm.cache;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
import io.protostuff.Schema;
import io.protostuff.runtime.RuntimeSchema;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.TimeUnit;

@Slf4j
public class ProtobufSchemaCache {

    private static class SchemaCacheHolder {
        private static ProtobufSchemaCache cache = new ProtobufSchemaCache();
    }

    public static ProtobufSchemaCache getInstance() {
        return SchemaCacheHolder.cache;
    }

    Cache<Class<?>, Schema<?>> cache = Caffeine.newBuilder()
            .expireAfterWrite(1, TimeUnit.HOURS)
            .maximumSize(1000)
            .build();


    public Schema<?> get( Class<?> cls) {
        return get(cls, cache);
    }


    private  Schema<?>  get( Class<?> cls, Cache<Class<?>,Schema<?>> cache){
        Schema<?> schema ;
        try{
            schema = cache.get(cls,key->  RuntimeSchema.createFrom(cls));
        }catch (Exception e) {
            e.printStackTrace();
            log.error("ProtobufSchemaCache get error,cls:{}",cls.getEnclosingClass().getName());
            return  null;
        }
        return schema;
    }




}
