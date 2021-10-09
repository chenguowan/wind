package fortest.common;

import lombok.Data;

/**
 * @Author: Chengw
 * @Date: 2021/9/13
 */
@Data
public class B {

    private String id;
    private Integer age;

    private String eatThing;

    public String eat(String msg){
        return " eat: " + msg;
    }

}
