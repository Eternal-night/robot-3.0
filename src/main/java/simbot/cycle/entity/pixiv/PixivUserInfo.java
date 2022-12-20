package simbot.cycle.entity.pixiv;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * create by MikuLink on 2021/1/6 4:01
 * for the Reisen
 * P站 用户信息
 */
@Data
public class PixivUserInfo {
    /**
     * 图片P站id
     */
    private String id;
    /**
     * 用户昵称
     */
    private String nick;
    /**
     * 头像图片链接
     */
    private String logoUrl;
}
