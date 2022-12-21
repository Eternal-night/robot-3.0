package simbot.cycle.service;

import love.forte.simbot.message.Message;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.resources.Resource;
import org.springframework.stereotype.Service;
import simbot.cycle.util.CollectionUtil;

import java.io.File;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RabbitBotService {

    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     *
     * @param localImgPath 本地图片路径
     * @return 消息链
     */
    public MessagesBuilder parseMsgChainByLocalImgs(String localImgPath) {
        return parseMsgChainByLocalImgs(Arrays.asList(localImgPath));
    }

    /**
     * 针对本地图片路径上传并拼接成消息连做的代码封装方法
     * 重载 批量处理
     *
     * @param localImgsPath 本地图片路径
     * @return 消息链
     */
    public MessagesBuilder parseMsgChainByLocalImgs(List<String> localImgsPath) {
        List<Resource> imageList = this.uploadMiraiImage(localImgsPath);
        return this.parseMsgChainByImgs(imageList);
    }

    /**
     * 上传图片，获取图片id
     *
     * @param localImagesPath 本地图片列表
     * @return mirai图片id列表
     */
    public List<Resource> uploadMiraiImage(List<String> localImagesPath) {
        List<Resource> miraiImgList = new ArrayList<>();
        //上传并获取每张图片的id
        if (CollectionUtil.isEmpty(localImagesPath)) {
            return miraiImgList;
        }
        for (String localImagePath : localImagesPath) {
            Resource tempMiraiImg = uploadMiraiImage(localImagePath);
            miraiImgList.add(tempMiraiImg);
        }
        return miraiImgList;
    }

    /**
     * @description: 根据本地图片路径获取对应的资源对象
     * @author: 陈杰
     * @date: 2022/12/21 9:22
     * @param: localImagesPath 本地图片路径
     * @return: love.forte.simbot.resources.Resource
     **/
    public Resource uploadMiraiImage(String localImagesPath) {
        return Resource.of(new File(localImagesPath));
    }


    /**
     * 针对多张图拼接成消息连做的代码封装方法
     *
     * @param imgList mirai图片集合
     * @return 消息链
     */
    public MessagesBuilder parseMsgChainByImgs(List<Resource> imgList) {
        MessagesBuilder builder = new MessagesBuilder();
        for (Resource resource : imgList) {
            builder.image(resource);
        }
        return builder;
    }
}
