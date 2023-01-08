package simbot.cycle.service;

import java.io.IOException;
import java.io.InputStream;

public interface ImageTagService {

    /**
     * 获取随机图片
     * @return
     * @throws IOException
     */
    InputStream ranDom() throws IOException;


    /**
     * 获取随机r18图片
     * @return
     * @throws IOException
     */
    InputStream ranDomR18() throws IOException;

    /**
     * 获取指定词条R18图片
     * @param word
     * @return
     * @throws IOException
     */
    InputStream ranDomR18(String word) throws IOException;

}
