package simbot.cycle.service.impl;


import lombok.RequiredArgsConstructor;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.stereotype.Service;
import simbot.cycle.entity.YuGiOh.YuGiOh;
import simbot.cycle.entity.YuGiOh.YuGiOhDetails;
import simbot.cycle.service.YuGiOhService;
import simbot.cycle.util.OkHttpUtils;

import java.io.IOException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;


@Service
@RequiredArgsConstructor
public class YuGiOhServiceImpl implements YuGiOhService {

    //卡片地址获取
    @Override
    public List<YuGiOh> YuGiOhInquire(String name) throws IOException {
        OkHttpClient client = OkHttpUtils.getOkHttpClient();
        String encode = URLEncoder.encode(name, "utf-8");
        List<YuGiOh> yuGiOhList = new ArrayList<>();
        Request build = new Request.Builder().url("https://ygocdb.com/?search=" + encode)
                .addHeader("user-agent", "Mozilla/5.0 (Windows NT 10.0; Win64; x64) AppleWebKit/537.36 (KHTML, like Gecko) Chrome/103.0.0.0 Safari/537.36")
                .build();
        Response response = client.newCall(build).execute();
        if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
        String html = Objects.requireNonNull(response.body()).string();
            // 创建Document对象
            Document document = Jsoup.parse(html);
            // 获取查询结果
            Elements elements = document.getElementsByClass("row card result");
            if (!elements.isEmpty()) {
                for (Element element : elements) {
                    YuGiOh yuGiOh = new YuGiOh();
                    Element imageElement = element.getElementsByClass("col-md-3 col-xs-4 cardimg").first();
                    assert imageElement != null;
                    String imageUrl = imageElement.getElementsByTag("img").attr("data-original");
                    yuGiOh.setImageUrl(imageUrl);
                    Element nameElement = element.getElementsByClass("col-md-6 col-xs-8 names").first();
                    assert nameElement != null;
                    Elements nameSpan = nameElement.getElementsByTag("span");
                    List<String> names = nameSpan.stream().map(n -> n.text()).collect(Collectors.toList());
                    yuGiOh.setNameList(names);
                    Element descElemen = element.getElementsByClass("desc").first();
                    assert descElemen != null;
                    String text = descElemen.html();
                    YuGiOhDetails details = handle(text);
                    yuGiOh.setDetails(details);
                    yuGiOhList.add(yuGiOh);
                }
            }
        return yuGiOhList;
    }

    //卡片详情处理
    private YuGiOhDetails handle(String text) {
        YuGiOhDetails details = new YuGiOhDetails();
        String[] split = text.split("</strong>");
        String dail = split[1];
        String[] split2 = dail.split("<hr>");
        int length = split2.length;
        if (length == 2) {
            details.setInformation(split2[0].replaceAll("<br>", ""));
            details.setSwingingEffect(split2[1].replaceAll("<br>", ""));
        } else if (length == 3) {
            details.setInformation(split2[0].replaceAll("<br>", ""));
            details.setSwingingEffect(split2[1].replaceAll("<br>", ""));
            details.setMonsterEffect(split2[2].replaceAll("<br>", ""));
        }
        return details;
    }

}
