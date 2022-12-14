package simbot.cycle.service;

import com.alibaba.fastjson2.JSONObject;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.component.mirai.message.MiraiSendOnlyForwardMessage;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.resources.Resource;
import net.dreamlu.mica.core.utils.StringUtil;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simbot.cycle.apirequest.pixiv.*;
import simbot.cycle.constant.ConstantCommon;
import simbot.cycle.constant.ConstantImage;
import simbot.cycle.constant.ConstantPixiv;
import simbot.cycle.entity.ReString;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.entity.pixiv.PixivImageUrlInfo;
import simbot.cycle.entity.pixiv.PixivRankImageInfo;
import simbot.cycle.entity.pixiv.PixivUserInfo;
import simbot.cycle.exceptions.CycleApiException;
import simbot.cycle.exceptions.CycleException;
import simbot.cycle.util.*;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.net.Proxy;
import java.net.SocketTimeoutException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;


@Service
public class PixivService {
    private static final Logger logger = LoggerFactory.getLogger(PixivService.class);

    //pixiv曲奇 不登录的话，功能有所限制
    @Value("${pixiv.cookie:0}")
    private String pixivCookie;

    @Autowired
    private ImageService imageService;
    @Autowired
    private RabbitBotService rabbitBotService;
    @Autowired
    private SwitchService switchService;
    @Autowired
    private ProxyService proxyService;

    /**
     * * 查询p站图片id并返回结果
     *
     * @param pid p站图片id
     * @return 拼装好的结果信息
     * @throws IOException 异常继续上抛，调用端处理
     */
    public PixivImageInfo getPixivImgInfoById(Long pid) throws IOException {
        if (null == pid) {
            return null;
        }
        //根据pid获取图片列表
        PixivIllustGet request = new PixivIllustGet(pid);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        return request.getPixivImageInfo();
    }

    /**
     * 根据标签，搜索出一张图片
     * 会从所有结果中随机出一张
     * 根据图片分数会有不同的随机权重
     *
     * @param tag 标签 参数在上一层过滤好再进来
     * @return 结果对象
     */
    public PixivImageInfo getPixivIllustByTag(String tag, String param) throws CycleException, IOException {
        //1.查询这个tag下的总结果
        PixivIllustTagGet request = new PixivIllustTagGet();
        request.setWord(tag);
        request.setP(1);
        request.setMode(param);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        //总结果数量
        int total = request.getTotal();
        if (0 >= total) {
            throw new CycleException(ConstantPixiv.PIXIV_IMAGE_TAG_NO_RESULT);
        }

        //2.随机获取结果中的一条
        //先按照指定页数算出有多少页，随机其中一页 (模拟页面，每页默认60条数据)
        int totalPage = NumberUtil.toIntUp(total / 60 * 1.0);
        //最多只能获取到第1000页
        if (totalPage > 1000) {
            totalPage = 1000;
        }
        //随机一个页数
        int randomPage = RandomUtil.roll(totalPage);
        if (0 >= randomPage) {
            randomPage = 1;
        }


        //获取该页数的数据
        request = new PixivIllustTagGet();
        request.setWord(tag);
        request.setMode(param);
        request.setP(randomPage);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        List<PixivImageInfo> responses = request.parseImageList();

        //todo 页面上没有作品评分，如果真做就需要去获取每个pid的评分，这一页就是60个pid，那就是近乎瞬间60次页面请求，也需要保存60个Obj
        //累积得分
//        Integer scoredCount = 0;
//        Map<Long, Integer> scoredMap = new HashMap<>();
//        Map<Object, Double> additionMap = new HashMap<>();
//        Map<Long, ImjadPixivResponse> imgRspMap = new HashMap<>();
//
//        for (PixivImageInfo response : responses) {
//            //r18过滤
//            if (1 == response.getXRestrict()) {
//                String configR18 = ConstantConfig.common_config.get(ConstantConfig.CONFIG_R18);
//                if (StringUtil.isBlank(configR18) || ConstantCommon.OFF.equalsIgnoreCase(configR18)) {
//                    continue;
//                }
//            }
//
//            Integer scored = response.getStats().getScored_count();
//            scoredCount += scored;
//            scoredMap.put(response.getId(), scored);
//            imgRspMap.put(response.getId(), response);
//        }
//        if (0 >= scoredMap.size()) {
//            return new ReString(false, ConstantPixiv.PIXIV_IMAGE_TAG_ALL_R18);
//        }
//
//        //计算权重
//        for (Long pixivId : scoredMap.keySet()) {
//            Integer score = scoredMap.get(pixivId);
//            //结果肯定介于0-1之间，然后换算成百分比，截取两位小数
//            Double addition = NumberUtil.keepDecimalPoint((score * 1.0) / scoredCount * 100.0, 2);
//            additionMap.put(pixivId, addition);
//        }
//
//        //根据权重随机出一个元素
//        Object obj = RandomUtil.rollObjByAddition(additionMap);

        PixivImageInfo pixivImageInfo = RandomUtil.rollObjFromList(responses);

        //3.获取该图片信息
        Long pixivId = NumberUtil.toLong(pixivImageInfo.getId());
        return getPixivImgInfoById(pixivId);
    }


