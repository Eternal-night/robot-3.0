package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.cycle.entity.Contraband;
import simbot.cycle.mapper.ContrabandMapper;
import simbot.cycle.service.ContrabandService;


@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class ContrabandServiceImpl extends ServiceImpl<ContrabandMapper, Contraband> implements ContrabandService {
}
