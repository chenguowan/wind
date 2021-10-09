package storm.dota.common;

import lombok.AllArgsConstructor;
import lombok.RequiredArgsConstructor;

import java.io.IOException;

/**
 * @Author: Chengw
 * @Date: 2021/9/10
 */
public class RpcException extends RuntimeException {

    public RpcException() {
        super();
    }

    public RpcException(String message) {
        super(message);
    }

    public RpcException(String message, Throwable cause) {
        super(message, cause);
    }

    public RpcException(Throwable cause) {
        super(cause);
    }

    protected RpcException(String message, Throwable cause,
                           boolean enableSuppression,
                           boolean writableStackTrace) {
        super(message, cause, enableSuppression, writableStackTrace);
    }


}
