package simbot.cycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.cycle.entity.YuGiOh.Card;


@Mapper
public interface CardMapper extends BaseMapper<Card> {
}
