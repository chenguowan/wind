package storm.dota.common;

/**
 * @Author: Chengw
 * @Date: 2021/9/13
 */
public class RpcConfigs {

    /**
     * Rpc服务暴露的端口
     */
    public static int rpcExposePort = 9999;
    /**
     * zookeeper注册中心地址
     */
    public static String zkAddress = "127.0.0.1:2181";
    /**
     * 服务调用超时时间
     */
    public static int timeoutMs = 5 * 1000;


}
