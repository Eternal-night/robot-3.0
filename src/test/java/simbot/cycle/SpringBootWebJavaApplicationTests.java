package simbot.cycle;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.FileResource;
import love.forte.simbot.resources.PathResource;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.service.PixivService;

import java.io.File;
import java.io.InputStream;

@SpringBootTest
class SpringBootWebJavaApplicationTests {


    @Autowired
    private PixivService pixivService;

    @Test
    public void test() throws Exception{

        File file = new File("data/images/pixiv/60180381_p0.png");

        FileResource fileResource = new FileResource(file);

        InputStream stream = fileResource.getStream();
    }


}
