package com.dis.rpc.client.proxy;

import com.dis.rpc.client.RPCRespFuture;
import com.dis.rpc.client.handler.ClientRespHanndler;
import com.dis.rpc.comm.vo.DisRpcRequest;
import lombok.extern.slf4j.Slf4j;

import java.util.UUID;

/**
 * 具体的代理对象实现者
 * @param <T>
 */
@Slf4j
public class ObjectProxy<T> implements IAsyncObjectProxy {

    private Class<T> clazz;

    private ClientRespHanndler hanndler;

    public ObjectProxy(Class<T> clazz,ClientRespHanndler hanndler) {
        this.clazz = clazz;
        this.hanndler = hanndler;
    }


    @Override
    public RPCRespFuture call(String funcName, Object... args) {
        DisRpcRequest request = buildRpcRequest(clazz.getName(),funcName,args);
        return   hanndler.doExecute(request);
    }


    private DisRpcRequest buildRpcRequest(String className, String methodName, Object[] args){
        DisRpcRequest request = new DisRpcRequest();
        request.setRequestId(UUID.randomUUID().toString());
        request.setMethodName(methodName);
        request.setInterfaceName(className);
        request.setParameters(args);

        //build parameterTypes
        Class[] parameterTypes = new Class[args.length];
        for (int i = 0; i < args.length; i++) {
            parameterTypes[i] = args[i].getClass();
        }
        request.setParameterTypes(parameterTypes);
        return request;
    }

}
