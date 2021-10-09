package storm.dota.common.register;

import lombok.Builder;
import lombok.Data;

/**
 * @Author: Chengw
 * @Date: 2021/9/22
 */
@Data
@Builder
public class RpcServiceInstance {
    private String address;
    private Integer port;
}
