package com.leewan.share.message;

import lombok.Data;

import java.io.Serializable;
import java.util.List;

/**
 * 方法元数据
 */
@Data
public class MethodMeta {
    /**
     * 方法标记 优先使用方法标记
     * 如果为-1 则使用下面的元数据
     */
    private int methodFlag = -1;
    private String clazzName;
    private String methodName;
    private List<String> parameterTypeNames;
}
