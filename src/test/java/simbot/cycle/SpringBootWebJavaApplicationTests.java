package simbot.cycle;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.service.PixivService;

@SpringBootTest
class SpringBootWebJavaApplicationTests {


    @Autowired
    private PixivService pixivService;

    @Test
    public void test() throws Exception{

        PixivImageInfo tag = pixivService.getPixivIllustByTag("狂三");

        pixivService.parseImages(tag);


    }


}
