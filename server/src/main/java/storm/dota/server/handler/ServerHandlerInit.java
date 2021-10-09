package storm.dota.server.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.ChannelPipeline;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import storm.dota.common.codec.RpcDecoder;
import storm.dota.common.codec.RpcEncoder;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.RpcResponse;
import storm.dota.common.codec.serialize.KryoSerialize;
import storm.dota.common.codec.serialize.Serialize;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public class ServerHandlerInit extends ChannelInitializer<SocketChannel> {

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ChannelPipeline pipeline = ch.pipeline();
        pipeline.addLast(new LengthFieldBasedFrameDecoder(1024*64,0,4));
        Serialize serialize = new KryoSerialize();
        pipeline.addLast(new RpcDecoder(RpcRequest.class, serialize));
        pipeline.addLast(new RpcEncoder(RpcResponse.class, serialize));
        pipeline.addLast(new RequestHandler());
    }
}
