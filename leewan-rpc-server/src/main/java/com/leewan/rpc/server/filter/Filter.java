package com.leewan.rpc.server.filter;

import com.leewan.rpc.share.message.RequestMessage;
import com.leewan.rpc.share.message.ResponseMessage;

public interface Filter {

    void doFilter(RequestMessage request, ResponseMessage response, FilterChain chain) throws Exception;
}
