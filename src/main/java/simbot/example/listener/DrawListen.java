package simbot.example.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessageReceipt;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.example.service.DrawService;
import simbot.example.service.TagsService;
import simbot.example.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class DrawListen {
    private static Integer FALG = 2;
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

            String replaceAll = tags.replaceAll("，", ",");
            String[] tagArr = replaceAll.split(",");

            StringBuilder builder = new StringBuilder();

            for (String tag : tagArr) {
                String keyWord = tagsService.tags(tag);
                if (keyWord != null) {
                    builder.append(keyWord).append(",");
                }
            }
            tags = builder.toString();

            if (StringUtils.isEmpty(tags)) {
                event.getSource().sendBlocking("想要释放魔法？你还早了两万年！回去检查检查自己的咒文吧！o(▼皿▼メ;)o");
            } else {
                mapping(event, tags);
            }
            FALG = 2;
        }
    }

    private void mapping(GroupMessageEvent event, String tags) throws IOException {
        event.getSource().sendBlocking("咒文咏唱中╮(￣▽￣)╭");
        InputStream inputStream = drawService.drafting(tags);
        MessagesBuilder builder = new MessagesBuilder();
        StandardResource resource = Resource.of(inputStream);
        Member author = event.getAuthor();
        builder.text("魔法施展成功啦(*>∀<)ﾉ))★ \n").image(resource).at(author.getId());
        MessageReceipt messageReceipt = event.getSource().sendBlocking(builder.build());

        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(15);
                messageReceipt.deleteBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }
}
