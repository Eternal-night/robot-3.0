package simbot.example.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.definition.Group;
import love.forte.simbot.event.*;
import love.forte.simbot.message.MessageReceipt;
import org.springframework.stereotype.Component;


@Component
public class ZafkielListen {


//    @Listener
    @Filter(value = "刻刻帝", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws InterruptedException {
        Group source = event.getSource();
        MessageReceipt messageReceipt = source.sendBlocking("iii");

        Thread.sleep(10000);

        messageReceipt.deleteBlocking();

    }
}
