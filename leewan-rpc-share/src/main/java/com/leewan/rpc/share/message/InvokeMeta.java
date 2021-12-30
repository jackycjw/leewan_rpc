package com.leewan.rpc.share.message;

import lombok.Data;

import java.util.List;
import java.util.Objects;

/**
 * 方法元数据
 */
@Data
public class InvokeMeta {
    /**
     * 方法标记 优先使用方法标记
     * 如果为-1 则使用下面的元数据
     */
    private int invokeId = -1;

    private String clazzName;
    private String methodName;
    private List<String> parameterTypeNames;


    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        InvokeMeta that = (InvokeMeta) o;
        return Objects.equals(clazzName, that.clazzName) && Objects.equals(methodName, that.methodName) && Objects.equals(parameterTypeNames, that.parameterTypeNames);
    }

    @Override
    public int hashCode() {
        return Objects.hash(clazzName, methodName, parameterTypeNames);
    }
}
