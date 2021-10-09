package storm.dota.client.handler;

import cn.hutool.core.exceptions.StatefulException;
import cn.hutool.core.map.MapUtil;
import cn.hutool.core.util.StrUtil;
import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import lombok.extern.slf4j.Slf4j;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.RpcResponse;

import java.util.Map;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Slf4j
public class ResponseHandler extends SimpleChannelInboundHandler<RpcResponse> {

    private static Map<String, ResponseHandler> responseHandlerMap = MapUtil.newHashMap();

    public static ResponseHandler getHandler(String address) {
        return responseHandlerMap.get(address);
    }


    private Map<String, RpcPromise> rpcPromiseMap = MapUtil.newHashMap();

    private ChannelHandlerContext ctx;

    public ResponseHandler(String addressAndPort) {
        responseHandlerMap.put(addressAndPort, this);
    }

    @Override
    public void channelRegistered(ChannelHandlerContext ctx) throws Exception {
        this.ctx = ctx;
        super.channelRegistered(ctx);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcResponse response) throws Exception {

        String requestId = response.getRequestId();
        if (!rpcPromiseMap.containsKey(requestId)) {
            throw new RuntimeException("can not find RpcPromise by requestId");
        }
        RpcPromise rpcPromise = rpcPromiseMap.get(requestId);
        String error = response.getError();
        if (error != null) {
            rpcPromise.setFailure(new StatefulException(StrUtil.format("call remote method error [{}]", rpcPromise.getRequest().toString())));
        } else {
            rpcPromise.setSuccess(response.getResult());
        }
        rpcPromiseMap.remove(requestId);
    }


    public RpcPromise sendRequest(RpcRequest request) {
        RpcPromise rpcPromise = new RpcPromise(request);
        rpcPromiseMap.put(request.getRequestId(), rpcPromise);
        this.ctx.writeAndFlush(request).addListener(f -> {
            if (!f.isSuccess()) {
                rpcPromise.setFailure(new StatefulException(1, StrUtil.format("Send request {} error", request.getRequestId())));
                rpcPromiseMap.remove(request.getRequestId());
                this.ctx.close();
            }
        });
        return rpcPromise;
    }
}
