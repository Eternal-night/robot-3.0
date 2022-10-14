package simbot.example.entity;

import lombok.Data;

@Data
public class ImageEntity {
    //图片id
    private Long id;
    //图片标题
    private String title;
    //图片地址
    private Urlentity image_urls;

}
