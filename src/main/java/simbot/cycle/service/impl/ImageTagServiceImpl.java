package simbot.cycle.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import simbot.cycle.entity.picture.ImageEntity;
import simbot.cycle.entity.picture.ReceiveImageEntity;
import simbot.cycle.service.ImageTagService;
import simbot.cycle.util.OkHttpUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URLEncoder;
import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.util.*;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.ThreadLocalRandom;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class ImageTagServiceImpl implements ImageTagService {

    //缓存图片地址查询结果
    private static final Map<String, List<String>> ADDRESS_SET = new ConcurrentHashMap<>();

    /**
     * @description: 获取图片地址 【www.vilipix.com】
     * @author: 陈杰
     * @date: 2022/10/8 13:39
     * @param: name
     * @return: java.lang.String
     **/
    @Override
    public String getImageUrl(String name) throws IOException {

        this.makeSeek(name);

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList.isEmpty()) {
            return null;
        } else {
            //随机获取其中一个url

            ThreadLocalRandom random = ThreadLocalRandom.current();
            String url = urlList.get(random.nextInt(urlList.size()));

             OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url("https://www.vilipix.com" + url)
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String html = Objects.requireNonNull(response.body()).string();

            // 创建Document对象
            Document document = Jsoup.parse(html);

            Element layout = document.getElementById("__layout");
            assert layout != null;
            Element element = layout.getElementsByClass("illust-pages").first();
            assert element != null;
            Element img = element.getElementsByTag("img").first();

            assert img != null;
            return img.attr("src");

        }


    }



    /**
     * @description: 获取图片文件流 【pixiviz.pwp.app】
     * @author: 陈杰
     * @date: 2022/10/8 13:37
     * @param: name
     * @return: java.io.InputStream
     **/
    @Override
    public InputStream getImageInputStream(String name) throws IOException {

        this.findJson(name);

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList.isEmpty()) {
            return null;
        } else {
            //随机获取其中一个url
            Random random = new Random();
            int index = random.nextInt(urlList.size());
            String url = urlList.get(index);
            String replace = url.replace("i.pximg.net", "pixiv-image.pwp.link");

            OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url(replace)
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .addHeader("Referer", "https://pixiviz.pwp.app/")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            try {
                //获取文件流
                return Objects.requireNonNull(response.body()).byteStream();
            } catch (Exception e) {
                e.printStackTrace();
                return null;
            }

        }

    }


    /**
     * @description: 获取图片地址 【www.duitang.com】
     * @author: 陈杰
     * @date: 2022/10/8 13:39
     * @param: name
     * @return: java.lang.String
     **/
    @Override
    public String getDuitangUrl(String name) throws IOException {

        this.makeDuitangId(name);

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList.isEmpty()) {
            return null;
        } else {
            //随机获取其中一个url
            Random random = new Random();
            int index = random.nextInt(urlList.size());
            String url = urlList.get(index);

            OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url("https://www.duitang.com/blog/?id=" + url)
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .addHeader("referer", "https://www.duitang.com/")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            //获取内容
            String html = Objects.requireNonNull(response.body()).string();

            // 创建Document对象
            Document document = Jsoup.parse(html);

            Element element = document.getElementsByClass("img-out").first();
            assert element != null;
            return element.attr("href");

        }


    }

    /**
     * @description: 获取随机图片文件流
     * @author: 陈杰
     * @date: 2022/10/11 11:18
     * @return: java.io.InputStream
     **/
    @Override
    public InputStream ranDom() throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        Request build = new Request.Builder().url("https://iw233.cn/API/Random.php")
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                .build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        return Objects.requireNonNull(response.body()).byteStream();
    }

    /**
     * @description: 清空临时数据
     * @author: 陈杰
     * @date: 2022/10/8 13:39
     **/
    @Override
    public void ClearData() {
        List<String> rank = ADDRESS_SET.get("rank");
        ADDRESS_SET.clear();
        if (rank != null) {
            ADDRESS_SET.put("rank", rank);
        }
    }

    /**
     * @description: 初始化资源库
     * @author: 陈杰
     * @date: 2022/10/11 14:34
     **/
    @Override
    public Boolean initRank(){

        try {
            LocalDate date = LocalDate.now();
            String format = date.format(DateTimeFormatter.ISO_DATE);
            this.getRankData(format,1);
            this.getRankData(format,2);
            this.getRankData(format,3);
            return true;
        }catch (Exception e){
            e.printStackTrace();
            return false;
        }

    }

    /**
     * @description: 构建图片地址 【www.vilipix.com】
     * @author: 陈杰
     * @date: 2022/10/8 13:39
     * @param: name
     **/
    private void makeSeek(String name) throws IOException {

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList == null) {

            List<String> list = new ArrayList<>();

            String encode = URLEncoder.encode(name, "utf-8");

            OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url("https://www.vilipix.com/tags/" + encode + "/illusts")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            String html = Objects.requireNonNull(response.body()).string();

            // 创建Document对象
            Document document = Jsoup.parse(html);
            //获取查询结果列表
            Element result = document.getElementsByClass("illust-content").first();
            assert result != null;
            Elements elementList = result.getElementsByTag("li");

            for (Element element : elementList) {
                Element imageElement = element.getElementsByClass("illust").first();
                if (imageElement != null) {
                    String link = imageElement.getElementsByTag("a").first().attr("href");
                    list.add(link);
                }
            }
            ADDRESS_SET.put(name, list);
        }


    }


    /**
     * @description: 获取关键字检索结果 【pixiviz.pwp.app】
     * @author: 陈杰
     * @date: 2022/10/8 13:37
     * @param: name
     **/
    private void findJson(String name) throws IOException {

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList == null) {

            String encode = URLEncoder.encode(name, "utf-8");

            OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url("https://pixiviz-api-tc.pwp.link/v1/illust/search?word=" + encode + "&page=1")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .addHeader("Referer", "https://pixiviz.pwp.app/")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            try {
                String json = Objects.requireNonNull(response.body()).string();
                //解析
                ReceiveImageEntity imageEntity = JSON.parseObject(json, ReceiveImageEntity.class);
                List<ImageEntity> illusts = imageEntity.getIllusts();
                urlList = illusts.stream().map(n -> n.getImage_urls().getMedium()).collect(Collectors.toList());
            } catch (Exception e) {
                e.printStackTrace();
            }

            ADDRESS_SET.put(name, urlList);
        }
    }


    /**
     * @description: 构建图片地址 【www.duitang.com】
     * @author: 陈杰
     * @date: 2022/10/8 13:39
     * @param: name
     **/
    private void makeDuitangId(String name) throws IOException {

        List<String> urlList = ADDRESS_SET.get(name);

        if (urlList == null) {

            List<String> list = new ArrayList<>();

            String encode = URLEncoder.encode(name, "utf-8");

            OkHttpClient client = OkHttpUtils.getOkHttpClient();

            Request build = new Request.Builder().url("https://www.duitang.com/search/?kw=" + encode + "&type=feed")
                    .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                    .addHeader("referer", "https://www.duitang.com/")
                    .build();

            Response response = client.newCall(build).execute();

            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);

            try {
                //获取内容
                String html = Objects.requireNonNull(response.body()).string();

                Document document = Jsoup.parse(html);

                Elements elements = document.getElementsByClass("woo");

                for (Element element : elements) {
                    String attr = element.attr("data-id");
                    list.add(attr);
                }

            } catch (Exception e) {
                e.printStackTrace();
            }

            ADDRESS_SET.put(name, list);
        }
    }



    /**
     * @description: 获取资源库
     * @author: 陈杰
     * @date: 2022/10/11 14:25
     **/
    private void getRankData(String date,Integer page) throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        Request build = new Request.Builder().url("https://pixiviz-api-tc.pwp.link/v1/illust/rank?mode=month&date="+date+"&page="+page)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                .addHeader("Referer","https://pixiviz.pwp.app/")
                .build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String data = response.body().string();
        JSONObject jsonObject = JSON.parseObject(data);
        ReceiveImageEntity entity = jsonObject.to(ReceiveImageEntity.class);
        List<ImageEntity> illusts = entity.getIllusts();
        ADDRESS_SET.put("rank",illusts.stream().map(n -> n.getImage_urls().getMedium()).collect(Collectors.toList()));
    }
}