    /**
     * 查询R18标签
     *
     * @param tag
     * @return
     * @throws CycleException
     * @throws IOException
     */
    public PixivImageInfo getPixivIllustByR18Tag(String tag) throws CycleException, IOException {

        tag = tag + CycleUtils.getCOLLECTION();

        //1.查询这个tag下的总结果
        PixivIllustR18TagGet request = new PixivIllustR18TagGet();
        request.setWord(tag);
        request.setP(1);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        //总结果数量
        int total = request.getTotal();
        if (0 >= total) {
            throw new CycleException(ConstantPixiv.PIXIV_IMAGE_TAG_NO_RESULT);
        }

        //2.随机获取结果中的一条
        //先按照指定页数算出有多少页，随机其中一页 (模拟页面，每页默认60条数据)
        int totalPage = NumberUtil.toIntUp(total / 60 * 1.0);
        //最多只能获取到第1000页
        if (totalPage > 1000) {
            totalPage = 1000;
        }
        //随机一个页数
        int randomPage = RandomUtil.roll(totalPage);
        if (0 >= randomPage) {
            randomPage = 1;
        }


        //获取该页数的数据
        request = new PixivIllustR18TagGet();
        request.setWord(tag);
        request.setP(randomPage);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        List<PixivImageInfo> responses = request.parseImageList();

        PixivImageInfo pixivImageInfo = RandomUtil.rollObjFromList(responses);

        //3.获取该图片信息
        Long pixivId = NumberUtil.toLong(pixivImageInfo.getId());
        return getPixivImgInfoById(pixivId);
    }

