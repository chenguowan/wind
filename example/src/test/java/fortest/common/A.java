package fortest.common;

import lombok.Data;

/**
 * @Author: Chengw
 * @Date: 2021/9/13
 */
@Data
public class A {

    private String id;
    private Integer age;

    public String echo(String msg, int count){
        return count + " Hi! " + msg;
    }

}
