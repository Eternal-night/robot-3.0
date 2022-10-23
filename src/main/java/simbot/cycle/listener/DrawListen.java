package simbot.cycle.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.bot.Bot;
import love.forte.simbot.component.mirai.message.MiraiForwardMessageBuilder;
import love.forte.simbot.component.mirai.message.MiraiSendOnlyForwardMessage;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.*;
import love.forte.simbot.resources.Resource;
import love.forte.simbot.resources.StandardResource;
import net.mamoe.mirai.message.data.ForwardMessage;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.Contraband;
import simbot.cycle.entity.Tags;
import simbot.cycle.service.ContrabandService;
import simbot.cycle.service.DrawService;
import simbot.cycle.service.TagsService;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.StringUtils;

import java.io.IOException;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.CompletableFuture;
import java.util.concurrent.TimeUnit;
import java.util.stream.Collectors;

@Component
public class DrawListen {

    private static Integer DRAW_FALG = 2;

    @Autowired
    private DrawService drawService;
    @Autowired
    private TagsService tagsService;

    @Autowired
    private ContrabandService contrabandService;


    @Listener
    @Filter(value = "#咒文", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgMapping(GroupMessageEvent event) throws IOException {

        if (DRAW_FALG == 1) {
            event.getSource().sendBlocking("不要打断我咏唱咒文啊魂淡(╬￣皿￣)=○");
        } else if (DRAW_FALG == 2) {
            DRAW_FALG = 1;
            String massageText = event.getMessageContent().getPlainText();
            String tags = massageText.replaceAll("#咒文", "");

            String replaceAll = tags.replaceAll("，", ",");
            String[] tagArr = replaceAll.split(",");

            List<String> contrabandList = CycleUtils.getContrabandList();

            if (contrabandList.isEmpty()) {
                List<Contraband> list = contrabandService.list();
                List<String> collect = list.stream().map(Contraband::getWord).collect(Collectors.toList());
                contrabandList.addAll(collect);
            }

            StringBuilder builder = new StringBuilder();

            for (String tag : tagArr) {
                if (!contrabandList.contains(tag)) {
                    String keyWord = tagsService.tags(tag);
                    if (keyWord != null) {
                        builder.append(keyWord).append(",");
                    }
                }
            }
            tags = builder.toString();

            if (StringUtils.isEmpty(tags)) {
                event.getSource().sendBlocking("想要释放魔法？你还早了两万年！回去检查检查自己的咒文吧！o(▼皿▼メ;)o");
            } else {
                mapping(event, tags);
            }
            DRAW_FALG = 2;
        }
    }

    @Listener
    @Filter(value = "#查询咒文{{tags}}", matchType = MatchType.REGEX_CONTAINS)
    @ContentTrim
    public void onGroupMsgEntry(GroupMessageEvent event, @FilterValue("tags") String tags) {
        List<Tags> tagsList = tagsService.findTags(tags);
        if (tagsList.isEmpty()) {
            event.getSource().sendBlocking("没有查询到相关咒文呢(」＞＜)」");
        } else {
            MiraiForwardMessageBuilder builder = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
            builder.add(event.getBot(), Text.of("查到咯(*´ﾟ∀ﾟ｀)ﾉ " + "\n"));
            int size = tagsList.size();
            if (size >= 200) {
                for (int i = 0; i <= 188; i++) {
                    builder.add(event.getBot(), Text.of(tagsList.get(i).getChinese() + " : " + tagsList.get(i).getTagName()));
                }
                builder.add(event.getBot(), Text.of("数量过多仅显示前188条哦ㄟ( ▔, ▔ )ㄏ "));
            } else {
                for (Tags tag : tagsList) {
                    builder.add(event.getBot(), Text.of(tag.getChinese() + " : " + tag.getTagName()));
                }
            }
            MiraiSendOnlyForwardMessage message = builder.build();
            event.getSource().sendBlocking(message);
        }
    }


    @Listener
    @Filter(value = "#禁止咒文", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgProhibit(GroupMessageEvent event) {
        String massageText = event.getMessageContent().getPlainText();

        Member author = event.getAuthor();

        String id = author.getId().toString();
        Date date = new Date();

        String tags = massageText.replaceAll("#禁止咒文", "");
        String replaceAll = tags.replaceAll("，", ",");
        String[] tagArr = replaceAll.split(",");

        List<Contraband> list = new ArrayList<>();

        for (String word : tagArr) {
            Contraband contraband = new Contraband();
            contraband.setWord(word);
            contraband.setCreateBy(id);
            contraband.setCreateTime(date);
            list.add(contraband);
        }

        boolean saveBatch = contrabandService.saveBatch(list);

        if (saveBatch) {
            event.getSource().sendBlocking("该咒文已经丢进小黑屋啦ヽ(ﾟ∀ﾟ)ﾒ(ﾟ∀ﾟ)ﾉ ");
        } else {
            event.getSource().sendBlocking("封印咒文失败了/(ㄒoㄒ)/~~");
        }

    }


    private void mapping(GroupMessageEvent event, String tags) throws IOException {
        event.getSource().sendBlocking("咒文咏唱中╮(￣▽￣)╭");
        InputStream inputStream = drawService.drafting(tags);
        StandardResource resource = Resource.of(inputStream);
        Member author = event.getAuthor();
        String qq = author.getId().toString();
        String username = author.getUsername();
        Bot bot = event.getBot();
        MiraiForwardMessageBuilder chain = new MiraiForwardMessageBuilder(ForwardMessage.DisplayStrategy.Default);
        MessagesBuilder builder = new MessagesBuilder();
        chain.add(bot, builder.image(resource).build());
        chain.add(bot,Text.of("施法者："+username+"("+qq+")"));
        chain.add(bot,Text.of("咒文:"+tags));
        MiraiSendOnlyForwardMessage build = chain.build();
        MessageReceipt messageReceipt = event.getSource().sendBlocking(build);

        CompletableFuture.runAsync(() -> {
            try {
                TimeUnit.SECONDS.sleep(30);
                messageReceipt.deleteBlocking();
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        });

    }
}
