package simbot.example.service.impl;


import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.example.entity.Tags;
import simbot.example.repository.TagsRepository;
import simbot.example.service.TagsService;

import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class TagsServiceImpl extends ServiceImpl<TagsRepository, Tags> implements TagsService {

    private static List<Tags> TAGS = new ArrayList<>();


    @Override
    public String tags(String chinese) {
        if (TAGS.isEmpty()) {
            List<Tags> list = this.list();
            TAGS.addAll(list);
        }
        List<Tags> tagsList = TAGS.stream().filter(n -> n.getChinese().contains(chinese)).collect(Collectors.toList());

        if (!tagsList.isEmpty()) {
            return tagsList.get(0).getTagName();
        } else {
            return null;
        }
    }
}
