package simbot.cycle.entity;


import lombok.Data;

import java.util.List;

@Data
public class Translate {

    private String type;
    private Integer errorCode;
    private Integer elapsedTime;
    private List<List<TranslateData>> translateResult;

}
