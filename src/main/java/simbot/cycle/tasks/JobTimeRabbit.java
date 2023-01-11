package simbot.cycle.tasks;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.definition.Group;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.utils.item.Items;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.message.data.MessageChain;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import simbot.cycle.constant.ConstantCommon;
import simbot.cycle.constant.ConstantPixiv;
import simbot.cycle.entity.ReString;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.entity.pixiv.PixivRankImageInfo;
import simbot.cycle.service.*;
import simbot.cycle.util.BotUtils;
import simbot.cycle.util.DateUtil;
import simbot.cycle.util.RandomUtil;
import simbot.cycle.util.StringUtils;

import java.util.Date;
import java.util.List;

/**
 * create by MikuLink on 2019/12/3 12:58
 * for the Reisen
 * <p>
 * 1小时执行一次的定时器
 */
@Component
@EnableScheduling
public class JobTimeRabbit {
    private static final Logger logger = LoggerFactory.getLogger(JobTimeRabbit.class);

    //兔叽
    @Value("${bot.name.cn:刻刻帝}")
    public String rabbit_bot_name;
    //当前时间，方便其他地方使用
    private int hour_now = 0;

    @Autowired
    private RabbitBotService rabbitBotService;
    @Autowired
    private PixivService pixivService;
    @Autowired
    private SetuService setuService;

    @Autowired
    private MirlKoiService mirlKoiService;

   // @Scheduled(cron = "0 0 * * * ?")
    public void execute() {
        //刷新当前时间
        hour_now = DateUtil.getHour();
        //报时
        timeRabbit();
        //色图
        setuOnDay();
    }

    //报时
    private void timeRabbit() {
        //附加短语
        String msgEx = getMsgEx();
        //群报时，时间间隔交给定时器，这里返回值取当前时间即可
        String msg = String.format("这里是%s报时：%s%s", rabbit_bot_name, DateUtil.toString(new Date()), msgEx);
        try {
            //给每个群发送报时
            List<Bot> botList = BotUtils.getBotList();
            for (Bot bot : botList) {
                List<Group> groups = bot.getGroups().collectToList();
                for (Group group : groups) {
                    group.sendBlocking(msg);
                }
            }
        } catch (Exception ex) {
            logger.error("报时 消息发送异常" + ex, ex);
        }
    }

    //获取附加短语，可以放一些彩蛋性质的东西，会附带在报时消息尾部
    private String getMsgEx() {
        switch (hour_now) {
            //半夜0点
            case 0:
                return ConstantCommon.NEXT_LINE + "新的一天开始啦ヽ(#`Д´)ノ";
            //凌晨4点
            case 4:
                return ConstantCommon.NEXT_LINE + "还有人活着嘛~";
            //早上7点
            case 7:
                return ConstantCommon.NEXT_LINE + "早上好,该起床了哦~~";
            //中午11点
            case 11:
                return ConstantCommon.NEXT_LINE + "开始做饭了吗，外卖点了吗";
            //中午12点
            case 12:
                return ConstantCommon.NEXT_LINE + "午安，该是吃午饭的时间了";
            //下午18点
            case 18:
                return ConstantCommon.NEXT_LINE + "到了下班的时间啦!";
            //晚上23点
            case 23:
                return ConstantCommon.NEXT_LINE + "已经很晚了，早点休息哦~~";
        }
        return "";
    }


    //每日色图
    private void setuOnDay() {
//        if (hour_now != 20) {
//            return;
//        }
        try {
            String pixivSetu = ConstantCommon.common_config.get(ConstantPixiv.PIXIV_CONFIG_SETU);
            List<String> setu = null;
            PixivImageInfo pixivImageInfo = null;
            if (StringUtils.isNotEmpty(pixivSetu) && "1".equalsIgnoreCase(pixivSetu)) {
                pixivImageInfo = setuService.getSetu();
            } else {
                setu = mirlKoiService.downloadASetu(1);
            }
            //给每个群发送消息
            List<Bot> botList = BotUtils.getBotList();
            for (Bot bot : botList) {
                List<Group> groups = bot.getGroups().collectToList();
                for (Group group : groups) {

                    MessagesBuilder messageChain = null;
                    if (StringUtils.isNotEmpty(pixivSetu) && "1".equalsIgnoreCase(pixivSetu)) {
                        messageChain = pixivService.parsePixivImgInfoByApiInfo(pixivImageInfo);
                    } else {
                        messageChain = rabbitBotService.parseMsgChainByLocalImgs(setu.get(0));
                    }

                    group.sendBlocking(RandomUtil.rollStrFromList(ConstantPixiv.SETU_DAY_EX_MSG_List));
                    group.sendBlocking(messageChain.build());
                }
            }
        } catch (Exception ex) {
            logger.error(ConstantPixiv.SETU_DAY_ERROR, ex);
        }
    }



}
