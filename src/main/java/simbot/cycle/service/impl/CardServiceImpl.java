package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.cycle.entity.YuGiOh.Card;
import simbot.cycle.entity.YuGiOh.CardData;
import simbot.cycle.entity.YuGiOh.CardText;
import simbot.cycle.mapper.CardMapper;
import simbot.cycle.service.CardDataService;
import simbot.cycle.service.CardService;
import simbot.cycle.service.CardTextService;


@Service
@Transactional(value = "transactionManager", rollbackFor = Exception.class)
public class CardServiceImpl extends ServiceImpl<CardMapper, Card> implements CardService {

    @Autowired
    private CardDataService cardDataService;
    @Autowired
    private CardTextService cardTextService;

    @Override
    public void saveCard(Card card) {

        try {
            CardData data = card.getData();
            CardText text = card.getText();

            data.setCardId(card.getId());
            text.setCardId(card.getId());

            this.save(card);
            cardDataService.save(data);
            cardTextService.save(text);
        }catch (Exception e){
            e.printStackTrace();
            System.out.println(card.toString());
        }


    }

    @Override
    public Card cardInfo(Integer id) {

        Card one = this.getOne(new LambdaQueryWrapper<Card>().eq(Card::getCid, id).last("limit 1"));

        CardText text = cardTextService.getById(one.getId());
        CardData data = cardDataService.getById(one.getId());
        one.setText(text);
        one.setData(data);
        return one;
    }
}
