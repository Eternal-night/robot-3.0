package simbot.example.repository;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.example.entity.Tags;

/**
 * {@link Reply} 持久层交互
 *
 * @author ForteScarlet
 */
@Mapper
public interface TagsRepository extends BaseMapper<Tags> {
}
