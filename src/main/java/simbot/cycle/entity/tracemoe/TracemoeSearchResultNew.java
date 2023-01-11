package simbot.cycle.entity.tracemoe;

import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Setter
@Getter
public class TracemoeSearchResultNew {
    //搜索的帧总数
    private Long frameCount;
    //错误信息
    private String error;
    //所搜索结果
    private List<TracemoeSearchDocNew> result;
}
