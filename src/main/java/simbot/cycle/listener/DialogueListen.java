package simbot.cycle.listener;

import com.github.plexpt.chatgpt.Chatbot;
import love.forte.simboot.annotation.ContentTrim;
import love.forte.simboot.annotation.Filter;
import love.forte.simboot.annotation.FilterValue;
import love.forte.simboot.annotation.Listener;
import love.forte.simboot.filter.MatchType;
import love.forte.simbot.definition.Member;
import love.forte.simbot.event.ContinuousSessionContext;
import love.forte.simbot.event.GroupMessageEvent;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import simbot.cycle.entity.dialogue.Conversation;
import simbot.cycle.service.RedisService;
import simbot.cycle.util.StringUtils;

import java.io.IOException;
import java.util.List;
import java.util.Map;
import java.util.Random;

/**
 * @description: ai对话
 * @author: 陈杰
 **/
@Component
public class DialogueListen {

    private static String token = "eyJhbGciOiJkaXIiLCJlbmMiOiJBMjU2R0NNIn0..GSj3pIfZcQOfbKDj.EZrqpzCqrpML3D9aME8L-Z0vLo3WcTd9xJkAJHVT-Qe9izQp2ktC7u8x94GkZKgLC6yF2zAiodqSY7HcAkFcjelBwcbxgct6H-joZH9LlNwCEd0Te_zPHu-_Y7RrToqiV3nBurT8aLfx40AG82uUhR2QfafSsfz4v8TupoAgbpjRpa8n37YLGgp6MDUprnGInKKRjPQY_Nb0D6sdaYwB48pg-Wec7VchGURyH84pLaBB8TQBHd7vDZCfUOwzYgvlL3yW3IuJjP0duKAP0lWi4K_XO2O6nLxfh9XOquCavBujk1KGAfDJvxfg8I_V_5rM4PZXzaHpkgu7UPsJPG9otmxUrmJCpUeHHIw1qZjzNPQPV3hOZSWkZgbcfLbp4P_ebw9GXGtYg8Pc8W7THkkKTSPcmuiRwyRHGyO4yPZ-xVYdhsIbrKLFAgI3R9lyPpEkbixNuP6YipRqScrMHS3rJaqg8bS77mVS2-YJSvqWIuPkud5324TFJSEIFzub9cfSn8vnnG8P1SgIXC3orFnLlX9H-Nz8uBVbt0tDLVnC98Hnmv3yvpoezTDbLI9xckcHAYI4dg5fBfAlOm-iekwrAU2CnQTRU2AecDiYeDe_EvbuKnb1PIglx_JQ7ycGGmhEWMT9yfbIvFBwekvgsCfJh2aJO5oQFr_plfTDPrktepJjieNjMQnb-whttdyJSB9_3KQSykDczClnmKZwGihSSCZQhTkY1TqFHM5m3QEJJzVj5fPDxhpMQ5qTJ257xIhD87-eteD3dwICaPmzCLVwFg71IlOAwmglUFmlVUiA71LeNkZLt3ya-xIj1QEo-gOPRw7pYoMj2VaTxn5Tw6v3iOqsT-j-WLAqhQmkNVSKoKITP6Sph7ZrKyBO0YMbpTG8eOLhi_D4Paj95Vnm85DbTZENkG6vNUK3dQW28yxz8KnRnpQZLcITqqQiWKiJd6D9g41LF5HY8IimsUvfy5VOR5FFFV8ChrSEW4fkXMphNtw1v8P9r0REnB36ZYKITMQBjTjHp6ZaUoL1qfaqL197MuAPyrbMcBpOZ9ksshJiAVoVmnnvbZhIFcfiUv8npkPQDHr7cRFYi5ZmleX1w-y6NAO-DaFXArmMrXBhNbFwSht6xO442eGzQIsrT-Hx0GrkfFHbvq6QPb8HUdpu-YIj9tAt_LeSw_6uWHDOOYUuryv-o0Y-g1pM6CmZPQVsEypVfCppjJz8H3Io6xjdKnJaAE5bf4vw39xT4fJQcT3g811HLZPnkk2aaDwC2iA_qtZjFtNUZEzYA-iDm5Nbomrlwr2l0h4y3Uav9I-ouYpaGnsrL6uldVf3UrKZ3mEVXY333pgzhPsKPvyR4vRYgQzrPkKP7-XifAUV9I5Px2__Yw19ghiygOMXWxxIixycvlDPcdzKrShr15F4PIj3895tJYlIrWbyqOCjLWCfkfRQceL8s9osH5OI1u5xzuItnz9jWbdbjdLeTzoMf0Q8Km1O3nkl2bdKROC1Gpa-w8mHlqrI6RomKToXwVp4n9asFa3zFQzEOm6G4pntimx683ZmXAeKRBzJthBluSdGJ2WlOxsXyG8wDlibKXMlHt7LBNwrwtEVdOFofwWwoyRCq8kEYIkgmqitMIro1J86aaWybk1xLv1_n_fneiPYgcrdmkaqla4bF5jS0TFOJe1bOTPlek4gHTfMmH-ZOIRusLZeKexahLdNppH_gyx_-3CaRKgsZVwt6C6kbeV3OS7N1zNCgNAU-LJrnWR8liSE_yHQ1j-YTzzX9jYDwyE_Xm84-LYQFx6Mpk-Q5L74eboMGNkCuyqBPlCtnL7J0p8wcdEaWZjUAjz2hBb6Zhz7bFzhnYosf1OAd40Pz_spSICPieQlUC-3tXLqxwU2sLF8kkPx9mO-mxBiOm91PR48fehQ_aV16kmdBxB8WhAnESst7Awdz8_M7lDJDU_A2_VBHZoPczXqnx8IMrCaZCjtvcfHWdiufCpqPt9_4M8Zxg0HXBrZznx_iD36bmTGcyRAnWaenV-0SGA9_F7DCIwgHzrUJMhkDhoiDWkS1XA-9hZMJ6MehiuobmhkOZC-nlWcScLUKbkN8Ac4-jmm_AzHr0iOHbR-FtgctdUeWvtIoJeiCgLWthpoWK9niSUI0pxGtX0vYMM-Rs4DK3LC3IKoAOwGw3irdzWcn7uvoQOcPePQ01ggTokN5YsjnBF4K0blrWDymI7axCJ5KUOm9l9pp57zDEVyQXHy2f2IbLerjnNBIP7Bw_B1l4UV31e7P-3qSxIlvzrk0UyqmwF6IK0b4StiCi-eVGbjfBofOZT5SO_fZTlGLg-aLqGk.aJHUOmeSyvzK2XFa6f0_yQ";

