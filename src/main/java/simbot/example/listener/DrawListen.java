package simbot.example.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.definition.Member;
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

    private static Boolean TRANSLATE = false;
    @Autowired
    private DrawService drawService;
    @Autowired
    private TagsService tagsService;

    @Listener
    @Filter(value = "#咒文", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgMapping(GroupMessageEvent event) throws IOException, InterruptedException {

        if (FALG == 1) {
            event.getSource().sendBlocking("不要打断我咏唱咒文啊魂淡(╬￣皿￣)=○");
        } else if (FALG == 2) {
            FALG = 1;

            String massageText = event.getMessageContent().getPlainText();
            String tags = massageText.replaceAll("#咒文", "");

            if (TRANSLATE) {
                String replaceAll = tags.replaceAll("，", ",");
                String[] tagArr = replaceAll.split(",");

                StringBuilder builder = new StringBuilder();

                for (String tag : tagArr) {
                    String keyWord = tagsService.tags(tag);
                    if (keyWord != null && !keyWord.equals("")) {
                        builder.append(keyWord).append(",");
                    }
                }
                tags = builder.toString();
            }
            mapping(event, tags);
            FALG = 2;
        }else if (FALG == 3){
            event.getSource().sendBlocking("我在等CD，你在等什么？(▼皿▼#)");
        }
    }

    @Listener
    @Filter(value = "刻刻帝{{falg}}", matchType = MatchType.REGEX_CONTAINS)
    @ContentTrim
    public void onGroupMsgTranslate(GroupMessageEvent event, @FilterValue("falg") String falg){
            if ("中文".equals(falg)){
                TRANSLATE = true;
                event.getSource().sendBlocking("魔法咒文已经切换到中文啦，英文会失效哦✧*｡٩(ˊᗜˋ*)و✧*｡");
            }else if ("英文".equals(falg)){
                TRANSLATE = false;
                event.getSource().sendBlocking("魔法咒文已经切换到英文啦，中文会失效哦✧⁺⸜(●˙▾˙●)⸝⁺✧ ");
            }
    }

    private void mapping(GroupMessageEvent event, String tags) throws IOException, InterruptedException {

        if (!StringUtils.isEmpty(tags)) {
            event.getSource().sendBlocking("咒文咏唱中╮(￣▽￣)╭");
            InputStream inputStream = drawService.drafting(tags);
            MessagesBuilder builder = new MessagesBuilder();
            StandardResource resource = Resource.of(inputStream);

            Member author = event.getAuthor();

            builder.text("魔法施展成功啦(*>∀<)ﾉ))★ \n").image(resource).at(author.getId());
            MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());
            FALG = 3;
            Thread.sleep(10000);
            messageReceipt.deleteBlocking();
        }


    }


}
