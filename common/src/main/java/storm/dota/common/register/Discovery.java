package storm.dota.common.register;

/**
 * @Author: Chengw
 * @Date: 2021/9/22
 */
public interface Discovery {

    public RpcServiceInstance getService(Class interfaceClazz) throws Exception;

}
