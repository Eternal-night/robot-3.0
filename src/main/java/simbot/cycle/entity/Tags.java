package simbot.cycle.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

//ai画图的标签表，存放了标签的中英文对照及训练次数
@Data
public class Tags {

    //主键
    @TableField("id")
    private Integer id;

    //标签-英文
    @TableField("tag_name")
    private String tagName;

    //标签-中文
    @TableField("chinese")
    private String chinese;

    //训练次数
    @TableField("quote")
    private Integer quote;

}
