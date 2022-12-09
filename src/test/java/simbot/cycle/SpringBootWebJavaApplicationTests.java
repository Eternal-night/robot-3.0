package simbot.cycle;

import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import simbot.cycle.util.OkHttpUtils;

import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

@SpringBootTest
class SpringBootWebJavaApplicationTests {


    @Test
    void test() throws IOException {

        OkHttpClient client = OkHttpUtils.getOkHttpClient();

        Request build = new Request.Builder().url("http://region-3.seetacloud.com:33792/file=outputs/txt2img-images/00076-3601225356-tohsaka%20rin,1girl,solo.png")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                .addHeader("Referer", "http://region-3.seetacloud.com:33792/")
                .build();

        Response response = client.newCall(build).execute();
        InputStream inputStream = response.body().byteStream();

    }

}
