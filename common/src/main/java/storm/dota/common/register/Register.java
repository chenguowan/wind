package storm.dota.common.register;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public interface Register {

    public void registerService(Class interfaceClass) throws Exception;

    public void unregisterService(Class interfaceClass) throws Exception;

}
