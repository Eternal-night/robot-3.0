package simbot.example.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.example.entity.Tags;


@Mapper
public interface TagsMapper extends BaseMapper<Tags> {
}
