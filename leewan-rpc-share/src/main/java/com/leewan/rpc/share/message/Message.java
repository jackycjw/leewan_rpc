package com.leewan.rpc.share.message;

import java.io.Serializable;

/**
 * @author chenjw
 * @Date 2022/1/11 12:47
 */
public interface Message extends Serializable {
    byte TYPE_REQUEST_MESSAGE = 0;
    byte TYPE_RESPONSE_MESSAGE = 1;
    byte TYPE_HEART_BEAT = 2;

    byte getType();
}
