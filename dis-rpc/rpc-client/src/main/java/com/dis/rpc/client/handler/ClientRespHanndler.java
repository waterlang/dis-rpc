package com.dis.rpc.client.handler;

import com.dis.rpc.client.RPCRespFuture;
import com.dis.rpc.client.aop.RpcHook;
import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.comm.vo.DisRpcResponse;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.util.concurrent.Future;
import lombok.extern.slf4j.Slf4j;

import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class ClientRespHanndler extends SimpleChannelInboundHandler<DisRpcResponse> {


    /**
     * 请求编号与响应对象关系
     */
    public static ConcurrentHashMap<String,RPCRespFuture> respFutureMap = new ConcurrentHashMap<>() ;


    public  ChannelHandlerContext ctx;

    private RpcHook rpcHook;


    public ClientRespHanndler(ConcurrentHashMap<String,DisRpcResponse> map,RpcHook rpcHook ){
        this.rpcHook = rpcHook;
    }


    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        super.channelRegistered(ctx);
        log.info("client channelRegistered .............");
    }


    @Override
    public void channelActive(ChannelHandlerContext ctx) throws Exception {
        super.channelActive(ctx);
        log.info("client channelActive .............");
        this.ctx = ctx;
    }

    protected void channelRead0(ChannelHandlerContext channelHandlerContext, DisRpcResponse rpcResp) throws Exception {
        log.info("--- rpc resp:"+rpcResp);

        RPCRespFuture respFuture = respFutureMap.get(rpcResp.getRequestId());
        if(respFuture != null){
            respFutureMap.remove(rpcResp.getRequestId());
            if(rpcResp.hasException()){
                respFuture.setException(rpcResp.getException());
            }else {
                respFuture.setResult(rpcResp.getResult());
            }
        }

    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("client catch exception",cause);
        ctx.close();
    }


    public  RPCRespFuture doExecute(DisRpcRequest rpcRequest){
        RPCRespFuture respFuture = new RPCRespFuture();
        try {
            beforeDo(rpcRequest);

            ctx.channel().writeAndFlush(rpcRequest).addListener(
                    (f)->{
                        handleSendCompleteListener(f,rpcRequest);
                     }
            ).isSuccess();

            afterDo(rpcRequest);
            respFutureMap.put(rpcRequest.getRequestId(),respFuture);
        }catch (Exception e){
            e.printStackTrace();
            return  null;
        }
        return  respFuture;
    }


    private void handleSendCompleteListener(Future f,DisRpcRequest rpcRequest){
        if(f.isSuccess()){
            log.info("Send request success,data:{}" + rpcRequest);
        }else {
            log.info("Send request error,error info :{}" + f.cause());
        }
    }


    private void beforeDo(DisRpcRequest rpcRequest){
        if(rpcHook != null){
            rpcHook.before(rpcRequest);
        }
    }


    private void afterDo(DisRpcRequest rpcRequest){
        if(rpcHook != null){
            rpcHook.after(rpcRequest);
        }
    }




}




