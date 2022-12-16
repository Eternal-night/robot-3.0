package simbot.cycle.exceptions;

/**
 * 自定义业务异常 api专用
 */
public class CycleApiException extends Exception {
    public CycleApiException(String msg) {
        super(msg);
    }
}
