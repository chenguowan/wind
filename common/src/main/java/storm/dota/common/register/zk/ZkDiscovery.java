package storm.dota.common.register.zk;

import cn.hutool.core.convert.Convert;
import cn.hutool.core.exceptions.StatefulException;
import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceProvider;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import org.apache.curator.x.discovery.strategies.RoundRobinStrategy;
import storm.dota.common.register.Discovery;
import storm.dota.common.register.RpcServiceInstance;

import java.io.Closeable;
import java.io.IOException;
import java.util.Map;

/**
 * @Author: Chengw
 * @Date: 2021/9/17
 */
@Slf4j
public class ZkDiscovery implements Closeable, Discovery {
    private ServiceDiscovery<ZkPayload> serviceDiscovery;
    private CuratorFramework client;

    private Map<String, ServiceProvider<ZkPayload>> providerMap = Maps.newHashMap();

    public ZkDiscovery(String zkAddress) throws Exception {
        ZkConnector zkConnector = new ZkConnector(zkAddress);
        client = zkConnector.getClient();
        JsonInstanceSerializer<ZkPayload> serializer = new JsonInstanceSerializer(ZkPayload.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ZkPayload.class)
                .client(client)
                .basePath(ZkRegister.DEFAULT_PATH)
                .serializer(serializer)
                .build();
        serviceDiscovery.start();
    }

    private ServiceProvider newProvider(String name){
        ServiceProvider<ZkPayload> p = serviceDiscovery.serviceProviderBuilder().serviceName(name)
                .providerStrategy(new RoundRobinStrategy())/**策略有 ：{@link RoundRobinStrategy} StickyStrategy RandomStrategy 三种 */
                .build();
        try {
            p.start();
        } catch (Exception e) {
            throw new RuntimeException("error when ZkDiscovery.newProvider.start()",e);
        }
        return p;
    }

    public RpcServiceInstance getService(Class interfaceClazz) throws Exception {
        String name = interfaceClazz.getName();
        ServiceProvider<ZkPayload> serviceProvider = providerMap.computeIfAbsent(name,
                t -> newProvider(name)
        );
        ServiceInstance<ZkPayload> instance = serviceProvider.getInstance();
        if(instance == null){
            throw new StatefulException(2,"no service find instance in register");
        }
        return this.convertToRpcServiceInstance(instance);
    }

    private RpcServiceInstance convertToRpcServiceInstance(ServiceInstance<ZkPayload> instance){
        RpcServiceInstance build = RpcServiceInstance.builder().address(instance.getAddress())
                .port(instance.getPort()).build();
        return build;
    }


    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }

}
