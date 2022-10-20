package simbot.example.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.ID;
import love.forte.simbot.action.DeleteSupport;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.definition.Friend;
import love.forte.simbot.definition.Group;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.*;
import love.forte.simbot.message.Message;
import love.forte.simbot.message.MessageReceipt;
import love.forte.simbot.message.Messages;
import love.forte.simbot.message.MessagesBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.io.IOException;
import java.time.Duration;

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
