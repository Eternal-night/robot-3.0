package simbot.cycle.tasks;

import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.annotation.EnableScheduling;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;
import simbot.cycle.constant.ConstantBilibili;
import simbot.cycle.constant.ConstantImage;
import simbot.cycle.entity.ReString;
import simbot.cycle.service.BilibiliService;
import simbot.cycle.service.SwitchService;
import simbot.cycle.util.DateUtil;
import simbot.cycle.util.RandomUtil;

/**
 * create by MikuLink on 2019/12/3 12:58
 * for the Reisen
 * <p>
 * 1分钟执行一次的定时器
 */
@Component
@EnableScheduling
public class JobMain {
    private static final Logger logger = LoggerFactory.getLogger(JobMain.class);

    //正常间隔(毫秒) 目前为2小时
    private static final Long SPLIT_NORMAL = 1000L * 60 * 60 * 2;
    //随机间隔最大值(分钟) 目前最长延迟1小时
    private static final Integer SPLIT_RANDOM_MAX = 60;


    @Autowired
    private BilibiliService bilibiliService;

    @Scheduled(cron = "0 * * * * ?")
    public void execute() {



    }


}
