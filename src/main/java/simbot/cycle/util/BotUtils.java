package simbot.cycle.util;

import cn.hutool.core.util.ReUtil;
import lombok.extern.slf4j.Slf4j;
import love.forte.simbot.ID;
import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessageContent;

@Slf4j
public class BotUtils {
    /**
     * 持续会话
     *
     * @return java.lang.String
     * @Author Sama05
     * @Date 16:43 2022/5/16
     * @Param [id , key, sessionContext]
     **/
    public static MessageContent waiting(ContinuousSessionContext sessionContext, GroupMessageEvent event) {
        final ID id = event.getBot().getId();
        final ID qqId = event.getAuthor().getId();
        final ID groupId = event.getGroup().getId();
        try {
            return sessionContext.waitingForNextMessage((context, messageEvent) -> {
                //log.info(messageEvent.toString());
                GroupMessageEvent groupMessageEvent = (GroupMessageEvent) messageEvent;
                final ID sid = groupMessageEvent.getBot().getId();
                final ID sqqId = groupMessageEvent.getAuthor().getId();
                final ID sgroupId = groupMessageEvent.getGroup().getId();
                return id.equals(sid) && groupId.equals(sgroupId) && sqqId.equals(qqId);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }

    /**
     * 支持正则表达式的持续会话
     *
     * @return love.forte.simbot.message.MessageContent
     * @Author Sama05
     * @Date 15:47 2022/8/24
     * @Param [sessionContext , event, r]
     **/
    public static MessageContent waiting(ContinuousSessionContext sessionContext, GroupMessageEvent event, String r) {
        final ID id = event.getBot().getId();
        final ID qqId = event.getAuthor().getId();
        final ID groupId = event.getGroup().getId();
        try {
            return sessionContext.waitingForNextMessage((context, messageEvent) -> {
                //log.info(messageEvent.toString());
                GroupMessageEvent groupMessageEvent = (GroupMessageEvent) messageEvent;
                final ID sid = groupMessageEvent.getBot().getId();
                final ID sqqId = groupMessageEvent.getAuthor().getId();
                final ID sgroupId = groupMessageEvent.getGroup().getId();
                String s = groupMessageEvent.getMessageContent().getMessages().toString();
                return id.equals(sid) && groupId.equals(sgroupId) && sqqId.equals(qqId) && ReUtil.contains(r, s);
            });
        } catch (Exception e) {
            log.error(e.getMessage());
        }
        return null;
    }
}
