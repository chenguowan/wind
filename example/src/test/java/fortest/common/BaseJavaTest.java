package fortest.common;

import cn.hutool.core.convert.Convert;
import com.google.common.base.Preconditions;
import com.google.common.base.Predicates;
import org.apache.curator.x.discovery.ServiceInstance;
import org.apache.curator.x.discovery.ServiceInstanceBuilder;
import org.junit.Test;
import storm.dota.common.register.RpcServiceInstance;

import java.net.InetAddress;
import java.net.SocketException;
import java.net.UnknownHostException;
import java.util.Collection;
import java.util.UUID;

/**
 * @Author: Chengw
 * @Date: 2021/9/13
 */
public class BaseJavaTest {

    @Test
    public void uuid() {
        UUID uuid = UUID.randomUUID();
        System.out.println("uuid.toString() = " + uuid.toString() + ", length=" + uuid.toString().length());
    }

    @Test
    public void ip() throws SocketException, UnknownHostException {
        Collection<InetAddress> ips = ServiceInstanceBuilder.getAllLocalIPs();
        System.out.println(ips);

        InetAddress addr = InetAddress.getLocalHost();
        System.out.println("Local HostAddress: " + addr.getHostAddress());
        String hostname = addr.getHostName();
        System.out.println("Local host name: " + hostname);

    }

    @Test
    public void convert() throws Exception {
        A a = new A();
        a.setId("xx");
        a.setAge(88);
        B b = Convert.convert(B.class, a);
        System.out.println(b);

        ServiceInstance serviceInstance = ServiceInstance.builder().address("addxx").port(88).name("xname").build();
        RpcServiceInstance instance = Convert.convert(RpcServiceInstance.class, serviceInstance);
        System.out.println(instance);
    }

    @Test
    public void predicates() {
        String address = null;
        Preconditions.checkNotNull(address);
        boolean apply = Predicates.notNull().apply(address);
        System.out.println(apply);
    }
}
