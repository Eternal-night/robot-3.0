package simbot.cycle.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import net.dreamlu.mica.core.utils.StringUtil;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import simbot.cycle.constant.ConstantImage;
import simbot.cycle.entity.picture.ImageEntity;
import simbot.cycle.entity.picture.ReceiveImageEntity;
import simbot.cycle.service.ImageService;
import simbot.cycle.service.ImageTagService;
import simbot.cycle.service.PixivService;
import simbot.cycle.util.*;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.Proxy;
import java.net.URLEncoder;
import java.nio.file.Files;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ImageTagServiceImpl implements ImageTagService {

    private static final Logger logger = LoggerFactory.getLogger(ImageTagServiceImpl.class);

    @Autowired
    private ImageService imageService;



    /**
     * @description: 获取随机图片文件流
     * @author: 陈杰
     * @date: 2022/10/11 11:18
     * @return: java.io.InputStream
     **/
    @Override
    public InputStream ranDom() throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        String imageUrl = CycleUtils.getImageUrl();
        Request build = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
        String localurl = downloadImage(inputStream, ConstantImage.DEFAULT_IMAGE_SAVE_PATH_RANDOM);
        File file = new File(localurl);
        return Files.newInputStream(file.toPath());
    }

    @Override
    public InputStream ranDomR18() throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        String imageUrl = CycleUtils.getImageUrlR18();
        Request build = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
        String localurl = downloadImage(inputStream, ConstantImage.DEFAULT_IMAGE_SAVE_PATH_R18);
        logger.info(localurl);
        File file = new File(localurl);
        return Files.newInputStream(file.toPath());
    }

    @Override
    public InputStream ranDomR18(String word) throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        String imageUrl = CycleUtils.getImageUrlR18() + word;
        Request build = new Request.Builder().url(imageUrl).build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        InputStream inputStream = Objects.requireNonNull(response.body()).byteStream();
        String localurl = downloadImage(inputStream, ConstantImage.DEFAULT_IMAGE_SAVE_PATH_R18);
        logger.info(localurl);
        File file = new File(localurl);
        return Files.newInputStream(file.toPath());
    }

    private  String downloadImage(InputStream inStream,String localUrl) throws IOException {
        ByteArrayOutputStream outStream = new ByteArrayOutputStream();
        byte[] buffer = new byte[1024];
        int len = 0;
        while ((len = inStream.read(buffer)) != -1) {
            outStream.write(buffer, 0, len);
        }
        inStream.close();
        //把图片信息存下来，写入内存
        byte[] data = outStream.toByteArray();
        //创建本地文件
        File result = new File(localUrl);
        if (!result.exists()) {
            result.mkdirs();
        }
        //写入图片数据
        String fileFullName = result + File.separator + UUID.randomUUID().toString()+".jpg";
        FileOutputStream fileOutputStream = new FileOutputStream(fileFullName);
        fileOutputStream.write(data);
        fileOutputStream.flush();
        fileOutputStream.close();
        //返回文件路径
        return fileFullName;
    }


}



