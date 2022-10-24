package simbot.cycle.listener;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.ID;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.definition.Group;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.MessageContent;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.Text;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.Card;
import simbot.cycle.entity.YuGiOh;
import simbot.cycle.entity.YuGiOhDetails;
import simbot.cycle.service.CardService;
import simbot.cycle.service.YuGiOhService;
import simbot.cycle.util.BotUtils;

import java.io.IOException;
import java.io.InputStream;
import java.net.MalformedURLException;
import java.net.URL;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;

@Component
public class YuGiOhListen {

    @Autowired
    private CardService cardService;

    @Listener
    @Filter(value = "#卡片{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgYgo(ContinuousSessionContext sessionContext, GroupMessageEvent event, @FilterValue("name") String name) throws IOException {
        String trimName = name.trim();
        Group source = event.getSource();
        if (!trimName.isEmpty()) {
            List<Card> cards = cardService.list(new LambdaQueryWrapper<Card>().like(Card::getCnName, trimName));
            if (cards.isEmpty()) {
                source.sendBlocking("抱歉没有查询到呢(；´д｀)ゞ");
            } else {
                if (cards.size() >= 100) {
                    source.sendBlocking("查询结果太多啦，请重新输入关键词(｀・ω・´)");
                } else {
                    Member author = event.getAuthor();
                    MessagesBuilder builder = new MessagesBuilder();
                    builder.at(author.getId()).text("请选择对应的卡片查询详情ヾ(ﾟ∀ﾟゞ)");
                    source.sendBlocking(builder.build());
                    MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);

                    for (Card card : cards) {
                        chain.add(event.getBot(), Text.of("#" +card.getCnName()));
                    }
                    source.sendBlocking(chain.build());
                }
            }
        }
    }


    @Listener
    @ContentTrim // 去除内容消息前后空格
    @Filter(value = "#{{name}}", matchType = MatchType.REGEX_MATCHES)
    public void text(GroupMessageEvent event,@FilterValue("name") String name) throws IOException {
        List<Card> list = cardService.list(new LambdaQueryWrapper<Card>().eq(Card::getCnName, name));
        if (!list.isEmpty()) {
            Card cardAfter = list.stream().findFirst().orElse(null);
            Card card = cardService.cardInfo(cardAfter.getId());
            MessagesBuilder cardBuilder = new MessagesBuilder();
            cardBuilder.image(Resource.of(new URL(card.getImageUrl()).openStream()));
            cardBuilder.text("中文名:" + card.getCnName() + "\n")
                    .text("日文名:" + card.getJpName() + "\n")
                    .text("英文名:" + card.getEnName() + "\n")
                    .text(card.getText().getTypes() + "\n")
                    .text(card.getText().getDesc() + "\n")
                    .text(card.getText().getPdesc() + "\n");
            event.getSource().sendBlocking(cardBuilder.build());
        }
    }

}
