package storm.dota.client.handler;

import io.netty.util.concurrent.DefaultPromise;
import lombok.Data;
import storm.dota.common.codec.RpcRequest;

/**
 * @Author: Chengw
 * @Date: 2021/9/24
 */
public class RpcPromise extends DefaultPromise {

    private RpcRequest request;

    public RpcPromise(RpcRequest request){
        super();
        this.request = request;
    }

    public RpcRequest getRequest() {
        return request;
    }

    public String getRequestId() {
        return request.getRequestId();
    }
}
