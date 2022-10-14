package simbot.example.service;

import simbot.example.entity.YuGiOh;

import java.io.IOException;
import java.util.List;

public interface YuGiOhService {
    //卡片地址获取
    List<YuGiOh> YuGiOhInquire(String name) throws IOException;
}
