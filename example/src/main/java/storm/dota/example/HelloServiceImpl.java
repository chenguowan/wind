package storm.dota.example;

import lombok.extern.slf4j.Slf4j;

import java.util.Random;

/**
 * @Author: Chengw
 * @Date: 2021/10/9
 */
@Slf4j
public class HelloServiceImpl implements HelloService{
    @Override
    public HelloPO hello(String name) {
        log.info("HelloServiceImpl.hello {}", name);
        HelloPO helloPO = new HelloPO();
        helloPO.setName(name);
        helloPO.setAge(new Random(30).nextInt() + 10);
        helloPO.setSex(new Random(1).nextInt() == 0 ? "男" : "女");
        return helloPO;
    }

    @Override
    public String makeFriend(String name) {
        log.info("HelloServiceImpl.makeFriend {}", name);
        return new Random(1).nextInt() == 0 ? "好呀" : "不想";
    }
}
