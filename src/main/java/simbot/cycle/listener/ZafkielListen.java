package simbot.cycle.listener;

import love.forte.simboot.annotation.*;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.event.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.service.ImageService;
import simbot.cycle.util.CycleUtils;


@Component
public class ZafkielListen {

    @Autowired
    private ImageService imageService;

//    @Listener
    @Filter(value = "刻刻帝", matchType = MatchType.TEXT_STARTS_WITH)
    @ContentTrim
    public void onGroupMsgAdminTwelve(GroupMessageEvent event) throws InterruptedException {


    }


    @Listener
    @Filter(value = "刻刻帝 十一之弹", matchType = MatchType.TEXT_EQUALS, targets = @Filter.Targets(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminEleven(GroupMessageEvent event){
        Integer FLAG = CycleUtils.getFLAG();
        if (FLAG ==1) {
            FLAG =2;
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了❥(ゝω・✿ฺ)");
        } else if (FLAG ==2) {
            FLAG =3;
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(๑＞ڡ＜)✿ ");
        } else if (FLAG ==3) {
            FLAG =1;
            imageService.ClearData();
            event.getSource().sendBlocking("时间的流向改变了(*ﾉω・*)ﾃﾍ");
        }
    }
}