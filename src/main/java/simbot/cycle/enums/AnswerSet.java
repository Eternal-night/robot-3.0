package simbot.cycle.enums;

import lombok.Getter;

@Getter
public enum AnswerSet {


    ;


    private final String reply;

    AnswerSet(String reply) {
        this.reply = reply;
    }
}
