package simbot.cycle.entity.draw;

import lombok.Data;

import java.io.Serializable;

@Data
public class PostData implements Serializable {
    Integer fn_index = 11;
    Object[] data = null;
    String session_hash  ="mmkdncyzjnm";
}
