package storm.dota.server.handler;

import io.netty.channel.ChannelHandlerContext;
import io.netty.channel.SimpleChannelInboundHandler;
import io.netty.handler.codec.MessageToMessageCodec;
import io.netty.handler.codec.MessageToMessageDecoder;
import lombok.extern.slf4j.Slf4j;
import net.sf.cglib.reflect.FastClass;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.RpcResponse;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
@Slf4j
public class RequestHandler extends SimpleChannelInboundHandler<RpcRequest> {

    /**
     * 接口类和它实现类的映射
     */
    private static  Map<String, Class> classMap = new HashMap<>();
    public static void addClass(Class interfaceClass, Class implClass){
        classMap.put(interfaceClass.getName(), implClass);
    }

    @Override
    protected void channelRead0(ChannelHandlerContext ctx, RpcRequest request) throws Exception {
        RpcResponse response = new RpcResponse();
        response.setRequestId(request.getRequestId());
        response.setError(this.check(request));
        if(response.getError()==null){
            //todo 交给业务线程处理
            try {
                response.setResult(this.invoke(request));
            } catch (Exception e) {
                response.setError(e.getMessage());
                log.error("RPC Server handle request error", e);
            }
        }
        ctx.writeAndFlush(response).addListener((f) -> log.info("Send response for request " + request.getRequestId()));
    }

    @Override
    public void exceptionCaught(ChannelHandlerContext ctx, Throwable cause) throws Exception {
        log.error("Server caught exception: " + cause.getMessage());
        ctx.close();
    }

    private String check(RpcRequest request){
        if(request == null || request.getClassName()==null || request.getMethodName()==null || request.getRequestId()==null){
            log.error("RpcRequest or RpcRequest.Param null: " + request);
            return "RpcRequest or RpcRequest.Param null: " + request;
        }
        return null;
    }

    private Object invoke(RpcRequest request) throws Exception {
//        Class<?> clazz = Class.forName(request.getClassName());
        Class clazz = classMap.get(request.getClassName());
        FastClass fastClass = FastClass.create(clazz);
        int index = fastClass.getIndex(request.getMethodName(), request.getParameterTypes());
        return fastClass.invoke(index, fastClass.newInstance(), request.getParameters());
    }
}
