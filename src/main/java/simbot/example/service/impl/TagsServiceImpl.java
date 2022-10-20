package simbot.example.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.example.entity.Tags;
import simbot.example.repository.TagsRepository;
import simbot.example.service.TagsService;

import java.util.List;

@Service
@Transactional(value = "transactionManager",rollbackFor = Exception.class)
public class TagsServiceImpl extends ServiceImpl<TagsRepository, Tags> implements TagsService {

    @Override
    public String tags(String chinese) {
        List<Tags> tagsList = this.list(new LambdaQueryWrapper<Tags>().like(Tags::getChinese, chinese));
        if (tagsList!=null && !tagsList.isEmpty()){
          return   tagsList.get(0).getTagname();
        }else {
            return null;
        }
    }
}
