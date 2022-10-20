package simbot.example.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.annotation.TargetFilter;
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
import simbot.example.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.Map;
import java.util.concurrent.ConcurrentHashMap;

@Component
public class DrawListen {

    private static final Map<String, String> MAP = new ConcurrentHashMap<>();
    @Autowired
    private DrawService drawService;

    @Listener
    @Filter(value = "#咒文", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws IOException, InterruptedException {
        String flag = MAP.get("flag");
        if (flag == null) {
            MAP.put("flag", "1");
            MessageReceipt mapping = mapping(event);
            MAP.put("flag","2");
            if (mapping!=null){
                Thread.sleep(12000);
                mapping.deleteBlocking();
            }
        }else if ("1".equals(flag)){
            event.getSource().sendBlocking("不要打断我咏唱咒文啊魂淡(╬￣皿￣)=○");
        }else if ("2".equals(flag)){
            MAP.put("flag", "1");
            MessageReceipt mapping = mapping(event);
            MAP.put("flag","2");

            if (mapping!=null){
                Thread.sleep(12000);
                mapping.deleteBlocking();
            }
        }
    }

    private MessageReceipt mapping(GroupMessageEvent event) throws IOException {
        String massageText = event.getMessageContent().getPlainText();

        String[] split = massageText.split("#咒文");

        String name = split[split.length - 1];

        String trimName = name.trim();

        if (!StringUtils.isEmpty(trimName)){
            if (trimName.split(",").length>=70) {
                event.getSource().sendBlocking("不要使用禁咒啊魂淡(╬￣皿￣)=○");
            }else {
                if (!trimName.isEmpty()) {
                    event.getSource().sendBlocking("咒文咏唱中╮(￣▽￣)╭");
                    InputStream inputStream = drawService.drafting(trimName);
                    MessagesBuilder builder = new MessagesBuilder();
                    StandardResource resource = Resource.of(inputStream);
                    builder.text("魔法施展成功啦(*>∀<)ﾉ))★ \n").image(resource);
                    return event.getSource().sendBlocking(builder.build());
                }
            }
        }

        return null;

    }


}
