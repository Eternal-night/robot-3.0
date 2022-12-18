package simbot.cycle.listener;

import cn.hutool.core.io.resource.ClassPathResource;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;

import love.forte.simbot.definition.Group;
import love.forte.simbot.event.GroupMessageEvent;
import love.forte.simbot.message.Image;
import love.forte.simbot.message.MessagesBuilder;
import love.forte.simbot.message.ResourceImage;
import love.forte.simbot.resources.PathResource;
import love.forte.simbot.resources.Resource;
import net.mamoe.mirai.internal.deps.io.ktor.http.Url;
import net.mamoe.mirai.message.data.MessageChain;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;
import simbot.cycle.entity.pixiv.PixivImageInfo;
import simbot.cycle.exceptions.CycleException;
import simbot.cycle.service.ImageTagService;
import simbot.cycle.service.PixivService;
import simbot.cycle.util.CycleUtils;
import simbot.cycle.util.StringUtils;

import java.io.File;
import java.io.IOException;
import java.io.InputStream;
import java.net.URL;
import java.nio.file.Path;
import java.util.List;
import java.util.Random;


@Component
public class PictureListen {

    @Autowired
    private ImageTagService imageTagService;

    @Autowired
    private PixivService pixivService;


    @Listener
    @Filter(value = "来丶涩图", matchType = MatchType.TEXT_EQUALS)
    @ContentTrim
    public void onGroupMsgSt(GroupMessageEvent event) throws IOException {
        MessagesBuilder builder = new MessagesBuilder();
        builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(imageTagService.ranDom()));
        event.getSource().sendBlocking(builder.build());
    }

    @Listener
    @Filter(value = "来丶涩图{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void onGroupMsgStWord(GroupMessageEvent event, @FilterValue("name") String name){
        if (StringUtils.isNotBlank(name)) {
            try {
                PixivImageInfo pixivIllustByTag = pixivService.getPixivIllustByTag(name);
                pixivService.parseImages(pixivIllustByTag);
                List<String> localImgPathList = pixivIllustByTag.getLocalImgPathList();
                Random random = new Random();
                String url = localImgPathList.get(random.nextInt(localImgPathList.size()));
                File file = new File(url);
                MessagesBuilder builder = new MessagesBuilder();
                builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(file));
                    event.getSource().sendBlocking(builder.build());
            }catch (Exception e){
                e.printStackTrace();
                event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
            }
        }
    }


    @Listener
    @Filter(value = "涩图id{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void groupMsgStWord(GroupMessageEvent event, @FilterValue("name") Long name){
            try {
                PixivImageInfo pixivIllustByTag = pixivService.getPixivImgInfoById(name);
                pixivService.parseImages(pixivIllustByTag);
                List<String> localImgPathList = pixivIllustByTag.getLocalImgPathList();
                Random random = new Random();
                String url = localImgPathList.get(random.nextInt(localImgPathList.size()));
                File file = new File(url);
                MessagesBuilder builder = new MessagesBuilder();
                builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(file));
                event.getSource().sendBlocking(builder.build());
            }catch (Exception e){
                e.printStackTrace();
                event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
            }

    }



//    @Listener
//    @Filter(value = "来丶涩图{{name}}", matchType = MatchType.REGEX_MATCHES)
//    @ContentTrim
//    public void onGroupMsgStWord(GroupMessageEvent event, @FilterValue("name") String name) throws IOException {
//        Integer IMAGE_FLAG = CycleUtils.getImageFlag();
//        if (StringUtils.isNotBlank(name)) {
//            MessagesBuilder builder = new MessagesBuilder();
//            if (IMAGE_FLAG ==1) {
//                String imageUrl = imageTagService.getDuitangUrl(name);
//                if (imageUrl == null) {
//                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
//                } else {
//                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
//                    event.getSource().sendBlocking(builder.build());
//                }
//            } else if (IMAGE_FLAG ==2) {
//                String imageUrl = imageTagService.getImageUrl(name);
//                if (imageUrl == null) {
//                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
//                } else {
//                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(new URL(imageUrl).openStream()));
//                    event.getSource().sendBlocking(builder.build());
//                }
//            } else if (IMAGE_FLAG ==3) {
//                InputStream imageUrl = imageTagService.getImageInputStream(name);
//                if (imageUrl == null) {
//                    event.getSource().sendBlocking("抱歉没有找到呢ε(┬┬﹏┬┬)3");
//                } else {
//                    builder.text("您点的涩图(๑＞ڡ＜)☆\n").image(Resource.of(imageUrl));
//                    event.getSource().sendBlocking(builder.build());
//                }
//            }
//
//        }
//    }

}
