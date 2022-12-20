package simbot.cycle.service;

import love.forte.simbot.bot.Bot;
import love.forte.simbot.message.MessagesBuilder;
import net.mamoe.mirai.contact.ContactList;
import net.mamoe.mirai.contact.Group;
import net.mamoe.mirai.message.data.Image;
import net.mamoe.mirai.message.data.MessageChain;
import net.mamoe.mirai.message.data.MessageUtils;
import net.mamoe.mirai.utils.ExternalResource;
import org.springframework.stereotype.Service;
import simbot.cycle.util.CollectionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RabbitBotService {

    private Group group;

    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     *
     * @param localImgPath 本地图片路径
     * @return 消息链
     */
    public MessageChain parseMsgChainByLocalImgs(String localImgPath) {
        return parseMsgChainByLocalImgs(Arrays.asList(localImgPath));
    }

    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     * 重载 批量处理
     *
     * @param localImgsPath 本地图片路径
     * @return 消息链
     */
    public MessageChain parseMsgChainByLocalImgs(List<String> localImgsPath) {
        List<Image> imageList = this.uploadMiraiImage(localImgsPath);
        return this.parseMsgChainByImgs(imageList);
    }

    /**
     * 上传图片，获取图片id
     *
     * @param localImagesPath 本地图片列表
     * @return mirai图片id列表
     */
    public List<Image> uploadMiraiImage(List<String> localImagesPath) {
        List<Image> miraiImgList = new ArrayList<>();
        //上传并获取每张图片的id
        if (CollectionUtil.isEmpty(localImagesPath)) {
            return miraiImgList;
        }
        for (String localImagePath : localImagesPath) {
            Image tempMiraiImg = uploadMiraiImage(localImagePath);
            miraiImgList.add(tempMiraiImg);
        }
        return miraiImgList;
    }

    /**
     * @description:
     * @author: 陈杰
     * @date: 2022/12/20 14:26
 * @param: localImagesPath
 * @return: net.mamoe.mirai.message.data.Image
     **/
    public Image uploadMiraiImage(String localImagesPath) {
//        if (null == group) {
//            ContactList<Group> groupList = RabbitBot.getBot().getGroups();
//            for (Group grouptemp : groupList) {
//                group = grouptemp;
//                break;
//            }
//        }
        //上传
        return group.uploadImage(ExternalResource.create(new File(localImagesPath)).toAutoCloseable());
    }


    /**
     * 针对多张图拼接成消息连做的代码封装方法
     *
     * @param imgList mirai图片集合
     * @return 消息链
     */
    public MessageChain parseMsgChainByImgs(List<Image> imgList) {
        MessageChain messageChain = MessageUtils.newChain();
        for (Image image : imgList) {
            messageChain = messageChain.plus("").plus(image).plus("\n");
        }
        return messageChain;
    }
}
