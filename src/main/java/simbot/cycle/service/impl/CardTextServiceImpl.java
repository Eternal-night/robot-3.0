package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import simbot.cycle.entity.YuGiOh.CardText;
import simbot.cycle.mapper.CardTextMapper;
import simbot.cycle.service.CardTextService;

@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class CardTextServiceImpl extends ServiceImpl<CardTextMapper, CardText> implements CardTextService {

}
