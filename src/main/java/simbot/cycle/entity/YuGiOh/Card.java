package simbot.cycle.entity.YuGiOh;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.io.Serializable;

@Data
public class Card implements Serializable {

    //主键
    @TableId("id")
    private Integer id;
    //卡片id
    @TableField("cid")
    private Integer cid;
    //中文名称
    @TableField("cn_name")
    private String cnName;
    //中文OCG名称
    @TableField("cnocg_n")
    private String cnocgN;
    //日文名称
    @TableField("jp_name")
    private String jpName;
    //日文名称【副】
    @TableField("jp_ruby")
    private String jpRuby;
    //英文名称
    @TableField("en_name")
    private String enName;

    @TableField(exist = false)
    private String imageUrl;

    //魔法、陷阱、灵摆效果文本
    @TableField(exist = false)
    private CardText text;

    //怪兽效果文本
    @TableField(exist = false)
    private CardData data;

    public String getImageUrl() {
        return "https://cdn.233.momobako.com/ygopro/pics/"+id+".jpg";
    }

}
