package com.dis.rpc.comm.coder;

import com.dis.rpc.comm.serialization.MsgSerializeUtil;
import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import lombok.extern.slf4j.Slf4j;

import java.util.List;

/**
 * 
 */
@SuppressWarnings("all")
@Slf4j
public class RpcDecoder extends ByteToMessageDecoder {

    private Class<?> genericClass;

    private MsgSerializeUtil serializeUtil;

    public RpcDecoder(Class<?> genericClass, MsgSerializeUtil serializeUtil) {
        this.genericClass = genericClass;
        this.serializeUtil = serializeUtil;
    }

    /**
     * 
     * @param channelHandlerContext
     * @param byteBuf
     * @param list
     * @throws Exception
     */
    @Override
    protected void decode(ChannelHandlerContext channelHandlerContext, ByteBuf byteBuf, List<Object> list)
        throws Exception {
        // log.info("-------------decode-----------------");
        if (byteBuf.readableBytes() < 4) {
            return;
        }

        byteBuf.markReaderIndex();
        int dataLength = byteBuf.readInt();
        if (byteBuf.readableBytes() < dataLength) {
            byteBuf.resetReaderIndex();
            return;
        }

        // log.info("-----------decode data------------------");
        byte[] data = new byte[dataLength];
        byteBuf.readBytes(data);
        list.add(serializeUtil.deserialize(data, genericClass));
    }
}
