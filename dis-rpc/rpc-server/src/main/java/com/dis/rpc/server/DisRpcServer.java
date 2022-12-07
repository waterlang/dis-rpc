package com.dis.rpc.server;

import com.dis.rpc.comm.coder.RpcDecoder;
import com.dis.rpc.comm.coder.RpcEncoder;
import com.dis.rpc.comm.serialization.protobuf.ProtobufSerializeUtil;
import com.dis.rpc.comm.vo.DisRpcResponse;
import com.dis.rpc.comm.vo.DisRpcRequest;
import com.dis.rpc.server.annotation.DisRpcService;
import com.dis.rpc.server.handler.ServerRpcHandler;
import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.SocketChannel;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeansException;
import org.springframework.beans.factory.InitializingBean;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.ApplicationContext;
import org.springframework.context.ApplicationContextAware;
import org.springframework.stereotype.Component;
import org.springframework.util.ObjectUtils;

import java.util.HashMap;
import java.util.Map;

@Component
@Slf4j
public class DisRpcServer implements ApplicationContextAware, InitializingBean {

    @Value("${rpc.port}")
    private Integer port;

    /**
     * key:服务名   value：具体实例
     */
    private  static  final Map<String, Object> DIS_SERVICE_MAP = new HashMap<>();

    /**
     * 
     * @throws Exception
     */
    @Override
    public void afterPropertiesSet() throws Exception {
        ProtobufSerializeUtil serializeUtil = new ProtobufSerializeUtil();
        EventLoopGroup master = new NioEventLoopGroup();
        EventLoopGroup worker = new NioEventLoopGroup();
        try {
            // start server
            ServerBootstrap bootstrap = new ServerBootstrap();
            bootstrap.group(master, worker);
            bootstrap.channel(NioServerSocketChannel.class);
            bootstrap.childHandler(new ChannelInitializer<SocketChannel>() {
                protected void initChannel(SocketChannel channel) throws Exception {
                    ChannelPipeline pipeline = channel.pipeline();
                    pipeline.addLast(new RpcDecoder(DisRpcRequest.class, serializeUtil));
                    pipeline.addLast(new RpcEncoder(DisRpcResponse.class, serializeUtil));
                    pipeline.addLast(new ServerRpcHandler(DIS_SERVICE_MAP));
                }
            });

            ChannelFuture future = bootstrap.bind(port).sync();
            log.info("rpc server started，port:{}", port);

            // todo server register

            // release resource
            future.channel().closeFuture().sync();
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            worker.shutdownGracefully();
            master.shutdownGracefully();
        }
    }


    @Override
    public void setApplicationContext(ApplicationContext ctx) throws BeansException {
        // 扫描所有的带DisRpcService注解的类
        Map<String, Object> serviceBeanMap = ctx.getBeansWithAnnotation(DisRpcService.class);
        if (!ObjectUtils.isEmpty(serviceBeanMap)) {
            for (Object bean : serviceBeanMap.values()) {
                DisRpcService rpcService = bean.getClass().getAnnotation(DisRpcService.class);
                String name = rpcService.value().getName();
                DIS_SERVICE_MAP.put(name, bean);
            }
        }

    }
}
