package com.dis.rpc.client;

import com.dis.rpc.client.aop.RpcHook;
import com.dis.rpc.client.handler.ClientRespHandler;
import com.dis.rpc.client.proxy.IAsyncObjectProxy;
import com.dis.rpc.client.proxy.ObjectProxy;
import com.dis.rpc.comm.coder.RpcDecoder;
import com.dis.rpc.comm.coder.RpcEncoder;
import com.dis.rpc.comm.serialization.protobuf.ProtobufSerializeUtil;
import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.comm.vo.DisRpcResponse;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;

import java.lang.reflect.Proxy;
import java.util.UUID;

/**
 *
 */

@Slf4j
public class DisRpcClient {

    private static final ProtobufSerializeUtil SERIALIZE_UTIL = new ProtobufSerializeUtil();

    private ClientRespHandler handler = null;

    /**
     * 
     * @param hook
     * @param address
     * @param port
     */
    public void creatChannel(RpcHook hook, String address, Integer port) {
        for (int i = 0; i < 1; i++) {
            new Thread(() -> {
                log.info("client creatChannel..............");
                EventLoopGroup group = new NioEventLoopGroup(1);
                try {
                    Bootstrap bootstrap = new Bootstrap();
                    bootstrap.group(group);
                    bootstrap.channel(NioSocketChannel.class);
                    bootstrap.handler(new ChannelInitializer<SocketChannel>() {
                        public void initChannel(SocketChannel channel) throws Exception {
                            ChannelPipeline pipeline = channel.pipeline();
                            pipeline.addLast(new RpcEncoder(DisRpcRequest.class, SERIALIZE_UTIL));
                            pipeline.addLast(new RpcDecoder(DisRpcResponse.class, SERIALIZE_UTIL));
                            pipeline.addLast(new ClientRespHandler(hook));
                        }
                    });

                    ChannelFuture channelFuture = bootstrap.connect(address, port).sync();
                    // add listener
                    channelFuture.addListener((ChannelFutureListener) cf -> {
                        if (cf.isSuccess()) {
                            log.info("Success to connect to remote server:{},port:{}", address, port);
                        }
                        handler = channelFuture.channel().pipeline().get(ClientRespHandler.class);
                    });
                    channelFuture.channel().closeFuture().sync();
                } catch (Exception e) {
                    e.printStackTrace();
                } finally {
                    group.shutdownGracefully();
                }
            }).start();
        }
    }

    /**
     * 创建调用对象
     * 
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> T create(final Class<?> interfaceClass) {
        // 创建动态对象
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class<?>[] {interfaceClass},
            (proxy, method, args) -> {
                // create rpc request
                DisRpcRequest request = new DisRpcRequest();
                request.setRequestId(UUID.randomUUID().toString());
                request.setMethodName(method.getName());
                request.setInterfaceName(method.getDeclaringClass().getName());
                request.setParameterTypes(method.getParameterTypes());
                request.setParameters(args);

                RPCRespFuture respFuture = handler.doExecute(request);
                return respFuture.get();
            });
    }

    /**
     *
     * @param interfaceClass
     * @param <T>
     * @return
     */
    public <T> IAsyncObjectProxy createProxy(Class<T> interfaceClass) {
        return new ObjectProxy<T>(interfaceClass, handler);
    }

    /**
     * 
     * @return
     */
    public ClientRespHandler getClientRespHandler() {
        return this.handler;
    }

}
