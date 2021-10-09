package fortest.other;

import cn.hutool.core.img.ImgUtil;
import cn.hutool.core.io.FileUtil;
import org.junit.Test;

/**
 * @Author: Chengw
 * @Date: 2021/9/24
 */
public class ImgTest {

    @Test
    public void gray(){
        ImgUtil.gray(FileUtil.file("/Users/cgw/Downloads/pics/little_girl.jpg"), FileUtil.file("/Users/cgw/Downloads/result.jpg"));

    }
}
