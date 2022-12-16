package simbot.cycle.entity.draw;


import lombok.Data;

import java.util.List;

@Data
public class Translate {

    private String type;
    private Integer errorCode;
    private Integer elapsedTime;
    private List<List<TranslateData>> translateResult;

}
