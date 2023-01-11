package simbot.cycle;

import cn.hutool.core.io.resource.ClassPathResource;
import cn.hutool.core.io.resource.FileResource;
import com.alibaba.fastjson2.util.UUIDUtils;
import love.forte.simbot.resources.PathResource;
import net.dreamlu.mica.core.utils.StringUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import simbot.cycle.constant.ConstantImage;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.service.PixivService;
import simbot.cycle.service.SetuService;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.OkHttpUtils;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
class SpringBootWebJavaApplicationTests {

    @Autowired
    private SetuService setuService;

    @Test
    public void test() throws Exception {

        PixivImageInfo setu = setuService.getSetu();
        System.out.println(setu.getId());
    }




}
