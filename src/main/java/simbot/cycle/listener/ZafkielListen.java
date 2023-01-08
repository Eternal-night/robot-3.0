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
                .text("来丶色图:获取一张大人的色图\n")
                .text("来丶色图+tag:指定关键词，获取一张大人的色图\n")
                .text("色图过滤+数量:过滤色图的收藏数量，100、500、1000、5000、10000、20000、30000、50000、10000\n")
                .text("涩图排行:获取今日涩图排行\n")
                .text("涩图id:获取指定id的涩图\n")
                .text("涩图搜索:查询图片来源，仅限P站D站\n")
                .text("#卡片:查询游戏王卡片\n");
//                .text("#咒文:绘图\n")
//                .text("#查询咒文:查询绘图词条\n")
//                .text("#禁止咒文:禁止绘图词条\n");
        event.getSource().sendBlocking(builder.build());
    }


    @Listener
    @Filter(value = "刻刻帝{{name}}", matchType = MatchType.REGEX_MATCHES, targets = @Filter.Targets(authors = {"982319439"}))
    @ContentTrim
    public void onGroupMsgAdminEleven(GroupMessageEvent event,@FilterValue("name") String name){
        String text = "库存不足惹|*´Å`)ﾉ ";
        switch (name){
            case "一之弹":CycleUtils.setImageFlag(1);text="一之弹已装填٩(๑>◡<๑)۶ ";break;
            case "二之弹":CycleUtils.setImageFlag(2);text="二之弹已装填(๑╹◡╹)ﾉ\"\"\"";break;
            case "三之弹":CycleUtils.setImageFlag(3);text="三之弹已装填(๑´ㅂ`๑)";break;
            case "四之弹":text="四之弹未寻回〒▽〒";break;
            case "五之弹":text="五之弹未寻回/(ㄒoㄒ)/~~";break;
            case "六之弹":text="六之弹未寻回(*´д`*)";break;
            case "七之弹":text="七之弹未寻回..(｡•ˇ‸ˇ•｡)…";break;
            case "八之弹":text="八之弹尚未寻回|*´Å`)ﾉ ";break;
            case "九之弹":text="九之弹尚未寻回( Ĭ ^ Ĭ )";break;
            case "十之弹":text="十之弹尚未寻回〒▽〒";break;
            case "十一之弹":text="十一之弹尚未寻回(」＞＜)」";break;
            case "十二之弹":text="十二之弹尚未寻回 ヽ(。>д<)ｐ";break;
            default:
                break;
        }
        event.getSource().sendBlocking(text);
    }
}
