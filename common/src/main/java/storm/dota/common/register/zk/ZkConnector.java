package storm.dota.common.register.zk;

import lombok.extern.slf4j.Slf4j;
import org.apache.curator.framework.CuratorFramework;
import org.apache.curator.framework.CuratorFrameworkFactory;
import org.apache.curator.retry.ExponentialBackoffRetry;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Chengw
 * @Date: 2021/9/16
 */
@Slf4j
public class ZkConnector {

    public static final String NAMESPACE = "wind-rpc";
    private static final int DEFAULT_SESSION_TIMEOUT_MS = Integer.getInteger("curator-default-session-timeout", 60 * 1000);
    private static final int DEFAULT_CONNECTION_TIMEOUT_MS = Integer.getInteger("curator-default-connection-timeout", 15 * 1000);
    private CuratorFramework client;


    public ZkConnector(String zkAddress) throws InterruptedException {
        log.info("creating CuratorFramework Client in address {}", zkAddress);
        client = CuratorFrameworkFactory.builder().namespace(NAMESPACE).connectString(zkAddress)
                .sessionTimeoutMs(DEFAULT_SESSION_TIMEOUT_MS).connectionTimeoutMs(DEFAULT_CONNECTION_TIMEOUT_MS)
                .retryPolicy(new ExponentialBackoffRetry(1000, 3))
                .build();
        client.start();
        client.blockUntilConnected(20, TimeUnit.SECONDS);
    }

    public CuratorFramework getClient() {
        return client;
    }

}
