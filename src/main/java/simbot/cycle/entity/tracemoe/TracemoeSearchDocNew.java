package simbot.cycle.entity.tracemoe;

import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Setter
@Getter
public class TracemoeSearchDocNew {


    //匹配的 Anilist ID 或 Anilist 信息
    private Integer anilist;
    //找到匹配项的文件的文件名
    private String filename;
    //从文件名中提取的剧集编号
    private Integer episode;
    //匹配场景开始时间（秒）
    private Double from;
    //匹配场景结束时间（秒）
    private Double to;
    //与搜索图像的相似度
    private Double similarity;
    //匹配场景的预览视频地址
    private String video;
    //匹配场景预览图的URL
    private String image;

}
