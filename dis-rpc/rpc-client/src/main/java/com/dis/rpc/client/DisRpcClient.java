package com.dis.rpc.client;

import com.dis.rpc.client.aop.RpcHook;
import com.dis.rpc.client.handler.ClientRespHanndler;
import com.dis.rpc.client.proxy.IAsyncObjectProxy;
import com.dis.rpc.client.proxy.ObjectProxy;
import com.dis.rpc.comm.coder.RpcDecoder;
import com.dis.rpc.comm.coder.RpcEncoder;
import com.dis.rpc.comm.serialization.protobuf.ProtobufSerializeUtil;
import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.comm.vo.DisRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.*;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.lang.reflect.Proxy;
import java.util.UUID;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class DisRpcClient {


    /**
     * 请求编号与响应对象关系
     */
    private ConcurrentHashMap<String,DisRpcResponse> map = new ConcurrentHashMap<>();

    private static ProtobufSerializeUtil serializeUtil = new ProtobufSerializeUtil();

    public  ClientRespHanndler handler = null;



    public void  creatChannle(RpcHook hook,String address,Integer port){
        for (int i= 0;i<5;i++){
            new Thread(()->{
                log.info("client creatChannle..............");
                EventLoopGroup group = new NioEventLoopGroup(1);
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group);
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcEncoder(DisRpcRequest.class,serializeUtil));
                            pipeline.addLast(new RpcDecoder(DisRpcResponse.class,serializeUtil));
                            pipeline.addLast(new ClientRespHanndler(map,hook));
                        }
                    });

                    ChannelFuture channelFuture = bootstrap.connect(address,port).sync();
                    //add listener
                    channelFuture.addListener((ChannelFutureListener) cf -> {
                        if (cf.isSuccess()) {
                            log.info("Success to connect to remote server:{},port:{}",address,port );
                        }
                    handler = channelFuture.channel().pipeline().get(ClientRespHanndler.class);
                    });
                    channelFuture.channel().closeFuture().sync();
                }catch (Exception e){
                    e.printStackTrace();
                }finally {
                    group.shutdownGracefully();
                }
            }).start();
        }
    }


    /**
     * 创建调用对象
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> T create(final  Class<?> interfaceClass){
        //创建动态对象
        return  (T)Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[]{interfaceClass},
                new InvocationHandler() {
                    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
                        //create rpc request
                        DisRpcRequest reqest = new DisRpcRequest();
                        reqest.setRequestId(UUID.randomUUID().toString());
                        reqest.setMethodName(method.getName());
                        reqest.setInterfaceName(method.getDeclaringClass().getName());
                        reqest.setParameterTypes(method.getParameterTypes());
                        reqest.setParameters(args);

                        RPCRespFuture respFuture =  handler.doExecute(reqest);
                        return  respFuture.get();
                    }
                });
    }


    public  <T> IAsyncObjectProxy createAsync(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass,handler);
    }




}
