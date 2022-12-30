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
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.OkHttpUtils;

import java.io.*;
import java.util.Objects;
import java.util.UUID;

@SpringBootTest
class SpringBootWebJavaApplicationTests {

    @Test
    public void test() throws Exception {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        String imageUrl = CycleUtils.getImageUrlR18() + "狂三";
        Request build = new Request.Builder().url(imageUrl)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                .build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        InputStream inStream = Objects.requireNonNull(response.body()).byteStream();

        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[8192];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        //把图片信息存下来，写入内存
        byte[] data = outStream.toByteArray();

        //使用网络图片的中段目录
//            String path = imageUrl.substring(imageUrl.indexOf("n/") + 1, imageUrl.lastIndexOf("/"));

        //创建本地文件
        File result = new File(ConstantImage.DEFAULT_IMAGE_SAVE_PATH);
        if (!result.exists()) {
            result.mkdirs();
        }

        String string = UUID.randomUUID().toString();

        //写入图片数据
        String fileFullName = result + File.separator + string+".jpg";
        FileOutputStream fileOutputStream = new FileOutputStream(fileFullName);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();

    }

    /**
     * 输入流转文件
     *
     * @param ins
     * @param file
     */
    public static void inputStreamToFile(InputStream ins, File file) {
        BufferedOutputStream bos = null;
        BufferedInputStream bis = new BufferedInputStream(ins);
        try {
            bos = new BufferedOutputStream(new FileOutputStream(file));
            int bytesRead = 0;
            byte[] buffer = new byte[8192];
            while ((bytesRead = bis.read(buffer, 0, 8192)) != -1) {
                bos.write(buffer, 0, bytesRead);
            }
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            if (ins != null) {
                try {
                    ins.close();
                } catch (IOException e) {
                }
                ins = null;
            }
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                }
                bos = null;
            }
            if (bis != null) {
                try {
                    bis.close();
                } catch (IOException e) {
                }
                bis = null;
            }
        }
    }


}
