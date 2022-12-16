package simbot.cycle.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.*;
import love.forte.simbot.message.MessagesBuilder;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.service.ImageTagService;
import simbot.cycle.util.CycleUtils;


@Component
public class ZafkielListen {

    @Autowired
    private ImageTagService imageTagService;

    @Listener
    @Filter(value = "刻刻帝", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws InterruptedException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("可执行指令\n")
                .text("来丶涩图:获取一张涩图\n")
                .text("来丶涩图+tag:获取指定标签相关的涩图\n")
                .text("#卡片:查询游戏王卡片\n")
                .text("#咒文:绘图\n")
                .text("#查询咒文:查询绘图词条\n")
                .text("#禁止咒文:禁止绘图词条\n");
        event.getSource().sendBlocking(builder.build());
    }


    @Listener
    @Filter(value = "刻刻帝 十一之弹", matchType = MatchType.TEXT_EQUALS, targets = @Filter.Targets(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminEleven(GroupMessageEvent event){
        Integer IMAGE_FLAG = CycleUtils.getImageFlag();
        if (IMAGE_FLAG ==1) {
            CycleUtils.setImageFlag(2);
            imageTagService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了❥(ゝω・✿ฺ)");
        } else if (IMAGE_FLAG ==2) {
            CycleUtils.setImageFlag(3);
            imageTagService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(๑＞ڡ＜)✿ ");
        } else if (IMAGE_FLAG ==3) {
            CycleUtils.setImageFlag(1);
            imageTagService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(*ﾉω・*)ﾃﾍ");
        }
    }
}
