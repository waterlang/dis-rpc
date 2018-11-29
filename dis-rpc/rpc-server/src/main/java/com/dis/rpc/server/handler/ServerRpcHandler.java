package com.dis.rpc.server.handler;

import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.server.pool.RpcRecivePool;
import com.dis.rpc.server.task.DisReciveHandlerTask;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;

import java.util.Map;

@Slf4j
public class ServerRpcHandler extends SimpleChannelInboundHandler<DisRpcRequest> {

    /**
     * 服务名称 key
     * 服务实例 value
     */
    private final Map<String,Object> handlerMap ;


    public ServerRpcHandler( Map<String,Object> handlerMap){
        this.handlerMap = handlerMap;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, DisRpcRequest reqest) throws Exception {
        log.info("server channelRead0..........");
        handle(reqest,ctx);
    }


    /**
     * hanndle biz logic
     * @param reqest
     * @param ctx
     */
    private void handle(DisRpcRequest reqest,ChannelHandlerContext ctx) {
        DisReciveHandlerTask task = new DisReciveHandlerTask(reqest,handlerMap,ctx);
        RpcRecivePool.poolExecutor.submit(task);
    }



    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("server caught exception ",cause);
        ctx.close();
    }
}
