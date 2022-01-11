package com.leewan.rpc.share.message;

import lombok.Data;

import java.io.Serializable;

@Data
public abstract class SequenceMessage implements Message {
    //消息标记 每个通道里的消息顺序号
    private int sequence;
}
