package simbot.cycle.entity.draw;

import com.baomidou.mybatisplus.annotation.IdType;
import com.baomidou.mybatisplus.annotation.TableField;
import com.baomidou.mybatisplus.annotation.TableId;
import lombok.Data;

import java.util.Date;

@Data
public class Contraband {


    @TableId(type= IdType.AUTO)
    private Integer contrabandId;


    @TableField("word")
    private String word;

    /**
     * 创建人（关联用户表）
     */
    @TableField("create_by")
    private String createBy;
    /**
     * 创建时间
     */
    @TableField("create_time")
    private Date createTime;
    /**
     * 更新人（关联用户表）
     */
    @TableField("update_by")
    private String updateBy;
    /**
     * 更新时间
     */
    @TableField("update_time")
    private Date updateTime;
    /**
     * 删除标识 0-正常 1-删除
     */
    @TableField("del_flag")
    private Integer delFlag;






}
