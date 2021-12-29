package com.leewan.server.filter;

import com.leewan.share.message.RequestMessage;
import com.leewan.share.message.ResponseMessage;

public interface Filter {

    void doFilter(RequestMessage request, ResponseMessage response, FilterChain chain) throws Exception;
}
