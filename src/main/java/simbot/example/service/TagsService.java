package simbot.example.service;

import org.springframework.lang.Nullable;

public interface TagsService {

    /**
     * 提供关键词，对应的标签。
     *
     * @param chinese 关键词
     * @return 回复语句，或null。
     */
    String tags(String chinese);

}
