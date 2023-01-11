package simbot.cycle.service;


import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import simbot.cycle.constant.ConstantTarot;
import simbot.cycle.entity.TarotInfo;
import simbot.cycle.util.FileUtil;
import simbot.cycle.util.RandomUtil;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

/**
 * create by MikuLink on 2020/12/22 15:18
 * for the Reisen
 * 塔罗牌相关
 */
@Service
public class TarotService {
    @Autowired
    private RabbitBotService rabbitBotService;

    @Value("${file.path.data:}")
    private String dataPath;

    /**
     * 获取资源文件路径
     */
    public String getFilePath() {
        return dataPath + File.separator + "files" + File.separator + "tarot.txt";
    }

    /**
     * 抽一张塔罗牌
     * 只返回原始数据，包括上传图片在内的所有操作由上层执行
     */
    public TarotInfo getTarot() {
        //随机出一个元素
        int rollNum = RandomUtil.roll(ConstantTarot.TARTO_LIST.size() - 1);
        TarotInfo tarotInfo = ConstantTarot.TARTO_LIST.get(rollNum);
        //随机正位逆位
        tarotInfo.setStatus(RandomUtil.rollBoolean(0));
        return tarotInfo;
    }

    /**
     * 把塔罗牌数据转化为消息链
     *
     * @return 消息链
     */
    public MessageChain parseTarotMessage(TarotInfo tarotInfo) {
        MessageChain result = MessageUtils.newChain();

        if (null == tarotInfo) {
            return result;
        }

        String imagesPath = ConstantTarot.IMAGE_TAROT_SAVE_PATH;
        if (tarotInfo.isCat()) {
            imagesPath = ConstantTarot.IMAGE_CATROT_SAVE_PATH;
        }

        //图片处理
        Resource miraiImage = rabbitBotService.uploadMiraiImage(imagesPath + File.separator + tarotInfo.getImgName());
       // result = rabbitBotService.parseMsgChainByImg(miraiImage);

        StringBuilder resultStr = new StringBuilder();
        resultStr.append("[").append(tarotInfo.getName()).append(" ").append(tarotInfo.isStatus() ? "正位" : "逆位").append("]");
        resultStr.append("\n").append(tarotInfo.isStatus() ? tarotInfo.getNormalDes() : tarotInfo.getSeDlamron());
        result = result.plus(resultStr.toString());
        return result;
    }

    /**
     * 加载文件内容
     *
     * @throws IOException 读写异常
     */
    public void loadFile() throws IOException {
        File tarotLikeFile = FileUtil.fileCheck(this.getFilePath());
        //创建读取器
        BufferedReader reader = new BufferedReader(new FileReader(tarotLikeFile));

        //逐行读取文件
        String freeTimeStr = null;
        //跳过第一行 是标识的数据来源
        reader.readLine();
        while ((freeTimeStr = reader.readLine()) != null) {
            //过滤掉空行
            if (freeTimeStr.length() <= 0) continue;

            TarotInfo tempInfo = new TarotInfo();

            //首先是图片名称
            tempInfo.setImgName(freeTimeStr);
            //再往下读一行，是卡牌名称
            freeTimeStr = reader.readLine();
            tempInfo.setName(freeTimeStr);

            //再往下读一行，是正位描述
            freeTimeStr = reader.readLine();
            freeTimeStr = freeTimeStr.substring(freeTimeStr.indexOf("：") + 1);
            tempInfo.setNormalDes(freeTimeStr);

            //再往下读一行，是逆位描述
            freeTimeStr = reader.readLine();
            freeTimeStr = freeTimeStr.substring(freeTimeStr.indexOf("：") + 1);
            tempInfo.setSeDlamron(freeTimeStr);

            ConstantTarot.TARTO_LIST.add(tempInfo);
        }
        //关闭读取器
        reader.close();
    }
}
