package com.leewan.rpc.server.filter;

import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;

public interface FilterChain {
    void doFilter(RequestMessage request, ResponseMessage response);
}
