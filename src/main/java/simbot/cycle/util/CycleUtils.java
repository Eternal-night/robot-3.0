package simbot.cycle.util;

import java.util.ArrayList;
import java.util.List;

public class CycleUtils {

    private static Integer IMAGE_FLAG = 1;



    private static List<String> CONTRABAND_LIST = new ArrayList<>();

    public static void setImageFlag(Integer imageFlag) {
        IMAGE_FLAG = imageFlag;
    }


    public static void setContrabandList(List<String> contrabandList) {
        CONTRABAND_LIST = contrabandList;
    }

    public static Integer getImageFlag(){
        return IMAGE_FLAG;
    }


    public static List<String> getContrabandList() {
        return CONTRABAND_LIST;
    }
}
