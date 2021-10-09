package storm.dota.common.codec.serialize;

import com.esotericsoftware.kryo.Kryo;
import com.esotericsoftware.kryo.io.Input;
import com.esotericsoftware.kryo.io.Output;
import com.esotericsoftware.kryo.pool.KryoPool;
import lombok.SneakyThrows;
import org.objenesis.strategy.StdInstantiatorStrategy;
import storm.dota.common.codec.RpcRequest;
import storm.dota.common.codec.RpcResponse;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;

/**
 * @Author: Chengw
 * @Date: 2021/9/9
 */
public class KryoSerialize implements Serialize {

    private KryoPool pool = KryoPoolHolder.pool;

    public KryoPool getPool() {
        return pool;
    }

    @Override
    @SneakyThrows
    public byte[] serialize(Object obj) {
        Kryo kryo = pool.borrow();
        try (ByteArrayOutputStream baos = new ByteArrayOutputStream(); Output out = new Output(baos)) {
            kryo.writeObject(out, obj);
            return out.toBytes();
        } finally {
            pool.release(kryo);
        }
    }

    @Override
    @SneakyThrows
    public <T> T deSerialize(byte[] bytes, Class<T> clazz) {
        Kryo kryo = pool.borrow();
        try (ByteArrayInputStream bais = new ByteArrayInputStream(bytes); Input input = new Input(bais)) {
            return kryo.readObject(input, clazz);
        } finally {
            pool.release(kryo);
        }
    }

    private static class KryoPoolHolder {

        private static final KryoPool pool = new KryoPool.Builder(() -> {
            Kryo kryo = new Kryo();
            kryo.setReferences(false);
            kryo.register(RpcRequest.class);
            kryo.register(RpcResponse.class);
            Kryo.DefaultInstantiatorStrategy strategy = (Kryo.DefaultInstantiatorStrategy) kryo.getInstantiatorStrategy();
            strategy.setFallbackInstantiatorStrategy(new StdInstantiatorStrategy());
            return kryo;
        }).build();

    }


}
