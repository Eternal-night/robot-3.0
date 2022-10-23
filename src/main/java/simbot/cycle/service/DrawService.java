package simbot.cycle.service;

import java.io.InputStream;

public interface DrawService {

    /**
     * @description: 根据关键词生成图片
     * @author: 陈杰
     * @date: 2022/10/21 9:40
     * @param: keyWord 关键词
     * @return: java.io.InputStream
     **/
    public InputStream drafting(String keyWord);

}
