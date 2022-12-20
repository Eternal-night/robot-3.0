package simbot.cycle.entity.saucenao;

import lombok.Data;
import lombok.Getter;
import lombok.Setter;

/**
 * create by MikuLink on 2020/2/19 13:18
 * for the Reisen
 */
@Data
public class SaucenaoSearchInfoResult {
    private SaucenaoSearchInfoResultHeader header;
    private SaucenaoSearchInfoResultData data;
}
