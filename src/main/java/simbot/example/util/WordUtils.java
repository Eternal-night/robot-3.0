package simbot.example.util;

import lombok.val;

public class WordUtils {

    // 根据Unicode编码完美的判断中文汉字和符号
    public static Boolean isChinese(String word) {

        char[] chars = word.toCharArray();

        for (char aChar : chars) {

            Character.UnicodeBlock ub = Character.UnicodeBlock.of(aChar);

            return ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_COMPATIBILITY_IDEOGRAPHS || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_A || ub == Character.UnicodeBlock.CJK_UNIFIED_IDEOGRAPHS_EXTENSION_B || ub == Character.UnicodeBlock.CJK_SYMBOLS_AND_PUNCTUATION || ub == Character.UnicodeBlock.HALFWIDTH_AND_FULLWIDTH_FORMS || ub == Character.UnicodeBlock.GENERAL_PUNCTUATION;
        }

        return false;
    }


}
