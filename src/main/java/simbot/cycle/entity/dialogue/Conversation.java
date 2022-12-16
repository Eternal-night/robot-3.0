package simbot.cycle.entity.dialogue;

import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

@Data
public class Conversation {

    //主键
    @TableId("id")
    private Integer id;
    //问题
    @TableField("asking")
    private String asking;
    //回答
    @TableField("feedback")
    private String feedback;


}
