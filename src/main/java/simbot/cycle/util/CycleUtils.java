package simbot.cycle.util;


import java.util.ArrayList;
import java.util.List;

public class CycleUtils {

    private static String IMAGE_URL_1 = "https://iw233.cn/API/Random.php";
    private static String IMAGE_URL_2 = "https://img.paulzzh.tech/touhou/random";
    private static String IMAGE_URL_3 = "http://img.xjh.me/random_img.php";
    private static String IMAGE_URL_4 = "https://img.xjh.me/random_img.php";
    private static String IMAGE_URL_5 = "https://api.yimian.xyz/img";
    private static String IMAGE_URL_6 = "https://api.mtyqx.cn/tapi/random.php";
    private static String IMAGE_URL_7 = "https://api.ixiaowai.cn/api/api.php";

    private static String IMAGE_URL_R18 = "https://image.anosu.top/pixiv/direct?r18=1&keyword=";

    private static String IMAGE_URL = "";

    private static Integer IMAGE_FLAG = 1;

    private static List<String> CONTRABAND_LIST = new ArrayList<>();

    public static String getImageUrl() {
        switch (IMAGE_FLAG){
            case 1: return IMAGE_URL_1;
            case 2: return IMAGE_URL_2;
            case 3: return IMAGE_URL_3;
            case 4: return IMAGE_URL_4;
            case 5: return IMAGE_URL_5;
            case 6: return IMAGE_URL_6;
            case 7: return IMAGE_URL_7;
            case 12: return IMAGE_URL_R18;
            default: return IMAGE_URL;
        }
    }

    public static void setImageFlag(Integer imageFlag) {
        IMAGE_FLAG = imageFlag;
    }

    public static String getImageUrlR18() {
        return IMAGE_URL_R18;
    }

    public static void setContrabandList(List<String> contrabandList) {
        CONTRABAND_LIST = contrabandList;
    }

    public static List<String> getContrabandList() {
        return CONTRABAND_LIST;
    }
}
