package storm.dota.client.handler;

import io.netty.channel.Channel;
import io.netty.channel.ChannelInitializer;
import io.netty.channel.socket.SocketChannel;
import io.netty.handler.codec.LengthFieldBasedFrameDecoder;
import storm.dota.common.codec.RpcDecoder;
import storm.dota.common.codec.RpcEncoder;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.RpcResponse;
import storm.dota.common.codec.serialize.KryoSerialize;
import storm.dota.common.codec.serialize.Serialize;
import storm.dota.common.util.ClientUtils;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public class ClientHandlerInit extends ChannelInitializer<SocketChannel> {

    private String address;
    private int port;

    public ClientHandlerInit(String address, int port) {
        this.address = address;
        this.port = port;
    }

    @Override
    protected void initChannel(SocketChannel ch) throws Exception {
        ch.pipeline().addLast(new LengthFieldBasedFrameDecoder(1024 * 64, 0, 4));
        Serialize serialize = new KryoSerialize();
        ch.pipeline().addLast(new RpcDecoder(RpcResponse.class, serialize));
        ch.pipeline().addLast(new RpcEncoder(RpcRequest.class, serialize));
        ch.pipeline().addLast(new ResponseHandler(ClientUtils.makeConnectKey(address, port)));
    }
}
