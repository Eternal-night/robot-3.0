package simbot.cycle.entity.pixiv;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * create by MikuLink on 2020/3/13 16:37
 * for the Reisen
 * p站图片链接信息
 */
@Data
public class PixivImageUrlInfo {
    private String mini;
    private String thumb;
    private String small;
    private String regular;
    //原图
    private String original;
}
