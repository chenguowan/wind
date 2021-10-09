package storm.dota.common.codec.serialize;

/**
 * @Author: Chengw
 * @Date: 2021/9/7
 */
public interface Serialize {

    byte[] serialize(Object obj);

    <T> T deSerialize(byte[] bytes, Class<T>clazz);

}
