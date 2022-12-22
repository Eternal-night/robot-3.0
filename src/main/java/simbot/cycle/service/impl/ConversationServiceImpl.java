package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.cycle.entity.dialogue.Conversation;
import simbot.cycle.mapper.ConversationMapper;
import simbot.cycle.service.ConversationService;
import simbot.cycle.service.RedisService;

import javax.annotation.PostConstruct;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class ConversationServiceImpl extends ServiceImpl<ConversationMapper, Conversation> implements ConversationService {

    @Autowired
    private RedisService redisService;

}
