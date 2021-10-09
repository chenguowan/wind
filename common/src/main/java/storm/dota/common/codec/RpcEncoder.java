package storm.dota.common.codec;

import io.netty.buffer.ByteBuf;
import io.netty.channel.ChannelHandlerContext;
import io.netty.handler.codec.MessageToByteEncoder;
import storm.dota.common.codec.serialize.Serialize;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public class RpcEncoder extends MessageToByteEncoder {

    private Class clazz;
    private Serialize serialize;

    public RpcEncoder(Class clazz, Serialize serialize) {
        this.clazz = clazz;
        this.serialize = serialize;
    }

    @Override
    protected void encode(ChannelHandlerContext ctx, Object msg, ByteBuf out) throws Exception {
        byte[] bytes = this.serialize.serialize(msg);
        out.writeInt(bytes.length);
        out.writeBytes(bytes);
    }
}
