package simbot.cycle.apirequest.pixiv;



import com.alibaba.fastjson2.JSONObject;
import lombok.Data;
import lombok.Getter;
import lombok.Setter;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import simbot.cycle.apirequest.BaseRequest;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.util.HttpsUtil;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

/**
 * create by MikuLink on 2020/12/17 22:10
 * for the Reisen
 * 根据p站图片id获取插画图片信息 尚未找到官方API，或者稳定的第三方，所以使用爬虫
 * https://www.pixiv.net/artworks/82343475
 */
@Data
public class PixivIllustGet extends BaseRequest {
    //根据pid查看图片的页面，get请求，最后需要追加p站图片id
    private static final String URL = "https://www.pixiv.net/artworks/";

    //就一个参数，直接构造里传入
    public PixivIllustGet(Long pixivImgId) {
        pixivId = pixivImgId;
    }

    /**
     * p站图片id
     */
    private Long pixivId;

    /**
     * pixiv图片解析后的对象
     */
    private PixivImageInfo pixivImageInfo;

    /**
     * 执行请求
     *
     * @throws IOException 所有异常上抛，由业务处理
     */
    public void doRequest() throws IOException {
        if (null == pixivId) return;
        header.put("cookie","__utmz=235335808.1667921412.6.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not provided); _fbp=fb.1.1664507419868.1862602951; _gcl_au=1.1.481618285.1664507656; a_type=0; b_type=1; c_type=24; device_token=03fc95c7c0e06f62eb967d8f84dfc6b2; p_ab_d_id=2039590838; p_ab_id=8; p_ab_id_2=7; privacy_policy_agreement=5; privacy_policy_notification=0; pt_60er4xix=uid=hFoX142kAZ8ahatf3utX/w&nid=1&vid=A4VHu75Cm7Fqcy1OfFpqQA&vn=1&pvn=1&sact=1667960215757&to_flag=0&pl=IZoRf4OCSv6aouL5bhDEAg*pt*1667960092467; _im_vid=01GE65Z76F7K3ENYBVEDBQ7X34; login_ever=yes; adr_id=zYtXz85f1oclsoyfl46UpNA5aYIv8QXmIoeTOOMGjaoXSxaF; first_visit_datetime_pc=2022-09-30+12:10:17; yuid_b=SQB1RUA; __utmc=235335808; _gid=GA1.2.1003216965.1671588966; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; user_language=zh; __utma=235335808.516395827.1664507419.1671588961.1671607877.18; __utmt=1; __cf_bm=zaBLQoXWElWeTt09wzH4CauwWTe8WqiKFOYtgYWz2uw-1671609146-0-AdOdWTJjNlWCUlRzncgoLLW9qNEt9+ywW9iqoH8rXnThgaDVYThXtxLjHT3hZpfKrFk/oCDL2wulfCcDhfgylss0IH3/mPghGoqqhxWQ0c2SI9DBD19dLFiDvFZBQbGt1rNjGmdEf35HyuFd1D5Qyg3htD+JegWt21XkxYxHQ9YuOpw8Ier+LQtcBPzg1iIhJA2MFPkP/Zh5L70rZKO2A/8=; PHPSESSID=47152070_02SX0OsEpGEcuoektMNZoWTwdTYhJosv; _ga_MZ1NL4PHH0=GS1.1.1671609156.6.1.1671609260.0.0.0; __utmv=235335808.|2=login ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=47152070=1^9=p_ab_id=8=1^10=p_ab_id_2=7=1^11=lang=zh=1; _ga=GA1.2.998634401.1664507419; _gat_UA-1830249-3=1; cto_bundle=h1ef7V81bkY1VHliJTJGaWxOdHVtU0h5MmVGampwR0RFQkdMdiUyQmIlMkJVJTJCR3hGRHB2TGM5VHN4eUIxNXVIc2NRMGlGbVpNV2Z3UGl2ekVwbjdpUDd5Uk5KcnBkc09nQmtpa05GekclMkZZV3pxRURlTTc5WVFUa3p3YmgybGJlJTJGVEwlMkZ1VFhSMW9rcW5tOG9tbkYlMkJPZkRXM0pWY3FQaGhnJTNEJTNE; __utmb=235335808.8.10.1671607877; tag_view_ranking=-RR2Rsko5M~2nSCQQsTc0~WqJ8j0AdVx~MnQk2KrTaG~Ikk8Watd0M~RTJMXD26Ak~Lt-oEicbBr~5OXRF8yfCA~XpQ4ZH7dmM~BQFWWhxtER~PGh9Qwfchx~g5QWNKvqWG~YF75pHGGsJ~JzbKqbNQRw~PjCCJkiD1-~MgCvx1Tr0K~OEfLPRhb4I~-g5xRar-5o~X1wsnjZiV4~sIiQpu87ZS~6RGIKaaoI0~vEwIx5JKyv~lGfGcfePHt~2wbHjR4kz3~4nHKycFnns~vwbZBw15pU~FFz0M9z5uL~kUbnfdh8c_~Lu4zOaUsEB~-98s6o2-Rp~cuF6-_UeSz~9oafiNFKrr~KOnmT1ndWG~8Le-BdaoRB~mHukPa9Swj~c9a3RoAIGM~-1ptKh53rB~pCXwCwUx-_~2V_Hzbh2hg~pzzjRSV6ZO~oYAm9klH0r~dGFX4_Ftmp~2-ZLcTJsOe~CrFcrMFJzz~0Sds1vVNKR~SF27tOuLS4~40drnC0w2C~ixJ21_XZkb~tgP8r-gOe_~s1DI4r3R9d~i4Q_o7CyIB~_hSAdpN9rx~3WMR0mAYlN~Mll6Av0Rvx~4-_9de7LBH~Xq0AUR-upz~cpt_Nk5mjc~xCR2NgnI9z~dlfkwgDL8s~CRMYKjF-AY~gwZWjfYJz0~2zE5ERppfi~rpFAkqMwRB~y8GNntYHsi~j0QoKstmJz~OIWp81xmT1~fn5nUXtjWI~IM8w4TyKT9~Mj8JOWqtax~Jmyuf6Ki0a~XHY_w__Yr8~gooMLQqB9a; _ga_75BBYNYN9J=GS1.1.1671607878.18.1.1671609302.0.0.0");
        //获取pid图片详情信息
        byte[] resultBytes = HttpsUtil.doGet(URL + pixivId,header ,proxy);
        body = new String(resultBytes);
        //使用jsoup解析html
        Document document = Jsoup.parse(body);
        //这里从页面可以获取到图片详情信息，是一个超长的json字符串
        String pidJsonStr = document.getElementById("meta-preload-data").attr("content");
        //取需要的即可，信息太多，不需要的都不解析
        JSONObject pidInfoJson = JSONObject.parseObject(pidJsonStr);
        //结构特殊，还包含动态字段名称，需要多点步骤处理
        String illustInfoStr = JSONObject.toJSONString(pidInfoJson.get("illust"));
        pidInfoJson = JSONObject.parseObject(illustInfoStr);
        String imageInfoStr = JSONObject.toJSONString(pidInfoJson.get(String.valueOf(pixivId)));
        pixivImageInfo = JSONObject.parseObject(imageInfoStr, PixivImageInfo.class);
    }
}
