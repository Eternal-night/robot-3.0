package simbot.example.listener;

import lombok.RequiredArgsConstructor;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.annotation.TargetFilter;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.definition.Friend;
import love.forte.simbot.event.EventResult;
import love.forte.simbot.event.FriendMessageEvent;
import love.forte.simbot.event.GroupMessageEvent;
import org.springframework.stereotype.Service;
import simbot.example.service.ReplyService;

import java.io.IOException;

/**
 * simbot中的监听类
 *
 * @author ForteScarlet
 */
@Service
@RequiredArgsConstructor
public class ReplyListener {

    private final ReplyService replyService;


//    @Listener
    @Filter(value = "刻刻帝", matchType = MatchType.TEXT_EQUALS, target =
    @TargetFilter(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws IOException {
        event.getSource().sendBlocking("涩图能量已充满(｡◕ˇ∀ˇ◕)");
    }

}
