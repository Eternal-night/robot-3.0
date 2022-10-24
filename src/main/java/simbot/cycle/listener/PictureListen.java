package simbot.cycle.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.ReceivedMessageContent;
import love.forte.simbot.resources.Resource;
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
    @Filter(value = "来丶涩图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event, @FilterValue("name") String name) throws IOException {
        Integer IMAGE_FLAG = CycleUtils.getImageFlag();
        if (StringUtils.isNotBlank(name)) {
            MessagesBuilder builder = new MessagesBuilder();
            if (IMAGE_FLAG ==1) {
                String imageUrl = imageService.getDuitangUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
                    event.getSource().sendBlocking(builder.build());
                }
            } else if (IMAGE_FLAG ==2) {
                String imageUrl = imageService.getImageUrl(name);
                if (imageUrl == null) {
                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
                } else {
                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
                    event.getSource().sendBlocking(builder.build());
                }
            } else if (IMAGE_FLAG ==3) {
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
