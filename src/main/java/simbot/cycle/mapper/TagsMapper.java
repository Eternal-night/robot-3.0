package simbot.cycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.cycle.entity.draw.Tags;


@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
}
