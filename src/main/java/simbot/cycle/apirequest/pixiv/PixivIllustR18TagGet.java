package simbot.cycle.apirequest.pixiv;


import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import simbot.cycle.apirequest.BaseRequest;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.util.EncodingUtil;
import simbot.cycle.util.HttpUtil;
import simbot.cycle.util.HttpsUtil;
import simbot.cycle.util.NumberUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * create by MikuLink on 2020/12/18 2:10
 * for the Reisen
 * 根据tag获取pixiv插图信息 尚未找到官方API，或者稳定的第三方，所以使用爬虫
 * 如果不登录，有很多功能受限
 * <p>
 * https://www.pixiv.net/ajax/search/artworks/時崎狂三 1000users入り?word=時崎狂三 1000users入り&order=date_d&mode=r18&p=1&s_mode=s_tag&type=all&lang=zh
 */
public class PixivIllustR18TagGet extends BaseRequest {
    //根据tag搜索图片
    private static final String URL = "https://www.pixiv.net/ajax/search/artworks/";

    /**
     * 关键词
     */
    @Setter
    private String word;
    /**
     * 排序方式
     * date_d 从新到旧
     * 其他方式需要登录
     */
    private String order = "date_d";
    /**
     * 可能是涉及R18什么的
     */
    private String mode = "r18";
    /**
     * 搜索模式
     */
    private String s_mode = "s_tag";
    /**
     * 图片类型
     * illust_and_ugoira 点击插画也是传入的这个，不知道ugoira代表的是什么
     */
    private String type = "all";

    /**
     * 第几页，默认为第一页，最大1000页，如果不登录，最大为10页
     * 页书大小不可选，因为是模拟网页访问，
     */
    @Setter
    private int p = 1;

    //排行榜解析结果
    @Getter
    private List<PixivImageInfo> responseList = new ArrayList<>();

