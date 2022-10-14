package simbot.example.listener;

import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.annotation.TargetFilter;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.GroupMessageEvent;
import org.springframework.stereotype.Component;

import java.io.IOException;

@Component
public class ZafkielListen {

    @Listener
    @Filter(value = "刻刻帝 十二之弹", matchType = MatchType.TEXT_EQUALS, target =
    @TargetFilter(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws IOException {
        event.getSource().sendBlocking("涩图能量已充满(｡◕ˇ∀ˇ◕)");
    }

}
