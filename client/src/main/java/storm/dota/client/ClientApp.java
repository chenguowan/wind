package storm.dota.client;

import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import storm.dota.client.handler.ClientHandlerInit;
import storm.dota.client.proxy.ObjectProxy;
import storm.dota.common.register.Discovery;
import storm.dota.common.register.zk.ZkDiscovery;

import java.lang.reflect.Proxy;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public class ClientApp {

    private Discovery discovery;

    public ClientApp(String zkAddress) throws Exception {
        discovery = new ZkDiscovery(zkAddress);
    }

    public <T> T createService(Class<T> interfaceClass) {
        return (T) Proxy.newProxyInstance(interfaceClass.getClassLoader(), new Class[]{interfaceClass}, new ObjectProxy(interfaceClass, discovery));
    }

    public Discovery getDiscovery() {
        return discovery;
    }


}