    /**
     * 执行请求
     *
     * @throws IOException 所有异常上抛，由业务处理
     */
    public void doRequest() throws IOException {
        //拼装参数
        addParam();
        header.put("cookie","__utmz=235335808.1667921412.6.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not provided); _fbp=fb.1.1664507419868.1862602951; _gcl_au=1.1.481618285.1664507656; a_type=0; b_type=1; c_type=24; device_token=03fc95c7c0e06f62eb967d8f84dfc6b2; p_ab_d_id=2039590838; p_ab_id=8; p_ab_id_2=7; privacy_policy_agreement=5; privacy_policy_notification=0; pt_60er4xix=uid=hFoX142kAZ8ahatf3utX/w&nid=1&vid=A4VHu75Cm7Fqcy1OfFpqQA&vn=1&pvn=1&sact=1667960215757&to_flag=0&pl=IZoRf4OCSv6aouL5bhDEAg*pt*1667960092467; _im_vid=01GE65Z76F7K3ENYBVEDBQ7X34; login_ever=yes; adr_id=zYtXz85f1oclsoyfl46UpNA5aYIv8QXmIoeTOOMGjaoXSxaF; first_visit_datetime_pc=2022-09-30+12:10:17; yuid_b=SQB1RUA; __utmc=235335808; _gid=GA1.2.1003216965.1671588966; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; user_language=zh; __utma=235335808.516395827.1664507419.1671588961.1671607877.18; __utmt=1; __cf_bm=zaBLQoXWElWeTt09wzH4CauwWTe8WqiKFOYtgYWz2uw-1671609146-0-AdOdWTJjNlWCUlRzncgoLLW9qNEt9+ywW9iqoH8rXnThgaDVYThXtxLjHT3hZpfKrFk/oCDL2wulfCcDhfgylss0IH3/mPghGoqqhxWQ0c2SI9DBD19dLFiDvFZBQbGt1rNjGmdEf35HyuFd1D5Qyg3htD+JegWt21XkxYxHQ9YuOpw8Ier+LQtcBPzg1iIhJA2MFPkP/Zh5L70rZKO2A/8=; PHPSESSID=47152070_02SX0OsEpGEcuoektMNZoWTwdTYhJosv; _ga_MZ1NL4PHH0=GS1.1.1671609156.6.1.1671609260.0.0.0; __utmv=235335808.|2=login ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=47152070=1^9=p_ab_id=8=1^10=p_ab_id_2=7=1^11=lang=zh=1; _ga=GA1.2.998634401.1664507419; _gat_UA-1830249-3=1; cto_bundle=h1ef7V81bkY1VHliJTJGaWxOdHVtU0h5MmVGampwR0RFQkdMdiUyQmIlMkJVJTJCR3hGRHB2TGM5VHN4eUIxNXVIc2NRMGlGbVpNV2Z3UGl2ekVwbjdpUDd5Uk5KcnBkc09nQmtpa05GekclMkZZV3pxRURlTTc5WVFUa3p3YmgybGJlJTJGVEwlMkZ1VFhSMW9rcW5tOG9tbkYlMkJPZkRXM0pWY3FQaGhnJTNEJTNE; __utmb=235335808.8.10.1671607877; tag_view_ranking=-RR2Rsko5M~2nSCQQsTc0~WqJ8j0AdVx~MnQk2KrTaG~Ikk8Watd0M~RTJMXD26Ak~Lt-oEicbBr~5OXRF8yfCA~XpQ4ZH7dmM~BQFWWhxtER~PGh9Qwfchx~g5QWNKvqWG~YF75pHGGsJ~JzbKqbNQRw~PjCCJkiD1-~MgCvx1Tr0K~OEfLPRhb4I~-g5xRar-5o~X1wsnjZiV4~sIiQpu87ZS~6RGIKaaoI0~vEwIx5JKyv~lGfGcfePHt~2wbHjR4kz3~4nHKycFnns~vwbZBw15pU~FFz0M9z5uL~kUbnfdh8c_~Lu4zOaUsEB~-98s6o2-Rp~cuF6-_UeSz~9oafiNFKrr~KOnmT1ndWG~8Le-BdaoRB~mHukPa9Swj~c9a3RoAIGM~-1ptKh53rB~pCXwCwUx-_~2V_Hzbh2hg~pzzjRSV6ZO~oYAm9klH0r~dGFX4_Ftmp~2-ZLcTJsOe~CrFcrMFJzz~0Sds1vVNKR~SF27tOuLS4~40drnC0w2C~ixJ21_XZkb~tgP8r-gOe_~s1DI4r3R9d~i4Q_o7CyIB~_hSAdpN9rx~3WMR0mAYlN~Mll6Av0Rvx~4-_9de7LBH~Xq0AUR-upz~cpt_Nk5mjc~xCR2NgnI9z~dlfkwgDL8s~CRMYKjF-AY~gwZWjfYJz0~2zE5ERppfi~rpFAkqMwRB~y8GNntYHsi~j0QoKstmJz~OIWp81xmT1~fn5nUXtjWI~IM8w4TyKT9~Mj8JOWqtax~Jmyuf6Ki0a~XHY_w__Yr8~gooMLQqB9a; _ga_75BBYNYN9J=GS1.1.1671607878.18.1.1671609302.0.0.0");
        header.put("referer","https://www.pixiv.net/tags/" +
                EncodingUtil.encodeURIComponent(word) +
                "/artworks?mode=r18&s_mode=s_tag");
        //爬虫获取排行榜信息
        byte[] resultBytes = HttpsUtil.doGet(URL + EncodingUtil.encodeURIComponent(word) + HttpUtil.parseUrlEncode(param), header, proxy);
        body = new String(resultBytes);
    }

    /**
     * 解析返回报文
     */
    public List<PixivImageInfo> parseImageList() {
        Map<?, ?> rootMap = JSONObject.parseObject(body, HashMap.class);

        Map<?, ?> bodyMap = JSONObject.parseObject(JSONObject.toJSONString(rootMap.get("body")), HashMap.class);

        Map<?, ?> illustMap = JSONObject.parseObject(JSONObject.toJSONString(bodyMap.get("illustManga")), HashMap.class);

        //解析结果
        responseList = JSONObject.parseArray(JSONObject.toJSONString(illustMap.get("data")), PixivImageInfo.class);

        return responseList;
    }

    /**
     * 获取搜索结果总数
     */
    public int getTotal() {
        Map<?, ?> rootMap = JSONObject.parseObject(body, HashMap.class);

        Map<?, ?> bodyMap = JSONObject.parseObject(JSONObject.toJSONString(rootMap.get("body")), HashMap.class);

        Map<?, ?> illustMap = JSONObject.parseObject(JSONObject.toJSONString(bodyMap.get("illustManga")), HashMap.class);

        return NumberUtil.toInt(illustMap.get("total"));
    }

    //拼装参数
    private void addParam() {
        param.put("word", word);
        param.put("order", order);
        param.put("s_mode", s_mode);
        param.put("mode", mode);
        param.put("type", type);
        param.put("p", p);
    }
}
