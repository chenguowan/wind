package storm.dota.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.ByteToMessageDecoder;
import storm.dota.common.codec.serialize.Serialize;

import java.util.List;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public class RpcDecoder extends ByteToMessageDecoder {

    private Class clazz;
    private Serialize serialize;

    public RpcDecoder(Class clazz, Serialize serialize) {
        this.clazz = clazz;
        this.serialize = serialize;
    }

    @Override
    protected void decode(ChannelHandlerContext ctx, ByteBuf in, List<Object> out) throws Exception {
        int dataLength = in.readInt();
        byte[] bytes = new byte[dataLength];
        in.readBytes(bytes);
        Object o = serialize.deSerialize(bytes, clazz);
        out.add(o);
    }
}
