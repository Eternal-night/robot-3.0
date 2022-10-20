package simbot.example.entity;

import com.baomidou.mybatisplus.annotation.TableField;
import lombok.Data;

@Data
public class Tags {
    @TableField("id")
    private Integer id;
    @TableField("tag_name")
    private String tagName;
    @TableField("chinese")
    private String chinese;

}
