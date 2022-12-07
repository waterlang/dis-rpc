package com.dis.rpc.server.task;

import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.comm.vo.DisRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Method;
import java.util.Map;
import java.util.concurrent.Callable;

/**
 * 
 */
@Slf4j
public class DisReciveHandlerTask implements Callable {

    private DisRpcRequest request;

    private Map<String, Object> handlerMap;

    private ChannelHandlerContext ctx;

    /**
     * 
     * @param disRpcRequest
     * @param handlerMap
     * @param ctx
     */
    public DisReciveHandlerTask(DisRpcRequest disRpcRequest, Map<String, Object> handlerMap,
        ChannelHandlerContext ctx) {
        this.request = disRpcRequest;
        this.handlerMap = handlerMap;
        this.ctx = ctx;
    }

    /**
     * 执行具体操作
     * 
     * @return
     * @throws Exception
     */
    @Override
    public Object call() throws Exception {
        log.info("server 执行call 方法");
        // get 服务实例
        String serviceName = this.request.getInterfaceName();
        Object serviceBean = handlerMap.get(serviceName);
        DisRpcResponse rpcResp;
        if (serviceBean == null) {
            rpcResp = new DisRpcResponse();
            rpcResp.setRequestId(request.getRequestId());
            rpcResp.setException(new RuntimeException("找不到对应的服务实例"));
        } else {
            rpcResp = invoke(serviceBean);
        }

        log.info("server call执行完成，rpcResp:{}", rpcResp);
        ctx.writeAndFlush(rpcResp).addListener((future) -> log.info("Send response for request " + request.getRequestId()));
        return rpcResp;
    }

    /**
     * 实际调用操作
     * 
     * @param serviceBean
     * @return
     * @throws Exception
     */
    private DisRpcResponse invoke(Object serviceBean) throws Exception {
        DisRpcResponse rpcResp = new DisRpcResponse();
        // 获取信息
        Class serviceClass = serviceBean.getClass();
        String methodName = this.request.getMethodName();
        Class<?>[] parmTypes = this.request.getParameterTypes();
        Object[] parms = this.request.getParameters();

        Method method = serviceClass.getMethod(methodName, parmTypes);
        method.setAccessible(true);
        Object obj = method.invoke(serviceBean, parms);

        rpcResp.setResult(obj);
        rpcResp.setRequestId(request.getRequestId());
        return rpcResp;
    }
}
