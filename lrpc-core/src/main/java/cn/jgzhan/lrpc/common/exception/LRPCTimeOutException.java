package cn.jgzhan.lrpc.common.exception;


import java.util.concurrent.TimeoutException;

/**
 * @author jgzhan
 * @version 1.0
 * @date 2024/12/9
 */
public class LRPCTimeOutException extends Exception {

    public LRPCTimeOutException(String message) {
        super(message);
    }
}
