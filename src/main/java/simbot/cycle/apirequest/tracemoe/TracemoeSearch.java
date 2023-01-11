package simbot.cycle.apirequest.tracemoe;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;
import net.dreamlu.mica.core.utils.StringUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import simbot.cycle.apirequest.BaseRequest;
import simbot.cycle.entity.tracemoe.TracemoeSearchResult;
import simbot.cycle.entity.tracemoe.TracemoeSearchResultNew;
import simbot.cycle.util.HttpUtil;
import simbot.cycle.util.HttpsUtil;

import java.io.IOException;

/**
 * create by MikuLink on 2020/2/19 12:38
 * for the Reisen
 * 以图搜番
 * 官网https://trace.moe/
 * 官方文档 https://soruly.github.io/trace.moe/#/
 */
@Getter
@Setter
public class TracemoeSearch extends BaseRequest {
    private static final Logger logger = LoggerFactory.getLogger(TracemoeSearch.class);
    //private static final String URL = "https://trace.moe/api/search";
    private static final String URL = "https://api.trace.moe/search";

    /**
     * 图片链接
     */
    private String imgUrl;

    //执行接口请求
    public void doRequest() throws IOException {
        //拼装参数
        String s = URL + "?cutBorders&url=" + imgUrl;
        //请求
        byte[] resultBytes = HttpsUtil.doGet(s);
        body = new String(resultBytes);

        //记录接口请求与返回日志
        logger.info(String.format("Api Request TracemoeSearch,param:%s,resultBody:%s", JSONObject.toJSONString(param), body));
    }

    //拼装参数
    private void addParam() {
        param.put("url", imgUrl);
    }

    //获取解析后的结果对象
    public TracemoeSearchResultNew getEntity() {
        if (StringUtil.isEmpty(body)) {
            return null;
        }
        return JSONObject.parseObject(body, TracemoeSearchResultNew.class);
    }
}