    /**
     * 获取当前P站日榜数据
     *
     * @param pageSize 获取个数，传个5个10个的差不多了，一次大概最多50个
     * @return 日榜图片信息
     */
    public List<PixivRankImageInfo> getPixivIllustRank(int pageSize) throws IOException {
        //获取排行榜信息
        PixivIllustRankGet request = new PixivIllustRankGet();
        request.setMode("daily");
        request.setContent("illust");
        request.setPageSize(pageSize);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        List<PixivRankImageInfo> rankImageList = request.getResponseList();

        logger.info("PixivService getPixivIllustRank list:{}", JSONObject.toJSONString(rankImageList));

        //根据pid，去单独爬图片的信息
        for (PixivRankImageInfo rankImageInfo : rankImageList) {
            //根据pid获取图片信息
            PixivImageInfo pixivImageInfo = getPixivImgInfoById(rankImageInfo.getPid());

            //下载图片到本地
            try {

                PixivImageUrlInfo urls = pixivImageInfo.getUrls();

                if (urls != null) {
                    if (urls.getOriginal() != null) {
                        parseImages(pixivImageInfo);
                    }
                }

            } catch (SocketTimeoutException stockTimeoutEx) {
                logger.warn("PixivService getPixivIllustRank {}", ConstantPixiv.PIXIV_IMAGE_TIMEOUT + stockTimeoutEx.toString(), stockTimeoutEx);
            }

            //信息合并
            //图片
            rankImageInfo.setLocalImagesPath(pixivImageInfo.getLocalImgPathList());
            //标题
            rankImageInfo.setTitle(pixivImageInfo.getTitle());
            //简介
            rankImageInfo.setCaption(pixivImageInfo.getDescription());
            //创建时间
            rankImageInfo.setCreatedTime(pixivImageInfo.getCreateDate());
            //总P数
            rankImageInfo.setPageCount(pixivImageInfo.getPageCount());
            //作者id
            rankImageInfo.setUserId(pixivImageInfo.getUserId());
            //作者名称
            rankImageInfo.setUserName(pixivImageInfo.getUserName());
        }

        return rankImageList;
    }

    /**
     * 接口返回的图片信息拼装为群消息
     *
     * @param imageInfo 接口返回对象
     * @return 群消息
     * @throws IOException api异常
     */
    public MessagesBuilder parsePixivImgInfoByApiInfo(PixivImageInfo imageInfo) throws IOException {
        return parsePixivImgInfoByApiInfo(imageInfo, null);
    }

    /**
     * 接口返回的图片信息拼装为群消息
     * 重载 为识图结果提供相似度参数
     *
     * @param imageInfo  接口返回对象
     * @param similarity 相似度
     * @return 群消息
     * @throws IOException api异常
     */
    public MessagesBuilder parsePixivImgInfoByApiInfo(PixivImageInfo imageInfo, String similarity) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();

        //r18过滤
        boolean showImage = true;
//        Integer xRestrict = imageInfo.getXRestrict();
//        if (null != xRestrict && 1 == xRestrict) {
//            ReString reStringSwitch = switchService.switchCheck(imageInfo.getSender(), imageInfo.getSubject(), "pixivR18");
//            if (!reStringSwitch.isSuccess()) {
//                builder = builder.text(ConstantPixiv.PIXIV_IMAGE_R18);
//                showImage = false;
//            }
//        }

