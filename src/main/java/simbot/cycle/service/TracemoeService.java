package simbot.cycle.service;

import com.alibaba.fastjson.JSONObject;
import love.forte.simbot.message.MessagesBuilder;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import simbot.cycle.apirequest.tracemoe.TracemoeSearch;
import simbot.cycle.constant.ConstantAnime;
import simbot.cycle.constant.ConstantImage;
import simbot.cycle.entity.tracemoe.TracemoeSearchDoc;
import simbot.cycle.entity.tracemoe.TracemoeSearchDocNew;
import simbot.cycle.entity.tracemoe.TracemoeSearchResult;
import simbot.cycle.entity.tracemoe.TracemoeSearchResultNew;
import simbot.cycle.util.EncodingUtil;
import simbot.cycle.util.ImageUtil;

import java.io.IOException;
import java.util.List;

/**
 * create by MikuLink on 2020/2/21 14:18
 * for the Reisen
 * 以图搜番服务
 */
@Service
public class TracemoeService {
    private static final Logger logger = LoggerFactory.getLogger(TracemoeService.class);

    //预览图请求链接https://media.trace.moe/image/${anilist_id}/${encodeURIComponent(filename)}?t=${at}&token=${tokenthumb}
    private static final String URL = "https://media.trace.moe/image/%s/%s?t=%s&token=%s&size=l";

    /**
     * 以图搜番，用trace.moe
     *
     * @param imgUrl 网络图片链接
     * @return 返回消息
     */
    public List<TracemoeSearchDocNew> searchAnimeFromTracemoe(String imgUrl) {
        try {
            TracemoeSearch request = new TracemoeSearch();
            request.setImgUrl(imgUrl);
            request.doRequest();
            TracemoeSearchResultNew result = request.getEntity();

            List<TracemoeSearchDocNew> docList = result.getResult();
            if (null == docList || docList.size() <= 0) {
                return null;
            }

            return docList;
        } catch (IOException ioEx) {
            logger.error(ConstantAnime.TRACE_MOE_API_REQUEST_ERROR, ioEx);
            return null;
        }
    }

    /**
     * 下载预览图
     * 需要搜番结果里的很多参数
     *
     * @param doc 搜番结果
     * @return 下载后的本地图片资源路径
     */
    public String imagePreviewDownload(TracemoeSearchDoc doc) {
        if (null == doc) {
            return null;
        }
        try {
            //url加入参数
            //编码fileName
            String fileName_encodeURIComponent = EncodingUtil.encodeURIComponent(doc.getFilename());
            String requestUrl = String.format(URL, doc.getAnilist_id(), fileName_encodeURIComponent, doc.getAt(), doc.getTokenthumb());
            //下载预览图
            //本地文件名称，由视频数据id和时间轴坐标组成，格式固定jpg吧
            String localImageName = String.format("%s%s.jpg", doc.getAnilist_id(), doc.getAt());
            return ImageUtil.downloadImage(requestUrl, ConstantImage.IMAGE_TRACEMOE_SAVE_PATH, localImageName);

        } catch (Exception ex) {
            //允许业务忽略异常继续执行业务
            logger.error("Api Request TracemoeService imagePreviewDownload,doc:{}", JSONObject.toJSONString(doc), ex);
            return null;
        }
    }

    public MessagesBuilder parseResultMsg(TracemoeSearchDocNew doc) {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("[相似度] "+(doc.getSimilarity() * 100)+"%")
                .text("\n[匹配的 Anilist ID] "+doc.getAnilist())
                .text("\n[文件名] "+doc.getFilename())
                .text("\n[集数] "+doc.getEpisode())
                .text("\n[图片位置] "+doc.getFrom()+"秒")
                .text("\n[图片预览地址] "+doc.getImage())
                .text("\n[视频预览地址] "+doc.getVideo());
        return builder;
    }
}
