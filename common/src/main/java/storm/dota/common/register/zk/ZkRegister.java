package storm.dota.common.register.zk;

import com.google.common.collect.Maps;
import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.utils.CloseableUtils;
import org.apache.curator.x.discovery.ServiceDiscovery;
import org.apache.curator.x.discovery.ServiceDiscoveryBuilder;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.details.JsonInstanceSerializer;
import storm.dota.common.RpcConfigs;
import storm.dota.common.register.Register;
import storm.dota.common.register.RpcServiceInstance;
import storm.dota.common.util.NetUtils;

import java.io.Closeable;
import java.io.IOException;
import java.util.Arrays;
import java.util.Map;
import java.util.stream.Collectors;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Slf4j
public class ZkRegister implements Closeable, Register {

    public static final String DEFAULT_PATH = "";
    private ServiceDiscovery<ZkPayload> serviceDiscovery;
    private CuratorFramework client;
    private int rpcPort = 0;

    public ZkRegister(String zkAddress, int rpcExposePort) throws Exception {
        this.rpcPort = rpcExposePort;
        ZkConnector zkConnector = new ZkConnector(zkAddress);
        client = zkConnector.getClient();
        // if you mark your payload class with @JsonRootName the provided JsonInstanceSerializer will work
        JsonInstanceSerializer<ZkPayload> serializer = new JsonInstanceSerializer(ZkPayload.class);
        serviceDiscovery = ServiceDiscoveryBuilder.builder(ZkPayload.class)
                .client(client)
                .basePath(DEFAULT_PATH)
                .serializer(serializer)
                .build();

        serviceDiscovery.start();
    }


    private Map<Class, ServiceInstance<ZkPayload>> registerServiceMap = Maps.newHashMap();
    public void registerService(Class interfaceClass) throws Exception {
        if(registerServiceMap.containsKey(interfaceClass)){
            log.info("interfaceClass already register: " + interfaceClass.getName());
            return;
        }
        ZkPayload rpcPayload = new ZkPayload();
        rpcPayload.setMethods(Arrays.stream(interfaceClass.getMethods()).map(m->m.getName()).collect(Collectors.joining(",")));
        rpcPayload.setDescription("desc");
        ServiceInstance<ZkPayload> serviceInstance = ServiceInstance.<ZkPayload>builder()
                .address(NetUtils.getLocalHost()).port(this.rpcPort)
                .name(interfaceClass.getName())
                .payload(rpcPayload)
                .build();
        serviceDiscovery.registerService(serviceInstance);
        registerServiceMap.put(interfaceClass, serviceInstance);
        log.info("register service [{}] instance success", interfaceClass.getName());
    }

    public void unregisterService(Class interfaceClass) throws Exception {
        if(registerServiceMap.containsKey(interfaceClass)){
            ServiceInstance<ZkPayload> instance = registerServiceMap.get(interfaceClass);
            serviceDiscovery.unregisterService(instance);
            log.info("unregister service [{}] instance success", interfaceClass.getName());
        }
    }

    @Override
    public void close() throws IOException {
        CloseableUtils.closeQuietly(serviceDiscovery);
        CloseableUtils.closeQuietly(client);
    }


}