        //展示图片
        if (showImage) {
            parseImages(imageInfo);
            List<Resource> miraiImageList = rabbitBotService.uploadMiraiImage(imageInfo.getLocalImgPathList());
            builder = rabbitBotService.parseMsgChainByImgs(miraiImageList);
        }
        if (1 < imageInfo.getPageCount()) {
            builder.text("该Pid包含"+imageInfo.getPageCount()+"张图片\n");
        }
        if (StringUtil.isNotBlank(similarity)) {
            builder.text("[相似度]"+similarity+"%\n");
        }
        builder.text("[P站id] "+imageInfo.getId()+"\n");
        builder.text("[标题] "+imageInfo.getTitle()+"\n");
        builder.text("[作者] "+imageInfo.getUserName()+"\n");
        builder.text("[作者id] "+imageInfo.getUserId()+"\n");
        builder.text("[上传时间] "+imageInfo.getCreateDate()+"\n");
        return builder;
    }

    /**
     * @description: 将图片信息组装为群消息链
     * @author: 陈杰
     * @date: 2022/12/29 9:06
     * @param: imageInfo
     * @param: bot
     * @return: love.forte.simbot.component.mirai.message.MiraiSendOnlyForwardMessage
     **/
    public MiraiSendOnlyForwardMessage parsePixivImgInfo(PixivImageInfo imageInfo, Bot bot) throws IOException {
        MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
        parseImages(imageInfo);
        List<Resource> miraiImageList = rabbitBotService.uploadMiraiImage(imageInfo.getLocalImgPathList());
        for (Resource resource : miraiImageList) {
            MessagesBuilder builder = new MessagesBuilder();
            builder.image(resource);
            chain.add(bot, builder.build());
        }
        return chain.build();
    }

    /**
     * 排行榜图片信息拼装为群消息
     *
     * @param imageInfo 排行榜图片信息
     * @return 群消息
     */
    public MessagesBuilder parsePixivImgInfoByApiInfo(PixivRankImageInfo imageInfo) {
        //日榜正常榜，不用r18过滤
        //展示图片
        List<Resource> miraiImageList = rabbitBotService.uploadMiraiImage(imageInfo.getLocalImagesPath());
        MessagesBuilder builder = rabbitBotService.parseMsgChainByImgs(miraiImageList);

        if (1 < imageInfo.getPageCount()) {
            builder.text("\n该Pid包含"+imageInfo.getPageCount()+"张图片");
        }
        builder.text("[排名] "+imageInfo.getRank()+"\n");
        builder.text("[昨日排名] "+imageInfo.getPreviousRank()+"\n");
        builder.text("[P站id] "+imageInfo.getPid()+"\n");
        builder.text("[标题] "+imageInfo.getTitle()+"\n");
        builder.text("[作者] "+imageInfo.getUserName()+"\n");
        builder.text("[作者id] "+imageInfo.getUserId()+"\n");
        builder.text("[创建时间] "+imageInfo.getCreatedTime()+"\n");
        return builder;
    }

    /**
     * 搜索p站用户
     * 可模糊搜索
     *
     * @param userNick 用户昵称
     */
    public List<PixivUserInfo> pixivUserSearch(String userNick) throws IOException {
        //请求pixiv用户搜索
        PixivUserSearch request = new PixivUserSearch();
        request.getHeader().put("cookie", pixivCookie);
        request.setPixivUserNick(userNick);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        return request.getResponseList();
    }

    /**
     * 随机返回用户投稿的插画
     *
     * @param pixivUserId pixiv用户id
     * @return 插画列表
     */
    public List<PixivImageInfo> getPixivIllustByUserId(String pixivUserId) throws CycleApiException, IOException {
        return getPixivIllustByUserId(pixivUserId, null);
    }

    /**
     * 随机返回用户投稿的插画
     *
     * @param pixivUserId pixiv用户id
     * @param count       返回数量，默认为3
     * @return 插画列表
     */
    public List<PixivImageInfo> getPixivIllustByUserId(String pixivUserId, Integer count) throws CycleApiException, IOException {
        //默认数量为3
        if (null == count) count = 3;
        List<PixivImageInfo> pixivImageInfos = new ArrayList<>();

        //1.获取该用户下所有插画id
        PixivIllustUserGet request = new PixivIllustUserGet();
        request.setUserId(pixivUserId);
        request.getHeader().put("cookie", pixivCookie);
        request.setProxy(proxyService.getProxy());
        request.doRequest();
        List<String> allPid = request.getResponseList();

        //2.随机不重复的指定数量的插画，根据pid获取图片详情
        Map<String, String> tempReMap = new HashMap<>();
        int errCount = 0;
        for (int i = 1; i <= count; ) {
            String tempPid = RandomUtil.rollStrFromList(allPid);
            //判重，用户作品数量过少时，防止陷入死循环
            if (allPid.size() > count && tempReMap.containsKey(tempPid)) {
                continue;
            }
            //获取图片信息
            try {
                pixivImageInfos.add(getPixivImgInfoById(NumberUtil.toLong(tempPid)));
            } catch (Exception ex) {
                //异常跳过，进行下一个，异常次数过多跳出方法，防止死循环
                errCount++;
                logger.error("PixivService getPixivIllustByUserId getPixivImgInfoById error({})", count, ex);
                if (errCount < 5) {
                    continue;
                } else {
                    break;
                }
            }
            tempReMap.put(tempPid, tempPid);
            i++;
        }
        return pixivImageInfos;
    }

    //下载图片到本地
    public void parseImages(PixivImageInfo imageInfo) throws IOException {
        Long pixivId = NumberUtil.toLong(imageInfo.getId());
        List<String> localImagesPathList = new ArrayList<>();
        String original = imageInfo.getUrls().getOriginal();
        if (1 < imageInfo.getPageCount()) {
            //多图 如果因为没登录而获取不到，则只取封面
            try {
                localImagesPathList.addAll(downloadPixivImgs(pixivId));
            } catch (FileNotFoundException fileNotFoundEx) {
                logger.warn("pixiv多图获取失败，可能登录过期,imageInfo:{}", JSONObject.toJSONString(imageInfo), fileNotFoundEx);
                //限制级会要求必须登录，如果不登录会抛出异常
                if (original != null) {
                    localImagesPathList.add(downloadPixivImg(original, pixivId));
                }
            }
        } else {
            if (original != null) {
                //单图
                localImagesPathList.add(downloadPixivImg(original, pixivId));
            }
        }
        imageInfo.setLocalImgPathList(localImagesPathList);
    }

    /**
     * 下载P站图片
     *
     * @param url p站图片链接
     * @return 压缩后的本地图片地址
     */
    public String downloadPixivImg(String url, Long pixivId) throws IOException {

        //先检测是否已下载，如果已下载直接送去压图
        String pixivImgFileName = url.substring(url.lastIndexOf("/") + 1);
        String localPixivFilePath = ConstantImage.DEFAULT_IMAGE_SAVE_PATH + File.separator + "pixiv" + File.separator + pixivImgFileName;
        if (FileUtil.exists(localPixivFilePath)) {
            return imageService.scaleForceByLocalImagePath(localPixivFilePath);
        }

        //是否不加载p站图片，由于从p站本体拉数据，还没代理，很慢
        String pixiv_image_ignore = ConstantCommon.common_config.get(ConstantPixiv.PIXIV_CONFIG_IMAGE_IGNORE);
        if ("1".equalsIgnoreCase(pixiv_image_ignore)) {
            return null;
        }

        String scaleForceLocalUrl = null;
        try {
            String localUrl = downloadPixivImgByPixivImgUrl(url, pixivId);

            if (StringUtil.isNotBlank(localUrl)) {
                scaleForceLocalUrl = imageService.scaleForceByLocalImagePath(localUrl);
            }

            if (StringUtil.isBlank(scaleForceLocalUrl)) {
                //图片下载或压缩失败
                logger.warn(String.format("PixivImjadService downloadPixivImg %s url:%s", ConstantPixiv.PIXIV_IMAGE_DOWNLOAD_FAIL, url));
            }

        } catch (FileNotFoundException fileNotFoundEx) {
            //图片被删了
            logger.warn(String.format("PixivImjadService downloadPixivImg %s pixivId:%s", ConstantPixiv.PIXIV_IMAGE_DELETE, pixivId));
        }
        return scaleForceLocalUrl;
    }

    /**
     * 下载并压缩P站图片(多图)
     *
     * @return 处理后的本地图片地址列表
     */
    public List<String> downloadPixivImgs(Long pixivId) throws IOException {
        List<String> localImagesPath = new ArrayList<>();

        //查看多图展示数量配置，默认为3
        String pixiv_config_images_show_count = ConstantCommon.common_config.get(ConstantPixiv.PIXIV_CONFIG_IMAGES_SHOW_COUNT);
        if (!NumberUtil.isNumberOnly(pixiv_config_images_show_count)) {
            pixiv_config_images_show_count = ConstantPixiv.PIXIV_CONFIG_IMAGES_SHOW_COUNT_DEFAULT;
        }
        Integer showCount = NumberUtil.toInt(pixiv_config_images_show_count);

        //获取多图
        PixivIllustPagesGet request = new PixivIllustPagesGet(pixivId);
        try {
            //如果是登录可见图片，必须附带cookie，不然会抛出404异常
            Map<String, String> header = new HashMap<>();
            header.put("cookie", pixivCookie);
            request.setHeader(header);
            request.setProxy(proxyService.getProxy());
            request.doRequest();
        } catch (CycleApiException rabApiEx) {
            logger.warn("Pixiv downloadPixivImgs apierr msg:{}", rabApiEx.getMessage(), rabApiEx);
            return new ArrayList<>();
        }
        List<PixivImageUrlInfo> urlInfoList = request.getResponseList();

        int i = 0;

        for (PixivImageUrlInfo urlInfo : urlInfoList) {
            //下载并压缩图片
            String scaleForceLocalUrl = downloadPixivImg(urlInfo.getOriginal(), pixivId);
            localImagesPath.add(scaleForceLocalUrl);
            //达到指定数量，结束追加图片
            i++;
            if (i >= showCount) {
                break;
            }
        }
        return localImagesPath;
    }


    /**
     * 下载并压缩P站图片(多图)
     *
     * @return 处理后的本地图片地址列表
     */
    public List<String> downloadPixivImgsAll(Long pixivId) throws IOException {
        List<String> localImagesPath = new ArrayList<>();
        //获取多图
        PixivIllustPagesGet request = new PixivIllustPagesGet(pixivId);
        try {
            //如果是登录可见图片，必须附带cookie，不然会抛出404异常
            Map<String, String> header = new HashMap<>();
            header.put("cookie", pixivCookie);
            request.setHeader(header);
            request.setProxy(proxyService.getProxy());
            request.doRequest();
        } catch (CycleApiException rabApiEx) {
            logger.warn("Pixiv downloadPixivImgs apierr msg:{}", rabApiEx.getMessage(), rabApiEx);
            return new ArrayList<>();
        }
        List<PixivImageUrlInfo> urlInfoList = request.getResponseList();
        for (PixivImageUrlInfo urlInfo : urlInfoList) {
            //下载并压缩图片
            String scaleForceLocalUrl = downloadPixivImg(urlInfo.getOriginal(), pixivId);
            localImagesPath.add(scaleForceLocalUrl);
        }
        return localImagesPath;
    }

    /**
     * 根据p站图片链接下载图片
     * 带图片后缀的那种，比如
     * https://i.pximg.net/img-original/img/2018/03/31/01/10/08/67994735_p0.png
     *
     * @param url     p站图片链接
     * @param pixivId p站图片id，用于防爬链，必须跟url中的id一致
     * @return 下载后的本地连接
     */
    public String downloadPixivImgByPixivImgUrl(String url, Long pixivId) throws IOException {
        logger.info("Pixiv image download:" + url);
        //目前一共遇到的
        //1.似乎是新连接，最近UI改了 https://i.pximg.net/img-original/img/2020/02/17/22/07/00/79561788_p0.jpg
        //Referer: https://www.pixiv.net/artworks/79561788
        //2.没研究出来的链接，还是403，但是把域名替换成正常链接的域名，可以正常获取到数据 https://i-cf.pximg.net/img-original/img/2020/02/17/22/07/00/79561788_p0.jpg
        HashMap<String, String> header = new HashMap<>();
        if (url.contains("i-cf.pximg.net")) {
            url = url.replace("i-cf.pximg.net", "i.pximg.net");
        }
        //加入p站防爬链
        header.put("referer", "https://www.pixiv.net/artworks/" + pixivId);
        // 创建代理
        Proxy proxy = proxyService.getProxy();
        //下载图片
        return ImageUtil.downloadImage(header, url, ConstantImage.DEFAULT_IMAGE_SAVE_PATH + File.separator + "pixiv", null, proxy);
    }
}
