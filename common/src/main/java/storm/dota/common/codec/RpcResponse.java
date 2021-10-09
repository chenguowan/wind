package storm.dota.common.codec;

import lombok.Data;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Data
public class RpcResponse {
    private String requestId;
    private Object result;
    private String error;
}
