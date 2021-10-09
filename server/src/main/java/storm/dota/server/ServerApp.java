package storm.dota.server;

import io.netty.bootstrap.ServerBootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelOption;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioServerSocketChannel;
import lombok.extern.slf4j.Slf4j;
import storm.dota.common.RpcConfigs;
import storm.dota.common.register.Register;
import storm.dota.common.register.zk.ZkRegister;
import storm.dota.server.handler.RequestHandler;
import storm.dota.server.handler.ServerHandlerInit;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Slf4j
public class ServerApp {

    private int rpcExposePort;
    private String zkAddress;
    private Register register;

    public ServerApp(int rpcExposePort, String zkAddress) {
        this.rpcExposePort = rpcExposePort;
        this.zkAddress = zkAddress;
    }

    public void start() throws Exception {
        //连接到注册中心
        this.register = new ZkRegister(this.zkAddress, this.rpcExposePort);
        //启动netty服务，监听请求
        new Thread(() -> {
            EventLoopGroup boss = new NioEventLoopGroup();
            EventLoopGroup worker = new NioEventLoopGroup();
            try {
                ServerBootstrap serverBootstrap = new ServerBootstrap();
                serverBootstrap.group(boss, worker)
                        .channel(NioServerSocketChannel.class)
                        .option(ChannelOption.SO_BACKLOG, 100)
                        .childHandler(new ServerHandlerInit());
                ChannelFuture f = serverBootstrap.bind(rpcExposePort).sync();
                f.channel().closeFuture().sync();
            } catch (InterruptedException e) {
                log.error("", e);
            } finally {
                boss.shutdownGracefully();
                worker.shutdownGracefully();
            }
        }).start();

    }

    public void registerService(Class interfaceClass, Class implClass) throws Exception {
        this.register.registerService(interfaceClass);
        RequestHandler.addClass(interfaceClass, implClass);
    }

    public void unregisterService(Class interfaceClass) throws Exception {
        this.register.unregisterService(interfaceClass);
    }

}
