package simbot.cycle.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import simbot.cycle.entity.Card;
import simbot.cycle.entity.CardData;
import simbot.cycle.entity.CardText;
import simbot.cycle.mapper.CardMapper;
import simbot.cycle.service.CardDataService;
import simbot.cycle.service.CardService;
import simbot.cycle.service.CardTextService;

import java.util.List;


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
        Card card = this.getById(id);
        CardText text = cardTextService.getById(card.getId());
        CardData data = cardDataService.getById(card.getId());
        card.setText(text);
        card.setData(data);
        return card;
    }
}
