package simbot.cycle.listener;

import love.forte.simboot.annotation.Listener;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.ReceivedMessageContent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.Conversation;
import simbot.cycle.service.RedisService;

import java.util.List;
import java.util.Random;
import java.util.concurrent.TimeUnit;

@Component
public class DialogueListen {

    @Autowired
    private RedisService redisService;

    @Listener
    public void reply(GroupMessageEvent event) throws InterruptedException {
        String plainText = event.getMessageContent().getPlainText();
        if (redisService.hasKey(plainText)) {
            List<Object> cacheList = redisService.getCacheList(plainText);
            Random random = new Random();
            if (!cacheList.isEmpty()) {
                Object obj = cacheList.get(random.nextInt(cacheList.size()));
                if (obj!=null){
                    Member author = event.getAuthor();
                    String name = author.getUsername();
                    Conversation conversation = new Conversation();
                    BeanUtils.copyProperties(obj,conversation);
                    String feedback = conversation.getFeedback();
                    String replaceAll = feedback.replaceAll("\\{name}", name);
                    String[] split = replaceAll.split("\\{segment}");
                    for (String message : split) {
                        event.getGroup().sendBlocking(message);
                    }
                }
            }
        }
    }




}
