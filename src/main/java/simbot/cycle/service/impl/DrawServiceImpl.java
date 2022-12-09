package simbot.cycle.service.impl;

import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import okhttp3.*;
import org.springframework.stereotype.Service;
import simbot.cycle.entity.AutoSavePluginConfig;
import simbot.cycle.entity.PostData;
import simbot.cycle.service.DrawService;
import simbot.cycle.util.OkHttpUtils;

import java.io.*;
import java.net.URL;
import java.util.Base64;
import java.util.Random;
import java.util.UUID;
import java.util.concurrent.TimeUnit;

@Service
@RequiredArgsConstructor
public class DrawServiceImpl implements DrawService {

    private static final String allChar = "0123456789abcdefghijklmnopqrstuvwxyzABCDEFGHIJKLMNOPQRSTUVWXYZ";

    /**
     * @description: 根据关键词生成图片
     * @author: 陈杰
     * @date: 2022/10/21 9:41
     * @param: keyWord  关键词
     * @return: java.io.InputStream
     **/
    @Override
    public InputStream drafting(String keyWord) {
        try {
            OkHttpClient client = new OkHttpClient.Builder()
                    .connectTimeout(10, TimeUnit.MINUTES)
                    .readTimeout(10, TimeUnit.MINUTES).build();

            AutoSavePluginConfig config = new AutoSavePluginConfig();

            String seed;

            StringBuilder builder = new StringBuilder();

            if (config.seed == -1L) {
                Random random = new Random();
                for (int i = 0; i < 10; i++) {
                    builder.append(random.nextInt(9));
                }
                seed = builder.toString();
            } else {
                seed = config.seed.toString();
            }

            String randomString = generateString(11);

            PostData data = new PostData();

            data.setFn_index(config.textFnIndex);
            data.setSession_hash(randomString);

            Object[] arr = {keyWord,
                    config.negativePrompt,
                    config.promptStyle,//None
                    config.promptStyle2,//None
                    config.steps,//20
                    config.samplerIndex,//Euler a
                    config.restoreFaces,//false
                    config.tiling,//false
                    config.nIter,//1
                    config.batchSize,//1
                    config.cfgScale,//7
                    Long.parseLong(seed),//-1
                    config.subSeed,//-1
                    config.subSeedStrength,//0
                    config.seedResizeFromH,//0
                    config.seedResizeFromW,//0
                    config.seedEnableExtras,//false
                    config.height,//512
                    config.width,//512
                    config.enableHr,//false
                    //Config.scaleLatent,
                    config.denoisingStrength,//0.7
                    0,
                    0,
                    config.script,//None
                    config.putVariablePartsAtStartOfPrompt,//false
                    false,
                    null,
                    "",
                    config.xtype,
                    config.xvalues,
                    config.ytype,
                    config.yvalues,
                    config.drawLegend,//true
                    config.keepRandomSeeds,//false
                    false,
                    null,
                    "",
                    ""};

            data.setData(arr);

            // 构造 Content-Type 头
            MediaType mediaType = MediaType.parse("application/json; charset=UTF-8");
            // 构造请求数据
            RequestBody requestBody = RequestBody.create(JSON.toJSONString(data), mediaType);

            Request request = new Request.Builder()
                    .url(config.stableDiffusionWebui + "/api/predict/")
                    .post(requestBody)
                    .addHeader("content-type", "application/json").build();

            Response response = client.newCall(request).execute();

            String jsonString = response.body().string();

            JSONObject jsonObject = JSON.parseObject(jsonString);

            String dataUrl = JSON.parseArray(jsonObject.getJSONArray("data").get(0).toString()).get(0).toString();

            if (dataUrl.contains("data:image")) {
                dataUrl = dataUrl.substring(dataUrl.indexOf(",") + 1);
                File file = base64ToFile(dataUrl, ".png");
                return new FileInputStream(file);
            }else {
                Object name = JSON.parseObject(dataUrl).get("name");
                String url = config.stableDiffusionWebui + "/file=" + name;

                Request build = new Request.Builder().url(url)
                        .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                        .addHeader("Referer", config.stableDiffusionWebui+"/")
                        .build();

                InputStream inputStream = client.newCall(build).execute().body().byteStream();

                return inputStream;
            }
        } catch (Exception e) {
            return null;
        }

    }


    /**
     * 返回一个定长的随机字符串(只包含大小写字母、数字)
     *
     * @param length 随机字符串长度
     * @return 随机字符串
     */
    public static String generateString(int length) {

        StringBuilder sb = new StringBuilder();

        Random random = new Random();

        for (int i = 0; i < length; i++) {

            sb.append(allChar.charAt(random.nextInt(allChar.length())));

        }

        return sb.toString();

    }


    /**
     * @description: 将DATA URL转换为文件
     * @author: 陈杰
     * @date: 2022/10/21 9:21
     * @param: base64FileStr DATA URL
     * @param: fileType 文件类型
     * @return: java.io.File
     **/
    public static File base64ToFile(String base64FileStr, String fileType) throws Exception {
        base64FileStr = base64FileStr.replace("\r\n", "");
        // 在用户temp目录下创建临时文件
        File file = File.createTempFile(UUID.randomUUID().toString(), fileType);
        BufferedOutputStream bos = null;
        FileOutputStream fos = null;
        try {
            // 用Base64进行解码后获取的字节数组可以直接转换为文件
            byte[] bytes = Base64.getDecoder().decode(base64FileStr);
            fos = new FileOutputStream(file);
            bos = new BufferedOutputStream(fos);
            bos.write(bytes);
        } finally {
            if (bos != null) {
                try {
                    bos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (fos != null) {
                try {
                    fos.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return file;
    }
}
