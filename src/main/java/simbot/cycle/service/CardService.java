package simbot.cycle.service;

import com.baomidou.mybatisplus.extension.service.IService;
import simbot.cycle.entity.Card;


public interface CardService extends IService<Card> {
    void saveCard(Card card);

    public Card cardInfo(Integer id);
}
