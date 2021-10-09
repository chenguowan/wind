package storm.dota.common.codec;

import lombok.Data;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Data
public class RpcRequest {
    private String requestId;
    private String className;
    private String methodName;
    private Class<?>[] parameterTypes;
    private Object[] parameters;
//    private Throwable t;
}
