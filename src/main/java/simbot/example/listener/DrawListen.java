package simbot.example.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessageReceipt;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import love.forte.simbot.resources.URLResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.example.service.DrawService;
import simbot.example.service.TagsService;
import simbot.example.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Arrays;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DrawListen {

    private static Integer FALG = 2;

    private static Boolean TRANSLATE = true;
    @Autowired
    private DrawService drawService;
    @Autowired
    private TagsService tagsService;

    @Listener
    @Filter(value = "#咒文{{tags}}", matchType = MatchType.REGEX_CONTAINS)
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event, @FilterValue("tags") String tags) throws IOException, InterruptedException {

        if (FALG == 1) {
            event.getSource().sendBlocking("不要打断我咏唱咒文啊魂淡(╬￣皿￣)=○");
        } else if (FALG == 2) {
            FALG = 1;
            if (TRANSLATE) {
                String replaceAll = tags.replaceAll("，", ",");
                String[] tagArr = replaceAll.split(",");

                for (String tag : tagArr) {
                    String keyWord = tagsService.tags(tag);
                    if (keyWord != null && !keyWord.equals("")) {
                        tag = keyWord;
                    }
                }
                tags = Arrays.toString(tagArr);
            }
            mapping(event, tags);
            FALG = 2;
        }else if (FALG == 3){
            event.getSource().sendBlocking("我在等CD，你在等什么？(▼皿▼#)");
        }
    }

    private void mapping(GroupMessageEvent event, String tags) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(tags)) {
            event.getSource().sendBlocking("咒文咏唱中╮(￣▽￣)╭");
            InputStream inputStream = drawService.drafting(tags);
            MessagesBuilder builder = new MessagesBuilder();
            StandardResource resource = Resource.of(inputStream);
            builder.text("魔法施展成功啦(*>∀<)ﾉ))★ \n").image(resource);
            MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
            FALG = 3;
            Thread.sleep(12000);
            messageReceipt.deleteBlocking();
        }


    }


}
