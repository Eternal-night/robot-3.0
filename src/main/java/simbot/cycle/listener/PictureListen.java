package simbot.cycle.listener;

import cn.hutool.core.io.resource.ClassPathResource;
import com.alibaba.fastjson2.JSONObject;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.definition.Group;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.ResourceImage;
import love.forte.simbot.resources.PathResource;
import love.forte.simbot.resources.Resource;
import net.dreamlu.mica.core.utils.StringUtil;
import net.mamoe.mirai.internal.deps.io.ktor.http.Url;
import net.mamoe.mirai.message.data.ForwardMessage;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.entity.pixiv.PixivRankImageInfo;
import simbot.cycle.exceptions.CycleException;
import simbot.cycle.service.ImageTagService;
import simbot.cycle.service.PixivService;
import simbot.cycle.service.RabbitBotService;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.NumberUtil;
import simbot.cycle.util.StringUtils;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.List;
import java.util.Random;


@Component
public class PictureListen {

    @Autowired
    private ImageTagService imageTagService;

    @Autowired
    private PixivService pixivService;
    @Autowired
    private RabbitBotService rabbitBotService;


    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(imageTagService.ranDom()));
        event.getSource().sendBlocking(builder.build());
    }

    @Listener
    @Filter(value = "涩图排行", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgRank(GroupMessageEvent event) throws IOException {
        MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
        Bot bot = event.getBot();
        List<PixivRankImageInfo> pixivIllustRank = pixivService.getPixivIllustRank(10);

        for (PixivRankImageInfo pixivRankImageInfo : pixivIllustRank) {
            MessagesBuilder messagesBuilder = pixivService.parsePixivImgInfoByApiInfo(pixivRankImageInfo);
            chain.add(bot, messagesBuilder.build());
        }
        event.getSource().sendBlocking(chain.build());
    }

    @Listener
    @Filter(value = "来丶涩图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event, @FilterValue("name") String name) {
        if (StringUtils.isNotBlank(name)) {
            try {
                PixivImageInfo pixivIllustByTag = pixivService.getPixivIllustByTag(name);
                MessagesBuilder builder = pixivService.parsePixivImgInfoByApiInfo(pixivIllustByTag);
                event.getSource().sendBlocking(builder.build());
            } catch (Exception e) {
                e.printStackTrace();
                event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
            }
        }
    }


    @Listener
    @Filter(value = "涩图id{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void groupMsgStWord(GroupMessageEvent event, @FilterValue("name") Long name) {
        try {
            PixivImageInfo imageInfo = pixivService.getPixivImgInfoById(name);
            this.parseImages(imageInfo);
            List<Resource> miraiImageList = rabbitBotService.uploadMiraiImage(imageInfo.getLocalImgPathList());
            MessagesBuilder builder = rabbitBotService.parseMsgChainByImgs(miraiImageList);
            StringBuilder resultStr = new StringBuilder();
            if (1 < imageInfo.getPageCount()) {
                resultStr.append("\n该Pid包含").append(imageInfo.getPageCount()).append("张图片");
            }
            resultStr.append("\n[P站id] ").append(imageInfo.getId());
            resultStr.append("\n[标题] ").append(imageInfo.getTitle());
            resultStr.append("\n[作者] ").append(imageInfo.getUserName());
            resultStr.append("\n[作者id] ").append(imageInfo.getUserId());
            resultStr.append("\n[上传时间] ").append(imageInfo.getCreateDate());
            builder = builder.text(resultStr.toString());
            event.getSource().sendBlocking(builder.build());
        } catch (Exception e) {
            e.printStackTrace();
            event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
        }

    }


    private void parseImages(PixivImageInfo imageInfo) throws IOException {
        Long pixivId = NumberUtil.toLong(imageInfo.getId());
        List<String> strings = pixivService.downloadPixivImgsAll(pixivId);
        imageInfo.setLocalImgPathList(strings);
    }


}
