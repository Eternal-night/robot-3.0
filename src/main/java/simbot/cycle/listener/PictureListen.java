package simbot.cycle.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.apirequest.pixiv.PixivIllustGet;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.entity.pixiv.PixivRankImageInfo;
import simbot.cycle.service.*;
import simbot.cycle.util.BotUtils;
import simbot.cycle.util.NumberUtil;
import simbot.cycle.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Component
public class PictureListen {

    @Autowired
    private ImageTagService imageTagService;
    @Autowired
    private PixivService pixivService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProxyService proxyService;


    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new File(imageTagService.ranDom())));
        event.getSource().sendBlocking(builder.build());
    }

    @Listener
    @Filter(value = "来丶色图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStR18(GroupMessageEvent event,@FilterValue("name") String name) throws IOException {
        if (StringUtils.isNotBlank(name)) {
            event.getSource().sendBlocking("正在努力检索d(´ω｀*)");
            MessagesBuilder builder = new MessagesBuilder();
            String url = imageTagService.ranDomR18(name);
            File file = new File(url);
            builder.text("您点的色图(๑＞ڡ＜)☆\n").image(Resource.of(file));
            MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
            CompletableFuture.runAsync(() -> {
                try {
                    TimeUnit.SECONDS.sleep(30);
                    messageReceipt.deleteBlocking();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            });
        }
    }

    @Listener
    @Filter(value = "涩图排行", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgRank(GroupMessageEvent event) throws IOException {
        event.getSource().sendBlocking("正在统计排行榜( • ̀ω•́ )✧");
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
                event.getSource().sendBlocking("正在努力检索d(´ω｀*)");
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
            event.getSource().sendBlocking("正在检索ヽ(￣▽￣)ﾉ");
            Member author = event.getAuthor();
            //根据pid获取图片列表
            PixivIllustGet request = new PixivIllustGet(name);
            request.setProxy(proxyService.getProxy());
            request.doRequest();
            PixivImageInfo pixivImageInfo = request.getPixivImageInfo();
            this.parseImages(pixivImageInfo);
            MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
            MessagesBuilder builder = new MessagesBuilder();
            if (1 < pixivImageInfo.getPageCount()) {
                builder.text("\n该Pid包含"+pixivImageInfo.getPageCount()+"张图片");
            }
            builder.text("\n[P站id] "+pixivImageInfo.getId());
            builder.text("\n[标题] "+pixivImageInfo.getTitle());
            builder.text("\n[作者] "+pixivImageInfo.getUserName());
            builder.text("\n[作者id] "+pixivImageInfo.getUserId());
            builder.text("\n[上传时间] "+pixivImageInfo.getCreateDate());
            chain.add(author,builder.build());

            for (String url : pixivImageInfo.getLocalImgPathList()) {
                MessagesBuilder messagesBuilder = new MessagesBuilder();
               // String imagePath = imageService.scaleForceByLocalImagePath(url);
                File file = new File(url);
                messagesBuilder.image(Resource.of(file));
                chain.add(author,messagesBuilder.build());
            }
            event.getSource().sendBlocking(chain.build());
        } catch (Exception e) {
            e.printStackTrace();
            event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
        }

    }



    @Listener
    @Filter(value = "涩图搜索", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void searchImage(ContinuousSessionContext sessionContext, GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.at(event.getAuthor().getId()).text("请于30s内请发送一张图片");
        event.getSource().sendBlocking(builder.build());
        MessageContent waiting = BotUtils.waiting(sessionContext, event);
        Messages messages = waiting.getMessages();
        Image image = (Image) messages.get(0);
        MessagesBuilder messagesBuilder = imageService.searchImgByImgUrl(image.getResource().getName(), null, null);
        messagesBuilder.at(event.getAuthor().getId());
        event.getSource().sendBlocking(messagesBuilder.build());
    }



    private void parseImages(PixivImageInfo imageInfo) throws IOException {
        Long pixivId = NumberUtil.toLong(imageInfo.getId());
        List<String> strings = pixivService.downloadPixivImgsAll(pixivId);
        imageInfo.setLocalImgPathList(strings);
    }


}
