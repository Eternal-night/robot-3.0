package simbot.cycle.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.YuGiOh;
import simbot.cycle.entity.YuGiOhDetails;
import simbot.cycle.service.YuGiOhService;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.util.List;

@Component
public class YuGiOhListen {

    @Autowired
    private YuGiOhService yuGiOhService;

//    @Listener
    @Filter(value = "ygo{{name}}", matchType = MatchType.REGEX_CONTAINS)
    @ContentTrim
    public void onGroupMsgYgo(GroupMessageEvent event, @FilterValue("name") String name) throws IOException {

        String trimName = name.trim();

        if (!trimName.isEmpty()) {

            MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);

            List<YuGiOh> yuGiOhList = yuGiOhService.YuGiOhInquire(trimName);

            if (!yuGiOhList.isEmpty()) {
                for (YuGiOh yuGiOh : yuGiOhList) {
                    MessagesBuilder builder = new MessagesBuilder();

                    String imageUrl = yuGiOh.getImageUrl();

                    List<String> nameList = yuGiOh.getNameList();
                    YuGiOhDetails details = yuGiOh.getDetails();
                    String trim1 = details.getInformation().trim();
                    String trim2 = details.getSwingingEffect().trim();
                    URL url = new URL(imageUrl);
                    InputStream inputStream = url.openStream();
                    builder.image(Resource.of(inputStream))
                            .text("中文名:" + nameList.get(0) + "\n")
                            .text("日文名:" + nameList.get(1) + "\n")
                            .text("英文名:" + nameList.get(2) + "\n")
                            .text(trim1 + "\n")
                            .text(trim2 + "\n");
                    if (details.getMonsterEffect() != null) {
                        String trim3 = details.getMonsterEffect().trim();
                        builder.text(trim3 + "\n");
                    }
                    chain.add(event.getBot(),builder.build());
                    inputStream.close();
                }
                event.getSource().sendBlocking(chain.build());
            } else {
                event.getSource().sendBlocking("~没有查询到呢~ QAQ");
            }
        }

    }

}
