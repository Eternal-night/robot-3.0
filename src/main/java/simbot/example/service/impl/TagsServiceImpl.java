package simbot.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.example.entity.Tags;
import simbot.example.repository.TagsMapper;
import simbot.example.service.TagsService;
import simbot.example.util.StringUtils;

import java.util.ArrayList;
import java.util.Comparator;
import java.util.List;
import java.util.stream.Collectors;

import static simbot.example.util.WordUtils.isChinese;

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


    public List<Tags> getTAGS() {
        if (TAGS.isEmpty()) {
            List<Tags> list = this.list();
            TAGS.addAll(list);
        }
        return TAGS;
    }
}
