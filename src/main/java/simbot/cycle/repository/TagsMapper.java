package simbot.cycle.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.cycle.entity.Tags;


@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
}
