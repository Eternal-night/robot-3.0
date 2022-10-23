package simbot.cycle.service.impl;


import com.alibaba.fastjson2.JSON;
import com.alibaba.fastjson2.JSONObject;
import lombok.val;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;
import org.springframework.stereotype.Service;
import simbot.cycle.entity.Translate;
import simbot.cycle.service.TranslationService;
import simbot.cycle.util.OkHttpUtils;

import java.io.IOException;

@Service
public class TranslationServiceImpl implements TranslationService {
    @Override
    public String ChineseToEnglish(String chinese) {
        try {
            OkHttpClient client = OkHttpUtils.getOkHttpClient();
            Request build = new Request.Builder().url("https://fanyi.youdao.com/translate?&doctype=json&type=ZH_CN2EN&i="+chinese).get().build();
            Response response = client.newCall(build).execute();
            if (!response.isSuccessful()) throw new IOException("Unexpected code " + response);
            Translate translate = JSON.parseObject(response.body().string()).to(Translate.class);
            return translate.getTranslateResult().get(0).get(0).getTgt();
        } catch (Exception e) {
            return null;
        }
    }
}
