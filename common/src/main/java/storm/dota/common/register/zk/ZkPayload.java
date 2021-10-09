package storm.dota.common.register.zk;

import lombok.Data;
import org.codehaus.jackson.map.annotate.JsonRootName;

/**
 * @Author: Chengw
 * @Date: 2021/9/16
 */
@JsonRootName("rpcPayload")
@Data
public class ZkPayload {

    private String description;

    private String methods;

}