    @Autowired
    private RedisService redisService;

//    @Listener
    public void reply(GroupMessageEvent event) throws InterruptedException {
        String plainText = event.getMessageContent().getPlainText();
        if (redisService.hasKey(plainText)) {
            List<Object> cacheList = redisService.getCacheList(plainText);
            Random random = new Random();
            if (!cacheList.isEmpty()) {
                Object obj = cacheList.get(random.nextInt(cacheList.size()));
                if (obj!=null){
                    Member author = event.getAuthor();
                    String name = author.getUsername();
                    Conversation conversation = new Conversation();
                    BeanUtils.copyProperties(obj,conversation);
                    String feedback = conversation.getFeedback();
                    String replaceAll = feedback.replaceAll("\\{name}", name);
                    String[] split = replaceAll.split("\\{segment}");
                    for (String message : split) {
                        event.getGroup().sendBlocking(message);
                    }
                }
            }
        }
    }



    @Listener
    @Filter(value = "刻刻帝{{name}}", matchType = MatchType.REGEX_MATCHES)
    @ContentTrim
    public void chatGPT(ContinuousSessionContext sessionContext, GroupMessageEvent event, @FilterValue("name") String name) throws IOException {
        if (StringUtils.isNotEmpty(name)) {
            Chatbot chatbot = new Chatbot(token);
            Map<String, Object> chatResponse = chatbot.getChatResponse(name);
            Object message = chatResponse.get("message");
            event.getGroup().sendBlocking(String.valueOf(message));
        }
    }


}
