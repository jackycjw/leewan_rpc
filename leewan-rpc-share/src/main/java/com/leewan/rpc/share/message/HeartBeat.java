package com.leewan.rpc.share.message;

/**
 * @author chenjw
 * @Date 2022/1/11 12:49
 */
public class HeartBeat implements Message {
    @Override
    public byte getType() {
        return TYPE_HEART_BEAT;
    }
}
