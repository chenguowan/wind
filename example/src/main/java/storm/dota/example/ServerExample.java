package storm.dota.example;

import storm.dota.common.RpcConfigs;
import storm.dota.server.ServerApp;

/**
 * @Author: Chengw
 * @Date: 2021/10/9
 */
public class ServerExample {

    public static void main(String[] args) throws Exception {
        ServerApp serverApp = new ServerApp(RpcConfigs.rpcExposePort, RpcConfigs.zkAddress);
        serverApp.start();
        serverApp.registerService(HelloService.class, HelloServiceImpl.class);
    }
}
