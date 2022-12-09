package simbot.cycle.mapper;

import com.baomidou.mybatisplus.core.mapper.BaseMapper;
import org.apache.ibatis.annotations.Mapper;
import simbot.cycle.entity.Conversation;

@Mapper
public interface ConversationMapper extends BaseMapper<Conversation> {
}
