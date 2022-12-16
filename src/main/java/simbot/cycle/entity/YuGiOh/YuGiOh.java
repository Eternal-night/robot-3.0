package simbot.cycle.entity.YuGiOh;

import lombok.Data;

import java.util.List;

@Data
public class YuGiOh {
    //图片地址
    private String imageUrl;
    //名称
    private List<String> nameList;
    //效果
    private YuGiOhDetails details;
}
