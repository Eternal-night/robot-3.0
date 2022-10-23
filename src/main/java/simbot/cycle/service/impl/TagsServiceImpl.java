package simbot.cycle.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.cycle.entity.Tags;
import simbot.cycle.repository.TagsMapper;
import simbot.cycle.service.TagsService;
import simbot.cycle.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static simbot.cycle.util.WordUtils.isChinese;

@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class TagsServiceImpl extends ServiceImpl<TagsMapper, Tags> implements TagsService {

    private static List<Tags> TAGS = new ArrayList<>();

    @Override
    public String tags(String chinese) {
        if (StringUtils.isEmpty(chinese)) {
            return null;
        }

        //判断是否为中文
        if (!isChinese(chinese)) {
            return chinese;
        }

        String tag = null;

        //判断是否有完全一致的标签
        tag = getTAGS().stream().filter(n -> chinese.equals(n.getChinese())).map(Tags::getTagName).findFirst().orElse(null);

        if (tag != null) {
            return tag;
        }
        //返回训练次数最多的标签

        tag = getTAGS().stream()
                .filter(n -> n.getChinese().contains(chinese))
                .max(Comparator.comparing(Tags::getQuote))
                .map(Tags::getTagName).orElse(null);

        return tag;
    }

    @Override
    public List<Tags> findTags(String tags) {
        String trim = StringUtils.trim(tags);
        if (StringUtils.isEmpty(trim)) {
            return null;
        }

        String replaceAll = trim.replaceAll("，", ",");

        String[] split = replaceAll.split(",");

        List<Tags> tagList = new ArrayList<>();

        for (String tag : split) {
            if (isChinese(tag)) {
                tagList.addAll(getTAGS().stream().filter(n -> n.getChinese().contains(tags)).collect(Collectors.toList()));
            }else {
                tagList.addAll(getTAGS().stream().filter(n -> n.getTagName().contains(tags)).collect(Collectors.toList()));
            }
        }

        if (tagList.isEmpty()) {
         return tagList;
        }

        return tagList.stream().distinct().collect(Collectors.toList());
    }


    public List<Tags> getTAGS() {
        if (TAGS.isEmpty()) {
            List<Tags> list = this.list();
            TAGS.addAll(list);
        }
        return TAGS;
    }
}
