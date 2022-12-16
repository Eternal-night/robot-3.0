package simbot.cycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class CardText implements Serializable {

    @TableId("card_id")
    private Integer cardId;
    //卡片种类文本
    @TableField("types")
    private String types;
    //魔法、陷阱效果
    @TableField("pdesc")
    private String pdesc;
    //灵摆效果
    @TableField("`desc`")
    private String desc;
}
