package storm.dota.client.proxy;

import cn.hutool.core.exceptions.ExceptionUtil;
import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.util.StrUtil;
import com.google.common.base.Preconditions;
import io.netty.bootstrap.Bootstrap;
import io.netty.channel.ChannelFuture;
import io.netty.channel.ChannelFutureListener;
import io.netty.channel.EventLoopGroup;
import io.netty.channel.nio.NioEventLoopGroup;
import io.netty.channel.socket.nio.NioSocketChannel;
import lombok.extern.slf4j.Slf4j;
import storm.dota.client.handler.ClientHandlerInit;
import storm.dota.client.handler.ResponseHandler;
import storm.dota.common.RpcConfigs;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.register.Discovery;
import storm.dota.common.register.RpcServiceInstance;
import storm.dota.common.util.ClientUtils;

import java.lang.reflect.InvocationHandler;
import java.lang.reflect.Method;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

/**
 * @Author: Chengw
 * @Date: 2021/9/13
 */
@Slf4j
public class ObjectProxy implements InvocationHandler {

    private Class interfaceClazz;
    private Discovery discovery;

    public ObjectProxy(Class clazz, Discovery discovery) {
        this.interfaceClazz = clazz;
        this.discovery = discovery;
    }

    @Override
    public Object invoke(Object proxy, Method method, Object[] args) throws Throwable {
        System.out.println(method.getDeclaringClass());
        if (Object.class == method.getDeclaringClass()) {
            String name = method.getName();
            if ("equals".equals(name)) {
                return proxy == args[0];
            } else if ("hashCode".equals(name)) {
                return System.identityHashCode(proxy);
            } else if ("toString".equals(name)) {
                return proxy.getClass().getName() + "@" + Integer.toHexString(System.identityHashCode(proxy)) + ", with InvocationHandler " + this;
            }
        }
        RpcServiceInstance serviceInstance = discovery.getService(interfaceClazz);
        if (serviceInstance == null) {
            throw new RuntimeException(String.format("no service found for [%s]", interfaceClazz.getName()));
        }
        String address = serviceInstance.getAddress();
        Preconditions.checkNotNull(address);
        Integer port = serviceInstance.getPort();
        Preconditions.checkNotNull(port);
        RpcRequest rpcRequest = this.makeRequest(method, args);
        ResponseHandler handler = getHandler(address, port);
        //调用并阻塞
        return handler.sendRequest(rpcRequest).get(RpcConfigs.timeoutMs, TimeUnit.MILLISECONDS);
    }

    private RpcRequest makeRequest(Method method, Object[] args) {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId(UUID.randomUUID().toString());
        rpcRequest.setClassName(this.interfaceClazz.getName());
        rpcRequest.setMethodName(method.getName());
        rpcRequest.setParameterTypes(method.getParameterTypes());
        rpcRequest.setParameters(args);
        return rpcRequest;
    }

    private ResponseHandler getHandler(String address, int port) {
        ResponseHandler handler = ResponseHandler.getHandler(ClientUtils.makeConnectKey(address, port));
        if (handler == null) {
            try {
                this.connect(address, port);
            } catch (InterruptedException e) {
                ExceptionUtil.wrapAndThrow(e);
            }
        }
        handler = ResponseHandler.getHandler(ClientUtils.makeConnectKey(address, port));
        if (handler == null) {
            throw new Error("can not get client handler");
        }
        return handler;
    }

    private void connect(String ip, int port) throws InterruptedException {
        EventLoopGroup group = new NioEventLoopGroup();
        Bootstrap b = new Bootstrap();
        b.channel(NioSocketChannel.class).group(group)
                .handler(new ClientHandlerInit(ip, port));
        ChannelFuture future = b.connect(ip, port).sync();
        if(!future.isSuccess()){
            throw new StatefulException(5, StrUtil.format("connect to remote[{},{}] address fail", ip,port));
        }
    }

}
