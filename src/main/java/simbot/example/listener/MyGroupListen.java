package simbot.example.listener;


import lombok.extern.slf4j.Slf4j;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.annotation.TargetFilter;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.Bot;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.ReceivedMessageContent;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import love.forte.simbot.resources.URLResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.example.entity.YuGiOh;
import simbot.example.entity.YuGiOhDetails;
import simbot.example.service.ImageService;
import simbot.example.service.YuGiOhService;
import simbot.example.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
@Slf4j
public class MyGroupListen {

    private static final Map<String, String> MAP = new ConcurrentHashMap<>();

    @Autowired
    private YuGiOhService yuGiOhService;
    @Autowired
    private ImageService imageService;

    @Listener
    @Filter(value = "ygo", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgYgo(GroupMessageEvent event) throws IOException {

        String massageText = event.getMessageContent().getPlainText();

        String[] split = massageText.split("ygo");

        String name = split[split.length - 1];

        String trimName = name.trim();

        if (!trimName.isEmpty()) {

            MessagesBuilder builder = new MessagesBuilder();

            Bot bot = event.getBot();

            List<YuGiOh> yuGiOhList = yuGiOhService.YuGiOhInquire(trimName);

            if (!yuGiOhList.isEmpty()) {
                int count = 0;
                for (YuGiOh yuGiOh : yuGiOhList) {
                    if (count >= 3) {
                        break;
                    }
                    String imageUrl = yuGiOh.getImageUrl();
                    List<String> nameList = yuGiOh.getNameList();
                    YuGiOhDetails details = yuGiOh.getDetails();
                    String trim1 = details.getInformation().trim();
                    String trim2 = details.getSwingingEffect().trim();
                    URLResource urlResource = new URLResource(new URL(imageUrl), "card");
                    builder.image(bot, urlResource)
                            .text("中文名:" + nameList.get(0) + "\n")
                            .text("日文名:" + nameList.get(1) + "\n")
                            .text("英文名:" + nameList.get(2) + "\n")
                            .text(trim1 + "\n")
                            .text(trim2 + "\n");
                    if (details.getMonsterEffect() != null) {
                        String trim3 = details.getMonsterEffect().trim();
                        builder.text(trim3 + "\n");
                    }
                    count++;
                }
                Messages build = builder.build();
                event.getSource().sendBlocking(build);
            } else {
                event.getSource().sendBlocking("~没有查询到呢~ QAQ");
            }
        }

    }

    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        Bot bot = event.getBot();
        InputStream imageUrl = imageService.ranDom();
        StandardResource resource = Resource.of(imageUrl);
        builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(bot, resource);
        event.getSource().sendBlocking(builder.build());
    }

    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event) throws IOException {
        ReceivedMessageContent messageContent = event.getMessageContent();
        String plainText = messageContent.getPlainText();
        String name = plainText.replace("来丶涩图", "");
        if (StringUtils.isNotBlank(name)) {

            String flag = MAP.get("flag");

            if (flag == null) {
                MAP.put("flag", "1");
                flag = "1";
            }
            MessagesBuilder builder = new MessagesBuilder();
            Bot bot = event.getBot();

            if ("1".equals(flag)) {
                String imageUrl = imageService.getDuitangUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    URLResource urlResource = new URLResource(new URL(imageUrl), "picture");
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(bot, urlResource);
                    event.getSource().sendBlocking(builder.build());
                }
            } else if ("2".equals(flag)) {
                String imageUrl = imageService.getImageUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    URLResource urlResource = new URLResource(new URL(imageUrl), "picture");
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(bot, urlResource);
                    event.getSource().sendBlocking(builder.build());
                }
            } else if ("3".equals(flag)) {
                InputStream imageUrl = imageService.getImageInputStream(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    StandardResource resource = Resource.of(imageUrl);
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(bot, resource);
                    event.getSource().sendBlocking(builder.build());
                }
            }

        }
    }

    @Listener
    @Filter(value = "刻刻帝 十一之弹", matchType = MatchType.TEXT_EQUALS, target =
    @TargetFilter(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminEleven(GroupMessageEvent event) throws IOException {
        if ("1".equals(MAP.get("flag"))) {
            MAP.put("flag", "2");
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了❥(ゝω・✿ฺ)");
        } else if ("2".equals(MAP.get("flag"))) {
            MAP.put("flag", "3");
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(๑＞ڡ＜)✿ ");
        } else if ("3".equals(MAP.get("flag"))) {
            MAP.put("flag", "1");
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(*ﾉω・*)ﾃﾍ");
        }
    }

}
