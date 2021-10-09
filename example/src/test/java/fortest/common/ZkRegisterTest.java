package fortest.common;

import org.junit.Test;
import storm.dota.common.RpcConfigs;
import storm.dota.common.RpcConstants;
import storm.dota.common.register.RpcServiceInstance;
import storm.dota.common.register.zk.ZkDiscovery;
import storm.dota.common.register.zk.ZkRegister;

import java.util.concurrent.TimeUnit;

/**
 * @Author: Chengw
 * @Date: 2021/9/16
 */
public class ZkRegisterTest {

    @Test
    public void t0() throws Exception {
        ZkRegister zkRegister = new ZkRegister(RpcConfigs.zkAddress, RpcConfigs.rpcExposePort);
        zkRegister.registerService(HelloService.class);
        zkRegister.registerService(FuckService.class);
        TimeUnit.MINUTES.sleep(1);
        zkRegister.close();
    }

    @Test
    public void t1() throws Exception {
        ZkRegister zkRegister = new ZkRegister(RpcConfigs.zkAddress, RpcConfigs.rpcExposePort);
        ZkRegister zkRegister1 = new ZkRegister(RpcConfigs.zkAddress, RpcConfigs.rpcExposePort -1);
        ZkDiscovery zkDiscovery = new ZkDiscovery(RpcConfigs.zkAddress);

        zkRegister.registerService(HelloService.class);
        zkRegister.registerService(FuckService.class);

        zkRegister1.registerService(HelloService.class);
        zkRegister1.registerService(FuckService.class);

        for (int i = 0; i < 6; i++) {
            RpcServiceInstance rpcServiceInstance = zkDiscovery.getService(HelloService.class);
            System.out.println(rpcServiceInstance);
        }

        zkRegister1.unregisterService(HelloService.class);
        System.out.println(RpcConstants.SEPARATE_LINE);
//        TimeUnit.SECONDS.sleep(1);

        for (int i = 0; i < 6; i++) {
            RpcServiceInstance rpcServiceInstance = zkDiscovery.getService(HelloService.class);
            System.out.println(rpcServiceInstance);
        }

        TimeUnit.SECONDS.sleep(1);


        try {
            zkRegister.close();
            zkRegister1.close();
            zkDiscovery.close();
        } catch (Exception e) {
            e.printStackTrace();
        }
        System.out.println("over");

    }

}
