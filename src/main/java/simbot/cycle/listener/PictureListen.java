package simbot.cycle.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.ID;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.Event;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.apirequest.pixiv.PixivIllustGet;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.entity.pixiv.PixivRankImageInfo;
import simbot.cycle.entity.tracemoe.TracemoeSearchDoc;
import simbot.cycle.entity.tracemoe.TracemoeSearchDocNew;
import simbot.cycle.service.*;
import simbot.cycle.util.BotUtils;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.NumberUtil;
import simbot.cycle.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;


@Component
public class PictureListen {

    private static final Logger logger = LoggerFactory.getLogger(PictureListen.class);
    @Autowired
    private ImageTagService imageTagService;
    @Autowired
    private PixivService pixivService;
    @Autowired
    private ImageService imageService;
    @Autowired
    private ProxyService proxyService;

    @Autowired
    private TracemoeService tracemoeService;

    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessageReceipt receipt = event.getSource().sendBlocking("正在努力检索d(´ω｀*)");
        deleteBlock(receipt,15);
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的涩图(๑＞ڡ＜)☆");
        builder.image(Resource.of(imageTagService.ranDom()));
        MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
        deleteBlock(messageReceipt,15);
    }



    @Listener
    @Filter(value = "来丶涩图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event, @FilterValue("name") String name) {
        if (StringUtils.isNotBlank(name)) {
            try {
                MessageReceipt receipt = event.getSource().sendBlocking("正在努力检索d(´ω｀*)");
                deleteBlock(receipt,15);
                PixivImageInfo pixivIllustByTag = pixivService.getPixivIllustByTag(name,"all");
                MessagesBuilder builder = pixivService.parsePixivImgInfoByApiInfo(pixivIllustByTag);
                MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
                deleteBlock(messageReceipt,15);
            } catch (Exception e) {
                e.printStackTrace();
                MessageReceipt messageReceipt = event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                deleteBlock(messageReceipt,15);
            }
        }
    }

    @Listener
    @Filter(value = "来丶色图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgStRun(GroupMessageEvent event) throws IOException {
        MessageReceipt receipt = event.getSource().sendBlocking("正在努力检索d(´ω｀*),但是可能被拦截哦(￣３￣)a ");
        deleteBlock(receipt,15);
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的色图(๑＞ڡ＜)☆");
        builder.image(Resource.of(imageTagService.ranDomR18()));
        MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
        deleteBlock(messageReceipt,15);
    }

    @Listener
    @Filter(value = "来丶色图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStR18(GroupMessageEvent event, @FilterValue("name") String name) throws IOException {
        if (StringUtils.isNotBlank(name)) {
            try {
                MessageReceipt receipt = event.getSource().sendBlocking("正在努力检索d(´ω｀*),但是可能被拦截哦(￣３￣)a ");
                deleteBlock(receipt,15);
                PixivImageInfo pixivIllustByTag = pixivService.getPixivIllustByR18Tag(name);
                MessagesBuilder builder = pixivService.parsePixivImgInfoByApiInfo(pixivIllustByTag);
                MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
                deleteBlock(messageReceipt,15);
            } catch (Exception e) {
                e.printStackTrace();
                MessageReceipt messageReceipt = event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                deleteBlock(messageReceipt,15);
            }
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
    @ContentTrim
    public void groupMsgStWord(GroupMessageEvent event) {
        try {
            Messages messages = event.getMessageContent().getMessages();
            List<Text> texts = messages.get(Text.Key);
            if (texts.size()==1) {
                Text text = texts.get(0);
                String pid = text.getText();
                if (pid.length()>=9) {
                    //根据pid获取图片列表
                    PixivIllustGet request = new PixivIllustGet(Long.valueOf(pid));
                    request.setProxy(proxyService.getProxy());
                    request.doRequest();
                    PixivImageInfo pixivImageInfo = request.getPixivImageInfo();
                    this.parseImages(pixivImageInfo);
                    Bot bot = event.getAuthor().getBot();
                    MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
                    MessagesBuilder builder = new MessagesBuilder();
                    if (1 < pixivImageInfo.getPageCount()) {
                        builder.text("\n该Pid包含" + pixivImageInfo.getPageCount() + "张图片");
                    }
                    builder.text("\n[P站id] " + pixivImageInfo.getId());
                    builder.text("\n[标题] " + pixivImageInfo.getTitle());
                    builder.text("\n[作者] " + pixivImageInfo.getUserName());
                    builder.text("\n[作者id] " + pixivImageInfo.getUserId());
                    builder.text("\n[上传时间] " + pixivImageInfo.getCreateDate());
                    chain.add(bot, builder.build());
                    for (String url : pixivImageInfo.getLocalImgPathList()) {
                        MessagesBuilder messagesBuilder = new MessagesBuilder();
                        File file = new File(url);
                        messagesBuilder.image(Resource.of(file));
                        chain.add(bot, messagesBuilder.build());
                    }
                    MessageReceipt messageReceipt = event.getSource().sendBlocking(chain.build());
                    deleteBlock(messageReceipt,15);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

    }


    @Listener
    @Filter(value = "色图过滤{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void groupMsgColation(GroupMessageEvent event, @FilterValue("name") String name) {
        String text = "命令执行失败惹|*´Å`)ﾉ ";
        switch (name){
            case "100":CycleUtils.setCollectionFlag(1);text="收藏数量100٩(๑>◡<๑)۶ ";break;
            case "500":CycleUtils.setCollectionFlag(2);text="收藏数量500٩(๑>◡<๑)۶ ";break;
            case "1000":CycleUtils.setCollectionFlag(3);text="收藏数量1000٩(๑>◡<๑)۶ ";break;
            case "5000":CycleUtils.setCollectionFlag(4);text="收藏数量5000٩(๑>◡<๑)۶ ";break;
            case "10000":CycleUtils.setCollectionFlag(5);text="收藏数量10000٩(๑>◡<๑)۶ ";break;
            case "20000":CycleUtils.setCollectionFlag(6);text="收藏数量20000٩(๑>◡<๑)۶ ";break;
            case "30000":CycleUtils.setCollectionFlag(7);text="收藏数量30000٩(๑>◡<๑)۶ ";break;
            case "50000":CycleUtils.setCollectionFlag(8);text="收藏数量50000٩(๑>◡<๑)۶ ";break;
            case "100000":CycleUtils.setCollectionFlag(9);text="收藏数量100000٩(๑>◡<๑)۶ ";break;
            default:
                break;
        }
        event.getSource().sendBlocking(text);

    }


    @Listener
    @Filter(value = "涩图搜索", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void searchImage(ContinuousSessionContext sessionContext, GroupMessageEvent event){
        MessageReceipt messageReceipt = event.getSource().sendBlocking("请于30s内请发送一张图片");
        deleteBlock(messageReceipt,15);
        GroupMessageEvent next = sessionContext.next(event, GroupMessageEvent.Key);
        Messages messages = next.getMessageContent().getMessages();
        List<Image<?>> images = messages.get(Image.Key);
        if (images.isEmpty()) {
            MessageReceipt receipt = event.getSource().sendBlocking("没有找到图片信息，请重新触发此命令");
            deleteBlock(receipt,15);
        }else {
            MessageReceipt receipts = event.getSource().sendBlocking("正在检索中，请稍候");
            deleteBlock(receipts,15);
            for (Image<?> image : images) {
                MessagesBuilder messagesBuilder = imageService.searchImgByImgUrl(image.getResource().getName(), null, null);
                messagesBuilder.at(event.getAuthor().getId());
                MessageReceipt receipt = event.getSource().sendBlocking(messagesBuilder.build());
                deleteBlock(receipt,15);
            }
        }
    }


    @Listener
    @Filter(value = "番剧搜索", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void searchFan(ContinuousSessionContext sessionContext, GroupMessageEvent event){
        MessageReceipt messageReceipt = event.getSource().sendBlocking("请于30s内请发送一张图片");
        deleteBlock(messageReceipt,15);
        GroupMessageEvent next = sessionContext.next(event, GroupMessageEvent.Key);
        Messages messages = next.getMessageContent().getMessages();
        List<Image<?>> images = messages.get(Image.Key);
        if (images.isEmpty()) {
            MessageReceipt receipt = event.getSource().sendBlocking("没有找到图片信息，请重新触发此命令");
            deleteBlock(receipt,15);
        }else {
            MessageReceipt receipt = event.getSource().sendBlocking("正在检索中，请稍候");
            deleteBlock(receipt,15);
            for (Image<?> image : images) {
                MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
                Bot bot = event.getAuthor().getBot();
                List<TracemoeSearchDocNew> list = tracemoeService.searchAnimeFromTracemoe(image.getResource().getName());
                for (TracemoeSearchDocNew tracemoeSearchDocNew : list) {
                    MessagesBuilder builder = tracemoeService.parseResultMsg(tracemoeSearchDocNew);
                    chain.add(bot,builder.build());
                }
            }
        }
    }

    private void parseImages(PixivImageInfo imageInfo) throws IOException {
        Long pixivId = NumberUtil.toLong(imageInfo.getId());
        List<String> strings = pixivService.downloadPixivImgsAll(pixivId);
        imageInfo.setLocalImgPathList(strings);
    }


    /**
     * 撤回消息
     * @param messageReceipt
     * @param time
     */
    private void deleteBlock(MessageReceipt messageReceipt,Integer time){
        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(time);
                messageReceipt.deleteBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });
    }
}
