package simbot.cycle.apirequest.pixiv;

import lombok.Getter;
import lombok.Setter;
import net.dreamlu.mica.core.utils.StringUtil;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import simbot.cycle.apirequest.BaseRequest;
import simbot.cycle.entity.pixiv.PixivUserInfo;
import simbot.cycle.util.CollectionUtil;
import simbot.cycle.util.HttpsUtil;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

/**
 * create by MikuLink on 2021/1/6 3:33
 * for the Reisen
 * 搜索pixiv用户 只搜索投稿用户
 * https://www.pixiv.net/search_user.php?nick=初音ミク&s_mode=s_usr
 */
@Setter
@Getter
public class PixivUserSearch extends BaseRequest {
    //模拟页面请求，需要解析html
    private static final String URL = "https://www.pixiv.net/search_user.php?nick=%s&s_mode=s_usr&p=%s";

    /**
     * p站用户名称
     */
    private String pixivUserNick;
    /**
     * 页数
     * 一般没那么多重名的
     */
    private Integer p = 1;

    /**
     * pixiv用户列表
     */
    private List<PixivUserInfo> responseList = new ArrayList<>();

    /**
     * 执行请求
     *
     * @throws IOException 所有异常上抛，由业务处理
     */
    public void doRequest() throws IOException {
        if (StringUtil.isBlank(pixivUserNick)) return;

        //挂上referer
        header.put("referer", String.format("https://www.pixiv.net/tags/%s/artworks?s_mode=s_tag", pixivUserNick));
        header.put("cookie","__utmz=235335808.1667921412.6.2.utmcsr=google|utmccn=(organic)|utmcmd=organic|utmctr=(not provided); _fbp=fb.1.1664507419868.1862602951; _gcl_au=1.1.481618285.1664507656; a_type=0; b_type=1; c_type=24; device_token=03fc95c7c0e06f62eb967d8f84dfc6b2; p_ab_d_id=2039590838; p_ab_id=8; p_ab_id_2=7; privacy_policy_agreement=5; privacy_policy_notification=0; pt_60er4xix=uid=hFoX142kAZ8ahatf3utX/w&nid=1&vid=A4VHu75Cm7Fqcy1OfFpqQA&vn=1&pvn=1&sact=1667960215757&to_flag=0&pl=IZoRf4OCSv6aouL5bhDEAg*pt*1667960092467; _im_vid=01GE65Z76F7K3ENYBVEDBQ7X34; login_ever=yes; adr_id=zYtXz85f1oclsoyfl46UpNA5aYIv8QXmIoeTOOMGjaoXSxaF; first_visit_datetime_pc=2022-09-30+12:10:17; yuid_b=SQB1RUA; __utmc=235335808; _gid=GA1.2.1003216965.1671588966; QSI_S_ZN_5hF4My7Ad6VNNAi=v:0:0; user_language=zh; __utma=235335808.516395827.1664507419.1671588961.1671607877.18; __utmt=1; __cf_bm=zaBLQoXWElWeTt09wzH4CauwWTe8WqiKFOYtgYWz2uw-1671609146-0-AdOdWTJjNlWCUlRzncgoLLW9qNEt9+ywW9iqoH8rXnThgaDVYThXtxLjHT3hZpfKrFk/oCDL2wulfCcDhfgylss0IH3/mPghGoqqhxWQ0c2SI9DBD19dLFiDvFZBQbGt1rNjGmdEf35HyuFd1D5Qyg3htD+JegWt21XkxYxHQ9YuOpw8Ier+LQtcBPzg1iIhJA2MFPkP/Zh5L70rZKO2A/8=; PHPSESSID=47152070_02SX0OsEpGEcuoektMNZoWTwdTYhJosv; _ga_MZ1NL4PHH0=GS1.1.1671609156.6.1.1671609260.0.0.0; __utmv=235335808.|2=login ever=yes=1^3=plan=normal=1^5=gender=male=1^6=user_id=47152070=1^9=p_ab_id=8=1^10=p_ab_id_2=7=1^11=lang=zh=1; _ga=GA1.2.998634401.1664507419; _gat_UA-1830249-3=1; cto_bundle=h1ef7V81bkY1VHliJTJGaWxOdHVtU0h5MmVGampwR0RFQkdMdiUyQmIlMkJVJTJCR3hGRHB2TGM5VHN4eUIxNXVIc2NRMGlGbVpNV2Z3UGl2ekVwbjdpUDd5Uk5KcnBkc09nQmtpa05GekclMkZZV3pxRURlTTc5WVFUa3p3YmgybGJlJTJGVEwlMkZ1VFhSMW9rcW5tOG9tbkYlMkJPZkRXM0pWY3FQaGhnJTNEJTNE; __utmb=235335808.8.10.1671607877; tag_view_ranking=-RR2Rsko5M~2nSCQQsTc0~WqJ8j0AdVx~MnQk2KrTaG~Ikk8Watd0M~RTJMXD26Ak~Lt-oEicbBr~5OXRF8yfCA~XpQ4ZH7dmM~BQFWWhxtER~PGh9Qwfchx~g5QWNKvqWG~YF75pHGGsJ~JzbKqbNQRw~PjCCJkiD1-~MgCvx1Tr0K~OEfLPRhb4I~-g5xRar-5o~X1wsnjZiV4~sIiQpu87ZS~6RGIKaaoI0~vEwIx5JKyv~lGfGcfePHt~2wbHjR4kz3~4nHKycFnns~vwbZBw15pU~FFz0M9z5uL~kUbnfdh8c_~Lu4zOaUsEB~-98s6o2-Rp~cuF6-_UeSz~9oafiNFKrr~KOnmT1ndWG~8Le-BdaoRB~mHukPa9Swj~c9a3RoAIGM~-1ptKh53rB~pCXwCwUx-_~2V_Hzbh2hg~pzzjRSV6ZO~oYAm9klH0r~dGFX4_Ftmp~2-ZLcTJsOe~CrFcrMFJzz~0Sds1vVNKR~SF27tOuLS4~40drnC0w2C~ixJ21_XZkb~tgP8r-gOe_~s1DI4r3R9d~i4Q_o7CyIB~_hSAdpN9rx~3WMR0mAYlN~Mll6Av0Rvx~4-_9de7LBH~Xq0AUR-upz~cpt_Nk5mjc~xCR2NgnI9z~dlfkwgDL8s~CRMYKjF-AY~gwZWjfYJz0~2zE5ERppfi~rpFAkqMwRB~y8GNntYHsi~j0QoKstmJz~OIWp81xmT1~fn5nUXtjWI~IM8w4TyKT9~Mj8JOWqtax~Jmyuf6Ki0a~XHY_w__Yr8~gooMLQqB9a; _ga_75BBYNYN9J=GS1.1.1671607878.18.1.1671609302.0.0.0");
        //返回的是一个html
        byte[] resultBytes = HttpsUtil.doGet(String.format(URL, pixivUserNick, p), header, proxy);
        body = new String(resultBytes);
        //使用jsoup解析html
        Document document = Jsoup.parse(body);

        //选择目标节点，类似于JS的选择器
        Elements rankElements = document.getElementsByClass("user-recommendation-item");
        //未找到任何用户
        if (CollectionUtil.isEmpty(rankElements)) return;

        //解析用户
        for (Element element : rankElements) {
            //<a href="/users/6098039"class="_user-icon size-128 cover-texture ui-scroll-view"target="_blank"title="初音ミクの猫"data-filter="lazy-image"data-src="https://i.pximg.net/user-profile/img/2018/11/02/15/48/48/14969934_e78e358458e1a790ac4b75cacfba0c11_170.png">
            String userNick = element.childNodes().get(0).attr("title");
            String userId = element.childNodes().get(0).attr("href");
            String logo = element.childNodes().get(0).attr("data-src");
            userId = userId.substring(userId.lastIndexOf("/") + 1);

            PixivUserInfo tempUserInfo = new PixivUserInfo();
            tempUserInfo.setId(userId);
            tempUserInfo.setNick(userNick);
            tempUserInfo.setLogoUrl(logo);
            responseList.add(tempUserInfo);
        }
    }
}
