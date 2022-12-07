package com.dis.rpc.comm.coder;

import com.dis.rpc.comm.serialization.MsgSerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import lombok.extern.slf4j.Slf4j;

/**
 * 
 */

@Slf4j
public class RpcEncoder extends MessageToByteEncoder {

    private Class<?> genericClass;

    private MsgSerializeUtil serializeUtil;

    public RpcEncoder(Class<?> genericClass, MsgSerializeUtil serializeUtil) {
        this.genericClass = genericClass;
        this.serializeUtil = serializeUtil;
    }

    @Override
    protected void encode(ChannelHandlerContext channelHandlerContext, Object o, ByteBuf byteBuf) throws Exception {
        // log.info("-------------encode start ----------------");
        if (genericClass.isInstance(o)) {
            byte[] data = serializeUtil.serialize(o);
            byteBuf.writeInt(data.length);
            byteBuf.writeBytes(data);
        }
        // log.info("-------------encode end ----------------");
    }

}
