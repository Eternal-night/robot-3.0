package simbot.cycle.entity;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostData implements Serializable {
    Object[] data= null;
    Integer fn_index = 11;
    String session_hash  ="mqmlyym41fl";
}
