package simbot.cycle.util;


import java.util.ArrayList;
import java.util.List;

public class CycleUtils {

    private static String IMAGE_URL_1 = "https://iw233.cn/API/Random.php";
    private static String IMAGE_URL_2 = "https://img.paulzzh.tech/touhou/random";
    private static String IMAGE_URL_3 = "https://api.ixiaowai.cn/api/api.php";
    private static String IMAGE_URL_4 = "";
    private static String IMAGE_URL_5 = "";
    private static String IMAGE_URL_6 = "";
    private static String IMAGE_URL_7 = "";

    private static String IMAGE_URL_8 = "";

    private static String IMAGE_URL_9 = "";

    private static String IMAGE_URL_10 = "";

    private static String IMAGE_URL_11 = "";

    private static String IMAGE_URL_12 = "";

    private static String IMAGE_URL_R18 = "https://image.anosu.top/pixiv/direct?r18=1&keyword=";

    private static String IMAGE_URL = "";

    private static Integer IMAGE_FLAG = 1;

    private static List<String> CONTRABAND_LIST = new ArrayList<>();


    private static String COLLECTION_100 = " 100users入り";
    private static String COLLECTION_500 = " 500users入り";
    private static String COLLECTION_1000 = " 1000users入り";
    private static String COLLECTION_5000 = " 5000users入り";
    private static String COLLECTION_10000 = " 10000users入り";
    private static String COLLECTION_20000 = " 20000users入り";
    private static String COLLECTION_30000 = " 30000users入り";
    private static String COLLECTION_50000 = " 50000users入り";
    private static String COLLECTION_100000 = " 100000users入り";

    private static String COLLECTION = "";

    private static Integer COLLECTION_FLAG = 3;

    public static String getCOLLECTION() {
        switch (COLLECTION_FLAG){
            case 1: return COLLECTION_100;
            case 2: return COLLECTION_500;
            case 3: return COLLECTION_1000;
            case 4: return COLLECTION_5000;
            case 5: return COLLECTION_10000;
            case 6: return COLLECTION_20000;
            case 7: return COLLECTION_30000;
            case 8: return COLLECTION_50000;
            case 9: return COLLECTION_100000;
            default: return COLLECTION;
        }
    }

    public static void setCollectionFlag(Integer collectionFlag) {
        COLLECTION_FLAG = collectionFlag;
    }

    public static String getImageUrl() {
        switch (IMAGE_FLAG){
            case 1: return IMAGE_URL_1;
            case 2: return IMAGE_URL_2;
            case 3: return IMAGE_URL_3;
            case 4: return IMAGE_URL_4;
            case 5: return IMAGE_URL_5;
            case 6: return IMAGE_URL_6;
            case 7: return IMAGE_URL_7;
            case 8: return IMAGE_URL_8;
            case 9: return IMAGE_URL_9;
            case 10: return IMAGE_URL_10;
            case 11: return IMAGE_URL_11;
            case 12: return IMAGE_URL_12;
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
