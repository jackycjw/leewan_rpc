package com.leewan.rpc.share.message;

import lombok.Data;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * 最底层的消息体
 */
@Data
public class RequestMessage extends SequenceMessage {
    /**
     * 方法元数据
     */
    private InvokeMeta invokeMeta;
    /**
     * 消息头信息
     */
    private Map<String, String> headers = new HashMap<>();

    /**
     * 方法入参
     */
    private List<Object> parameters = new ArrayList<>(3);

    @Override
    public byte getType() {
        return TYPE_REQUEST_MESSAGE;
    }
}
