package fortest.common;

import com.esotericsoftware.kryo.pool.KryoPool;
import net.sf.cglib.reflect.FastClass;
import org.junit.Test;
import storm.dota.common.RpcConstants;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.serialize.KryoSerialize;

import java.lang.reflect.Method;

/**
 * @Author: Chengw
 * @Date: 2021/9/10
 */
public class KryoSerializeTest {


    public RpcRequest create() {
        RpcRequest rpcRequest = new RpcRequest();
        rpcRequest.setRequestId("10086");
        rpcRequest.setClassName(A.class.getName());
        Method method1 = A.class.getDeclaredMethods()[0];
        rpcRequest.setMethodName(method1.getName());
        rpcRequest.setParameterTypes(method1.getParameterTypes());
        rpcRequest.setParameters(new Object[]{"jesik", 99});
//        rpcRequest.setT(new Exception("fuck"));

        try {
            Method[] declaredMethods = A.class.getDeclaredMethods();
            Method method = declaredMethods[0];
            Object o = method.invoke(new A(), "jesik", 99);
            System.out.println(o);
        } catch (Exception e) {
            e.printStackTrace();
        }
        return rpcRequest;
    }

    @Test
    public void isSingle(){
        KryoSerialize k1 = new KryoSerialize();
        KryoSerialize k2 = new KryoSerialize();
        KryoPool p1 = k1.getPool();
        KryoPool p2 = k2.getPool();
        System.out.println(p1 == p2);
    }

    @Test
    public void serialize(){
        RpcRequest rpcRequest = this.create();
        System.out.println(rpcRequest);
        KryoSerialize kryoSerialize = new KryoSerialize();
        byte[] bytes = kryoSerialize.serialize(rpcRequest);
        System.out.println(RpcConstants.SEPARATE_LINE);
        RpcRequest deSerialize = kryoSerialize.deSerialize(bytes, RpcRequest.class);
        System.out.println(deSerialize);
        String className = rpcRequest.getClassName();
        try {
            Class<?> aClass = Class.forName(className);
            FastClass serviceFastClass = FastClass.create(aClass);
            int index = serviceFastClass.getIndex(rpcRequest.getMethodName(), rpcRequest.getParameterTypes());
            Object invokeResult = serviceFastClass.invoke(index, aClass.newInstance(), rpcRequest.getParameters());
            System.out.println("invokeResult: " + invokeResult);
        } catch (Exception e) {
            e.printStackTrace();
        }


    }


}
