package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import simbot.cycle.entity.YuGiOh.CardData;
import simbot.cycle.mapper.CardDataMapper;
import simbot.cycle.service.CardDataService;

@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class CardDataServiceImpl extends ServiceImpl<CardDataMapper, CardData> implements CardDataService {

}
