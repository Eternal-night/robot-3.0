package simbot.cycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;

@lombok.Data
public class CardData {


    @TableId("card_id")
    private Integer cardId;
    //
    @TableField("ot")
    private Integer ot;
    //
    @TableField("setcode")
    private Integer setcode;
    //
    @TableField("type")
    private Integer type;
    //攻击力
    @TableField("atk")
    private Integer atk;
    //防御力
    @TableField("def")
    private Integer def;
    //
    @TableField("level")
    private Integer level;
    //
    @TableField("race")
    private Integer race;
    //
    @TableField("attribute")
    private Integer attribute;

}
