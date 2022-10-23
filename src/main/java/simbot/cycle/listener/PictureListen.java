package simbot.cycle.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.ReceivedMessageContent;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import love.forte.simbot.resources.URLResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.service.ImageService;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;



@Component
public class PictureListen {

    @Autowired
    private ImageService imageService;


    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(imageService.ranDom()));
        event.getSource().sendBlocking(builder.build());
    }

    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event) throws IOException {
        Integer FLAG = CycleUtils.getFLAG();
        ReceivedMessageContent messageContent = event.getMessageContent();
        String plainText = messageContent.getPlainText();
        String name = plainText.replace("来丶涩图", "");
        if (StringUtils.isNotBlank(name)) {
            MessagesBuilder builder = new MessagesBuilder();
            if (FLAG ==1) {
                String imageUrl = imageService.getDuitangUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
                    event.getSource().sendBlocking(builder.build());
                }
            } else if (FLAG ==2) {
                String imageUrl = imageService.getImageUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
                    event.getSource().sendBlocking(builder.build());
                }
            } else if (FLAG ==3) {
                InputStream imageUrl = imageService.getImageInputStream(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(imageUrl));
                    event.getSource().sendBlocking(builder.build());
                }
            }

        }
    }

}
