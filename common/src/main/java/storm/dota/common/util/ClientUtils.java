package storm.dota.common.util;

/**
 * @Author: Chengw
 * @Date: 2021/10/9
 */
public class ClientUtils {

    public static String makeConnectKey(String address, int port) {
        return address + ":" + port;
    }
}
