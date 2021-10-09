package storm.dota.example;

import storm.dota.client.ClientApp;
import storm.dota.common.RpcConfigs;

/**
 * @Author: Chengw
 * @Date: 2021/10/9
 */
public class ClientExample {
    public static void main(String[] args) throws Exception {
        ClientApp clientApp = new ClientApp(RpcConfigs.zkAddress);
        HelloService helloService = clientApp.createService(HelloService.class);
        HelloPO helloPO = helloService.hello("杰克");
        System.out.println("hello = " + helloPO);
        String makeFriend = helloService.makeFriend("露娜");
        System.out.println("makeFriend = " + makeFriend);
    }
}
